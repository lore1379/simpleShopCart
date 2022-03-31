package com.mycompany.shopcart.repository.mongo;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.MongoDBContainer;

import com.mongodb.MongoClient;
import com.mongodb.MongoCommandException;
import com.mongodb.ServerAddress;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mycompany.shopcart.model.Product;

public class ProductMongoRepositoryTestContainersIT {
	
	@ClassRule
	public static final MongoDBContainer mongo = 
		new MongoDBContainer("mongo:4.4.3");

	private static MongoClient client;
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
				new ProductMongoRepository(client, 
						SHOP_DB_NAME, PRODUCT_COLLECTION_NAME);
		MongoDatabase database = client.getDatabase(SHOP_DB_NAME);
		database.drop();
		productCollection = database.getCollection(PRODUCT_COLLECTION_NAME);
	}

	@After
	public void tearDown() {
		client.close();
	}

	@Test
	public void testFindAllWhenDatabaseIsEmpty() {
		assertThat(productRepository.findAll()).isEmpty();
	}

	@Test
	public void testFindAllWhenDatabaseIsNotEmpty() {
		addTestProductToDatabase("1", "test1");
		addTestProductToDatabase("2", "test2");
		assertThat(productRepository.findAll())
			.containsExactly(
					new Product("1", "test1"), 
					new Product("2", "test2"));
	}

	@Test
	public void testFindByIdNotFound() {
		assertThat(productRepository.findById("1")).isNull();
	}

	@Test
	public void testFindByIdFound() {
		addTestProductToDatabase("1", "test1");
		addTestProductToDatabase("2", "test2");
		assertThat(productRepository.findById("2"))
			.isEqualTo(new Product("2", "test2"));
	}

	@Test
	public void testDeleteWhenProductIsInDatabase() {
		addTestProductToDatabase("1", "test1");
		assertThat(productRepository.delete(productRepository.getNewClientSession(), "1"))
						.isNotNull();
		assertThat(productCollection.find()).isEmpty();
	}
	
	@Test
	public void testDeleteWhenProductIsNotInDatabase() {
		assertThat(productRepository.delete(productRepository.getNewClientSession(), "1"))
						.isNull();
	}
	
	@Test(expected = MongoCommandException.class)
	public void testDeleteWhenExceptionIsThrown() {
		@SuppressWarnings("unchecked")
		MongoCollection<Document> mockCollection = mock(MongoCollection.class);
		ClientSession session = productRepository.getNewClientSession();
		doThrow(MongoCommandException.class).when(mockCollection).findOneAndDelete(session, Filters.eq("id", "1"));
		productRepository.setProductCollection(mockCollection);
		productRepository.delete(session, "1");

	}

	private void addTestProductToDatabase(String id, String name) {
		productCollection.insertOne(
				new Document()
					.append("id", id)
					.append("name", name));
	}
}
