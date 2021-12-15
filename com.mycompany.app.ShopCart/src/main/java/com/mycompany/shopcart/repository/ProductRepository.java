package com.mycompany.shopcart.repository;

import java.util.List;

import com.mycompany.shopcart.model.Product;

public interface ProductRepository {
<<<<<<< HEAD
	
=======
>>>>>>> branch 'main' of https://github.com/lore1379/SimpleShopCart.git
	public List<Product> findAll();

	public Product findById(String id);

	public void save(Product product);

	public void delete(String id);
}