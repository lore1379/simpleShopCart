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
		productView.addProductToCart(productRepository.findById(productToBuy.getId()));		
	}

	public void removeProduct(Product productToRemove) {
		productView.removeProductFromCart(productRepository.findById(productToRemove.getId()));
	}

}
