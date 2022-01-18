package com.mycompany.shopcart.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mongodb.MongoClient;
import com.mongodb.MongoCommandException;
import com.mongodb.MongoWriteException;
import com.mongodb.TransactionOptions;
import com.mongodb.WriteConcern;
import com.mongodb.client.ClientSession;
import com.mycompany.shopcart.model.Product;
import com.mycompany.shopcart.repository.mongo.ProductMongoRepositoryInTransaction;
import com.mycompany.shopcart.view.ShopView;

public class ShopControllerInTransaction {

	private static final Logger LOGGER = LogManager.getLogger(ShopControllerInTransaction.class);

	private ShopView shopView;
	private ProductMongoRepositoryInTransaction productRepository;
	private MongoClient mongoClient;

	public ShopControllerInTransaction(MongoClient mongoClient, ShopView productView,
			ProductMongoRepositoryInTransaction productRepository) {
		this.mongoClient = mongoClient;
		this.shopView = productView;
		this.productRepository = productRepository;
	}

	public void checkoutProduct(Product productInCart) {
		ClientSession session = mongoClient.startSession();
		try {
			session.startTransaction(TransactionOptions.builder().writeConcern(WriteConcern.MAJORITY).build());
			final Product availableProduct = productRepository.findById(productInCart.getId());
			if (availableProduct == null) {
				shopView.showErrorProductNotFound("The product you are trying to buy is no longer available",
						productInCart);
				return;
			}
			productRepository.delete(session, availableProduct.getId());
			shopView.checkoutProduct(availableProduct);
			sleep();
			session.commitTransaction();
		} catch (MongoCommandException | MongoWriteException e) {
			session.abortTransaction();
			shopView.showErrorProductNotFound("The product you are trying to buy is no longer available",
					productInCart);
		} finally {
			session.close();
		}
	}

	private void sleep() {
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			LOGGER.error("Exception!", e);
			Thread.currentThread().interrupt();
		}
	}

}
