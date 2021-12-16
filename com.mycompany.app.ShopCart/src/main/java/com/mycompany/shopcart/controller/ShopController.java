package com.mycompany.shopcart.controller;

import com.mycompany.shopcart.model.Product;
import com.mycompany.shopcart.repository.ProductRepository;
import com.mycompany.shopcart.view.ProductView;

public class ShopController {

	private ProductView productView;
	private ProductRepository productRepository;
	
	public ShopController(ProductView productView, ProductRepository productRepository) {
		this.productView = productView;
		this.productRepository = productRepository;
	}

	public void allProducts() {
		productView.showAllProducts(productRepository.findAll());
	}

	public void buyProduct(Product productToBuy) {
		final Product availableProduct = productRepository.findById(productToBuy.getId());
		if (availableProduct == null) {
			productView.showError("The product you are trying to buy is no longer available");
			return;
		}
		productView.addProductToCart(productRepository.findById(productToBuy.getId()));		
	}

	public void removeProduct(Product productToRemove) {
		productView.removeProductFromCart(productToRemove);
	}

	public void checkoutProducts() {
		// TODO Auto-generated method stub
		
	}

}
