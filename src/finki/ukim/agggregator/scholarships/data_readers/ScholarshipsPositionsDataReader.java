package finki.ukim.agggregator.scholarships.data_readers;



import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import finki.ukim.agggregator.general.data_model.Data;
import finki.ukim.agggregator.general.data_model.DataReader;
import finki.ukim.agggregator.general.data_model.Source;
import finki.ukim.agggregator.general.data_model.text_data.TextDataContent;
import finki.ukim.agggregator.general.data_model.text_data.TextData;
import finki.ukim.agggregator.scholarships.UrlFetcher;

public class ScholarshipsPositionsDataReader  extends DataReader  {

	private static final String URL_PATH1 = "http://scholarship-positions.com/blog";
	
	private ArrayList<String> links; 
	private int current_index;
	
	private Pattern links_pattern;
	private Pattern title_pattern;
	private Pattern study_subjects_pattern;
	private Pattern course_level_pattern;
	private Pattern scholarship_provider_pattern;
	private Pattern scholarship_taken_at_pettern;
	private Pattern eligibility_pattern;
	private Pattern is_scholarship_international_pattern;
	private Pattern countries_pattern;
	private Pattern description_pattern;
	private Pattern how_to_apply_pattern;
	private Pattern deadline_pattern;
	private Pattern link_further_pattern;
	private Pattern tags_pattern;

	/**
	 * Deprecated
	 */
	@Override
	public ArrayList<Data> getAllData() {
		return new ArrayList<Data>();
	}
	
	@Override
	public int totalData() {
		return links.size();
	}
	
	@Override
	public boolean hasNext() {
		return current_index >= 0 && current_index < links.size();
	}
	
	@Override
	public Data next() {
		Data result = null;
		if ( current_index >= 0 && current_index < links.size() ) {
			result = readDataFromLink(links.get(current_index));
			++current_index;
		}
		return result;
	}
	
	@Override
	public void init( int max_number_of_documents  ) {
		//set the source
		setSrc(new Source("Another boring scholarships page","Scholarship Positions","http://scholarship-positions.com"));
		//read all the links with real data
		current_index = 0;
		links_pattern = getLinksPattern();
		fillScholarshipLinks(max_number_of_documents);
		//load up and compile the required patterns
		title_pattern = getTitlePattern();
		study_subjects_pattern = getStudySubjectsPattern();
		course_level_pattern = getCourseLevelPattern();
		scholarship_provider_pattern = getScholarshipProviderPattern();	
		scholarship_taken_at_pettern = getScholarshipTakenAtPattern();
		eligibility_pattern = getEligibilityPattern();	
		is_scholarship_international_pattern = getIsScholarshipInternationalPattern();	
		countries_pattern = getCountriesPattern();	
		description_pattern = getDescriptionPattern();	
		how_to_apply_pattern = getHowToApplyPattern();	
		deadline_pattern = getDeadlinePattern();	
		link_further_pattern = getLinkFurtherPattern();	
		link_further_pattern = getLinkFurtherPattern();	
		tags_pattern = getTagsPattern();	
	}
				
