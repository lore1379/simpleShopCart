package com.mycompany.shopcart.view;

import java.util.List;

import com.mycompany.shopcart.model.Product;

public interface ProductView {

	void showAllProducts(List<Product> products);

	void addProductToCart(Product product);

	void removeProductFromCart(Product product);

	void showError(String message,Product product);

	void checkoutProduct(Product product);
	
	void showErrorProductNotFound(String message, Product product);

}
