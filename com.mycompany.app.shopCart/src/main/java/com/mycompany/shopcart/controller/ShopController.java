package com.mycompany.shopcart.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mongodb.MongoCommandException;
import com.mongodb.MongoWriteException;
import com.mongodb.TransactionOptions;
import com.mongodb.WriteConcern;
import com.mongodb.client.ClientSession;
import com.mycompany.shopcart.model.Product;
import com.mycompany.shopcart.repository.ProductRepository;
import com.mycompany.shopcart.view.ShopView;

public class ShopController {
	
	private static final Logger LOGGER = LogManager.getLogger(ShopController.class);

	private ShopView shopView;
	private ProductRepository productRepository;

	public ShopController(ShopView productView, ProductRepository productRepository) {
		this.shopView = productView;
		this.productRepository = productRepository;
	}

	public void allProducts() {
		shopView.showAllProducts(productRepository.findAll());
	}

	public void buyProduct(Product productToBuy) {
		final Product availableProduct = productRepository.findById(productToBuy.getId());
		if (availableProduct == null) {
			shopView.showError("The product you are trying to buy is no longer available", productToBuy);
			shopView.removeProductFromShop(productToBuy);
			return;
		}
		shopView.addProductToCart(productRepository.findById(productToBuy.getId()));
	}

	public void removeProduct(Product productToRemove) {
		shopView.removeProductFromCart(productToRemove);
	}

	public void checkoutProduct(Product productInCart) {
		ClientSession session = productRepository.getNewClientSession();
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
