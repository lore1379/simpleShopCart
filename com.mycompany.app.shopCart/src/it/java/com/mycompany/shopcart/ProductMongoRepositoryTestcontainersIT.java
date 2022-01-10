package com.mycompany.shopcart;

import static org.assertj.core.api.Assertions.assertThat;

import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.MongoDBContainer;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mycompany.shopcart.model.Product;
import com.mycompany.shopcart.repository.mongo.ProductMongoRepository;

public class ProductMongoRepositoryTestcontainersIT {
	
	@ClassRule
	public static final MongoDBContainer mongo =
		new MongoDBContainer("mongo:4.4.3");

	private MongoClient client;
	private ProductMongoRepository productRepository;
	private MongoCollection<Document> productCollection;

	private static final String SHOP_DB_NAME = "shop";
	private static final String PRODUCT_COLLECTION_NAME = "product";

	
	@Before
	public void setup() {
		client = new MongoClient(
			new ServerAddress(
				mongo.getContainerIpAddress(),
				mongo.getMappedPort(27017)));
		productRepository =
				new ProductMongoRepository(client, SHOP_DB_NAME, PRODUCT_COLLECTION_NAME);
		MongoDatabase database = client.getDatabase(SHOP_DB_NAME);
		// make sure we always start with a clean database
		database.drop();
		productCollection = database.getCollection(PRODUCT_COLLECTION_NAME);
	}
	
	@After
	public void tearDown() {
		client.close();
	}
	
	@Test
	public void testFindAll() {
		addTestProductToDatabase("1", "test1");
		addTestProductToDatabase("2", "test2");
		assertThat(productRepository.findAll())
			.containsExactly(
				new Product("1", "test1"),
				new Product("2", "test2"));
	}
	
	@Test
	public void testFindById() {
		addTestProductToDatabase("1", "test1");
		addTestProductToDatabase("2", "test2");
		assertThat(productRepository.findById("2"))
			.isEqualTo(new Product("2", "test2"));
	}
	
	@Test
	public void testDelete() {
		addTestProductToDatabase("1", "test1");
		productRepository.delete("1");
		assertThat(productCollection.find()).isEmpty();
	}
	
	private void addTestProductToDatabase(String id, String name) {
		productCollection.insertOne(
				new Document()
					.append("id", id)
					.append("name", name));
	}

}