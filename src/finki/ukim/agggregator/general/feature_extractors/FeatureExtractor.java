package finki.ukim.agggregator.general.feature_extractors;

import java.util.ArrayList;
import java.util.List;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.UnsupportedClassTypeException;
import finki.ukim.agggregator.general.data_model.Data;
import finki.ukim.agggregator.general.data_model.Feature;

/**
 * A stub class to be used for creating all sorts of methods for extracting features from data.
 * They are specific to the type of data therefore they should be used only with the data types they support.
 * the supported DataTypes should be listed in the documentation for each class 
 * @author Andrej Gajduk
 *
 */
public abstract class FeatureExtractor {
	
	/**
	 * the attributes this feature extractor will measure&compute
	 */
	protected FastVector attributes;
	
	/**
	 * @return the dataset
	 */
	public Instances getDataset() {
		return new Instances("Rel",attributes,0);
	}

	public FeatureExtractor() {
		attributes = new FastVector();
	}
	
	public void addAttribute( Attribute attr ) {
		attributes.addElement(attr);
	}
	
	/**
	 * Creates a Nominal attribute object, helper method
	 * @param attribute_name -the name of the attribute we wish to create
	 * @param attribute_possible_values - all the possible values this attribute may have
	 * @return
	 */
	public Attribute makeNominalAttribute ( String attribute_name, String...  attribute_possible_values) {
		FastVector fv = new FastVector(attribute_possible_values.length);
		for ( int i = 0 ; i < attribute_possible_values.length ; ++i ) fv.addElement(attribute_possible_values[i]);
		return new Attribute(attribute_name,fv);
	}
		
	/**
	 * Calculates the feature vector or map for a given data object, it may update the current dataset
	 * here is where most the real functionality happens
	 * @param data - the data we should perform our extraction on
	 * @return - a corresponding feature vector
	 */
	public abstract Feature getFeatures( Data data  ) throws UnsupportedClassTypeException;
	
	/**
	 * Calculates the feature vector or map for a given list of data objects by calling the getFeatures method on each data object, it may update the current dataset
	 * @param data - the data we should perform our extraction on
	 * @param feature_type - the type of feature vector we want to get can be full or sparse @see weka.core.instance
	 * @return - a corresponding feature vector
	 */
	public List<Feature> getFeatures( List<Data> data ) throws UnsupportedClassTypeException {
		ArrayList<Feature> result = new ArrayList<Feature>(data.size());
		for ( Data d : data ) {
			result.add(getFeatures(d));
		}
		return result;
	}
	
	/**
	 * Calculates the feature vector or map for a given data object using the supplied dataset
	 * @param data - the data we should perform our extraction on
	 * @param dataset - the dataset which contains the attributes we are measuring, no other attributes may be measured
	 * @param feature_type - the type of feature vector we want to get can be full or sparse @see weka.core.instance
	 * @return - a corresponding feature vector
	 */
	public abstract Feature getFeatures( Data data , Instances dataset ) throws UnsupportedClassTypeException;
	
	/**
	 * Calculates the feature vector or map for a given list of data objects using the supplied dataset
	 * @param data - the data we should perform our extraction on
	 * @param dataset - the dataset which contains the attributes we are measuring, no other attributes may be measured
	 * @param feature_type - the type of feature vector we want to get can be full or sparse @see weka.core.instance
	 * @return - a corresponding feature vector
	 */
	public List<Feature> getFeatures( List<Data> data , Instances dataset ) throws UnsupportedClassTypeException {
		ArrayList<Feature> result = new ArrayList<Feature>(data.size());
		for ( Data d : data ) {
			result.add(getFeatures(d,dataset));
		}
		return result;
	}
	
	/**
	 * uUseful for those feature extraction methods that need some global data to operate
	 * like the inverse turn document frequency
	 * @param data - the data we should perform our extraction on
	 * @param feature_type - the type of feature vector we want to get can be full or sparse @see weka.core.instance
	 * @return - a corresponding feature vector
	 */
	public abstract Instance getGlobalFeatures ( List<Data> data ) throws UnsupportedClassTypeException;
		
	/**
	 * Adds the attributes this data has to the current dataset.
	 * @param data - sample data, on which we will perform a feature extraction at a later stage
	 */
	public abstract void addAttributesForData ( Data data ) throws UnsupportedClassTypeException;
	
	/**
	 * same as @see #addAttributesForData(Data)
	 * only it works for a list of data
	 * @param data - sample data, on which we will perform a feature extraction at a later stage
	 */
	public void addAttributesForData ( List<Data> data ) throws UnsupportedClassTypeException {
		for ( Data d : data ) {
			addAttributesForData(d);
		}
	}
	
}
