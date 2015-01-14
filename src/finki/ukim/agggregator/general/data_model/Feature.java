package finki.ukim.agggregator.general.data_model;

import java.util.UUID;
import weka.core.Instance;

/**
 * Represents a features-data pair
 * For the data only the id is stored, and for the features values we use the Weka Instance object
 * The dataset (list of all the attributes) MUST NOT be stored in the Instance - too much overhead
 * @author Andrej Gajduk
 *
 */
public class Feature {
	
	/**
	 * the instance itself, containing the values for different attributes
	 * can be a regular instance as well as a SparseInstance
	 */
	private Instance instance;


	/**
	 * basic constructor supports both types Sparse and Dense instances
	 * @param type
	 * @param attribute_values
	 * @param data_id
	 */
	public Feature( Instance instance , UUID data_id ) {
		this.data_id = data_id;
		setInstance(instance);
	}
	
	public Feature() {}

	/**
	 * an id for the data object we extracted features from
	 */
	private UUID data_id;
	
	/**
	 * @return the data_id
	 */
	public UUID getData_id() {
		return data_id;
	}

	/**
	 * @param data_id the data_id to set
	 */
	public void setData_id(UUID data_id) {
		this.data_id = data_id;
	}
	
	/**
	 * returns the instance representing the feature vector
	 * @return
	 */
	public Instance getInstance() {
		return instance;
	}

	/**
	 * sets the instance
	 * @param instance
	 */
	public void setInstance( Instance instance ) {
		this.instance = instance;
	}



	
}
