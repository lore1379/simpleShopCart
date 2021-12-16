package com.mycompany.shopcart.view;

import java.util.List;

import com.mycompany.shopcart.model.Product;

public interface ProductView {

	void showAllProducts(List<Product> products);

	void showCartProducts(Product productToBuy);

}
