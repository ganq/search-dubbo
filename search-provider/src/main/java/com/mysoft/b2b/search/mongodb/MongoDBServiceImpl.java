package com.mysoft.b2b.search.mongodb;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.ServerAddress;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * MONGODB服务
 * 
 */
public class MongoDBServiceImpl implements MongoDBService {
	private static final Log log = LogFactory.getLog(MongoDBServiceImpl.class);
	private static Datastore datastore;
	
	private String adds;
	private String databaseName;
	private String userName;
	private String password;

	private static DB db;
	private String address;
	private int port;
    private Morphia morphia;

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setAdds(String adds) {
		this.adds = adds;
	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public DB getDb() {
		return db;
	}
	
	public Datastore getDatastore() {
		return datastore;
	}
	
	/**
	 * 启动MONGO
	 */
	public void creatMongoDb() {
		log.info("mongo run start***************************************************************************");
		List<ServerAddress> addr = new ArrayList<ServerAddress>();
		try {
			String [] _adds = adds.split(",");
			for(String _path : _adds){
				addr.add(new ServerAddress(_path));
			}
			
			Mongo mongo = new Mongo(addr);
			Morphia morphia = new Morphia();
			datastore = morphia.createDatastore(mongo, databaseName);
			datastore.ensureIndexes();  // ensureIndexes() 调用可以指示数据存储创建所需且不存在的索引
            this.morphia = morphia;
        } catch (UnknownHostException e) {
            log.info("mongo's host '" + adds + "' is unknow********************************** ");
        } catch (Exception e) {
			log.info("mongo run   error*******************************************************************************");
			log.error("mongo run error ",e);
		}
		log.info("mongo run success***************************************************************************");
	}

    public Morphia getMorphia() {
        return morphia;
    }

    public void setMorphia(Morphia morphia) {
        this.morphia = morphia;
    }
}
