package com.mycompany.shopcart.view.swing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.swing.launcher.ApplicationLauncher.application;
import static org.awaitility.Awaitility.*;

import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import javax.swing.JFrame;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.bson.Document;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.testcontainers.containers.MongoDBContainer;

import com.mongodb.MongoClient;
import com.mongodb.client.model.Filters;

@RunWith(GUITestRunner.class)
public class ShopSwingAppE2E extends AssertJSwingJUnitTestCase {

	@ClassRule
	public static final MongoDBContainer mongo = new MongoDBContainer("mongo:4.4.3");

	private static final String DB_NAME = "test-db";
	private static final String COLLECTION_NAME = "test-collection";

	private static final String PRODUCT_FIXTURE_1_ID = "1";
	private static final String PRODUCT_FIXTURE_1_NAME = "first product";
	private static final String PRODUCT_FIXTURE_2_ID = "2";
	private static final String PRODUCT_FIXTURE_2_NAME = "second product";

	private MongoClient mongoClient;

	private FrameFixture window;

	@Override
	protected void onSetUp() {
		String containerIpAddress = mongo.getContainerIpAddress();
		Integer mappedPort = mongo.getFirstMappedPort();
		mongoClient = new MongoClient(containerIpAddress, mappedPort);
		mongoClient.getDatabase(DB_NAME).drop();
		addTestProductToDatabase(PRODUCT_FIXTURE_1_ID, PRODUCT_FIXTURE_1_NAME);
		addTestProductToDatabase(PRODUCT_FIXTURE_2_ID, PRODUCT_FIXTURE_2_NAME);
		application("com.mycompany.shopcart.app.swing.ShopSwingApp")
			.withArgs(
				"--mongo-host=" + containerIpAddress,
				"--mongo-port=" + mappedPort.toString(), 
				"--db-name=" + DB_NAME, 
				"--db-collection=" + COLLECTION_NAME
			)
			.start();
		window = WindowFinder.findFrame(new GenericTypeMatcher<JFrame>(JFrame.class) {
			@Override
			protected boolean isMatching(JFrame frame) {
				return "Shop View".equals(frame.getTitle()) && frame.isShowing();
			}
		}).using(robot());
	}

	@Override
	protected void onTearDown() {
		mongoClient.close();
	}

	@Test
	@GUITest
	public void testOnStartAllDatabaseElementsAreShown() {
		assertThat(window.list("productList").contents())
				.anySatisfy(e -> assertThat(e).contains(PRODUCT_FIXTURE_1_ID, PRODUCT_FIXTURE_1_NAME))
				.anySatisfy(e -> assertThat(e).contains(PRODUCT_FIXTURE_2_ID, PRODUCT_FIXTURE_2_NAME));
	}

	@Test
	@GUITest
	public void testBuyButtonSuccess() {
		window.list("productList").selectItem(Pattern.compile(".*" + PRODUCT_FIXTURE_1_NAME + ".*"));
		window.button(JButtonMatcher.withText("Buy Selected")).click();
		assertThat(window.list("cartList").contents())
			.anySatisfy(e -> assertThat(e).contains(PRODUCT_FIXTURE_1_NAME));
	}

	@Test
	@GUITest
	public void testBuyButtonError() {
		window.list("productList").selectItem(Pattern.compile(".*" + PRODUCT_FIXTURE_1_NAME + ".*"));
		removeTestProductFromDatabase(PRODUCT_FIXTURE_1_ID);
		window.button(JButtonMatcher.withText("Buy Selected")).click();
		assertThat(window.label("errorMessageLabel").text())
			.contains(PRODUCT_FIXTURE_1_NAME);
	}

	@Test
	@GUITest
	public void testRemoveButtonAfterBuyButtonSuccess() {
		window.list("productList").selectItem(Pattern.compile(".*" + PRODUCT_FIXTURE_1_NAME + ".*"));
		window.button(JButtonMatcher.withText("Buy Selected")).click();
		assertThat(window.list("cartList").contents()).anySatisfy(e -> assertThat(e).contains(PRODUCT_FIXTURE_1_NAME));
		window.list("cartList").selectItem(Pattern.compile(".*" + PRODUCT_FIXTURE_1_NAME + ".*"));
		window.button(JButtonMatcher.withText("Remove Selected")).click();
		assertThat(window.list("productList").contents())
				.anySatisfy(e -> assertThat(e).contains(PRODUCT_FIXTURE_1_NAME));
	}

	@Test
	@GUITest
	public void testCheckoutButtonSuccessAfterBuyButtonSuccess() {
		window.list("productList").selectItem(Pattern.compile(".*" + PRODUCT_FIXTURE_1_NAME + ".*"));
		window.button(JButtonMatcher.withText("Buy Selected")).click();
		assertThat(window.list("cartList").contents()).anySatisfy(e -> assertThat(e).contains(PRODUCT_FIXTURE_1_NAME));
		window.list("cartList").selectItem(Pattern.compile(".*" + PRODUCT_FIXTURE_1_NAME + ".*"));
		window.button(JButtonMatcher.withText("Checkout")).click();
		await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> assertThat(window.list("cartList").contents())
				.noneMatch(e -> e.contains(PRODUCT_FIXTURE_1_NAME)));
	}

	@Test
	@GUITest
	public void testCheckoutButtonErrorAfterBuyButtonSuccess() {
		window.list("productList").selectItem(Pattern.compile(".*" + PRODUCT_FIXTURE_1_NAME + ".*"));
		window.button(JButtonMatcher.withText("Buy Selected")).click();
		assertThat(window.list("cartList").contents()).anySatisfy(e -> assertThat(e).contains(PRODUCT_FIXTURE_1_NAME));
		window.list("cartList").selectItem(Pattern.compile(".*" + PRODUCT_FIXTURE_1_NAME + ".*"));
		removeTestProductFromDatabase(PRODUCT_FIXTURE_1_ID);
		window.button(JButtonMatcher.withText("Checkout")).click();
		await().atMost(5, TimeUnit.SECONDS).untilAsserted(
				() -> assertThat(window.label("errorMessageLabel").text()).contains(PRODUCT_FIXTURE_1_NAME));
	}

	private void addTestProductToDatabase(String id, String name) {
		mongoClient
			.getDatabase(DB_NAME)
			.getCollection(COLLECTION_NAME)
			.insertOne(
					new Document()
						.append("id", id)
						.append("name", name));
	}

	private void removeTestProductFromDatabase(String id) {
		mongoClient
			.getDatabase(DB_NAME)
			.getCollection(COLLECTION_NAME)
			.deleteOne(Filters.eq("id", id));
	}
}
