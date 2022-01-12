package com.mycompany.shopcart.MVC;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.bson.Document;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.testcontainers.containers.MongoDBContainer;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mycompany.shopcart.controller.ShopController;
import com.mycompany.shopcart.model.Product;
import com.mycompany.shopcart.repository.mongo.ProductMongoRepository;
import com.mycompany.shopcart.view.swing.ShopSwingView;

@RunWith(GUITestRunner.class)
public class ModelViewControllerIT extends AssertJSwingJUnitTestCase {
	
	@ClassRule
	public static final MongoDBContainer mongo = new MongoDBContainer("mongo:4.4.3");
	
	private MongoClient mongoClient;
	private ProductMongoRepository productRepository;
	private FrameFixture window;
	private ShopSwingView shopSwingView;
	private ShopController shopController;
	private MongoCollection<Document> productCollection;
	
	private static final String SHOP_DB_NAME = "shop";
	private static final String PRODUCT_COLLECTION_NAME = "product";
	
	@Override
	protected void onSetUp() {
		mongoClient = new MongoClient(
				new ServerAddress(
						mongo.getContainerIpAddress(),
						mongo.getFirstMappedPort()));
		productRepository = new ProductMongoRepository(mongoClient, SHOP_DB_NAME, PRODUCT_COLLECTION_NAME);
		MongoDatabase database = mongoClient.getDatabase(SHOP_DB_NAME);
		database.drop();
		productCollection = database.getCollection(PRODUCT_COLLECTION_NAME);
		window = new FrameFixture(robot(), GuiActionRunner.execute(() -> {
			shopSwingView = new ShopSwingView();
			shopController = new ShopController(shopSwingView, productRepository);
			shopSwingView.setShopController(shopController);
			return shopSwingView;
		}));
		window.show();
	}
	
	@Override
	protected void onTearDown() {
		mongoClient.close();
	}
	
	@Test
	public void testCheckout() {
		Product product = new Product("99", "availableProduct");
		addTestProductToDatabase(product.getId(), product.getName());
		GuiActionRunner.execute(
			() -> shopController.allProducts());
		window.list("productList").selectItem(0);
		window.button(JButtonMatcher.withText("Buy Selected")).click();
		window.list("cartList").selectItem(0);
		window.button(JButtonMatcher.withText("Checkout")).click();
		assertThat(productRepository.findById("99")).isNull();
	}
	
	private void addTestProductToDatabase(String id, String name) {
		productCollection.insertOne(
				new Document()
					.append("id", id)
					.append("name", name));
	}

}
