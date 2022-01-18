package com.mycompany.shopcart.repository.mongo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoCommandException;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mycompany.shopcart.model.Product;

public class ProductMongoRepositoryInTransaction {
	
	private static final Logger LOGGER = LogManager.getLogger(ProductMongoRepositoryInTransaction.class);
	
	private MongoCollection<Document> productCollection;

	public ProductMongoRepositoryInTransaction(MongoClient mongoClient,
			String shopDbName, String productCollectionName) {
		productCollection = mongoClient
				.getDatabase(shopDbName)
				.getCollection(productCollectionName);
	}

	public Product findById(String id) {
		Document d = productCollection.find(Filters.eq("id", id)).first();
		if (d != null)
			return fromDocumentToProduct(d);
		return null;
	}

	public void delete(ClientSession session, String id) {
		try {
            productCollection.deleteOne(session, Filters.eq("id", id));
        } catch (MongoCommandException e) {
            LOGGER.error("Exception!", e);
            throw e;
        }
	}
	
	private Product fromDocumentToProduct(Document d) {
		return new Product(""+d.get("id"), ""+d.get("name"));
	}

}
