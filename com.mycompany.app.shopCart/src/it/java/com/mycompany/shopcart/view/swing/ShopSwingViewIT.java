package com.mycompany.shopcart.view.swing;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.InetSocketAddress;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.bson.Document;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mycompany.shopcart.controller.ShopController;
import com.mycompany.shopcart.model.Product;
import com.mycompany.shopcart.repository.mongo.ProductMongoRepository;

import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;

public class ShopSwingViewIT extends AssertJSwingJUnitTestCase{
	
	private static MongoServer server;
	private static InetSocketAddress serverAddress;
	
	private MongoClient mongoClient;
	private ProductMongoRepository productRepository;
	private ShopSwingView shopSwingView;
	private ShopController shopController;
	private FrameFixture window;
	private MongoCollection<Document> productCollection;

	
	private static final String SHOP_DB_NAME = "shop";
	private static final String PRODUCT_COLLECTION_NAME = "product";

	@BeforeClass
	public static void setupServer() {
		server = new MongoServer(new MemoryBackend());
		serverAddress = server.bind();
	}
	
	@AfterClass
	public static void shutdownServer() {
		server.shutdown();
	}
	
	@Override
	protected void onSetUp() {
		mongoClient = new MongoClient(new ServerAddress(serverAddress));
		productRepository = new ProductMongoRepository(mongoClient, SHOP_DB_NAME, PRODUCT_COLLECTION_NAME);
		MongoDatabase database = mongoClient.getDatabase(SHOP_DB_NAME);
		database.drop();
		productCollection = database.getCollection(PRODUCT_COLLECTION_NAME);
		GuiActionRunner.execute(() -> {
			shopSwingView = new ShopSwingView();
			shopController = new ShopController(shopSwingView, productRepository);
			shopSwingView.setShopController(shopController);
			return shopSwingView;
		});
		window = new FrameFixture(robot(), shopSwingView);
		window.show();
	}	
	
	@Override
	protected void onTearDown() {
		mongoClient.close();
	}
	
	@Test @GUITest
	public void testAllproducts() {
		Product product1 = new Product("1", "test1");
		Product product2 = new Product("2", "test2");
		addTestProductToDatabase(product1.getId(), product1.getName());
		addTestProductToDatabase(product2.getId(), product2.getName());
		GuiActionRunner.execute(
				() -> shopController.allProducts());
		assertThat(window.list("productList").contents())
			.containsExactly(product1.toString(), product2.toString());
	}
	
	private void addTestProductToDatabase(String id, String name) {
		productCollection.insertOne(
				new Document()
					.append("id", id)
					.append("name", name));
	}

}
