package com.mycompany.shopcart.repository;

import java.util.List;

import org.bson.Document;

import com.mongodb.client.ClientSession;
import com.mycompany.shopcart.model.Product;

public interface ProductRepository {

	public List<Product> findAll();

	public Product findById(String id);

	public Document delete(ClientSession session, String id);

	public ClientSession getNewClientSession();

}
