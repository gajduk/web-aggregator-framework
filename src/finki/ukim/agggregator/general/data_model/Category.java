package finki.ukim.agggregator.general.data_model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * 
 * A Category in the Category Map.
 * 
 * Used to represents a logical group of related texts or data e.g. sports news in a news aggregator.
 * 
 * 
 * @author AndrejGajduk
 *
 */
/**
 * @author AndrejGajduk
 *
 */
public class Category {
	
	@JsonIgnore
	private List<Category> parents;
	
	private List<Category> children;
	
	private UUID _id;
	
	private String name;
	
	private List<DataTeaser> data_teasers; 
		
	public List<DataTeaser> getData_teasers() {
		return data_teasers;
	}

	public void setData_teasers(List<DataTeaser> data_teasers) {
		if ( children != null ) {
			System.out.println("SORRY CAN'T SET DATA TEASERS TO A NON LEAF CATEGORY");
			return;
		}
		else {
			this.data_teasers = data_teasers;
		}
	}

	public Category() {
		_id = UUID.randomUUID();
		//parents = new ArrayList<Category>();
		//children = new ArrayList<Category>();
		name = "";
		//data_teasers = new ArrayList<DataTeaser>();;
	}
	
	/**
	 * Creates a new category with all attributes explicitly stated.
	 * 
	 */
	public Category(List<Category> parents, List<Category> children, UUID _id , String name , List<DataTeaser> data_teasers ) {
		this.parents = parents;
		this.children = children;
		this._id = _id;
		this.name = name;
		this.data_teasers = data_teasers;
	}

	
	/**
	 * Creates a new Category with a given name and a random id, no parents or children.
	 * @param name
	 */
	public Category( String name ) {
		this.name = name;		
		_id = UUID.randomUUID();
		//parents = new ArrayList<Category>();
		//children = new ArrayList<Category>();
		//data_teasers = new ArrayList<DataTeaser>();;
	}

	
	/**
	 * Gets category with a given id.
	 * Checks this category, then recursively calls the same function on its children.
	 * @param id
	 * @return null if a category with that id can't be find.
	 */
	public Category getCategory( UUID id ) {
		if ( id.equals(this._id) ) {
			return this;
		}
		for ( Category category : children ) {
			Category possible_result = category.getCategory(id);
			if ( possible_result != null ) return possible_result;
		}
		return null;	
	}
	
	/**
	 * Gets category with a given name.
	 * Checks this category, then recursively calls the same function on its children.
	 * @param id
	 * @return null if a category with that name can't be find.
	 */
	public Category getCategory( String name ) {
		if ( name.equals(this.name) ) {
			return this;
		}
		for ( Category category : children ) {
			Category possible_result = category.getCategory(name);
			if ( possible_result != null ) return possible_result;
		}
		return null;	
	}

	/**
	 * @return the parents
	 */
	public List<Category> getParents() {
		return parents;
	}

	/**
	 * @param parents the parents to set
	 */
	public void setParents(List<Category> parents) {
		this.parents = parents;
	}

	/**
	 * @return the children
	 */
	public List<Category> getChildren() {
		return children;
	}

	/**
	 * @param children the children to set
	 */
	public void setChildren(List<Category> children) {
		if ( data_teasers != null ) {
			System.out.println("SORRY CAN'T ADD CHILDREN TO A LEAF CATEGORY");
			return;
		}
		this.children = children;
	}

	/**
	 * @return the _id
	 */
	public UUID get_id() {
		return _id;
	}
	
	public void addParent ( Category parent ) {
		if ( parents == null ) {
			parents = new ArrayList<Category>();
		}
		parents.add(parent);
	}
	
	/**
	 * Adds a new child category, if this category is not declared as LEAF.
	 * @param child
	 */
	public void addChild ( Category child ) {
		if ( data_teasers != null ) {
			System.out.println("SORRY CAN'T ADD CHILDREN TO A LEAF CATEGORY");
			return;
		}
		if ( children == null ) {
			children = new ArrayList<Category>();
		}
		children.add(child);
		child.addParent(this);
	}
	
