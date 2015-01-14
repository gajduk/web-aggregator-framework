package finki.ukim.agggregator.general.data_model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * A stub class that represents any data which should be processed and stored in the Database.
 * 
 * Implementation of TextData is provided for you. 
 * 
 * When overriding this class make sure to define the contents and the source
 * and add support for storing and loading it from the database.
 * 
 * @author Andrej Gajduk
 */
public class Data {

	/**
	 * id
	 */
	protected UUID _id;
	
	/**
	 * where the Data was obtained, from usually this should be a web page or some other type of reference
	 */
	protected Source source;
	
	/**
	 * when this data was accessed and fetched from the source
	 */
	protected Date accessed_at;
		
	/**
	 * until when this data is valid and relevant to be taken into consideration,
	 * after this date it should not be considered as relevant
	 */
	protected Date valid_until;
	
	/**
	 * the title of the Data 
	 * only use this for representing the Data to the user for quick reference, not for doing any feature extracting or stuff
	 */
	protected String title;
	
	/**
	 * id of the single cluster this Data belongs to
	 */
	protected ClusterTeaser cluster_teaser;

	/**
	 * a teaser of the data, fetch this if you're not interested in the whole data
	 */
	protected DataTeaser teaser;
		
	/**
	 * counts how many times this data was read form the db
	 */
	protected int counter;
	
	/**
	 * @return the cluster_teaser
	 */
	public ClusterTeaser getCluster_teaser() {
		return cluster_teaser;
	}

	/**
	 * @param cluster_teaser the cluster_teaser to set
	 */
	public void setCluster_teaser(ClusterTeaser cluster_teaser) {
		this.cluster_teaser = cluster_teaser;
	}

	
	
	/**
	 * @return the counter
	 */
	public int getCounter() {
		return counter;
	}

	/**
	 * @param counter the counter to set
	 */
	public void setCounter(int counter) {
		this.counter = counter;
	}

	/**
	 * @return the category_information
	 */
	public Map<String, String> getCategory_information() {
		return category_information;
	}

	/**
	 * @param category_information the category_information to set
	 */
	public void setCategory_information(Map<String, String> category_information) {
		this.category_information = category_information;
	}



	/**
	 * Stores key:value pairs of classifier_name:class_attribute_value for each classifier that was used on this data
	 * so we can display them to the user from the Data object, some of the pairs can be set at access time if the source provides them or a suitable replacement
	 */
	protected Map<String,String> category_information;
	
	/**
	 * a basic constructor that sets all the fields, initializes the classifiers_information to an empty map and generates a new random _id
	 */
	public Data( String title  , Source source , Date accessed_at , Date valid_until) {
		this.source = source;
		this.accessed_at = accessed_at;
		this.title = title;
		this.valid_until = valid_until;
		_id = UUID.randomUUID();
		category_information = new HashMap<String,String>();
		counter = 0;
		teaser = new DataTeaser(_id,accessed_at,title);
		
	}
	
	/**
	 * a basic constructor that sets all the fields, initializes the classifiers_information to an empty map and generates a new random _id
	 */
	public Data( String title  , Source source , Date accessed_at , Date valid_until , Map<String,String> category_information , ClusterTeaser cluster_teaser ) {
		this.source = source;
		this.accessed_at = accessed_at;
		this.title = title;
		this.valid_until = valid_until;
		this._id = UUID.randomUUID();
		this.category_information = category_information;
		this.cluster_teaser = cluster_teaser;
		counter = 0;
		teaser = new DataTeaser(_id,accessed_at,title);
		
	}
	
	/**
	 * adds a new key:value pairs of classifier_name:class_attribute_value to the map, if a field with that key (classifier name) already exists it is replaced
	 * @param classifier_name
	 * @param class_attribute_value
	 */
	public void setCategoryInformationField ( String category_name , String category_value ) {
		category_information.put(category_name, category_value);
	}
	
	/**
	 * removes an existing key:value pairs of classifier_name:class_attribute_value to the map, as specified by the key
	 * if that key does not exist it does nothing
	 * @param classifier_name - the classifier name which defines the key:value pair we wish to remove
	 */
	public void removeCategoryInformationField ( String category_name ) {
		category_information.remove(category_name);
	}
	
	/**
	 * get the field from the classification information that corresponds to the given key (classifier name)
	 * @param classifier_name
	 * @return the value for that classifier name or an empty string if such key does not occur in the map
	 */
	public String getCategoryInformationField ( String category_name ) {
		if ( category_information.containsKey(category_name) ) {
			return category_information.get(category_name);
		}
		return "";
	}
		
	/**
	 * @return the source
	 */
	public Source getSource() {
		return source;
	}

	/**
	 * @param source the source to set
	 */
	public void setSource(Source source) {
		this.source = source;
	}

	/**
	 * @return the accessed_at
	 */
	public Date getAccessed_at() {
		return accessed_at;
	}
	
	/**
	 * @param accessed_at the accessed_at to set
	 */
	public void setAccessed_at(Date accessed_at) {
		this.accessed_at = accessed_at;
	}
	
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	/**
	 * @return the classifiers_information
	 */
	public Map<String, String> getClassifiers_information() {
		return category_information;
	}
	
	/**
	 * @param classifiers_information the classifiers_information to set
	 */
	public void setClassifiers_information(
			Map<String, String> classifiers_information) {
		this.category_information = classifiers_information;
	}
	
	/**
	 * @return the _id
	 */
	public UUID get_id() {
		return _id;
	}
	
	/**
	 * @return the valid_until
	 */
	public Date getValid_until() {
		return valid_until;
	}

	/**
	 * @param valid_until the valid_until to set
	 */
	public void setValid_until(Date valid_until) {
		this.valid_until = valid_until;
	}

	/**
	 * displays all the fields
	 */
	@Override
	public String toString() {
		return "title:"+title+", source:"+source+", accessed at:"+accessed_at+", id:"+_id.toString()+" ; ";
	}

	public DataTeaser getTeaser ( ) {
		return teaser;
	}

	public void setTeaser ( DataTeaser teaser) {
		this.teaser = teaser;
	}

	public Data () {
	}
	
	public Data ( UUID id ) {
		this._id = id;
	}
	
	@Override
	public boolean equals(Object obj) {
		if ( obj instanceof Data ) {
			return _id.equals(((Data) obj).get_id());
		}
		return super.equals(obj);
	}
	
}

