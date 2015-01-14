package finki.ukim.agggregator.general.data_model;

/**
 * A helper class that an attribute name and its value.
 * Useful for sorting both names and values simultaneously. 
 * 
 * @author AndrejGajduk
 *
 */
public class AttributeValuePair implements Comparable<AttributeValuePair>{
	public String attribute_name;
	public Double value;
	
	public AttributeValuePair(String attribute_name, Double value) {
		this.attribute_name = attribute_name;
		this.value = value;
	}

	@Override
	public int compareTo(AttributeValuePair arg0) {
		if ( value>arg0.value ) return -1;
		else if ( value==arg0.value ) return 0;
		else return 1;
	}
}
