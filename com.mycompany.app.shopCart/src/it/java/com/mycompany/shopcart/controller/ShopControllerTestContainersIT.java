package com.mycompany.shopcart.controller;

import static org.mockito.Mockito.*;
import static java.util.Arrays.asList;

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
import com.mycompany.shopcart.repository.ProductRepository;
import com.mycompany.shopcart.repository.mongo.ProductMongoRepository;
import com.mycompany.shopcart.view.ShopView;

public class ShopControllerTestContainersIT {
	
	@ClassRule
	public static final MongoDBContainer mongo =
			new MongoDBContainer("mongo:4.4.3");

	@Mock
	private ShopView shopView;

	private ProductRepository productRepository;

	private ShopController shopController;

	private MongoClient client;

	private AutoCloseable closeable;

	private MongoCollection<Document> productCollection;

	private static final String SHOP_DB_NAME = "shop";
	private static final String PRODUCT_COLLECTION_NAME = "product";

	@Before
	public void setup() {
		closeable = MockitoAnnotations.openMocks(this);
		client = new MongoClient(
				new ServerAddress(
					mongo.getContainerIpAddress(),
					mongo.getMappedPort(27017)));
		productRepository = 
				new ProductMongoRepository(client, 
						SHOP_DB_NAME, PRODUCT_COLLECTION_NAME);
		MongoDatabase database = client.getDatabase(SHOP_DB_NAME);
		database.drop();
		shopController = new ShopController(shopView, productRepository);
		productCollection = database.getCollection(PRODUCT_COLLECTION_NAME);
	}

	@After
	public void tearDown() throws Exception {
		client.close();
		closeable.close();
	}

	@Test
	public void testAllProducts() {
		Product product = new Product("1", "test");
		addTestProductToDatabase(product.getId(), product.getName());
		shopController.allProducts();
		verify(shopView).showAllProducts(asList(product));
	}

	@Test
	public void testBuyProduct() {
		Product product = new Product("1", "test");
		addTestProductToDatabase(product.getId(), product.getName());
		shopController.buyProduct(product);
		verify(shopView).addProductToCart(product);

	}

	@Test
	public void testRemoveProduct() {
		Product product = new Product("1", "test");
		addTestProductToDatabase(product.getId(), product.getName());
		shopController.removeProduct(product);
		verify(shopView).removeProductFromCart(product);

	}

	@Test
	public void testCheckoutProducts() {
		Product productToCheckout = new Product("1", "test");
		addTestProductToDatabase(productToCheckout.getId(), productToCheckout.getName());
		shopController.checkoutProduct(productToCheckout);
		verify(shopView).checkoutProduct(productToCheckout);

	}

	private void addTestProductToDatabase(String id, String name) {
		productCollection.insertOne(new Document().append("id", id).append("name", name));
	}

}
