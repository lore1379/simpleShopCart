package com.mycompany.shopcart.controller;

import static org.awaitility.Awaitility.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.bson.Document;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testcontainers.containers.MongoDBContainer;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mycompany.shopcart.model.Product;
import com.mycompany.shopcart.repository.mongo.ProductMongoRepositoryInTransaction;
import com.mycompany.shopcart.view.ShopView;

@RunWith(GUITestRunner.class)
public class ShopControllerRaceConditionIT extends AssertJSwingJUnitTestCase {
		
	@ClassRule
	public static final MongoDBContainer mongo = new MongoDBContainer("mongo:4.4.3");
	
	@Mock
	private ShopView productView;
	
	private MongoClient mongoClient;
	private ProductMongoRepositoryInTransaction productRepository;
	private MongoCollection<Document> productCollection;
	private AutoCloseable closeable;
	
	private static final String SHOP_DB_NAME = "shop";
	private static final String PRODUCT_COLLECTION_NAME = "product";
	
	@Override
	protected void onSetUp() {
		closeable = MockitoAnnotations.openMocks(this);
		mongoClient = new MongoClient(
				new ServerAddress(
						mongo.getContainerIpAddress(),
						mongo.getFirstMappedPort()));
		productRepository = new ProductMongoRepositoryInTransaction(mongoClient, SHOP_DB_NAME, PRODUCT_COLLECTION_NAME);
		MongoDatabase database = mongoClient.getDatabase(SHOP_DB_NAME);
		database.drop();
		productCollection = database.getCollection(PRODUCT_COLLECTION_NAME);
	}
	
	@Override
	protected void onTearDown() throws Exception{
		mongoClient.close();
		closeable.close();
	}
	
	@Test @GUITest
	public void testCheckoutWithConcurrentThreadsOnlyOneShouldCheckout() {
		Product product = new Product("1", "test");
		addTestProductToDatabase(product.getId(), product.getName());
		List<Thread> threads = IntStream.range(0, 10)
				.mapToObj(i -> new Thread(
						() -> 
						new ShopControllerInTransaction(mongoClient, productView, productRepository).checkoutProduct(product)))
				.peek(t -> t.start())
				.collect(Collectors.toList());
		await().atMost(10, TimeUnit.SECONDS)
			.until(() -> threads.stream().noneMatch(t -> t.isAlive()));
		verify(productView, times(1)).checkoutProduct(product);
		verify(productView, times(9)).showErrorProductNotFound("The product you are trying to buy is no longer available", product);
	}
	
	private void addTestProductToDatabase(String id, String name) {
		productCollection.insertOne(
				new Document()
					.append("id", id)
					.append("name", name));
	}
	
}
	
	