package finki.ukim.agggregator.scholarships.data_readers;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import finki.ukim.agggregator.general.data_model.Data;
import finki.ukim.agggregator.general.data_model.DataReader;
import finki.ukim.agggregator.general.data_model.Source;
import finki.ukim.agggregator.general.data_model.text_data.TextDataContent;
import finki.ukim.agggregator.general.data_model.text_data.TextData;
import finki.ukim.agggregator.scholarships.UrlFetcher;


public class ScholarshipPortalDataReader  extends DataReader {

	private static final String URL_PATH = "http://www.scholarshipportal.eu/students/search-results/?q=tr-1||e3980e3c&start=10&length=10000";
	private static final String URL_PATH_1 = "http://www.scholarshipportal.eu/";
	
	private Pattern title_pattern;
	private Pattern provider_pattern;
	private Pattern duration_pattern;
	private Pattern summary_pattern;
	private Pattern purpose_pattern;
	private Pattern level_pattern;
	private Pattern quota_pattern;
	private Pattern application_deadline_pattern;
	private Pattern benefits_summary_pattern;
	private Pattern description_pattern;
	private Pattern eligibility_pattern;
	private Pattern duration_1_pattern;
	private Pattern benefits_pattern;
	private Pattern application_information_pattern;
	private Pattern relevant_links_pattern;
	private Pattern additional_information_pattern;
	
	private ArrayList<String> links; 
	private int current_index;
	
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
	public void init( int max_number_of_documents ) {
		//set the source
		setSrc(new Source("Another boring scholarships page", "Scholarship portal", "http://www.scholarshipportal.eu/"));
				
		current_index = 0;
		//read all the links to the actual data from the main page
		//there is a page size attribute in the query string, it's the last attribute, there are round 5000 scholarships here
		String URL = URL_PATH;
		String html = "";
		int number_of_documents_counter = 0;
		try {
			html = UrlFetcher.fetchGet(URL);
		} catch (IOException e) {
			e.printStackTrace();
		}
		links = new ArrayList<String>();
		Matcher matches = getLinksPattern().matcher(html);
		while ( matches.find() && number_of_documents_counter < max_number_of_documents ) {
			String link = matches.group(1);
			links.add(link);
			++number_of_documents_counter;
		}
		
		//load up and compile the required patterns
		title_pattern = getTitlePattern();
		provider_pattern = getProviderPattern();
		duration_pattern = getDurationPattern();
		summary_pattern = getSummaryPattern();
		purpose_pattern = getPurposePattern();
		level_pattern = getLevelPattern();
		quota_pattern = getQuotaPattern();
		application_deadline_pattern = getApplicationDeadlinePattern();
		benefits_summary_pattern = getBenefitsSummaryPattern();
		description_pattern = getDescriptionPattern();
		eligibility_pattern = getEligibilityPattern();
		duration_1_pattern = getDuration1Pattern();
		benefits_pattern = getBenefitsPattern();
		application_information_pattern = getApplicationInformationPattern();
		relevant_links_pattern = getRelevantLinksPattern();
		additional_information_pattern = getAdditionalInformationPattern();
	}
		
