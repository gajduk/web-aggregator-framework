package finki.ukim.agggregator.general.data_model;

import java.util.UUID;

/**
 * A simply teaser for a cluster that holds the cluster name and id.
 * 
 * @author AndrejGajduk
 *
 */
public class ClusterTeaser {

	
	private UUID cluster_id;
	
	private String cluster_name;
	
	

	/**
	 * @return the cluster_id
	 */
	public UUID getCluster_id() {
		return cluster_id;
	}



	/**
	 * @param cluster_id the cluster_id to set
	 */
	public void setCluster_id(UUID cluster_id) {
		this.cluster_id = cluster_id;
	}



	/**
	 * @return the cluster_name
	 */
	public String getCluster_name() {
		return cluster_name;
	}



	/**
	 * @param cluster_name the cluster_name to set
	 */
	public void setCluster_name(String cluster_name) {
		this.cluster_name = cluster_name;
	}

	/**
	 * @param cluster_id
	 * @param cluster_name
	 */
	public ClusterTeaser( String cluster_name) {
		this.cluster_id = UUID.randomUUID();
		this.cluster_name = cluster_name;
	}

	/**
	 * @param cluster_id
	 * @param cluster_name
	 */
	public ClusterTeaser(UUID cluster_id, String cluster_name) {
		this.cluster_id = cluster_id;
		this.cluster_name = cluster_name;
	}
	
	public ClusterTeaser() {}
	
	
	
}
