package finki.ukim.agggregator.scholarships;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import weka.classifiers.Classifier;
import weka.classifiers.trees.J48;
import weka.clusterers.Clusterer;
import weka.clusterers.SimpleKMeans;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.UnsupportedClassTypeException;
import finki.ukim.agggregator.general.data_model.Category;
import finki.ukim.agggregator.general.data_model.CategoryMap;
import finki.ukim.agggregator.general.data_model.ClusterTeaser;
import finki.ukim.agggregator.general.data_model.Data;
import finki.ukim.agggregator.general.data_model.DataReader;
import finki.ukim.agggregator.general.data_model.Feature;
import finki.ukim.agggregator.general.data_model.FeatureExtractorData;
import finki.ukim.agggregator.general.data_model.Problem;
import finki.ukim.agggregator.general.database.CategoryMapBuilder;
import finki.ukim.agggregator.general.database.DatabaseManager;
import finki.ukim.agggregator.general.database.FeatureExtractorDataHelper;
import finki.ukim.agggregator.general.feature_extractors.FeatureExtractor;
import finki.ukim.agggregator.general.feature_extractors.TermDocumentFrequency;
import finki.ukim.agggregator.scholarships.data_readers.ScholarshipLinksDataReader;
import finki.ukim.agggregator.scholarships.data_readers.ScholarshipPortalDataReader;
import finki.ukim.agggregator.scholarships.data_readers.ScholarshipsPositionsDataReader;


public class Main {
	
	public static final String problem_name = "scholarships";
	
	public static void main(String[] args) throws Exception {
		initialBatchJob();
		
		incrementalBatchJob();
		
//		runSampleQueries();
				
	}
	
	@SuppressWarnings("unused")
	private static void runSampleQueries() {
		allCategories();
		allDataForSearch();	
	}

	private static void allDataForSearch() {
		DatabaseManager dbmgt = DatabaseManager.getInstance();
		System.out.println("The ids of all the data for the following search (sorted by time):");
		Map<String,String> query = new HashMap<String,String>();
		System.out.println("level=Bachelor");
		query.put("level", "Bachelor");
		//System.out.println("source=Scholarship portal");
		//query.put("source", "Scholarship portal");
		List<Data> data = dbmgt.getDataForQueryForProblem(problem_name,query,"time");
		for ( Data d : data ) {
			System.out.println("id:"+d.get_id());
		}
		System.out.println("The ids of all the data for the following search more complex (sorted by time):");
		Map<String,String> q = new HashMap<String,String>();
		System.out.println("level=Master");
		q.put("level", "Master");
		System.out.println("sources=Scholarship portal");
		query.put("sources", "Scholarship portal");
		data = dbmgt.getDataForQueryForProblem(problem_name,query,"time");
		for ( Data d : data ) {
			System.out.println("id:"+d.get_id());
		}
		System.out.println("The ids of all the data for the previous search now sorted by times of access:");
		data = dbmgt.getDataForQueryForProblem(problem_name,query,"counter");
		for ( Data d : data ) {
			System.out.println("id:"+d.get_id()+" counter:"+d.getCounter());
		}
		System.out.println("A sample of the data retrived from the last query");
		System.out.println(
				dbmgt.getDataForProblem(problem_name, data.get(1).get_id()).toString());
		System.out.println("The same query yet again:");
		data = dbmgt.getDataForQueryForProblem(problem_name,query,"counter");
		for ( Data d : data ) {
			System.out.println("id:"+d.get_id()+" counter:"+d.getCounter());
		}
		
	}
	
	private static void allCategories() {
		DatabaseManager dbmgt = DatabaseManager.getInstance();
		CategoryMap category_map = dbmgt.getCategoryMapForProblemWithoutDataTeasers(problem_name);
		System.out.println("In your search you can specify the following categories:");
		Category root = category_map.getRoot_category();
		if ( root.getChildren() != null ) {
			for ( Category c : root.getChildren() ) {
				printCategory(c);
				System.out.println();
			}
		}
	}

