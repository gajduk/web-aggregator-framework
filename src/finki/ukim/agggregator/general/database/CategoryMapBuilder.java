package finki.ukim.agggregator.general.database;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import finki.ukim.agggregator.general.data_model.Category;
import finki.ukim.agggregator.general.data_model.CategoryMap;
import finki.ukim.agggregator.general.data_model.Data;
import finki.ukim.agggregator.general.data_model.Feature;
import finki.ukim.agggregator.general.data_model.FeatureExtractorData;

/**
 * A helper class for building the category Map.
 * @author AndrejGajduk
 *
 */
public class CategoryMapBuilder {
	
	public static final String sources_category_label = "sources";
	public static final String clusters_category_label = "clusters";
	
	public CategoryMap addClassToCategoryMap ( CategoryMap category_map , List<Data> data , String class_information_label , boolean add_teasers ) {
		Category root_classifier_category = new Category(class_information_label);
		Map<String,Category> classes = new HashMap<String,Category>();
		for ( Data d : data ) {
			String class_name = d.getCategoryInformationField(class_information_label);
			if ( class_name.length() != 0 ) {
				Category clas = classes.get(class_name);
				if ( clas == null ) {
					clas = new Category(class_name);
					root_classifier_category.addChild(clas);
					classes.put(class_name,clas);
				}
				if ( add_teasers ) clas.addDataTeaser(d.getTeaser());
			}
		}
		category_map.getRoot_category().addChild(root_classifier_category);
		return category_map;
	}
	
	public CategoryMap addClassToCategoryMap ( CategoryMap category_map , List<Data> data , String class_information_label , boolean add_teasers , String parent_category) {
		Category root_classifier_category = new Category(class_information_label);
		Map<String,Category> classes = new HashMap<String,Category>();
		for ( Data d : data ) {
			String class_name = d.getCategoryInformationField(class_information_label);
			if ( class_name.length() != 0 ) {
				Category clas = classes.get(class_name);
				if ( clas == null ) {
					clas = new Category(class_name);
					root_classifier_category.addChild(clas);
					classes.put(class_name,clas);
				}
				if ( add_teasers ) clas.addDataTeaser(d.getTeaser());
			}
		}
		category_map.getCategory(parent_category).addChild(root_classifier_category);
		return category_map;
	}

	public CategoryMap addSourcesToCategoryMap ( CategoryMap category_map , List<Data> data , boolean add_teasers  ) {
		Category root_source_category = new Category(sources_category_label);
		Map<String,Category> classes = new HashMap<String,Category>();
		for ( Data d : data ) {
			String source_name = d.getSource().getName();
			Category src_category = classes.get(source_name);
			if ( src_category == null ) {
				src_category = new Category(source_name);
				root_source_category.addChild(src_category);
				classes.put(source_name,src_category);
			}
			if ( add_teasers ) src_category.addDataTeaser(d.getTeaser());
			d.setCategoryInformationField(sources_category_label, source_name);
		}
		category_map.getRoot_category().addChild(root_source_category);
		return category_map;
	}
	
	public CategoryMap addClusterToCategoryMap ( CategoryMap category_map , List<Data> data , boolean add_teasers  ) {
		Category root_cluster_category = new Category(clusters_category_label);
		Map<UUID,Category> clusters = new HashMap<UUID,Category>();
		for ( Data d : data ) {
			UUID cluster_id = d.getCluster_teaser().getCluster_id();
			Category cluster = clusters.get(cluster_id);
			if ( cluster == null ) {
				cluster = new Category(d.getTitle());
				root_cluster_category.addChild(cluster);
				clusters.put(cluster_id, cluster);
			}
			if ( add_teasers ) cluster.addDataTeaser(d.getTeaser());
			d.setCategoryInformationField(clusters_category_label, cluster.getName());
		}
		category_map.getRoot_category().addChild(root_cluster_category);
		return category_map;
	}

	public CategoryMap addClusterToCategoryMap ( CategoryMap category_map , FeatureExtractorData f_data , List<Integer> cluster_ids , boolean add_teasers , DatabaseManager dbmgt , String problem_name ) {
		Category root_cluster_category = new Category(clusters_category_label);
		Map<Integer,Category> clusters = new HashMap<Integer,Category>();
		int i = 0;
		for ( Feature f : f_data.getFeatures() ) {
			int cluster_id = cluster_ids.get(i++);
			Category cluster = clusters.get(cluster_id);
			if ( cluster == null ) {
				String data_title = "";
				DBCollection coll = dbmgt.getCollection(problem_name+DatabaseManager.data_collection);
				DBObject query = new BasicDBObject("_id",f.getData_id().toString());
				DBObject fields_to_retrieve = new BasicDBObject("title",1);
				DBObject db_title = coll.findOne(query, fields_to_retrieve);
				data_title = db_title.get("title").toString();
				cluster = new Category(data_title);
				root_cluster_category.addChild(cluster);
				clusters.put(cluster_id, cluster);
			}
//			if ( add_teasers ) cluster.addDataTeaser(d.getTeaser());
			dbmgt.setClassifierInformationToData(problem_name,f.getData_id(),clusters_category_label,cluster.getName());
		}
		category_map.getRoot_category().addChild(root_cluster_category);
		return category_map;
	}
	
	
	
}
