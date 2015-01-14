package finki.ukim.agggregator.general.data_model;

import java.util.UUID;

/**
 * Defines a problem as a separate and different area where this framework would be applied.
 * For example in our work on the scholarship aggregator, we should build a Problem class that corresponds to the area we are working on i.e. scholarship_problem
 * 
 * This allows users to work on multiple problems simultaneously and reuse some of their code.
 * 
 * this class should not be extended
 * @author Andrej Gajduk
 *
 */
public class Problem {
	
	
	/**
	 * an id to be used for the database
	 */
	protected UUID _id;
	
	/**
	 * the name of the problem that is being researched, this should be used in establishing the Mongo database schema and file hierarchy,
	 * must be unique among the problems 
	 */
	private String name;
	
	/**
	 * contains a brief description about the problem, what is the goal&some more specific information about the area of research 
	 */
	private String description;

	public Problem () {}
	
	/**
	 * @param name
	 * @param description
	 */
	public Problem(String name, String description) {
		_id = UUID.randomUUID();
		this.name = name;
		this.description = description;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	

}