	private static void printCategory(Category category ) {
		System.out.print(category.getName());
		if ( category.getChildren() != null ) {
			System.out.print(": [");
			for ( Category c : category.getChildren() ) {
				printCategory(c);
				System.out.print(",");
			}
			System.out.print("]");
		}
	}

	private static void incrementalBatchJob() throws Exception {
		DatabaseManager dbmgt = DatabaseManager.getInstance();
		CategoryMap category_map = dbmgt.getCategoryMapForProblemWithoutDataTeasers(problem_name);
		
		List<Data> data = new ArrayList<Data>();
		//load the data from the readers
		data.addAll(readDataFromReader(new ScholarshipLinksDataReader(),dbmgt,problem_name,1,false,true));
//		data.addAll(readDataFromReader(new ScholarshipPortalDataReader(),dbmgt,problem_name,5,true,true));
//		data.addAll(readDataFromReader(new ScholarshipsPositionsDataReader(),dbmgt,problem_name,5,true,true));
		if ( data.size() == 0 ) return;
		//feature extraction
		String feature_extractor_name = "TermDocumentFrequency";
		FeatureExtractorData f_data = performFeatureExtraction(TermDocumentFrequency.getInstance(0,5),feature_extractor_name,data);
		//classification
		String classifier_name = "level";
		Classifier  classifier = dbmgt.getClassifierForProblem(classifier_name, problem_name);
		performClassification(data, classifier, f_data, classifier_name, dbmgt.getCategoryMapForProblemWithoutDataTeasers(problem_name));
		//now we can safely store the data in db
		CategoryMapBuilder helper = new CategoryMapBuilder();
		helper.addSourcesToCategoryMap(category_map, data, false);
		dbmgt.addDataToProblem(data, problem_name);
		//clustering, 
		//here we need to load the features for the other data already in the db
		FeatureExtractorData ff_data = dbmgt.getFeatureExtractorDataForProblem(feature_extractor_name, problem_name);
		List<Feature> all_features = ff_data.getFeatures();
		all_features.addAll(f_data.getFeatures());
		ff_data.setFeatures(all_features);
		SimpleKMeans clusterer = new SimpleKMeans();
		clusterer.setNumClusters(3);
		List<Integer> cluster_ids = performClusterization(clusterer, ff_data.getInstances());
		new CategoryMapBuilder().addClusterToCategoryMap(category_map, ff_data, cluster_ids, false, dbmgt, problem_name);
		//save everything to the database
		dbmgt.addFeaturesToFeatureExtractorDataForProblem(f_data.getFeatures(), ff_data.get_id(), problem_name);
//		dbmgt.updateCategoryMapWithDataTeasersForProblem(category_map, problem_name);
		dbmgt.saveCategoryMapForProblemWithoutDataTeasers(category_map, problem_name);
	}

	private static void initialBatchJob() throws Exception {
		DatabaseManager dbmgt = DatabaseManager.getInstance();
		dbmgt.cleanUp();
		dbmgt.createNewProblem(new Problem(problem_name,problem_name));
		List<Data> data = new ArrayList<Data>();
		//load the data from the readers
		data.addAll(readDataFromReader(new ScholarshipLinksDataReader(),dbmgt,problem_name,5,true,true));
		data.addAll(readDataFromReader(new ScholarshipPortalDataReader(),dbmgt,problem_name,5,true,true));
		data.addAll(readDataFromReader(new ScholarshipsPositionsDataReader(),dbmgt,problem_name,5,true,true));
		//feature extraction
		String feature_extractor_name = "TermDocumentFrequency";
		FeatureExtractorData f_data = performFeatureExtraction(TermDocumentFrequency.getInstance(0,5),feature_extractor_name,data);
		//classification
		Classifier  classifier = new J48();
		String classifier_name = "level";
		classifier = buildClassifierOnClassLabel(data,classifier,dbmgt,classifier_name,f_data);
		data = performClassification(data, classifier, f_data, classifier_name, null);
		//clustering
		SimpleKMeans clusterer = new SimpleKMeans();
		clusterer.setNumClusters(3);
		data = performClusterization(data,clusterer,f_data,dbmgt,true);
		//building&saving the category map
		CategoryMap category_map = new CategoryMap();
		CategoryMapBuilder helper = new CategoryMapBuilder();
		helper.addClassToCategoryMap(category_map, data, classifier_name,false);
		helper.addClusterToCategoryMap(category_map, data,false);
		helper.addSourcesToCategoryMap(category_map, data,false);
		//save everything to the database
//		dbmgt.saveCategoryMapForProblemIncludingDataTeasers(category_map,problem_name,false);
		dbmgt.saveCategoryMapForProblemWithoutDataTeasers(category_map, problem_name);
		dbmgt.addClassifierToProblem(classifier, classifier_name, problem_name);
		dbmgt.addFeatureExtractorDataToProblem(f_data, problem_name);
		dbmgt.addDataToProblem(data, problem_name);
		
	}

