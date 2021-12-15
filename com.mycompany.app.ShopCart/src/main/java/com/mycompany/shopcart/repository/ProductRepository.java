package com.mycompany.shopcart.repository;

import java.util.List;

import com.mycompany.shopcart.model.Product;

public interface ProductRepository {
	public List<Product> findAll();

	public Product findById(String id);

	public void save(Product product);

	public void delete(String id);
}