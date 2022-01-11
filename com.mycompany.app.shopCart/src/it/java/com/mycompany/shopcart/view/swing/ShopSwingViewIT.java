package com.mycompany.shopcart.view.swing;

import java.net.InetSocketAddress;

import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
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
		for (Product product : productRepository.findAll()) {
			productRepository.delete(product.getId());
		}
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
}