	@SuppressWarnings("deprecation")
	private Data readDataFromLink ( String link ) {
		String title = "";
		Matcher title_matcher = null;
		String provider = "";
		Matcher provider_matcher = null;
		String summary = "";
		Matcher summary_matcher = null;
		String duration = "";
		Matcher duration_matcher = null;
		String purpose = "";
		Matcher purpose_matcher = null;
		String level = "";
		Matcher level_matcher = null;
		String quota = "";
		Matcher quota_matcher = null;
		String application_deadline = "";
		Matcher application_deadline_matcher = null;
		String benefits_summary = "";
		Matcher benefits_summary_matcher = null;
		String description = "";
		Matcher description_matcher = null;
		String eligibility = "";
		Matcher eligibility_matcher = null;
		String duration_1 = "";
		Matcher duration_1_matcher = null;
		String benefits = "";
		Matcher benefits_matcher = null;
		String application_information = "";
		Matcher application_information_matcher = null;
		String relevant_links = "";
		Matcher relevant_links_matcher = null;
		String additional_information = "";
		Matcher additional_information_matcher = null;
		Pattern list_pattern = getListPattern();
		Matcher list_matcher = null;
		String URL = URL_PATH_1 + link;
		String html = "";
		try {
			html = UrlFetcher.fetchGet(URL);
		} catch (IOException e) {
			e.printStackTrace();
		}
		title_matcher = title_pattern.matcher(html);
		if ( title_matcher.find() )
			title = title_matcher.group(1);
		provider_matcher = provider_pattern.matcher(html);
		if ( provider_matcher.find() )
			provider = provider_matcher.group(1);
		summary_matcher = summary_pattern.matcher(html);
		if ( summary_matcher.find() )
			summary = summary_matcher.group(1);
		duration_matcher = duration_pattern.matcher(html);
		if ( duration_matcher.find() )
			duration = duration_matcher.group(1);
		purpose_matcher = purpose_pattern.matcher(html);
		if ( purpose_matcher.find() )
			purpose = purpose_matcher.group(1);
		list_matcher = list_pattern.matcher(purpose);
		purpose = "";
		while ( list_matcher.find() ) {
			purpose += list_matcher.group(1)+",";
		}
		level_matcher = level_pattern.matcher(html);
		
		if ( level_matcher.find() )
			level = level_matcher.group(1);
		list_matcher = list_pattern.matcher(level);
		level = "";
		while ( list_matcher.find() ) {
			level += list_matcher.group(1)+",";
		}
		quota_matcher = quota_pattern.matcher(html);
		if ( quota_matcher.find() )
			quota = quota_matcher.group(1);
		application_deadline_matcher = application_deadline_pattern.matcher(html);
		if ( application_deadline_matcher.find() )
			application_deadline = application_deadline_matcher.group(1);
		benefits_summary_matcher = benefits_summary_pattern.matcher(html);
		if ( benefits_summary_matcher.find() )
			benefits_summary = benefits_summary_matcher.group(1);
		description_matcher = description_pattern.matcher(html);
		if ( description_matcher.find() )
			description = description_matcher.group(1);
		eligibility_matcher = eligibility_pattern.matcher(html);
		if ( eligibility_matcher.find() )
			eligibility = eligibility_matcher.group(1);
		list_matcher = list_pattern.matcher(eligibility);
		eligibility = "";
		while ( list_matcher.find() ) {
			eligibility += list_matcher.group(1)+",";
		}
		duration_1_matcher = duration_1_pattern.matcher(html);
		if ( duration_1_matcher.find() )
			duration_1 = duration_1_matcher.group(1);
		benefits_matcher = benefits_pattern.matcher(html);
		if ( benefits_matcher.find() )
			benefits = benefits_matcher.group(1);
		application_information_matcher = application_information_pattern.matcher(html);
		if ( application_information_matcher.find() )
			application_information = application_information_matcher.group(1);
		relevant_links_matcher = relevant_links_pattern.matcher(html);
		if ( relevant_links_matcher.find() )
			relevant_links = relevant_links_matcher.group(1);
		additional_information_matcher = additional_information_pattern.matcher(html);
		if ( additional_information_matcher.find() )
			additional_information = additional_information_matcher.group(1);
		list_matcher = list_pattern.matcher(additional_information);
		additional_information = "";
		while ( list_matcher.find() ) {
			additional_information += list_matcher.group(1)+",";
		}
		DateFormat df = new SimpleDateFormat("dd MMMMMMMMM yyyy");
		Date d = null;
		try {
			d = df.parse(application_deadline);
		} catch (ParseException e) {
			d = new Date();
			d.setYear(1);
		}
		TextData data = new TextData(title,getSrc(),new Date(),d);
		data.setMainContent(new TextDataContent("main",description));
		data.addContentsField(new TextDataContent("application deadline",application_deadline));
		data.addContentsField(new TextDataContent("provider",provider));
		data.addContentsField(new TextDataContent("summary",summary));
		data.addContentsField(new TextDataContent("duration",duration));
		data.addContentsField(new TextDataContent("purpose",purpose));
		data.addContentsField(new TextDataContent("quota",quota));
		data.addContentsField(new TextDataContent("benefits_summary",benefits_summary));
		data.addContentsField(new TextDataContent("eligibility",eligibility));
		data.addContentsField(new TextDataContent("duration_1",duration_1));
		data.addContentsField(new TextDataContent("benefits",benefits));
		data.addContentsField(new TextDataContent("application_information",application_information));
		data.addContentsField(new TextDataContent("relevant_links",relevant_links));
		data.addContentsField(new TextDataContent("additional_information",additional_information));
		data.addContentsField(new TextDataContent("benefits",benefits));
		data.addContentsField(new TextDataContent("level",level));
		data.setCategoryInformationField("level", level.split(",")[0]);

		/* //uncomment if you want the data logged to syso
		System.out.println("Title:"+title);
		System.out.println("Provider:"+provider);
		System.out.println("Summary:"+summary);
		System.out.println("Duration:"+duration);
		System.out.println("Purpose:"+purpose);
		System.out.println("Level:"+level);
		System.out.println("quota:"+quota);
		System.out.println("application_deadline:"+application_deadline);
		System.out.println("benefits_summary:"+benefits_summary);
		System.out.println("description:"+description);
		System.out.println("eligibility:"+eligibility);
		System.out.println("duration_1:"+duration_1);
		System.out.println("benefits:"+benefits);
		System.out.println("application_information:"+application_information);
		System.out.println("relevant_links:"+relevant_links);
		System.out.println("additional_information:"+additional_information);
		*/
		return data;
	}

