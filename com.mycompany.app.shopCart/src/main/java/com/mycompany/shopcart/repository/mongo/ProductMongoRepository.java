package com.mycompany.shopcart.repository.mongo;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mycompany.shopcart.model.Product;
import com.mycompany.shopcart.repository.ProductRepository;

public class ProductMongoRepository implements ProductRepository {
	
	private MongoCollection<Document> productCollection;

	public ProductMongoRepository(MongoClient mongoClient,
			String shopDbName, String productCollectionName) {
		productCollection = mongoClient
				.getDatabase(shopDbName)
				.getCollection(productCollectionName);
	}

	@Override
	public List<Product> findAll() {
		return StreamSupport.
				stream(productCollection.find().spliterator(), false)
				.map(this::fromDocumentToProduct)
				.collect(Collectors.toList());
	}

	@Override
	public Product findById(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete(String id) {
		// TODO Auto-generated method stub

	}
	
	private Product fromDocumentToProduct(Document d) {
		return new Product(""+d.get("id"), ""+d.get("name"));
	}

}