	private static Classifier buildClassifierOnClassLabel ( List<Data> data, Classifier classifier, DatabaseManager dbmgt, String class_label, FeatureExtractorData feature_extractor_data ) {
		Instances dataset = new FeatureExtractorDataHelper().getInstancesAndAddANewClassAttribute(problem_name, feature_extractor_data, data, class_label, true, null);
		try {
			classifier.buildClassifier(dataset);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return classifier;
	}
	
	@SuppressWarnings("unused")
	private static List<String> performClassification( Classifier classifier , Instances dataset ) {
		List<String> result = new ArrayList<String>();
		for ( int i = dataset.numInstances()-1 ; i >= 0 ; --i) {
			if ( ! dataset.instance(i).classIsMissing() ) continue;
			double class_value = 0;
			try {
				class_value = classifier.classifyInstance(dataset.instance(i));
			} catch (Exception e) {
				e.printStackTrace();
			}
			String class_name = dataset.classAttribute().value((int)class_value);
			result.add(class_name);
		}
		return result;
	}

	private static List<Data> performClassification( List<Data> data , Classifier classifier , FeatureExtractorData feature_extractor_data , String class_label , CategoryMap category_map ) {
		FeatureExtractorDataHelper helper = new FeatureExtractorDataHelper();
		System.out.println();
		Instances dataset = null;
		if ( category_map != null )
			dataset = helper.getInstancesAndAddANewClassAttribute(problem_name, feature_extractor_data, data, class_label, false,helper.getAllValuesForCategoryFromCategoryMap(category_map, class_label));
		else 
			dataset = helper.getInstancesAndAddANewClassAttribute(problem_name, feature_extractor_data, data, class_label, false,null);
		for ( int i = dataset.numInstances()-1 ; i >= 0 ; --i) {
			Feature feature = feature_extractor_data.getFeatures().get(i);
			Data data_instance = data.get(data.indexOf(new Data(feature.getData_id())));
			if ( ! dataset.instance(i).classIsMissing() ) continue;
			double class_value = 0;
			try {
				class_value = classifier.classifyInstance(dataset.instance(i));
			} catch (Exception e) {
				e.printStackTrace();
			}
			String class_name = dataset.classAttribute().value((int)class_value);
			data_instance.setCategoryInformationField(class_label, class_name);
		}
		return data;
	}
	
	private static List<Data> performClusterization( List<Data> data , Clusterer clusterer , FeatureExtractorData feature_extractor_data , DatabaseManager dbmgt , boolean set_cluster_teaser) {
		Instances dataset = feature_extractor_data.getInstances();
		Map<Integer,ClusterTeaser> cluster_teasers = new HashMap<Integer,ClusterTeaser>();
		try {
			clusterer.buildClusterer(dataset);
		} catch (Exception e) {
			e.printStackTrace();
		}
		for ( Feature f : feature_extractor_data.getFeatures() ) {
			try {
				Data data_instance = data.get(data.indexOf(new Data(f.getData_id())));
				Instance instance = f.getInstance();
				instance.setDataset(dataset);
				int cluster_number = clusterer.clusterInstance(instance);
				ClusterTeaser cluster_teaser = cluster_teasers.get(cluster_number);
				if ( cluster_teaser == null ) {
					cluster_teaser = new ClusterTeaser(UUID.randomUUID(),data_instance.getTitle());
					cluster_teasers.put(cluster_number, cluster_teaser);
				}
				data_instance.setCluster_teaser(cluster_teaser);
			//	dbmgt.setClusterTeaserForDataForProblem(cluster_teaser,data_instance.get_id(),problem_name);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return data;
	}
	
	private static List<Integer> performClusterization( Clusterer clusterer , Instances dataset ) {
		List<Integer> result = new ArrayList<Integer>();
		try {
			clusterer.buildClusterer(dataset);
		for ( int i = 0 ; i < dataset.numInstances() ; ++i ) {
			result.add(clusterer.clusterInstance(dataset.instance(i)));
		}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static FeatureExtractorData performFeatureExtraction ( FeatureExtractor feature_extractor , String feature_extractor_name , DatabaseManager db , String problem_name  ) {
		List<Data> data = db.getDataForProblem(problem_name);
		FeatureExtractorData feature_extractor_data = new FeatureExtractorData(feature_extractor_name); 
		try {
			feature_extractor_data.setFeatures(feature_extractor.getFeatures(data));
		} catch (UnsupportedClassTypeException e) {
			e.printStackTrace();
		}
		feature_extractor_data.setDataset(feature_extractor.getDataset());
		db.addFeatureExtractorDataToProblem(feature_extractor_data, problem_name);
		return feature_extractor_data;
	}
	
	public static FeatureExtractorData performFeatureExtraction ( FeatureExtractor feature_extractor , String feature_extractor_name , List<Data> data ) {
		FeatureExtractorData feature_extractor_data = new FeatureExtractorData(feature_extractor_name); 
		try {
			feature_extractor_data.setFeatures(feature_extractor.getFeatures(data));
		} catch (UnsupportedClassTypeException e) {
			e.printStackTrace();
		}
		feature_extractor_data.setDataset(feature_extractor.getDataset());
		return feature_extractor_data;
	}
	
	//public static Collection<Data> readDataFromReader( DataReader reader , DatabaseManager dbmgt , String problem_name , int num_documents , boolean match_against_data_in_db , boolean print_progress ) {
	public static Collection<Data> readDataFromReader( DataReader reader , DatabaseManager dbmgt , String problem_name , int num_documents , boolean match_against_data_in_db , boolean print_progress ) {
		List<Data> result = new ArrayList<Data>();
		if ( print_progress ) System.out.println("Loading data into reader");
		reader.init(num_documents);
		int counter = 1;
		if ( print_progress ) System.out.println("Reading data from "+reader.getClass());
		long start = System.currentTimeMillis();
		while( reader.hasNext() ) {
			Data data = reader.next();
			if ( match_against_data_in_db ) {
				if ( dbmgt.existsDataForProblem(data,problem_name) ) {
					if ( print_progress ) System.out.println("This data already exists in the db, stop reading from this source");
					return result;
				}
			}
			//dbmgt.addDataToProblem(data, problem_name);
			result.add(data);
			if ( print_progress ) System.out.println("Progress: "+(counter++)+" out of "+reader.totalData());
			long current = System.currentTimeMillis();
			if ( print_progress ) System.out.println("Time elapsed: "+getTime(current,start)+" s");
		}
		long end = System.currentTimeMillis();
		if ( print_progress ) System.out.println("Done reading data from "+reader.getClass()+" total time: "+getTime(end,start)+ " s");
		return result;
	}
	
	private static String getTime(long current, long start) {
		String res = ""+( (current-start) % 1000);
		while( res.length() < 3 ) res = "0"+res;
		res = ( (current-start) / 1000)+"."+res;
		return res;
	}

	
	
	
}