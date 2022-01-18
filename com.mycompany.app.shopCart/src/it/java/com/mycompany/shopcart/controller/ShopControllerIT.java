package com.mycompany.shopcart.controller;

import static org.mockito.Mockito.*;
import static java.util.Arrays.asList;

import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mycompany.shopcart.model.Product;
import com.mycompany.shopcart.repository.ProductRepository;
import com.mycompany.shopcart.repository.mongo.ProductMongoRepository;
import com.mycompany.shopcart.view.ShopView;

public class ShopControllerIT {

	@Mock
	private ShopView productView;

	private ProductRepository productRepository;
	
	private ShopController shopController;
	
	private MongoClient mongoClient;
	
	private AutoCloseable closeable;

	private MongoCollection<Document> productCollection;

	private static final String SHOP_DB_NAME = "shop";
	private static final String PRODUCT_COLLECTION_NAME = "product";
	
	private static int mongoPort =
			Integer.parseInt(System.getProperty("mongo.port", "27017"));

	@Before
	public void setup() {
		closeable = MockitoAnnotations.openMocks(this);
		mongoClient = new MongoClient("localhost", mongoPort);
		productRepository = new ProductMongoRepository(mongoClient,
				SHOP_DB_NAME, PRODUCT_COLLECTION_NAME);
		MongoDatabase database = mongoClient.getDatabase(SHOP_DB_NAME);
		database.drop();
		shopController = new ShopController(productView, productRepository);
		productCollection = database.getCollection(PRODUCT_COLLECTION_NAME);
	}
	
	@After
	public void tearDown() throws Exception {
		mongoClient.close();
		closeable.close();
	}
	
	@Test
	public void testAllProducts() {
		Product product = new Product("1", "test");
		addTestProductToDatabase(product.getId(), product.getName());
		shopController.allProducts();
		verify(productView)
			.showAllProducts(asList(product));
	}
	
	@Test
	public void testBuyProduct() {
		Product product = new Product("1", "test");
		addTestProductToDatabase(product.getId(), product.getName());
		shopController.buyProduct(product);
		verify(productView).addProductToCart(product);

	}
	
	@Test
	public void testRemoveProduct() {
		Product product = new Product("1", "test");
		addTestProductToDatabase(product.getId(), product.getName());
		shopController.removeProduct(product);
		verify(productView).removeProductFromCart(product);

	}
	
	@Test
	public void testCheckoutProducts() {
		Product productToCheckout = new Product("1", "test");
		addTestProductToDatabase(productToCheckout.getId(), productToCheckout.getName());
		shopController.checkoutProduct(productToCheckout);
		verify(productView).checkoutProduct(productToCheckout);

	}
	
	private void addTestProductToDatabase(String id, String name) {
		productCollection.insertOne(
				new Document()
					.append("id", id)
					.append("name", name));
	}

}
