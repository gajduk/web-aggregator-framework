package finki.ukim.agggregator.general.database;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.ObjectMapper;

import weka.classifiers.Classifier;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.util.JSON;

import finki.ukim.agggregator.general.data_model.Category;
import finki.ukim.agggregator.general.data_model.CategoryMap;
import finki.ukim.agggregator.general.data_model.ClusterTeaser;
import finki.ukim.agggregator.general.data_model.Data;
import finki.ukim.agggregator.general.data_model.DataTeaser;
import finki.ukim.agggregator.general.data_model.Feature;
import finki.ukim.agggregator.general.data_model.FeatureExtractorData;
import finki.ukim.agggregator.general.data_model.Problem;

/**
 * database API 
 * Provide a ton of methods to access the database.
 * @author Andrej Gajduk
 *
 */
public class DatabaseManager {
	
	/**
	 * the name of the database of this manager
	 */
	private String db_name = "agregator_db";
	
	/**
	 * a connection object, this should be set only once
	 */
	private Mongo mongo = null;
	
	/**
	 * the ip address the database is expecting connection on 
	 */
	private String ip_address;
	
	/**
	 * the port number the database is expecting connection on 
	 */
	private String port_number;
	
	/**
	 * an instance of the database manager which should be supplied whenever getInstance is called
	 */
	private static DatabaseManager instance;
	
	/**
	 * use this object mapper for all your data serializing and de.serializng
	 * methods are
	 * g.writeValueAsString(object)
	 * g.readValue(json_string, Class)
	 */
	ObjectMapper json_parser;
	
	/*
	 * the names of the collections will are defined here
	 * not that for the data and feature collections the real names are made by concating wiht the problem_name 
	 */
	/**
	 * name of the problems collection
	 */
	public final static String problems_collection = "problems"; 
	/**
	 * name of the data collection
	 */
	public static final String data_collection = "_data";
	/**
	 * name of the feature extractor collection , here we store all the feature extractor data
	 */
	public static final String feature_extractor_collection = "_feature_extractors";
	/**
	 * name of the classifier collection, where we store the classifiers themselves
	 */
	public static final String classifier_collection = "_classifiers";
	/**
	 * name of the category map collection, here we store all the categories
	 */
	public static final String category_collection = "_categories";
	/**
	 * the name of the field where the classifier name is written
	 */
	public static final String classifier_name_field_name = "name";
	
	/**
	 * drops the entire database,
	 * USE WITH CAUTION
	 */
	public void cleanUp() {
		mongo.dropDatabase(db_name);
	}

	/**
	 * *IMPORTANT NOTICE: this method is useless when there is too much data in the DB, it should be broken down to read the data sequentially
	 * **IMPORTANT NOTICE: somehow the previous notice is not a problem anymore readings of over 10000 data objects with ease 
	 * reads all the Data object stored in database for a given problem
	 * @param problem_name - the name of the problem we want the data for
	 * @return a list of all the Data objects in database, null if unsuccessful
	 */
	public ArrayList<Data> getDataForProblem(String problem_name) {
		ArrayList<Data> result = new ArrayList<Data>();
		try {
			//get the adequate data collection
			DBCollection coll = getCollection(problem_name+data_collection);
			//get all the data from the collection
			DBCursor cursor = coll.find();
			while ( cursor.hasNext() ) {
				DBObject curr = cursor.next();
				Data data = json_parser.readValue(curr.toString(), Data.class);
				result.add(data);
			}
			DBObject update_command = new BasicDBObject("$inc",new BasicDBObject("counter",1));
			coll.update(new BasicDBObject(), update_command,false,true);
		}
		catch ( Exception e ) {
			e.printStackTrace();
		}
		return result;
	}
	