	/*
	 * <h3>Additional information</h3> <ul><li>Unless stated differently, the grants support one individual person - the grants do not take accompanying dependants into account. </li><li>The costs for medical care abroad withtin the EU are offset by the respective national social security institutions. Doing an academic exchange within the EU, the candidates should take a EU Social Security Card with them. </li><li>The applicants are aware that, in accordance with the Austrian federal law on data protection BGBl.Nr. 165/1999, the personal data in the application can be sharded with the office in charge of the exchange programme, the partner institutions and other Austrian institutions granting financial support. The applicants agree to this process. The scholarship reports will be published on the Action&acute;s website.</li><li>Incomplete and delayed applications cannot be taken into account.</li><li>Grants have to be consumed within the period of time agreed upon. </li><li>Based on an application to the rector, Austrian students can be suspended from the tuition fee if they complete their studies or internships within the course of transnational, EU, national or academic mobility programmes abroad. </li></ul> </div>
	 */
	private Pattern getAdditionalInformationPattern() {
		return Pattern.compile("<h3>Additional information</h3> <ul>(.*?)</ul>");
	}

	/*
	 * <h3>Relevant Links</h3> <ol class="BasicLinks LinksList">  <li class=""> <a href="http://www.omaa.hu/" target="mp_external_33074" onmouseup="Portal.trackExternal('url', '33074'.toInt(), event);"> Visit scholarship website (hungarian) </a> </li>  </ol>
	 */
	private Pattern getRelevantLinksPattern() {
		return Pattern.compile("<h2>Relevant Linksy</h2> <ol class=\"BasicLinks LinksList\">(.*?)</ol>");
	}

	/*
	 * <h3>Application information</h3> <p>The Zonta International Amelia Earhart Fellowship Committee reviews the applications and recommends recipients to the Zonta International Board of Directors. All applicants will be notified of their status by the end of April.</p>
	 */
	private Pattern getApplicationInformationPattern() {
		return Pattern.compile("<h3>Application information</h3> <p>(.*?)</p>");
	}

	/*
	 * <h3>Benefits</h3> <p>The course attendance, accommodation and food supply are financed from Hungarian side.</p>
	 */
	private Pattern getBenefitsPattern() {
		return Pattern.compile("<h3>Benefits</h3> <p>(.*?)</p>");
	}

	/*
	 * <h2>Duration</h2> <p>One year</p> 
	 */
	private Pattern getDuration1Pattern() {
		return Pattern.compile("<h2>Duration</h2> <p>(.*?)</p>");
	}

