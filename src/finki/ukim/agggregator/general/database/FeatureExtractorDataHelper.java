package finki.ukim.agggregator.general.database;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SparseInstance;
import finki.ukim.agggregator.general.data_model.CategoryMap;
import finki.ukim.agggregator.general.data_model.Data;
import finki.ukim.agggregator.general.data_model.Feature;
import finki.ukim.agggregator.general.data_model.FeatureExtractorData;

/**
 * A helper class that contains some useful methods when extracting features.
 * @author Andrej Gajduk
 *
 */
public class FeatureExtractorDataHelper {
	
	public FeatureExtractorDataHelper () {
		
	}
	
	/**
	 * Iterates over the data and extracts features for it using the specified feature_extactor,
	 * then adds the relevant attribute value specified by the attribute key parameter.
	 * Requires a list of possible class values which can be obtained by calling getAllValuesForCategoryFromData
	 * @param problem_name
	 * @param feature_extractor_data
	 * @param data
	 * @param attribute_key
	 * @param remove_missing_values
	 * @param possible_values
	 * @return
	 */
	public Instances getInstancesAndAddANewClassAttribute(String problem_name,
			FeatureExtractorData feature_extractor_data, List<Data> data ,
			String attribute_key, boolean remove_missing_values ,
			FastVector possible_values ) {
				if ( possible_values == null ) {
					possible_values = getAllValuesForCategoryFromData(data, attribute_key);
				}
				Attribute class_attribute = new Attribute(attribute_key+":"+UUID.randomUUID().toString(), possible_values);
				//create a new empty dataset
				List<Instance> extracted_features_with_added_new_class_attribute = new ArrayList<Instance>();
				for ( Feature feature : feature_extractor_data.getFeatures() ) {
					//start adding all the extracted features one by one
					//but first check if the have a value for the new class attribute, we did this with the getInformationAbout ( key ) method in the data object and in order to do that,
					//we must first find the appropriate data object for the feature object
					Data d = data.get(data.indexOf(new Data(feature.getData_id())));
					//we check what is the value for that key
					String attribute_value = d.getCategoryInformationField(attribute_key);
					//we get the current instance that need to be modified to include the new class attribute
					Instance instance = feature.getInstance();
					//we check if the attribute_value is valid, if it's not valid it must be just an empty string
					if ( possible_values.contains(attribute_value) )  
						instance = addClassValueForInstance(instance,class_attribute.indexOfValue(attribute_value));
					else {
						//if we chose to remove the missing values than all extracted_features which don't contain a value about the given key should be ignored
						if ( remove_missing_values ) continue;
						instance = addClassValueForInstance(instance,Instance.missingValue());	
					}
					//finally add the new instance with class attribute to the list of extracted features
					extracted_features_with_added_new_class_attribute.add(instance);
				}
				//create a copy of all the previous attributes
				FastVector att_info = new FastVector(feature_extractor_data.getDataset().numAttributes()+1);
				for ( int i = feature_extractor_data.getDataset().numAttributes()-1 ; i >= 0 ; --i ) att_info.addElement(feature_extractor_data.getDataset().attribute(i));
				//and add the class attribute to this list
				att_info.addElement(class_attribute);
				//formulate the new dataset
				Instances result = new Instances("Rel",att_info,extracted_features_with_added_new_class_attribute.size());
				//set the class attribute
				result.setClass(class_attribute);
				//add all the extracted features as instances whilst setting their dataset
				for ( Instance instance : extracted_features_with_added_new_class_attribute ) {
					instance.setDataset(result);
					result.add(instance);
				}
				return result;
	}
	
	private Instance addClassValueForInstance(Instance instance, double value) {
		//we copy all the att_values from the old instance and add a new field for the class attribute which we set to the value of the class attribute 
		double new_att_values[] = new double[instance.numValues()+1];
		for ( int i = new_att_values.length-2 ; i >= 0 ; --i ) new_att_values[i] = instance.value(i);
		new_att_values[new_att_values.length-1] = value;
		//we check if the instance is a sparse instance, if so we need to copy all the attribute indices as well and add a new field to display the newly added class attribute's index
		if ( instance instanceof SparseInstance ) {
			int new_att_indices[] = new int[((SparseInstance) instance).numValues()+1];
			for ( int i = new_att_indices.length-2 ; i >= 0 ; --i ) new_att_indices[i] = instance.index(i);
			new_att_indices[new_att_indices.length-1] = instance.numAttributes();
			//create the SparseInstance with the new class attribute
			instance = new SparseInstance(instance.weight(),new_att_values,new_att_indices,instance.numAttributes()+1);
		}
		else {
			//create an ordinary Instance with the new class attribute
			instance = new Instance(instance.weight(), new_att_values);
		}
		return instance;
	}

	/**
	 * Iterates over the data, and returns a vector of possible category values e.g. basketball, tennis and golf (for sport news)
	 * @param data
	 * @param attribute_key
	 * @return
	 */
	public FastVector getAllValuesForCategoryFromData ( List<Data> data , String attribute_key ) {
		FastVector possible_values = new FastVector();
		for ( Data d : data ) {
			String category_value = d.getCategoryInformationField(attribute_key);
			if ( category_value.length() != 0 ) {
				if ( ! possible_values.contains(category_value) ) {
					possible_values.addElement(category_value);
				}
			}
		}
		return possible_values;
	}
	
	/**
	 * Iterates over the category map, and returns a list of possible category values e.g. basketball, tennis and golf (for sport news)
	 * @param data
	 * @param attribute_key
	 * @return
	 */
	public FastVector getAllValuesForCategoryFromCategoryMap ( CategoryMap cm , String category_name ) {
		FastVector possible_values = new FastVector();
		List<String> list_of_values = cm.getAllValuesForCategory(category_name);
		for ( String value : list_of_values ) {
			possible_values.addElement(value);
		}
		return possible_values;
	}

}