	public void addParents ( List<Category> parents ) {
		if ( this.parents == null ) {
			this.parents = new ArrayList<Category>();
		}
		this.parents.addAll(parents);
	}
	
	/**
	 * Adds new children, if this category is not declared as LEAF.
	 * @param child
	 */
	public void addChildren ( List<Category> children) {
		if ( data_teasers != null ) {
			System.out.println("SORRY CAN'T ADD CHILDREN TO A LEAF CATEGORY");
			return;
		}
		if ( this.children == null ) {
			this.children = new ArrayList<Category>();
		}
		this.children.addAll(children);
		for ( Category child : children ) {
			child.addParent(this);
		}
	}
	
	/**
	 * Removes a parent specified by id, if such parent does not exist it does nothing.
	 * @param child
	 */
	public Category removeParentById ( UUID parent_id ) {
		if ( parents == null ) return null;
		Category parent_to_remove = findCategoryInListById(parents, parent_id);
		parents.remove(parent_to_remove);
		parent_to_remove.getChildren().remove(this);
		return parent_to_remove;
	}
	
	/**
	 * Removes a child specified by id, if such parent does not exist it does nothing.
	 * @param child
	 */
	public Category removeChildById ( UUID child_id ) {
		if ( children == null ) return null;
		Category child_to_remove = findCategoryInListById(children, child_id);
		children.remove(child_to_remove);
		return child_to_remove;
	}
	
	
	/**
	 * Gets the data teasers recursively from this category and all children categories.
	 * @return
	 */
	public List<DataTeaser> getDataTeasers () {
		if ( data_teasers != null ) return data_teasers;
		if ( children == null ) return null;
		//otherwise fetch the data teasers from all the children
		List<DataTeaser> result = new ArrayList<DataTeaser>();
		for ( Category child : children ) {
			result.addAll(child.getDataTeasers());
		}
		return result;
	}
	
	
	/**
	 * Adds a new data teaser only if the category is not declared as a leaf category. 
	 * @param data_teaser_to_add
	 */
	public void addDataTeaser( DataTeaser data_teaser_to_add ) {
		if ( children != null ) {
			System.out.println("SORRY CAN'T ADD DATA TEASERS TO A NON LEAF CATEGORY");
			return;
		}
		else {
			if ( data_teasers == null ) {
				data_teasers = new ArrayList<DataTeaser>();
			}
			data_teasers.add(data_teaser_to_add);
		}
	}
	
	/**
	 * Adds a list of data teasers only if the category is not declared as a leaf category. 
	 * @param data_teaser_to_add
	 */
	public void addDataTeasers( List<DataTeaser> data_teasers_to_add ) {
		if ( children != null ) {
			System.out.println("SORRY CAN'T ADD DATA TEASERS TO A NON LEAF CATEGORY");
			return;
		}
		else {
			if ( data_teasers == null ) {
				data_teasers = new ArrayList<DataTeaser>();
			}
			data_teasers.addAll(data_teasers_to_add);
		}
	}
	
	/**
	 * Removes a data teaser by id, if it exists in any of the children. 
	 * @param data_teaser_to_add
	 */
	public DataTeaser removeDataTeaser ( UUID data_teaser_id ) {
		if ( children != null ) {
			for ( Category child : children ) {
				DataTeaser removed = child.removeDataTeaser(data_teaser_id);
				if ( removed != null ) return removed;
			}
			return null;
		}
		//this class has no children check in the immediate data_teasers
		DataTeaser result = null;
		if ( data_teasers == null ) return result;
		for ( DataTeaser data_teaser : data_teasers ) {
			if ( data_teaser.getData_id().equals(data_teaser_id) ) {
				result = data_teaser;
				break;
			}
		}
		data_teasers.remove(result);
		return result;
	}
	
	private Category findCategoryInListById ( List<Category> list_categories , UUID id ) {
		Category result = null;
		for ( Category category : list_categories ) {
			if ( category.get_id().equals(id) ) {
				result = category; break;
			}
		}
		return result;
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
		
	@Override
	public String toString () {
		return name;
	}

	/**
	 * @param _id the _id to set
	 */
	public void set_id(UUID _id) {
		this._id = _id;
	}
	
	
	
}
