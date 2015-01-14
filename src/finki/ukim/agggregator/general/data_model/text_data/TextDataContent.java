package finki.ukim.agggregator.general.data_model.text_data;

import java.util.ArrayList;
import java.util.List;


/**
 * A bunch of text content, identified by different descriptions.
 * 
 * @author AndrejGajduk
 *
 */
public class TextDataContent {
	
	String description;
	
	String content;
	
	List<TextDataContent> contents;
	
	public TextDataContent () {}	
	
	public TextDataContent( String description ) {
		this.description = description;
		contents = new ArrayList<TextDataContent>();
		this.content = "";
	}
	
	public TextDataContent ( String description , String content ) {
		this.description = description;
		this.content = content;
		contents = new ArrayList<TextDataContent>();
	}
	
	public TextDataContent ( String description , List<TextDataContent> contents ) {
		this.description = description;
		this.contents = contents;
		this.content = "";
	}
	
	public void addContent ( TextDataContent content ) {
		removeContentByDescription(content.getDescription());
		contents.add(content);
	}
	
	public TextDataContent getContentForDescription ( String description ) {
		if ( description.equals(this.description) ) return this;
		for ( TextDataContent content : contents ) {
			TextDataContent found = content.getContentForDescription(description);
			if ( found != null ) return found;
		}
		return null;
	}
	
	public TextDataContent removeContentByDescription ( String description ) {
		if ( description.equals(this.description) ) return this;
		TextDataContent to_be_removed = null;
		for ( TextDataContent content : contents ) {
			TextDataContent removed = content.removeContentByDescription(description);
			if ( removed == content ) {
				to_be_removed = content; break;
			}
		}
		contents.remove(to_be_removed);
		return to_be_removed;
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

	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @param content the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * @return the contents
	 */
	public List<TextDataContent> getContents() {
		return contents;
	}

	/**
	 * @param contents the contents to set
	 */
	public void setContents(List<TextDataContent> contents) {
		this.contents = contents;
	}
	
	@Override
	public String toString() {
		return toString(false);
	}

	public String toString(boolean include_description) {
		String result = "";
		if ( include_description ) result += description+" : ";
		if ( content.length() > 0 ) {
			result += content+" ; ";
		}
		else {
			result += " { ";
			for ( TextDataContent content : contents ) {
				result += content.toString(include_description);
			}
			result += " } ";
		}
		return result;
		
	}
	
}