package finki.ukim.agggregator.general.feature_extractors;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;

import weka.core.stemmers.LovinsStemmer;

/**
 * Loads a prebuilt dictionary form a file
 * It applies the lovins stemmer as it reads in the dictionary
 * @author AndrejGajduk
 *
 */
public class DictionaryLoader {
	
	
	private HashSet<String> dictionary;
	
	public DictionaryLoader ( ) {
		dictionary = new HashSet<String>();
	}
	
	public void load() {
		loadFromFile("word_list.txt");
	}
	
	/**
	 * Loads a prebuilt dictionary form a file
	 * It applies the lovins stemmer as it reads in the dictionary
	 * @param filename
	 */
	private void loadFromFile(String filename) {
		Scanner s = null;
		try {
			s = new Scanner(new FileInputStream(filename));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		weka.core.stemmers.Stemmer st = new LovinsStemmer();
		while ( s.hasNext() ) {
			String word = s.nextLine();
			word = st.stem(word);
			dictionary.add(word.toLowerCase());
		}
		s.close();
	}

	/**
	 * @return the dictionary
	 */
	public HashSet<String> getDictionary() {
		return dictionary;
	}

	/**
	 * @param dictionary the dictionary to set
	 */
	public void setDictionary(HashSet<String> dictionary) {
		this.dictionary = dictionary;
	}
	
	

}
