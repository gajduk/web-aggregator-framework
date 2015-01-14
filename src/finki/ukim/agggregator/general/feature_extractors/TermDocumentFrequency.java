package finki.ukim.agggregator.general.feature_extractors;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SparseInstance;
import weka.core.UnsupportedClassTypeException;
import weka.core.stemmers.LovinsStemmer;
import finki.ukim.agggregator.general.data_model.AttributeValuePair;
import finki.ukim.agggregator.general.data_model.Data;
import finki.ukim.agggregator.general.data_model.Feature;
import finki.ukim.agggregator.general.data_model.text_data.TextData;

/**
 * An implementation of the famous term document frequency metrics for text data.
 * Each data object i.e. text is represented as a bag of words were the weights for each word corresponds to its relative frequency in the text.
 * @author Andrej Gajduk
 *
 */
public class TermDocumentFrequency extends FeatureExtractor {

	/**
	 * a dictionary that contains all the words that encountered in the documents, this should be populated with the addAttributesForData 
	 * before trying to perform any feature extraction 
	 */
	private HashMap<String,Double> dictionary;
	
	/**
	 * an object used to check whether a given word is a stop word
	 */
	private stopWords stop_words;
	
	/**
	 * the index in a sorted list of word occurrences in descending order from which we should use words as attributes
	 * i.e. if we have the following situation { dodeka:7 , kniga:5 , mleko:3 , informatika:2 , politika:2 , Andrej:1 , Zivko:1 }
	 * for lower index = 1 and upper index = 5 we should only consider the kniga,mleko,informatika and politika as valid attributes
	 */
	private int lower_index;
	
	/**
	 * the index in a sorted list of word occurrences in descending order to which we should use words as attributes
	 * i.e. if we have the following situation { dodeka:7 , kniga:5 , mleko:3 , informatika:2 , politika:2 , Andrej:1 , Zivko:1 }
	 * for lower index = 1 and upper index = 5 we should only consider the kniga,mleko,informatika and politika as valid attributes
	 */
	private int upper_index;
		
	/**
	 * an instance of the TermDocumentFrequency feature extractor which should be supplied whenever getInstance is called
	 */
	private static TermDocumentFrequency instance;
	
	/**
	 * a constructor initializing the stopWords object and creating an empty dictionary
	 * @param upper_index2 
	 * @param lower_index2 
	 */
	private TermDocumentFrequency (int lower_index2, int upper_index2) {
		super();
		stop_words = new stopWords();
		stop_words.loadStopWords(); 
		dictionary = new HashMap<String,Double>();
		lower_index = lower_index2;
		upper_index = upper_index2;
		DictionaryLoader dl = new DictionaryLoader();
		dl.load();
		Set<String> dictionary = dl.getDictionary();
		int i = 0;
		for ( String word : dictionary ) {
			this.dictionary.put(word,(double)i);
			this.attributes.addElement(new Attribute(word));
			++i;
		}
	}
	
	public static TermDocumentFrequency getInstance( int lower_index , int upper_index) {
		if ( instance == null ) {
			instance = new TermDocumentFrequency(lower_index,upper_index);
		}
		return instance;
	}
		
	/**
	 * An implementation of the famous term document frequency metrics for text data.
	 * Each data object i.e. text is represented as a bag of words were the weights for each word corresponds to its relative frequency in the text.
	 *
	 */
	@Override
	public Feature getFeatures( Data data1 ) throws UnsupportedClassTypeException {
		TextData data = (TextData)data1;
		Map<String,Double> term_frequency_map = countTermOccurences(data);
		//sort the attribute value pairs by their value, only use those who are between lower_bound and upper_bound
		int counter = 0;
		AttributeValuePair attributes[] = new AttributeValuePair[term_frequency_map.size()];
		for ( Entry<String, Double> entry : term_frequency_map.entrySet() ) {
			//normalize the weights from the map
			attributes[counter++] = new AttributeValuePair(entry.getKey(), entry.getValue());
		}
		Arrays.sort(attributes);
		//truncate to use only those attributes that fall between the lower and the upper index in the original sorted array
		attributes = Arrays.copyOfRange(attributes, lower_index, upper_index>attributes.length?attributes.length:upper_index);
		//count the total number of terms, including the repeating ones so we can normalize the data later
		double number_of_terms = 0;
		for ( Double occurences : term_frequency_map.values() ) {
			number_of_terms += occurences;
		}
		//transform the AttributeValuePairs into an actual instance, use a sparse instance to conserve memory
		//at the same time normalize the values so that the total sum of the values for all the attributes is 1
		double attribute_values[] = new double[attributes.length];
		int attribute_indices[] = new int[attributes.length];
		counter = 0;
		for ( AttributeValuePair a : attributes ) {
			attribute_values[counter] = a.value/number_of_terms;
			attribute_indices[counter] = (int) ((double) dictionary.get(a.attribute_name));
			++counter;
		}
		return new Feature(new SparseInstance(1,attribute_values,attribute_indices,dictionary.size()),data.get_id());
	}
	
		/**
	 * a method that will separate all the different terms(words) that occur in the document and count how many times do they repeat themselves
	 * @return a map containing each word and a corresponding value for the number of its occurrences
	 */
	private Map<String,Double> countTermOccurences ( TextData data ) {
		//a map counting the occurrences of each term(word) in a given document
		Map<String,Double> result = new TreeMap<String,Double>();
		String content = data.getMainContent().toString();
		result = new TreeMap<String,Double>();
		//get the separate words
		String terms[] = content.split("\\W");
		//use a stemmer
		weka.core.stemmers.Stemmer st = new LovinsStemmer();
		//iterate over each word
		for ( String term : terms) {
			//make all letters lower-case to avoid the same word being interpreted differently just because some letters are in capital
			term = term.toLowerCase();
			//stem the term
			term = st.stem(term);
			//check if it's not an empty or small string, or if it is in-fact in the dictionary, or if it's a stop word
			if( term.length() >= 1 &&
				dictionary.containsKey(term) &&
				! stop_words.isStopWord(term) ) {
				//the word is valid, so add it to the map, or increment its frequency 
				Double value = result.get(term);
				if ( value != null ) result.put(term, value+1);
				else result.put(term, 1.0);
			}
		}
		return result;
	}

		@Override
		public Feature getFeatures(Data data, Instances dataset)
				throws UnsupportedClassTypeException {
			return null;
		}

		@Override
		public Instance getGlobalFeatures(List<Data> data)
				throws UnsupportedClassTypeException {
			return null;
		}

		@Override
		public void addAttributesForData(Data data)
				throws UnsupportedClassTypeException {
			
		}

	
}
