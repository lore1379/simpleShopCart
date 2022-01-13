package com.mycompany.shopcart.view.swing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.swing.launcher.ApplicationLauncher.application;

import javax.swing.JFrame;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.GenericTypeMatcher;
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

@RunWith(GUITestRunner.class)
public class ShopSwingAppE2E extends AssertJSwingJUnitTestCase {
	
	@ClassRule
	public static final MongoDBContainer mongo =
		new MongoDBContainer("mongo:4.4.3");

	private static final String DB_NAME = "test-db";
	private static final String COLLECTION_NAME = "test-collection";
	
	private MongoClient mongoClient;

	private FrameFixture window;

	@Override
	protected void onSetUp() {
		String containerIpAddress = mongo.getContainerIpAddress();
		Integer mappedPort = mongo.getFirstMappedPort();
		mongoClient = new MongoClient(containerIpAddress, mappedPort);
		mongoClient.getDatabase(DB_NAME).drop();
		addTestProductToDatabase("1", "first product");
		addTestProductToDatabase("2", "second product");
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
	
	@Test @GUITest
	public void testOnStartAllDatabaseElementsAreShown() {
		assertThat(window.list("productList").contents())
			.anySatisfy(e -> assertThat(e).contains("1", "first product"))
			.anySatisfy(e -> assertThat(e).contains("2", "second product"));
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
}