	@SuppressWarnings("deprecation")
	private Data readDataFromLink ( String link ) {
		Matcher matches;
		String URL = link;
		String html = "";
		try {
			html = UrlFetcher.fetchGet(URL);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String title = "";
		String intro = "";
		String studySubjects = "";
		String courseLevel = "";
		String scholarshipProvider = "";
		String scholarshipTakenAt = "";
		String eligibility = "";
		String isScholarshipInternational = "";
		String countries = "";	//It is not always included
		String description = "";
		String howToApply = "";
		String deadline = "";
		String linkFurther = "";
		String tags = "";
		matches = title_pattern.matcher(html);
		if ( matches.find() )
			title = matches.group(1);
		matches = study_subjects_pattern.matcher(html);
		if ( matches.find() )
			studySubjects = matches.group(1);
		matches = course_level_pattern.matcher(html);
		if ( matches.find() )
			courseLevel = matches.group(1);
		matches = scholarship_provider_pattern.matcher(html);
		if ( matches.find() )
			scholarshipProvider = matches.group(1);
		matches = scholarship_taken_at_pettern.matcher(html);
		if ( matches.find() )
			scholarshipTakenAt = matches.group(1);
		matches = eligibility_pattern.matcher(html);
		if ( matches.find() )
			eligibility = matches.group(1);
		matches = is_scholarship_international_pattern.matcher(html);
		if ( matches.find() )
			isScholarshipInternational = matches.group(1);
		matches = countries_pattern.matcher(html);
		if ( matches.find() )
			countries += matches.group(1);
		matches = description_pattern.matcher(html);
		if ( matches.find() )
		description = matches.group(1);
		matches = how_to_apply_pattern.matcher(html);
		if ( matches.find() )
		howToApply = matches.group(1);
		matches = deadline_pattern.matcher(html);
		if ( matches.find() )
			deadline = matches.group(1);
		matches = link_further_pattern.matcher(html);
		if ( matches.find() )
			linkFurther = matches.group(1);
		matches = tags_pattern.matcher(html);
		while(matches.find())
			tags += matches.group(1)+",";
		DateFormat df = new SimpleDateFormat("dd MMMMMMMMM yyyy");
		Date d = null;
		try {
			d = df.parse(deadline);
		} catch (ParseException e) {
			d = new Date();
			d.setYear(1);
		}
		TextData data = new TextData(title,getSrc(),new Date(),d);
		data.setMainContent(new TextDataContent("main",description));
		data.addContentsField(new TextDataContent("deadline",deadline));
		data.addContentsField(new TextDataContent("studySubjects",studySubjects));
		data.addContentsField(new TextDataContent("courseLevel",courseLevel));
		data.addContentsField(new TextDataContent("scholarshipProvider",scholarshipProvider));
		data.addContentsField(new TextDataContent("scholarshipTakenAt",scholarshipTakenAt));
		data.addContentsField(new TextDataContent("eligibility",eligibility));
		data.addContentsField(new TextDataContent("isScholarshipInternational",isScholarshipInternational));
		data.addContentsField(new TextDataContent("eligibility",eligibility));
		data.addContentsField(new TextDataContent("howToApply",howToApply));
		data.addContentsField(new TextDataContent("linkFurther",linkFurther));
		data.addContentsField(new TextDataContent("tags",tags));
		data.addContentsField(new TextDataContent("intro",intro));
		data.addContentsField(new TextDataContent("countries",countries));
		 
		/*// uncomment if you want the data logged to syso
		System.out.println("Title: "+title);
		System.out.println("Study Subjects: "+studySubjects);
		System.out.println("Course Level: "+courseLevel);
		System.out.println("Scholarship Provider: "+scholarshipProvider);
		System.out.println("Scholarship taken at: "+scholarshipTakenAt);
		System.out.println("Eligibility: "+eligibility);
		System.out.println("Is Scholarship International: "+isScholarshipInternational);
		System.out.println("Scholarship Description: "+description);
		System.out.println("How to apply: "+howToApply);
		System.out.println("Deadline: "+deadline);
		System.out.println("Further Information: "+linkFurther);
		System.out.println("Tags: "+tags);
		*/
		return data;
	}
	
	/**
	 * reads all the links to the real data on this website
	 * @param max_number_of_documents 
	 */
	public void fillScholarshipLinks(int max_number_of_documents) {
		links = new ArrayList<String>();
		//the data is given by years and weeks, only check for up to 2010 and remember there are 52 weeks in a year
		//first check for the given year
		GregorianCalendar gc = new GregorianCalendar();
		int current_year = gc.get(Calendar.YEAR);
		int current_week = gc.get(Calendar.WEEK_OF_YEAR);
		for ( int week = current_week ; week > 0 ; --week ) {
			addLinks(current_year+"",""+week,max_number_of_documents);
			if ( max_number_of_documents >= links.size() ) return;
		}
		//then for the years before
		for ( int year = current_year-1 ; year >= current_year-2 ; --year ) {
			for ( int week = 52 ; week >= 1 ; --week ) {
				addLinks(""+year,""+week,max_number_of_documents);
				if ( max_number_of_documents >= links.size() ) return;
			}
		}
	}
	
	/**
	 * adds the links from a given week and year to the links list
	 * This will take the published scholarships for a chosen week of the year
	 * Format: "scholarship-positions.com/2012/?w=5"
	 * @param year - in the query string
	 * @param week - in the query string
	 * @param max_number_of_documents 
	 */
	public void addLinks(String year, String week, int max_number_of_documents) {
		String URL = URL_PATH1 + "/" + year + "/" + "?w=" + week;
		String html = "";
		try {
			html = UrlFetcher.fetchGet(URL);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Matcher matches = links_pattern.matcher(html);
		while (matches.find() && max_number_of_documents > links.size() ) {
			String link = matches.group(1);
			links.add(link);
		}
	}

	/*
	 *  <a href="http://scholarship-positions.com/blog/endeavour-awards-of-australia-for-australian-and-international-students/201208/">.. [Read More..]</a></p>
	 */
	public Pattern getLinksPattern() {
		return Pattern.compile("a href=\"(.*?)\">.*[Read More..]");
	}

	public Pattern getTitlePattern() {
		return Pattern.compile("<h1 class=\"post-title\">(.*?)</h1>");
	}
	
	public Pattern getStudySubjectsPattern(){
		return Pattern.compile("<strong>Study Subject.s.</strong>(.*?)<br");
	}
	
	public Pattern getCourseLevelPattern(){
		return Pattern.compile("<strong>Course Level</strong>:(.*?)<br");
	}
	
	public Pattern getScholarshipProviderPattern(){
		return Pattern.compile("<strong>Scholarship Provider</strong>:(.*?)<br");
	}
	
	public Pattern getScholarshipTakenAtPattern(){
		return Pattern.compile("<strong>Scholarship can be taken at</strong>: (.*?)<");
	}
	
	/*
	 * Might be used different format??
	 */
	public Pattern getEligibilityPattern(){
		return Pattern.compile("<strong>Eligibility</strong>(.*?)</p>",Pattern.DOTALL);
	}
	
	public Pattern getIsScholarshipInternationalPattern(){
		return Pattern.compile("<strong>Scholarship Open for International Students</strong>:(.*?)<");
	}
	
	public Pattern getCountriesPattern(){
		return Pattern.compile("<strong>Scholarship Open for Students of Following Countries</strong>: (.*?)<");
	}
	
	public Pattern getDescriptionPattern(){
		return Pattern.compile("<strong>Scholarship Description</strong>(.*?)<");
	}
	
	public Pattern getHowToApplyPattern(){
		return Pattern.compile("<strong>How to Apply</strong>:(.*?)<");
	}
	
	public Pattern getDeadlinePattern(){
		return Pattern.compile("<strong>Scholarship Application Deadline</strong>:(.*?)<");
	}
	
	public Pattern getLinkFurtherPattern(){
		return Pattern.compile("title=\"Further Scholarship Information and Application Details\" href=\"(.*?)\"");
	}
	
	public Pattern getTagsPattern(){
		return Pattern.compile("rel=\"tag\">(.*?)<");
	}
	
}
