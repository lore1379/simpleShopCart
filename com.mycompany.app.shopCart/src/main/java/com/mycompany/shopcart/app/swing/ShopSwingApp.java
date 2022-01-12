package com.mycompany.shopcart.app.swing;

import java.awt.EventQueue;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mycompany.shopcart.controller.ShopController;
import com.mycompany.shopcart.repository.mongo.ProductMongoRepository;
import com.mycompany.shopcart.view.swing.ShopSwingView;

public class ShopSwingApp {
	
	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			try {
				String mongoHost = "localhost";
				int mongoPort = 27017;
				if (args.length > 0)
					mongoHost = args[0];
				if (args.length > 1)
					mongoPort = Integer.parseInt(args[1]);
				ProductMongoRepository productRepository =
						new ProductMongoRepository(
								new MongoClient(new ServerAddress(mongoHost, mongoPort)),
										"shop", "product");
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
	}
}