	/*
	* IMPORTANT NOTICE: this method is useless when there is too much data in the DB, it should be broken down to read the data sequentially
	* 
	*/
	public Data getDataForProblem(String problem_name , UUID data_id ) {
		Data result = null;
		try {
			//get the adequate data collection
			DBCollection coll = getCollection(problem_name+data_collection);
			//fetch the required object by id
			DBObject data_object = coll.findOne(new BasicDBObject("_id",data_id.toString()));
			//parse the object using GSON
			result = json_parser.readValue(data_object.toString(), Data.class);
			DBObject update_command = new BasicDBObject("$inc",new BasicDBObject("counter",1));
			coll.update(new BasicDBObject("_id",data_id.toString()),update_command,false,true);
		
		}
		catch ( Exception e ) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * gets the Instance i.e. the features from database for a given data object, that were obtained using the given feature extractor,
	 * the problem is defined by the problem name
	 * @param data_id - id of the data object
	 * @param feature_extractor_name - the name f the feature extractor
	 * @param problem_name - the name of the problem
	 * @return the Feature read from the DB that corresponds to the data, problem_name and the feature_extractor_name, null if unsuccessful
	 */
	public Feature getFeatureForDataForProblem ( UUID data_id , String feature_extractor_name , String problem_name ) {
		Feature result = null;
		try {
			//create the query object
			BasicDBObject query = new BasicDBObject("name",feature_extractor_name);
			query.put("features._id", data_id);
			//get the adequate feature extractor collection
			DBCollection coll = getCollection(problem_name+feature_extractor_collection);
			// finds the adequate feature extractor in the collection
			DBObject feature_object = coll.findOne(query);
			result = json_parser.readValue(feature_object.toString(), Feature.class);
		}
		catch ( Exception e ) {
			e.printStackTrace();
		}
		return result;
	}
		
	/**
	 * **IMPORTANT NOTICE: this method should be broken down to enable sequential reading from the DB of the instances and the attributes since
	 * get the featureExtractorData by name for given problem from the database
	 * @param feature_extractor_name - the name of the feature_extractor_data we want to read from database
	 * @param problem_name - the name of the problem want the feature extractor from
	 * @return the FeatureExtractorData read from the DB that corresponds to the problem_name and the feature_extractor_name, null if unsuccessful
	 */
	public FeatureExtractorData getFeatureExtractorDataForProblem ( String feature_extractor_name , String problem_name ) {
		FeatureExtractorData result = null;
		try {
			//create the query object
			BasicDBObject query = new BasicDBObject("name", feature_extractor_name);
			//get the adequate feature extractor collection
			DBCollection coll = getCollection(problem_name+feature_extractor_collection);
			// find the adequate feature extractor in the collection
			DBObject feature_extractor_data = coll.findOne(query);
			//parse it into the proper java object
			result = json_parser.readValue(feature_extractor_data.toString(), FeatureExtractorData.class);
			//Gson g = new Gson();
			//remove the dataset and store it later one by one
			/*
			FastVector attributes_info = new FastVector();
			DBObject dataset_data = (DBObject) data.removeField("dataset");
			Gson g = (new GsonBuilder().addDeserializationExclusionStrategy(new SpecificClassExclusionStrategy(ProtectedProperties.class))).create();
			
			int counter = 0;
			while ( dataset_data.containsField(Integer.toString(counter)) ) {
				attributes_info.addElement(g.fromJson(dataset_data.get(Integer.toString(counter)).toString(),weka.core.Attribute.class));
				++counter;
			}
			List<Feature> features = new ArrayList<Feature>();
			DBObject features_data = (DBObject) data.removeField("features");
			Gson gs = new GsonBuilder().addSerializationExclusionStrategy(new SpecificClassExclusionStrategy(Instance.class)).create();
			counter = 1;
			while ( features_data.containsField(Integer.toString(counter)) ) {
				DBObject feature_data = (DBObject)features_data.get(Integer.toString(counter));
				Feature f = gs.fromJson(feature_data.toString(),Feature.class);
				Instance instance = (Instance) gs.fromJson(feature_data.get("instance").toString(),Class.forName(Feature.getClassNameForType((Integer)feature_data.get(type_field_name))));
				f.setInstance(instance);
				features.add(f);
				++counter;
			}
			result = g.fromJson(data.toString(), FeatureExtractorData.class);
			result.setFeatures(features); result.setDataset(new Instances("Dataset", attributes_info, attributes_info.size()));
			*/
				/*
			Instances dataset = new Instances();
			feature_extractor_data.setDataset(new Instances("Rel", new FastVector(0), 0));
			//do the same with the features
			List<Feature> features = feature_extractor_data.getFeatures();
			feature_extractor_data.setFeatures(new LinkedList<Feature>());
			DBObject data_to_insert = (DBObject) JSON.parse(g.toJson(feature_extractor_data));
			
			BasicDBObject dataset_to_insert = new BasicDBObject();
			for ( int i = 0 ; i < dataset.numAttributes() ; ++i ) {
//				System.out.println("Progress in attributes: "+i+" out of total: "+dataset.numAttributes());
				Attribute attribute_data = dataset.attribute(i);
				DBObject attribute_data_to_insert = (DBObject) JSON.parse(g.toJson(attribute_data));
				dataset_to_insert.put(Integer.toString(i),attribute_data_to_insert);
			}
			data_to_insert.put("dataset",dataset_to_insert);
			BasicDBObject features_to_insert = new BasicDBObject();
			int counter = 0;
			for ( Feature feature : features ) {
//				System.out.println("Progress in features: "+(counter++)+" out of total: "+features.size());
				DBObject feature_data_to_insert = (DBObject) JSON.parse(g.toJson(feature));
				features_to_insert.put(Integer.toString(counter),feature_data_to_insert);
			}
			data_to_insert.put("features",features_to_insert);
			*/
			
		}
		catch ( Exception e ) {
			e.printStackTrace();
		}
		return result;
	}

	/** 
	 * adds a group of features to the feature extractor in the database
	 * @param features_to_add - the feature we wish to link to the feature extractor for the given problem
	 * @param feature_extractor_id - the id of the feature extractor we wish to link the feature to
	 * @param problem_name - the name of the problem that holds the given feature extractor
	 * @return true if successful
	 */
	public boolean addFeaturesToFeatureExtractorDataForProblem( List<Feature> features_to_add , UUID feature_extractor_id , String problem_name ) {
		try {
			//create the new DBObject to insert
			DBObject data_to_insert = (DBObject) JSON.parse(json_parser.writeValueAsString(features_to_add));
			//create the query to locate the appropriate feature_extractor
			BasicDBObject update_query = new BasicDBObject("_id", feature_extractor_id.toString());
			//create the update query to add the feature in the list of extracted features
			BasicDBObject update_command = new BasicDBObject("$pushAll", new BasicDBObject("features.1", data_to_insert.get("1")));
			//get the adequate feature extractor collection
			DBCollection coll = getCollection(problem_name+feature_extractor_collection);
			//do the update
			coll.update(update_query, update_command);

		}
		catch ( Exception e ) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/** 
	 * adds a single feature to the feature extractor in the database
	 * @param feature_to_add - the feature we wish to link to the feature extractor for the given problem
	 * @param feature_extractor_id - the id of the feature extractor we wish to link the feature to
	 * @param problem_name - the name of the problem that holds the given feature extractor
	 * @return true if successful
	 */
	public boolean addFeatureToFeatureExtractorDataForProblem( Feature feature_to_add , UUID feature_extractor_id , String problem_name ) {
		try {
			//create the new DBObject to insert
			DBObject data_to_insert = (DBObject) JSON.parse(json_parser.writeValueAsString(feature_to_add));
			//create the query to locate the appropriate feature_extractor
			BasicDBObject update_query = new BasicDBObject("_id", feature_extractor_id.toString());
			//create the update query to add the feature in the list of extracted features
			BasicDBObject update_command = new BasicDBObject("$push", new BasicDBObject("features", data_to_insert));
			//get the adequate feature extractor collection
			DBCollection coll = getCollection(problem_name+feature_extractor_collection);
			//do the update
			coll.update(update_query, update_command);
			
		}
		catch ( Exception e ) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * adds a new FeatureExtractor for a given Problem, if there are many attributes it should write them sequentially one by one
	 * @param feature_extractor_data - the feature extractor we are trying to add tot the database
	 * @param problem_name - the name of the problem we want to link this feature extractor to
	 * @return true if successful
	 */
	public boolean addFeatureExtractorDataToProblem( FeatureExtractorData feature_extractor_data ,String problem_name ) {
		try {
			/*
			//remove the dataset and store it later one by one
			Instances dataset = feature_extractor_data.getDataset();
			feature_extractor_data.setDataset(new Instances("Rel", new FastVector(0), 0));
			//do the same with the features
			List<Feature> features = feature_extractor_data.getFeatures();
			feature_extractor_data.setFeatures(new LinkedList<Feature>());
			//create the data that will be inserted at end
			DBObject data_to_insert = (DBObject) JSON.parse(json_parser.writeValueAsString(feature_extractor_data));
			//creating the dataset separately
			DBObject dataset_to_insert = (DBObject) ((DBObject)((DBObject)((DBObject) data_to_insert.get("dataset")).get("m_Attributes")).get("m_Objects")).get("1");
			
			for ( int i = 0 ; i < dataset.numAttributes() ; ++i ) {
				Attribute attribute_data = dataset.attribute(i);
				DBObject attribute_data_to_insert = (DBObject) JSON.parse(json_parser.writeValueAsString(attribute_data));
				dataset_to_insert.put(Integer.toString(i),attribute_data_to_insert);
			}
			((DBObject)((DBObject)((DBObject) data_to_insert.get("dataset")).get("m_Attributes")).get("m_Objects")).put("1", dataset_to_insert);
			//creating the features separately
			DBObject features_to_insert = (DBObject) ((DBObject) data_to_insert.get("features")).get("1");
			int counter = 0;
			for ( Feature feature : features ) {
				DBObject feature_data_to_insert = (DBObject) JSON.parse(json_parser.writeValueAsString(feature));
				features_to_insert.put(Integer.toString(counter),feature_data_to_insert);
				++counter;
			}
			((DBObject) data_to_insert.get("features")).put("1",features_to_insert);
			
			//get the adequate feature extractor collection
			DBCollection coll = getCollection(problem_name+feature_extractor_collection);
			//insert the new data
			coll.insert(data_to_insert);
			*/
			
			//create the data that will be inserted
			DBObject data_to_insert = (DBObject) JSON.parse(json_parser.writeValueAsString(feature_extractor_data));
			//get the adequate feature extractor collection
			DBCollection coll = getCollection(problem_name+feature_extractor_collection);
			//insert the new data
			coll.insert(data_to_insert);
		}
		catch ( Exception e ) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * adding a list of data objects to the data collection corresponding to a given problem
	 * @param data_coll - the list of data we want to add
	 * @param problem_name - the problem we want to add to
	 * @return true if successful
	 */
	public boolean addDataToProblem( List<Data> data_coll , String problem_name ) {
		for ( Data data : data_coll ) {
			if ( !addDataToProblem(data,problem_name) ) return false;
		}
		return true;
	}
	
	/**
	 * reads a Classifier from DB, so we can use it to classify some new instances
	 * @param classifier_name - the name of the classifier
	 * @param problem_name - the name of the problem
	 * @return
	 */
	public Classifier getClassifierForProblem ( String classifier_name , String problem_name ) {
		Classifier result = null;
		try {
			//get the adequate classifier collection
			DBCollection coll = getCollection(problem_name+classifier_collection);
			//create the query by classifier name
			DBObject query = new BasicDBObject(classifier_name_field_name,classifier_name);
			//fetch the required classifier
			DBObject classifier_data = coll.findOne(query);
			classifier_data.removeField(classifier_name_field_name);
			result = json_parser.readValue(classifier_data.toString(), Classifier.class);
		}
		catch ( Exception e ) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * saves a new classifier for a given problem, you must specify the classifier's name
	 * @param classifier - the classifier object to save
	 * @param classifier_name - the name of the classifier object
	 * @param problem_name - the name of the problem this classiffier corresponds to
	 * @return
	 */
	public boolean addClassifierToProblem ( Classifier classifier , String classifier_name , String problem_name ) {
		try {
			/*
			GsonBuilder g_builder = new GsonBuilder().serializeSpecialFloatingPointValues();
			Gson g = g_builder.create();
			//create the new DBObject to insert
			DBObject clasiffier_to_insert = (DBObject) JSON.parse(g.toJson(classifier));
			*/
			//create the new DBObject to insert
			DBObject clasiffier_to_insert = (DBObject) JSON.parse(json_parser.writeValueAsString(classifier));
			//give the classifier a name
			clasiffier_to_insert.put(classifier_name_field_name,classifier_name);
			//get the adequate classifier collection
			DBCollection coll = getCollection(problem_name+classifier_collection);
			//insert the new data
			coll.insert(clasiffier_to_insert);
		}
		catch ( Exception e ) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * appends a category information to a data object already in the database
	 * @param problem_name
	 * @param data_id
	 * @param classifier_name
	 * @param classifed_value
	 * @return
	 */
	public boolean setClassifierInformationToData( String problem_name,	UUID data_id , String classifier_name, String classifed_value ) {
				//create the update command
				DBObject update_command = new BasicDBObject("$set",new BasicDBObject("category_information."+classifier_name, classifed_value));
				//generate the query to find the appropriate data
				DBObject update_query = new BasicDBObject("_id",data_id.toString());
				//get the adequate data collection
				DBCollection coll = getCollection(problem_name+data_collection);
				//perform the update
				coll.update(update_query, update_command);
				return true;
	}
	
	/**
	 * appends a classifier information to a data object already in the database and also appends the dataTeaser from that data to the specified classifier data
	 * @param problem_name
	 * @param data_teaser
	 * @param classifier_name
	 * @param classifed_value
	 * @return
	 */
	public boolean addClassfierInformationForDataForProblemAndViceVersa ( String problem_name, DataTeaser data_teaser , String classifier_name, String classifed_value ) {
		setClassifierInformationToData(problem_name,data_teaser.getData_id(),classifier_name,classifed_value);
		addDataTeaserToCategoryForClass(problem_name,data_teaser,classifier_name,classifed_value);
		return true;
	}

	/**
	 * UPDATE THIS FUNCTION.
	 * appends the dataTeaser from a data to a specified classifier data object which is already in the database
	 * @param problem_name
	 * @param data_teaser
	 * @param classifier_name
	 * @param classifed_value
	 * @return
	 */
	public boolean addDataTeaserToCategoryForClass(String problem_name,
		DataTeaser data_teaser , String classifier_name, String classifed_value) {
		try {
			//create the data_teaser to insert along
			DBObject data_teaser_to_insert = (DBObject) JSON.parse(json_parser.writeValueAsString(data_teaser));
			//create the update command
			DBObject update_command = new BasicDBObject("$push",new BasicDBObject("class_data.$.data_teasers",data_teaser_to_insert));
			//generate the query to find the appropriate data
			DBObject update_query = new BasicDBObject("classifier_name",classifier_name);
			update_query.put("class_data.class_name", classifed_value);
			//get the adequate data collection
			DBCollection coll = getCollection(problem_name+category_collection);
			//perform the update
			coll.update(update_query, update_command);
		}
		catch ( Exception e ) {
			e.printStackTrace();
			return false;
		}
		return true;		
	}

	/**
	 * adding a single data object to the data collection corresponding to a given problem
	 * @param data - the data object we want to add
	 * @param problem_name - the problem we want to add to
	 * @return true if successful
	 */
	public boolean addDataToProblem( Data data , String problem_name ) {
		try {
			//create the new DBObject to insert
			DBObject data_to_insert = (DBObject) JSON.parse(json_parser.writeValueAsString(data));
			//get the adequate data collection
			DBCollection coll = getCollection(problem_name+data_collection);
			//insert the new data
			coll.insert(data_to_insert);
		}
		catch ( Exception e ) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * use this method when you want to create a new area of research, a new problem for which you will gather data and proccess it
	 * @param problem - the problem you wish to add
	 * @return true if successful, otherwise false
	 */
	public boolean createNewProblem( Problem problem ) {
		try {
			//create the new DBObject to insert
			DBObject problem_to_insert = (DBObject) JSON.parse(json_parser.writeValueAsString(problem));
			//get the problems collection
			DBCollection coll = getCollection(problems_collection);
			//insert the new problem
			coll.insert(problem_to_insert);
			//make adequate Data and FeatureExtractor collections for the Problem
			makeCollection(problem.getName()+data_collection);
			makeCollection(problem.getName()+feature_extractor_collection);
			makeCollection(problem.getName()+classifier_collection);
			makeCollection(problem.getName()+category_collection);
		}
		catch ( Exception e ) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * makes a new collection with the given name
	 * @param name
	 * @return true if successfully created
	 */
	public boolean makeCollection( String name ) {
		try {
			//get the database
			DB db = mongo.getDB(db_name);
			//create the capp object
			BasicDBObject capp = new BasicDBObject();
//			capp.put("capped", false);
//			capp.put("size", 5000000);
			//create the new collection
			db.createCollection(name, capp);
		}
		catch ( Exception e ) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * gets the DB object used to access the database, this allows advanced users total control over the database and gives them
	 * the ability to perform custom queries and/or modifications
	 * @return the DB object for the aggregator database
	 */
	public DB getDB () {
		return mongo.getDB(db_name);
	}
	
	/**
	 * get a collection(relation) from the database, if no such collection exists creates a new one
	 * @param problemsRelation
	 * @return
	 */
	 public DBCollection getCollection(String collection_name) {
		DB db = mongo.getDB(db_name);
		return db.getCollection(collection_name);
	}
	 
	/**
	 * the only constructor for the DAtabase Manager, sets the database name to aggregator_db
	 * it should not be invoked directly only from the getInstance method  
	 */
    private DatabaseManager() {  
    	db_name = "agregator_db";
    	mongo = null;
    	json_parser = new ObjectMapper();
    	json_parser.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY );
    	json_parser.setVisibilityChecker(json_parser.getVisibilityChecker()
    			 // .with(Visibility.ANY)
    			  .withCreatorVisibility(Visibility.NONE)  
    		      .withGetterVisibility(Visibility.NONE)  
    		      .withIsGetterVisibility(Visibility.NONE)  
    		      .withSetterVisibility(Visibility.NONE));
//    	json_parser.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);  
//    	json_parser.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);  
//    	json_parser.setSerializationInclusion(Include.NON_DEFAULT);
    }
    
    /**
     * gets an instance for the database manager, if the instance is null create new instance
     * e.g.  init this instance e.g. create the database connection, if not simply returns the instance
     * @return a instance object of DatabaseManager
     */
    public static synchronized DatabaseManager getInstance() {
   	     if ( null == instance ) {
                    instance = new DatabaseManager();
                    instance.initDB();
         }
         return instance;
    }
    
    /**
     * connects to database with default ip address and port number
     * these are hardcoded 
     * *NOTICE: change this 
     * @return true if successful
     */
    public boolean connectToDatabase( ) {
		return connectToDatabase("127.0.0.1", "27017");
	}

    /**
     * connects to the database with a given ip_address and port number
     * @param ip_address - the ip address the database is waiting connections on
     * @param port_number -the port number the database is waiting connections on
     * @return true if successful
     */
	public boolean connectToDatabase( String ip_address , String port_number ) {
		try {
			mongo = new Mongo(ip_address,Integer.parseInt(port_number));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * initializes the DatabaseManager by creating a connection to the database and creating a new empty problems collection
	 */
	public  void initDB() {
		if (mongo == null) {
			connectToDatabase();
		}
		DB db = mongo.getDB(db_name);
		if ( db.getCollectionNames().size() == 0 ) {
			db.createCollection(problems_collection,new BasicDBObject());
		}
	}

	public boolean setClusterTeaserForDataForProblem(ClusterTeaser cluster_teaser,
			UUID data_id, String problemName) {
		try {
			//create the new DBObject to insert
			DBObject cluster_teaser_to_insert = (DBObject) JSON.parse(json_parser.writeValueAsString(cluster_teaser));
			//get the data collection
			DBCollection coll = getCollection(problemName+data_collection);
			//create the query to find the appropriate data
			DBObject update_query = new BasicDBObject("_id",data_id.toString());
			//create the command that will set the new cluster teaser
			DBObject update_command = new BasicDBObject("$set",new BasicDBObject("cluster_teaser",cluster_teaser_to_insert));
			//perform the update
			coll.update(update_query, update_command);
		}
		catch ( Exception e ) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean saveCategoryMapForProblemIncludingDataTeasers(CategoryMap category_map,
			String problemName , boolean include_non_leaf_categories ) {
		return saveCategoryForProblemIncludingDataTeasers(category_map.getRoot_category(),problemName,include_non_leaf_categories);
	}

	public boolean saveCategoryForProblemIncludingDataTeasers(
			Category category, String problem_name , boolean include_non_leaf_categories ) {
		try {
			//only save categories that have data teasers unless specified otherwise by the flag
			if ( include_non_leaf_categories || category.getData_teasers() != null ) {
				//create the category data object to insert
				DBObject category_to_insert = (DBObject) JSON.parse(json_parser.writeValueAsString(category));
				//get the category collection
				DBCollection coll = getCollection(problem_name+category_collection);
				//insert the new category
				coll.insert(category_to_insert);
			}
			//now insert all its children, if there are any
			if ( category.getChildren() != null ) {
				for ( Category c : category.getChildren() ) {
					saveCategoryForProblemIncludingDataTeasers(c,problem_name,include_non_leaf_categories);
				}
			}
		}
		catch ( Exception e ) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public Category getCategoryByIdForProblem( UUID category_id , String problem_name ) {
		Category result = null;
		try {
			//get the category collection
			DBCollection coll = getCollection(problem_name+category_collection);
			//insert the new category
			DBObject category_data_object = coll.findOne(new BasicDBObject("_id",category_id.toString()));
			//parse the read object to a category
			result = json_parser.readValue(category_data_object.toString(),Category.class);
		}
		catch ( Exception e ) {
			e.printStackTrace();
		}
		return result;
	}
	
	public boolean saveCategoryMapForProblemWithoutDataTeasers ( CategoryMap category_map , String problem_name  ) {
		try {
			//create a copy of the category map that contains no data teasers
			CategoryMap copy_category_map = new CategoryMap(copyWithoutDataTeasers(category_map.getRoot_category()));
			//create the category data object to insert
			DBObject category_map_to_insert = (DBObject) JSON.parse(json_parser.writeValueAsString(copy_category_map));
			//get the category collection
			DBCollection coll = getCollection(problem_name+category_collection);
			//make sure there isn't any other category map saved
			coll.remove(new BasicDBObject("@class",CategoryMap.class.getName()));
			//insert the new category
			coll.insert(category_map_to_insert);
		}
		catch ( Exception e ) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	private Category copyWithoutDataTeasers(Category category) {
		Category result = new Category(category.getName());
		result.set_id(category.get_id());
		if ( category.getChildren() != null ) {
			for ( Category child : category.getChildren() ) {
				result.addChild(copyWithoutDataTeasers(child));
			}
		}
		return result;
	}

	public CategoryMap getCategoryMapForProblemWithoutDataTeasers( String problem_name ) {
		CategoryMap result = null;
		try {
			//get the category collection
			DBCollection coll = getCollection(problem_name+category_collection);
			//insert the new category
			DBObject category_map_data_object = coll.findOne(new BasicDBObject("@class",CategoryMap.class.getName()));
			//parse the read object to a category map
			result = json_parser.readValue(category_map_data_object.toString(),CategoryMap.class);
		}
		catch ( Exception e ) {
			e.printStackTrace();
		}
		return result;
	}

	public boolean addDataTeasersToCategoryForProblem ( List<DataTeaser> data_teasers , UUID category_id , String problem_name ) {
		try {
			//create the data teaser object to insert
			BasicDBList data_teaser_to_insert = new BasicDBList();
			for ( DataTeaser data_teaser : data_teasers ) {
				data_teaser_to_insert.add(JSON.parse(json_parser.writeValueAsString(data_teaser)));
			}
			//create the update command object
			DBObject update_command = new BasicDBObject("$pushAll",new BasicDBObject("data_teasers.1",data_teaser_to_insert));
			//create the update query
			DBObject update_query = new BasicDBObject("_id",category_id.toString());
			//get the appropriate collection
			DBCollection coll = getCollection(problem_name+category_collection);
			//perform the update
			coll.update(update_query, update_command);
		}
		catch ( Exception e ) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean addDataTeaserToCategoryForProblem(DataTeaser data_teaser,
			UUID category_id, String problem_name) {
		try {
			//create the data teaser object to insert
			DBObject data_teaser_to_insert = (DBObject) JSON.parse(json_parser.writeValueAsString(data_teaser));
			//create the update command object
			DBObject update_command = new BasicDBObject("$push",new BasicDBObject("data_teasers.1",data_teaser_to_insert));
			//create the update query
			DBObject update_query = new BasicDBObject("_id",category_id.toString());
			//get the appropriate collection
			DBCollection coll = getCollection(problem_name+category_collection);
			//perform the update
			coll.update(update_query, update_command);
		}
		catch ( Exception e ) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean updateCategoryMapWithDataTeasersForProblem ( CategoryMap category_map , String problem_name ) {
		CategoryMap previous_cm = getCategoryMapForProblemWithoutDataTeasers(problem_name);
		Category root = previous_cm.getRoot_category();
		Category cluster_category = null;
		if ( root.getChildren() != null ) {	
			for ( Category child : root.getChildren() ) {
				if ( child.getName().equals(CategoryMapBuilder.clusters_category_label)) {
					cluster_category = child;
				}
			}	
		}
		if ( cluster_category != null ) {
			if ( cluster_category.getChildren() != null ) {
				DBCollection coll = getCollection(problem_name+category_collection);
				for ( Category child : cluster_category.getChildren() ) {
					coll.remove(new BasicDBObject("_id",child.get_id().toString()));
				}
			}
		}
		return updateCategoryWithDataTeasersForProblem(category_map.getRoot_category(),problem_name);
	}

	private boolean updateCategoryWithDataTeasersForProblem(
			Category category, String problem_name) {
		//if the category has children the recursevly update all of them
		if ( category.getName().equals(CategoryMapBuilder.clusters_category_label)) {
			if ( category.getChildren() != null ) {
				for ( Category child : category.getChildren() ) {
					saveCategoryForProblemIncludingDataTeasers(child,problem_name,false);
				}
			}
		}
		
		if ( category.getChildren() != null ) {
			for ( Category child : category.getChildren() ) {
				updateCategoryWithDataTeasersForProblem(child,problem_name);
			}
		}
		else {
			//if the category contains any new data teasers then push them to the db
			if ( category.getData_teasers() != null ) {
				addDataTeasersToCategoryForProblem(category.getData_teasers(), category.get_id(), problem_name);
			}
		}
		return true;
	}

	public boolean existsDataForProblem(Data data, String problem_name) {
		try {
			//create the new DBObject to insert
			DBObject data_to_query = (DBObject) JSON.parse(json_parser.writeValueAsString(data));
			//perform the comparison only on the contents & source
			DBObject data_to_query_improved = new BasicDBObject();
			data_to_query_improved.put("contents", data_to_query.removeField("contents"));
			data_to_query_improved.put("title", data_to_query.removeField("title"));
			//get the adequate data collection
			DBCollection coll = getCollection(problem_name+data_collection);
			//query the data
			DBObject found = coll.findOne(data_to_query_improved);
			return (found != null);
		}
		catch ( Exception e ) {
			e.printStackTrace();
			return false;
		}
	}

	public List<DataTeaser> getDataTeasersForCategoriesForProblem ( List <UUID> category_ids , String problem_name ) {
		List<DataTeaser> result = new ArrayList<DataTeaser>();
		try {
			//create the query
			BasicDBList list_ids = new BasicDBList();
			list_ids.addAll(category_ids);
			BasicDBObject query = new BasicDBObject("_id",new BasicDBObject("$in",list_ids));
			//fields to retrieve
			DBObject fields = new BasicDBObject("data_teasers","1");
			//get the appropriate collection
			DBCollection coll = getCollection(problem_name+category_collection);
			//perform the update
			DBCursor curr = coll.find(query, fields);
			while ( curr.hasNext() ) {
				DBObject current = curr.next();
				result.add(json_parser.readValue(current.toString(), DataTeaser.class));
			}
		}
		catch ( Exception e ) {
			e.printStackTrace();
		}
		return result;
	}

	public List<Data> getDataForQueryForProblem(String problem_name,
			Map<String, String> query , String order_by ) {
		if ( order_by.equals("time") ) {
			order_by = "accessed_at.1";
		}
		ArrayList<Data> result = new ArrayList<Data>();
		try {
			//get the adequate data collection
			DBCollection coll = getCollection(problem_name+data_collection);
			//create the query
			DBObject q = new BasicDBObject();
			for ( Map.Entry<String,String> entry : query.entrySet() ) {
				q.put("category_information."+entry.getKey(), entry.getValue());
			}
			//get all the data from the collection
			DBCursor cursor = coll.find(q).sort(new BasicDBObject(order_by,1));
			while ( cursor.hasNext() ) {
				DBObject curr = cursor.next();
				Data data = json_parser.readValue(curr.toString(), Data.class);
				result.add(data);
			}
			DBObject update_command = new BasicDBObject("$inc",new BasicDBObject("counter",1));
			coll.update(q, update_command,false,true);
		}
		catch ( Exception e ) {
			e.printStackTrace();
		}
		return result;
	}

	public String getIp_address() {
		return ip_address;
	}

	public void setIp_address(String ip_address) {
		this.ip_address = ip_address;
	}

	public String getPort_number() {
		return port_number;
	}

	public void setPort_number(String port_number) {
		this.port_number = port_number;
	}
	
}
