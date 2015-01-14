package finki.ukim.agggregator.general.data_model;

import java.util.UUID;

/**
 * A source is where you get your data from e.g. a webpage and rss feed etc.
 * 
 * It has a name, description and an URL link.
 * 
 * @author AndrejGajduk
 *
 */
public class Source {
	
		String description;
		
		String name;
		
		UUID _id;
		
		String link;

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
		 * @return the link
		 */
		public String getLink() {
			return link;
		}

		/**
		 * @param link the link to set
		 */
		public void setLink(String link) {
			this.link = link;
		}

		/**
		 * @param description
		 * @param name
		 * @param _id
		 * @param link
		 */
		public Source(String description, String name, UUID _id, String link) {
			this.description = description;
			this.name = name;
			this._id = _id;
			this.link = link;
		}

		/**
		 * @param description
		 * @param name
		 * @param link
		 */
		public Source(String description, String name, String link) {
			this.description = description;
			this.name = name;
			this.link = link;
			this._id = UUID.randomUUID();
		}
		
		public Source() {
			
		}
		

}
