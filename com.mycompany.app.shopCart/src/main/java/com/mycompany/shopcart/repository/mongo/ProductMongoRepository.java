package com.mycompany.shopcart.repository.mongo;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoCommandException;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mycompany.shopcart.model.Product;
import com.mycompany.shopcart.repository.ProductRepository;

public class ProductMongoRepository implements ProductRepository {
	
	private static final Logger LOGGER = LogManager.getLogger(ProductMongoRepository.class);

	private MongoClient mongoClient;
	private MongoCollection<Document> productCollection;

	public ProductMongoRepository(MongoClient mongoClient, 
			String shopDbName, String productCollectionName) {
				productCollection = mongoClient
						.getDatabase(shopDbName)
						.getCollection(productCollectionName);
				this.mongoClient = mongoClient;
	}
	

	@Override
	public List<Product> findAll() {
		return StreamSupport
				.stream(productCollection.find().spliterator(), false)
				.map(this::fromDocumentToProduct)
				.collect(Collectors.toList());
	}

	@Override
	public Product findById(String id) {
		Document d = productCollection.find(Filters.eq("id", id)).first();
		if (d != null)
			return fromDocumentToProduct(d);
		return null;
	}

	@Override
	public Document delete(ClientSession session, String id) {
		try {
			return productCollection.findOneAndDelete(session, Filters.eq("id", id)); 
		} catch (MongoCommandException e) {
			LOGGER.error("Exception!", e);
			throw e;
		}
	}
	
	@Override
	public ClientSession getNewClientSession() {
		return mongoClient.startSession();
	}
	
	private Product fromDocumentToProduct(Document d) {
		return new Product("" + d.get("id"), "" + d.get("name"));
	}

}