	/*
	* <h2>Eligibility</h2> <ul><li>Age limit of 35</li><li>Grants are only awarded the best students of many qualified candidates.</li><li>The presence of the chosen candidates on-site and the scientific pursuit of their projects / academic schedule are compulsory. </li><li>The chosen candidates are not allowed to engage in a gainful employment next besides their academic duties.</li></ul>
	*/
	private Pattern getEligibilityPattern() {
		return Pattern.compile("<h2>Eligibility</h2> <ul>(.*?)</ul>");
	}

	/*
	 * <h2>Description</h2> <p>Scholarship in any discipline for Austrian undergraduate students and graduates to Hungary and vice versa.</p> 
	 */
	private Pattern getDescriptionPattern() {
		return Pattern.compile("<h2>Description</h2> <p>(.*?)</p>");
	}

	/*
	 * <tr> <td class="Title">Benefits summary:</td> <td class="Field"> Free courses, food, accommodation </td>
	 */
	private Pattern getBenefitsSummaryPattern() {
		return Pattern.compile("<td class=\"Title\">Benefits summary:</td> <td class=\"Field\">(.*?)</td>");
	}

	/*
	 * <td class="Title">Application deadline:</td> <td class="Field"> End of October, December </td>
	 */
	private Pattern getApplicationDeadlinePattern() {
		return Pattern.compile("<td class=\"Title\">Application deadline:</td> <td class=\"Field\">(.*?)</td>");
	}

	/*
	 * <td class="Title">Quota:</td> <td class="Field"> Depends on the budget </td>
	 */
	private Pattern getQuotaPattern() {	
		return Pattern.compile("<td class=\"Title\">Quota:</td> <td class=\"Field\">(.*?)</td>");
	
	}

	private Pattern getListPattern() {
		return Pattern.compile("<li>(.*?)</li>");
	}

	/*
	 * <td class="Title">Level:</td> <td class="Field">  <ul class="PropertyList">  <li>Bachelor</li>  <li>Master</li>  </ul>  </td>
	 */
	private Pattern getLevelPattern() {
		return Pattern.compile("<td class=\"Title\">Level:(.*?)</ul>");
	}

	/* 
	 * <td class="Title">Purpose:</td> <td class="Field">  <ul class="PropertyList">  <li>  Study  </li>  </ul>
	 */
	private Pattern getPurposePattern() {
		return Pattern.compile("<td class=\"Title\">Purpose:</td> <td class=\"Field\">  <ul class=\"PropertyList\">(.*?)</ul>");
	}

	/*
	 * <tr> <td class="Title">Duration:</td> <td class="Field">  Max 1 months  </td> </tr> <tr> 
	 */
	private Pattern getDurationPattern() {
		return Pattern.compile("<tr> <td class=\"Title\">Duration:</td> <td class=\"Field\"> (.*?) </td>");
	}

	/*
	 * <div class=\"ShortDesc\"> Bla bla bal </div>
	 */
	private Pattern getSummaryPattern() {
		return Pattern.compile("<div class=\"ShortDesc\"> (.*?) </div>");
	}

	/*
	<div class="DetailsHeader_Line"></div> <h2> <a href="http://www.scholarshipportal.eu/students/browse/provider/178/stiftung-aktion-osterreich-ungarn.html">Stiftung Aktion Österreich-Ungarn</a> </h2>
	*/
	private Pattern getProviderPattern() {
		return Pattern.compile("<div class=\"DetailsHeader_Line\"></div> <h2> <a href=.*?\">(.*?)</a>");
	}

	/*
	 * <div id="DetailsHeader"> <h1 title=""><span>
	*/
	private Pattern getTitlePattern() {
		return Pattern.compile("<div id=\"DetailsHeader\"> <h1 title=\"(.*?)\"><span>");	
	}

	/*
	<li class="ScholarshipLink"><a title="" href="students/browse/scholarship/1068/edb-scholarship.html">Detailed Description</a></li>
	*/
	public Pattern getLinksPattern() {
		return Pattern.compile("<li class=\"ScholarshipLink\"><a title=\"\" href=\"(.*?)\"");
	}

}
