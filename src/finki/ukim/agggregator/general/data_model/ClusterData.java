package finki.ukim.agggregator.general.data_model;

import java.util.List;
import java.util.UUID;

/**
 * Stores the a list of data_tesers for a given cluster.
 * 
 * 
 * 
 * @author AndrejGajduk
 *
 */
public class ClusterData {
	
	/**
	 * the id of the cluster
	 */
	private UUID _id;
	
	/**
	 * list of all the data teasers in this cluster
	 */
	private List<DataTeaser> data_teasers;

	/**
	 * @return the _id
	 */
	public UUID get_id() {
		return _id;
	}

	/**
	 * @param _id the _id to set
	 */
	public void set_id(UUID _id) {
		this._id = _id;
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
	 * @param data_teasers
	 */
	public ClusterData(List<DataTeaser> data_teasers) {
		this.data_teasers = data_teasers;
		this._id = UUID.randomUUID();
	}

	/**
	 * @param _id
	 * @param data_teasers
	 */
	public ClusterData(UUID _id, List<DataTeaser> data_teasers) {
		this._id = _id;
		this.data_teasers = data_teasers;
	}
	
	/**
	 * @param data_teasers
	 */
	public ClusterData( ) {
		this._id = UUID.randomUUID();
	}

}
