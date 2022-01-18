package com.mycompany.shopcart.controller;

import com.mycompany.shopcart.model.Product;
import com.mycompany.shopcart.repository.ProductRepository;
import com.mycompany.shopcart.view.ShopView;

public class ShopController {

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
		final Product availableProduct = productRepository.findById(productInCart.getId());
		if (availableProduct == null) {
			shopView.showErrorProductNotFound("The product you are trying to buy is no longer available",
					productInCart);
			return;
		}
		productRepository.delete(availableProduct.getId());
		shopView.checkoutProduct(availableProduct);
	}

}
