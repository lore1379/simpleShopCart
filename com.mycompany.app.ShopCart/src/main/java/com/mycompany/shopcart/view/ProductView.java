package com.mycompany.shopcart.view;

import java.util.List;

import com.mycompany.shopcart.model.Product;

public interface ProductView {

	void showAllProducts(List<Product> products);

<<<<<<< HEAD
}
=======
	void showError(String message, Product product);

	void productAdded(Product product);

	void productRemoved(Product product);

	void showErrorProductNotFound(String message, Product product);

}
>>>>>>> branch 'main' of https://github.com/lore1379/SimpleShopCart.git