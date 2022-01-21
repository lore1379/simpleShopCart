package com.mycompany.shopcart.controller;

import org.bson.Document;

import com.mongodb.MongoCommandException;
import com.mongodb.TransactionOptions;
import com.mongodb.WriteConcern;
import com.mongodb.client.ClientSession;
import com.mycompany.shopcart.model.Product;
import com.mycompany.shopcart.repository.ProductRepository;
import com.mycompany.shopcart.view.ShopView;

public class ShopController {
	
	private ShopView shopView;
	private ProductRepository productRepository;
	
	private static final String ERROR_MESSAGE = "The product you are trying to buy is no longer available";
	
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
			shopView.showError(ERROR_MESSAGE, productToBuy);
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
		session.startTransaction(TransactionOptions.builder().writeConcern(WriteConcern.MAJORITY).build());
		try {
			Document availableProduct = productRepository.delete(session, productInCart.getId());
			if (availableProduct == null) {
				shopView.showErrorProductNotFound(ERROR_MESSAGE,
						productInCart);
				session.commitTransaction();
			} else {
				shopView.checkoutProduct(productInCart);
				session.commitTransaction();			
			}
		} catch (MongoCommandException e) {
			session.abortTransaction();
			shopView.showErrorProductNotFound(ERROR_MESSAGE,
					productInCart);
		} finally {
			session.close();
		}
	}

}
