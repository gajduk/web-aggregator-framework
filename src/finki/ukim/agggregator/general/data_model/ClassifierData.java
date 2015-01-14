package finki.ukim.agggregator.general.data_model;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores the classifier_name and a list of class data for a given classifier.
 * 
 * 
 * 
 * @author AndrejGajduk
 *
 */
public class ClassifierData {
	
	/**
	 * the name of the classifier this data corresponds to
	 */
	private String classifier_name;

	/**
	 * for each possible class all the data objects that have been classified to belong
	 */
	private List<ClassData> class_data;

	/**
	 * @return the classifier_name
	 */
	public String getClassifier_name() {
		return classifier_name;
	}

	/**
	 * @param classifier_name the classifier_name to set
	 */
	public void setClassifier_name(String classifier_name) {
		this.classifier_name = classifier_name;
	}

	/**
	 * @return the class_data
	 */
	public List<ClassData> getClass_data() {
		return class_data;
	}

	/**
	 * @param class_data the class_data to set
	 */
	public void setClass_data(List<ClassData> class_data) {
		this.class_data = class_data;
	}

	/**
	 * @param classifier_name
	 * @param class_data
	 */
	public ClassifierData(String classifier_name, List<ClassData> class_data) {
		this.classifier_name = classifier_name;
		this.class_data = class_data;
	}
	
	/**
	 * @param classifier_name
	 * @param class_data
	 */
	public ClassifierData(String classifier_name ) {
		this.classifier_name = classifier_name;
		this.class_data = new ArrayList<ClassData>();
	}

	/**
	 * default empty constructor
	 */
	public ClassifierData() {
	} 
		
}
