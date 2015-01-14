package finki.ukim.agggregator.general.data_model;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class CategoryMap {
	
	private UUID _id;
	
	private Category root_category;

	public CategoryMap() {
		root_category = new Category("root");
		_id = UUID.randomUUID();
	}
	
	public CategoryMap( Category root_category ) {
		this.root_category = root_category;
		_id = UUID.randomUUID();
	}
	
	public Category getCategory ( UUID category_id ) {
		return root_category.getCategory(category_id);
	}
	
	public Category getCategory ( String category_name ) {
		return root_category.getCategory(category_name);
	}
	
	public Category removeCategoryById ( UUID category_id , boolean cascade ) {
		Category category_to_remove = root_category.getCategory(category_id);
		for ( Category child : category_to_remove.getChildren() ) {
			if ( cascade ) {
				removeCategoryById(child.get_id(), cascade);
			}
			else {
				child.addParents(category_to_remove.getParents());
				child.removeParentById(category_to_remove.get_id());
			}
		}
		for ( Category parent : category_to_remove.getParents() ) {
			parent.removeChildById(category_to_remove.get_id());
		}
		return category_to_remove;
	}
	
	public void addCategory ( Category category , List<UUID> parents_ids ) {
		for ( UUID parent_id : parents_ids ) {
			Category parent = getCategory(parent_id);
			if ( parent != null ) {
				parent.addChild(category);
			}
		}
	}

	/**
	 * @return the root_category
	 */
	public Category getRoot_category() {
		return root_category;
	}

	/**
	 * @param root_category the root_category to set
	 */
	public void setRoot_category(Category root_category) {
		this.root_category = root_category;
	}
	
	public static CategoryMap buildSampleTestCategoryMap ( boolean add_teasers ) {
		Category root_category = new Category("root");
		Category M1 = new Category("M1");
		Category M2 = new Category("M2");
		Category M3 = new Category("M2");
		Category N1 = new Category("N1");
		Category N2 = new Category("N2");
		Category m1 = new Category("m1");
		Category m2 = new Category("m2");
		Category m3 = new Category("m3");
		Category m4 = new Category("m4");
		Category n1 = new Category("n1");
		Category n2 = new Category("n2");
		Category n3 = new Category("n3");
		root_category.addChild(M1);root_category.addChild(M2);root_category.addChild(M3);root_category.addChild(m1);
		root_category.addChild(N1);root_category.addChild(N2);
		M1.addChild(m1);M1.addChild(m3);
		M2.addChild(m3);M2.addChild(m2);M2.addChild(m4);
		N1.addChild(n1);
		N2.addChild(n2);N2.addChild(n3);	
		//use this to add some DataTeasers to the CategoryMap
		if ( add_teasers ) {
			m1.addDataTeaser(new DataTeaser(UUID.randomUUID(), new Date(), "m1 data teaser"));
			m2.addDataTeaser(new DataTeaser(UUID.randomUUID(), new Date(), "m2 data teaser"));
			m2.addDataTeaser(new DataTeaser(UUID.randomUUID(), new Date(), "m2 second data teaser"));
			m3.addDataTeaser(new DataTeaser(UUID.randomUUID(), new Date(), "m3 data teaser"));
			n1.addDataTeaser(new DataTeaser(UUID.randomUUID(), new Date(), "n1 data teaser"));
		}
		
		return new CategoryMap(root_category);
	}

	public UUID get_id() {
		return _id;
	}

	public void set_id(UUID _id) {
		this._id = _id;
	}
	
	public List<String> getAllValuesForCategory ( String category_name ) {
		List<String> result = new ArrayList<String>();
		Category the_category = getCategory(category_name);
		if ( the_category.getChildren() != null ) {
			for ( Category c : the_category.getChildren()  ) {
				if ( ! result.contains(c.getName()) )
					result.add(c.getName());			
			}
		}
		return result;
	}
	
	public List<String> getAllValuesForCategory ( UUID category_id ) {
		List<String> result = new ArrayList<String>();
		Category the_category = getCategory(category_id);
		if ( the_category.getChildren() != null ) {
			for ( Category c : the_category.getChildren()  ) {
				if ( ! result.contains(c.getName()) )
					result.add(c.getName());			
			}
		}
		return result;
	}
	
}
