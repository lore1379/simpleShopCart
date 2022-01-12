package com.mycompany.shopcart.app.swing;

import java.awt.EventQueue;
import java.util.concurrent.Callable;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mycompany.shopcart.controller.ShopController;
import com.mycompany.shopcart.repository.mongo.ProductMongoRepository;
import com.mycompany.shopcart.view.swing.ShopSwingView;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(mixinStandardHelpOptions = true)
public class ShopSwingApp implements Callable<Void>{
	
	@Option(names = { "--mongo-host" }, description = "MongoDB host address")
	private String mongoHost = "localhost";

	@Option(names = { "--mongo-port" }, description = "MongoDB host port")
	private int mongoPort = 27017;

	@Option(names = { "--db-name" }, description = "Database name")
	private String databaseName = "shop";

	@Option(names = { "--db-collection" }, description = "Collection name")
	private String collectionName = "product";
	
	public static void main(String[] args) {
		new CommandLine(new ShopSwingApp()).execute(args);
	}
	
	@Override
	public Void call() throws Exception {
		EventQueue.invokeLater(() -> {
			try {
				ProductMongoRepository productRepository =
						new ProductMongoRepository(
								new MongoClient(new ServerAddress(mongoHost, mongoPort)),
										databaseName, collectionName);
				ShopSwingView shopView = new ShopSwingView();
				ShopController shopController =
						new ShopController(shopView, productRepository);
				shopView.setShopController(shopController);
				shopView.setVisible(true);
				shopController.allProducts();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		return null;
	}
}
