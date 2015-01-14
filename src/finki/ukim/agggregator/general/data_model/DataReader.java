package finki.ukim.agggregator.general.data_model;

import java.util.ArrayList;

/**
 * Abstract stub class for all ways of acquiring data for a given problem,
 * Usually implemented as a web crawler, that fetches a html page then extract the data using regexes
 * @author Andrej Gajduk
 */
/**
 * @author AndrejGajduk
 *
 */
/**
 * @author AndrejGajduk
 *
 */
public abstract class DataReader {
	/**
	 * source, more data readers can have the same source
	 */
	private Source src;
	
	/**
	 * @param src
	 */
	public DataReader() {
	}
	
	/**
	 * @param src
	 */
	public DataReader(Source src) {
		this.src = src;
	}

	/**
	 * @return the src
	 */
	public Source getSrc() {
		return src;
	}

	/**
	 * @param src the src to set
	 */
	public void setSrc(Source src) {
		this.src = src;
	}

	/**
	 * DEPRACATED: TOO MUCH DATA TO BE READ AT ONCE
	 * Use next and hasNext construct instead
	 * @return - a list of Data objects that were read, an empty list if no data can be found
	 */
	public abstract ArrayList<Data> getAllData(); 
	
	/**
	 * Any required initialization should go here.
	 */
	public abstract void init( int max_number_of_documents );
	
	/**
	 * gets the next piece of data from the reader
	 * @return Data read
	 */
	public abstract Data next();
	
	/**
	 * checks if there is more data that can be read with the next method
	 * @return true - if more data can be read
	 */
	public abstract boolean hasNext();
	
	
	/**
	 * The total number of data objects returned
	 * @return
	 */
	public abstract int totalData();

}
