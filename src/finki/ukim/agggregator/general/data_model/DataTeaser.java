package finki.ukim.agggregator.general.data_model;

import java.util.Date;
import java.util.UUID;

/**
 * 
 * Teaser represents a part of the data to be used in lists and tables, where displaying the entire data object would take up too much visual space.
 * 
 * @author AndrejGajduk
 *
 */
public class DataTeaser {
	
	private UUID data_id;
	
	private Date accessed_at;
	
	private String title;

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
	 * @param data_id
	 * @param accessed_at
	 * @param title
	 */
	public DataTeaser(UUID data_id, Date accessed_at, String title) {
		this.data_id = data_id;
		this.accessed_at = accessed_at;
		this.title = title;
	}

	/**
	 * 
	 */
	public DataTeaser() {
	}
	
	

}
