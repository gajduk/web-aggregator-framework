package finki.ukim.agggregator.general.data_model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import weka.core.Instances;


/**
 * A feature extractor is used to obtain the features for a given Data collection.
 * It has a descriptive name of the feature extracting method used, and a collection of Feature objects each corresponding to an individual data object.
 * Also it contains the dataset  (list of all the attributes) for the given data and the feature extractor used.
 * @author Andrej Gajduk
 */
public class FeatureExtractorData {
	
	/**
	 * an id to be used for the database
	 */
	protected UUID _id;
	
	/**
	 * the name description of the feature extractor used
	 */
	private String name;
	
	/**
	 * the features extracted from the data collection
	 */
	private List<Feature> features;
	
	/**
	 * the dataset, a list of all the attributes
	 */
	private Instances dataset;

	/**
	 * combines the list of attributes with all the extracted features into a single instances object which can then be used for classification
	 * @return
	 */
	public Instances getInstances () {
		//make a copy of the dataset
		Instances result = new Instances(dataset);
		//add all the extracted features one by one
		for ( Feature feature : features ) {
			result.add(feature.getInstance());
		}
		return result;
	}
		
	/**
	 * @return the dataset
	 */
	public Instances getDataset() {
		return dataset;
	}

	/**
	 * @param dataset the dataset to set
	 */
	public void setDataset(Instances dataset) {
		this.dataset = dataset;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the _id
	 */
	public UUID get_id() {
		return _id;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the features
	 */
	public List<Feature> getFeatures() {
		return features;
	}

	/**
	 * @param features the features to set
	 */
	public void setFeatures(List<Feature> features) {
		this.features = features;
	}

	/**
	 * @param name
	 * @param features
	 */
	public FeatureExtractorData(String name, List<Feature> features) {
		_id = UUID.randomUUID();
		this.name = name;
		this.features = features;
	}

	/**
	 * @param name
	 */
	public FeatureExtractorData(String name) {
		_id = UUID.randomUUID();
		this.name = name;
		this.features = new ArrayList<Feature>();
	}


	public FeatureExtractorData() {	}
	
}
