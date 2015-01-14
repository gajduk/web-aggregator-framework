package finki.ukim.agggregator.general.data_model.text_data;

import java.util.Date;
import java.util.Map;

import finki.ukim.agggregator.general.data_model.ClusterTeaser;
import finki.ukim.agggregator.general.data_model.Data;
import finki.ukim.agggregator.general.data_model.Source;

/**
 * Represents all the textual data which should be processed and stored in the Database
 * 
 * Users are encouraged to extend this class for their own purposes
 * but please be carefull when overriding some of the key methods.
 * @author Andrej Gajduk
 *
 */
public class TextData extends Data {
	
	/**
	 * the description of the main content for this text
	 */
	private static final String main_content_description = "main";
	
	/**
	 * The entire contents of the text document this Data represents
	 * some text documents have some information tagged and this should be somehow imported in this contents
	 * e.g
	 * 
	 * In a scholarship web page you would normally see a table filled with the following info
	 *  Purpose: Study
	 *	Level: 	 Master,  PhD
	 *	Quota: 	Decided by the community
	 *	Application deadline: 	Summer 
	 *
	 * because this info can differ to a large extent from a web page to a web page, even within the same problem it's practically not good
	 * to have separate attributes for all such data, instead put them in a map file where the keys tell us what the value represents
	 * e.g. key:Application deadline - value : Summer.
	 * This way if we want the plain text data in order to lets say to compare the data from two different web pages in which the one gives some of the info separated whilst the other one does not
	 * which is a very common case, we should only fetch the values and concat them together
	 * and when we are comparing, classifying two Data objects that both have a value for some attribute like Application deadline we can easily access those values 
	 */
	private TextDataContent contents;
	
	public TextData () {
	}
	
	/**
	 * Initializes the content and the classifier information as empty, and sets all other fields fields required in the Data class,
	 * you can always set the classifier information and the contents later via the addXXXX methods
	 * @param title
	 * @param source
	 * @param accessed_a
	 */
	public TextData ( String title  , Source source , Date accessed_at , Date valid_until ) {
		super(title,source,accessed_at,valid_until);
		this.contents = new TextDataContent(title);
	}
	
	/**
	 * Initializes the content as empty and sets all other fields
	 * @param title
	 * @param source
	 * @param accessed_at
	 * @param classifier_information
	 */
	public TextData ( String title, Source source , Date accessed_at , Date valid_until , Map<String,String> classifier_information  , ClusterTeaser cluster_teaser ) {
		super(title,source,accessed_at,valid_until,classifier_information,cluster_teaser);
		this.contents = new TextDataContent(title);
	}
	
	/**
	 * Sets all the fields appropriately
	 * You can always update the classifier information and the contents later via the addXXXX methods
	 * @param title
	 * @param source
	 * @param accessed_at
	 * @param classifier_information
	 * @param contents
	 */
	public TextData ( String title , Source source , Date accessed_at  , Date valid_until , Map<String,String> classifier_information , ClusterTeaser cluster_teaser , TextDataContent contents) {
		super(title,source,accessed_at,valid_until,classifier_information,cluster_teaser);
		this.contents = contents;
	}
	
	/**
	 * Initializes the classifier information to an empty map, and sets all other fields
     * you can always set the classifier information and the contents later via the addXXXX methods
	 * @param title
	 * @param source
	 * @param accessed_at
	 * @param contents
	 */
	public TextData ( String title  , Source source , Date accessed_at  , Date valid_until , TextDataContent contents) {
		super(title,source,accessed_at,valid_until);
		this.contents = contents;
	}
	
	/**
	 * Adds a new key:value pairs of description:content to the map, if a field with that key (description) already exists it is replaced
	 * @param classifier_name
	 * @param class_attribute_value
	 */
	public void addContentsField ( TextDataContent content ) {
		this.contents.addContent(content);
	}
	
	/**
	 * Removes an existing key:value pairs of description:content to the map, as specified by the key
	 * if that key does not exist it does nothing
	 * @param description - the description which defines the key:value pair we wish to remove
	 */
	public void removeContentsField ( String description ) {
		this.contents.removeContentByDescription(description);
	}
	
	/**
	 * Gets the field from the contents that corresponds to the given key (content description)
	 * @param classifier_name
	 * @return the value for that content description or an empty string if such key does not occur in the map
	 */
	public TextDataContent getContentsField ( String description ) {
		return contents.getContentForDescription(description);
	}

	/**
	 * @return the contents
	 */
	public TextDataContent getContents() {
		return contents;
	}

	/**
	 * @param contents the contents to set
	 */
	public void setContents(TextDataContent contents) {
		this.contents = contents;
	}

	/**
	 * Gets the main content of this text data, it is the most relevant and (obemna) information retrieved considering this text file
	 * this is same as getContentsField(TextData.main_content_description)
	 * @return the main content 
	 */
	public TextDataContent getMainContent ( ) {
		return contents.getContentForDescription(main_content_description);
	}
	
	/**
	 * Sets the main content of this text data, it is the most relevant and (obemna) information retrieved considering this text file
	 * this is same as addContentsField(TextData.main_content_description)
	 * @param content - the content to set
	 */
	public void setMainContent ( TextDataContent content ) {
		content.setDescription(main_content_description);
		contents.addContent(content);
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString()+contents.toString(true);
	}
	
}
