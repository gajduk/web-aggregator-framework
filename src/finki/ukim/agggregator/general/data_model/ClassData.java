package finki.ukim.agggregator.general.data_model;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores the name and data teasers for a given class.
 * 
 * see ClassifierData
 * 
 * @author AndrejGajduk
 *
 */
public class ClassData {

	/**
	 * the name of the class
	 */
	private String class_name;
	
	/**
	 * DataTeasers of all the data objects that have been classified to this class
	 */
	private List<DataTeaser> data_teasers;

	/**
	 * @return the class_name
	 */
	public String getClass_name() {
		return class_name;
	}

	/**
	 * @param class_name the class_name to set
	 */
	public void setClass_name(String class_name) {
		this.class_name = class_name;
	}

	/**
	 * @return the data_teasers
	 */
	public List<DataTeaser> getData_teasers() {
		return data_teasers;
	}

	/**
	 * @param data_teasers the data_teasers to set
	 */
	public void setData_teasers(List<DataTeaser> data_teasers) {
		this.data_teasers = data_teasers;
	}

	/**
	 * @param class_name
	 * @param data_teasers
	 */
	public ClassData(String class_name, List<DataTeaser> data_teasers) {
		this.class_name = class_name;
		this.data_teasers = data_teasers;
	}

	/**
	 * @param class_name
	 */
	public ClassData(String class_name) {
		this.class_name = class_name;
		this.data_teasers = new ArrayList<DataTeaser>();
	}

	/**
	 * 
	 */
	public ClassData() {
	}
	
}
