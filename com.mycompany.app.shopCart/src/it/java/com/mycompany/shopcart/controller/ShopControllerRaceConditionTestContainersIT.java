package com.mycompany.shopcart.controller;

import static org.awaitility.Awaitility.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testcontainers.containers.MongoDBContainer;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mycompany.shopcart.model.Product;
import com.mycompany.shopcart.repository.mongo.ProductMongoRepository;
import com.mycompany.shopcart.view.ShopView;

public class ShopControllerRaceConditionTestContainersIT {

	@ClassRule
	public static final MongoDBContainer mongo = 
		new MongoDBContainer("mongo:4.4.3").withCommand(("--replSet rs0"));

	@Mock
	private ShopView shopView;

	private MongoClient mongoClient;
	private ProductMongoRepository productRepository;
	private MongoCollection<Document> productCollection;
	private AutoCloseable closeable;

	private static final String SHOP_DB_NAME = "shop";
	private static final String PRODUCT_COLLECTION_NAME = "product";

	@Before
	public void setup() {
		closeable = MockitoAnnotations.openMocks(this);
		mongoClient = new MongoClient(
				new ServerAddress(
						mongo.getContainerIpAddress(), 
						mongo.getFirstMappedPort()));
		productRepository = 
				new ProductMongoRepository(mongoClient, SHOP_DB_NAME, PRODUCT_COLLECTION_NAME);
		MongoDatabase database = mongoClient.getDatabase(SHOP_DB_NAME);
		database.drop();
		productCollection = database.getCollection(PRODUCT_COLLECTION_NAME);
	}

	@After
	public void tearDown() throws Exception {
		mongoClient.close();
		closeable.close();
	}

	@Test
	public void testCheckoutWithConcurrentThreadsOnlyOneShouldCheckout() {
		Product product = new Product("1", "test");
		addTestProductToDatabase(product.getId(), product.getName());
		List<Thread> threads = IntStream.range(0, 10)
				.mapToObj(i -> new Thread(
						() -> 
						new ShopController(shopView, productRepository)
							.checkoutProduct(product)))
				.peek(t -> t.start())
				.collect(Collectors.toList());
		await().atMost(10, TimeUnit.SECONDS)
		.until(() -> threads.stream().noneMatch(t -> t.isAlive()));
		verify(shopView, times(9)).showErrorProductNotFound("The product you are trying to buy is no longer available", product);;
		
	}

	private void addTestProductToDatabase(String id, String name) {
		productCollection.insertOne(new Document().append("id", id).append("name", name));
	}

}
