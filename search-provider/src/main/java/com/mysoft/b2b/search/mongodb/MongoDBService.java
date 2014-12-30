package com.mysoft.b2b.search.mongodb;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.mongodb.DB;

/**
 *
 */
public interface MongoDBService {
	Datastore getDatastore();
	DB getDb();
    Morphia getMorphia();

}
