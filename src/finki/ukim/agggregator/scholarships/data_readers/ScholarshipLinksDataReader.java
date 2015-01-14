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

public class ScholarshipLinksDataReader extends DataReader {
	
	

	private Pattern title_pattern;
	private Pattern all_info_pattern;
	private Pattern contents_pattern;
	private Pattern application_deadline_pattern;
	private Pattern contact_adress_pattern;
	private Pattern contact_email_pattern;
	private Pattern pdf_link_pattern;
	
	
	private ArrayList<String> links; 
	private int current_index;
	
	private static final String URL_PATH = "http://www.scholarships-links.com/index.php?pageNum_Recordset1=1&totalRows_Recordset1=4000";

	
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
		setSrc(new Source("Another boring scholarships page", "Scholarship Links", "http://www.scholarships-links.com"));
		//read all the links to the data itself, and store them in the links list
		links = new ArrayList<String>();
		Pattern link_pattern = getLinksPattern();
		int number_of_documents_counter = 0; 
		//there are 40 links per page, and about 70-80 pages of real data, go through every page and gather the links up
		for ( int i = 1 ; i < 100 ; ++i ) {
			String URL = URL_PATH.replace("=1&","="+i+"&");
			String html = "";
			try {
				html = UrlFetcher.fetchGet(URL);
			} catch (IOException e) {
				e.printStackTrace();
			}
			Matcher matches = link_pattern.matcher(html);
			while ( matches.find() && number_of_documents_counter < max_number_of_documents ) {
				String link = matches.group(1);
				links.add(link);
				++number_of_documents_counter;
			}
			if ( number_of_documents_counter >= max_number_of_documents ) break;
		}
		//compile the patterns needed
		title_pattern = getTitlePattern();
		all_info_pattern = getAllInfoPattern();
		contents_pattern = getContentsPattern();
		application_deadline_pattern = getApplicationDeadlinePattern();
		contact_adress_pattern = getContactAdressPattern();
		contact_email_pattern = getContactEmailPattern();
		pdf_link_pattern = getPdfLinkPattern();
		current_index = 0;
	}
	
	@SuppressWarnings("deprecation")
	private Data readDataFromLink ( String link ) {
		String title = "";
		Matcher title_matcher = null;
		String all_info = "";
		Matcher all_info_matcher = null;
		String contents = "";
		Matcher contents_matcher = null;
		String application_deadline = "";
		Matcher application_deadline_matcher = null;
		String contact_adress = "";
		Matcher contact_adress_matcher = null;
		String contact_email = "";
		Matcher contact_email_matcher = null;
		String pdf_link = "";
		Matcher pdf_link_matcher = null;
		String URL = link;
		String html = "";
		try {
			html = UrlFetcher.fetchGet(URL);
		} catch (IOException e) {
			e.printStackTrace();
		}
		title_matcher = title_pattern.matcher(html);
		if ( title_matcher.find() )
			title = title_matcher.group(1);
		all_info_matcher = all_info_pattern.matcher(html);
		if ( all_info_matcher.find() )
			all_info = all_info_matcher.group(1);
		
		contents_matcher = contents_pattern.matcher(all_info);
		while ( contents_matcher.find() ) {
			contents += contents_matcher.group(1);
		}
		int idx = contents.indexOf("Further Information");
		if ( idx != -1 ) {
			contents = contents.substring(0,idx);
		}
		contents = contents.replaceAll("&ldquo;","\"").replaceAll("&rdquo;", "\"");
		application_deadline_matcher = application_deadline_pattern.matcher(all_info);
		if ( application_deadline_matcher.find() )
			application_deadline = application_deadline_matcher.group(1);
		
		contact_adress_matcher = contact_adress_pattern.matcher(all_info);
		if ( contact_adress_matcher.find() )
			contact_adress = contact_adress_matcher.group(1);
		
		contact_email_matcher = contact_email_pattern.matcher(all_info);
		if ( contact_email_matcher.find() )
			contact_email = contact_email_matcher.group(1);
		
		pdf_link_matcher = pdf_link_pattern.matcher(all_info);
		if ( pdf_link_matcher.find() )
			pdf_link = pdf_link_matcher.group(1);
		DateFormat df = new SimpleDateFormat("dd MMMMMMMMM yyyy");
		Date d = null;
		try {
			d = df.parse(application_deadline);
		} catch (ParseException e) {
			d = new Date();
			d.setYear(1);
		}
		TextData data = new TextData(title,getSrc(),new Date(),d);
		data.setMainContent(new TextDataContent("main",contents));
		data.addContentsField(new TextDataContent("contact address", contact_adress));
		data.addContentsField(new TextDataContent("contact email", contact_email));
		data.addContentsField(new TextDataContent("pdf link", pdf_link));
		data.addContentsField(new TextDataContent("application deadline", application_deadline));
		
		/*//Uncomment if you want the data logged to syso
		System.out.println("Title:"+title);
		System.out.println("All info"+all_info);
		System.out.println("Contents:"+contents);
		System.out.println("Application deadline:"+application_deadline);
		System.out.println("Contact adress:"+contact_adress);
		System.out.println("Contact email:"+contact_email);
		System.out.println("pdf link:"+pdf_link);
		*/
//		System.out.println(link);
		return data;
	}

	/*
	 <div class="centerbox">

<DIV id=spacer></DIV>

<div id="sidebarcontent">
<div style="color:#6B6B61"> Sponsored Links </div>

<script type="text/javascript"><!--
google_ad_client = "ca-pub-1000790327492675";
/* sl_336x280 */                                  /* <- without this
google_ad_slot = "5712554146";
google_ad_width = 336;
google_ad_height = 280;
//-->
</script>
<script type="text/javascript"
src="http://pagead2.googlesyndication.com/pagead/show_ads.js">
</script>


</div> 

<p>&nbsp;</p>
<p class="MsoNormal"><span style="font-size: small;"><strong><span style="font-family: Tahoma; color: #333333;"><span lang="EN-GB">Scuola Superiore Sant Anna in Pisa, Italy, calls for applications for  the XI Edition Master of Arts in &ldquo;Human Rights and Conflict Management&rdquo; (a. y.  2012-2013).</span></span></strong><span style="font-family: Tahoma; color: #333333;"><span lang="EN-GB"> </span></span></span></p>
<p class="MsoNormal"><span style="font-size: small;"><span style="font-family: Tahoma; color: #333333;"><span lang="EN-GB">Applications shall be submitted online at</span></span><span style="font-family: Tahoma;"><span lang="EN-GB"> <a title="http://www.humanrights.sssup.it/ blocked::http://www.humanrights.sssup.it/" href="http://www.humanrights.sssup.it/" target="_blank">www.humanrights.sssup.it</a>. </span></span><span style="font-family: Tahoma;"><span lang="EN-GB"><br /><br /><strong><span style="font-family: Tahoma; color: #333333;">Applications for  admission by EU citizens shall be sent no later than 17<sup>th</sup> October  2012.</span></strong><span style="color: #333333;"><br /><br /><strong><span style="font-family: Tahoma;">The selection process of Non-EU  citizens will </span></strong><span style="font-family: Tahoma;">instead<strong> be held  in two rounds</strong>. The application deadline for the first round of  selection of non-EU candidates is set on <strong>2<sup>nd</sup> July 2012</strong>, while the  application deadline for the second round is set on <strong>17<sup>th</sup> September  2012</strong>.</span><br /><br /><span style="font-family: Tahoma;">We encourage applicants to apply in the first round,  as space in the class may be limited by the second round. In addition, applying  in the first round leaves more time for visa  procedures.</span></span></span></span></span></p>
<p class="MsoNormal" style="margin-bottom: 12pt;"><span style="font-size: small;"><strong><span style="font-family: Tahoma; color: green;"><span lang="EN-GB">KEY FACTS:</span></span></strong><span style="font-family: Tahoma;"><span lang="EN-GB"><br /><br /><span style="font-family: Tahoma; color: #333333;">1  year post-graduate professionalizing programme</span><span style="color: #333333;"><br /><br /><span style="font-family: Tahoma;">Language of instruction is  English</span><br /><br /><strong><span style="font-family: Tahoma;">Running from 14<sup>th</sup> January 2013 until Spring 2014</span></strong><br /><br /><span style="font-family: Tahoma;">440 hours of classroom lectures (+  individual studying)</span><br /><span style="font-family: Tahoma;">550 hours internship and final project  work</span></span></span></span></span></p>
<p class="MsoNormal"><span style="font-size: small;"><span style="font-family: Tahoma; color: #333333;"><span lang="EN-GB">The  <strong>curriculum</strong> is strongly  multidisciplinary and field oriented and includes courses in: International Law,  International Humanitarian Law, International Human Rights Law, Geopolitics and  regional issues in historical perspective, Philosophy of HR, Economic  Development, Theories and Techniques of Conflict Management, International PK  and PB operations, International HR Field operations, International Election  Observation missions, International Humanitarian operations, International  Project Development, Personal security, Preventive Medicine &amp; First Aid,  Essentials of Research and Writing, Career coaching.</span></span><span style="font-family: Tahoma; color: #333333;"><span lang="EN-GB"><br /><br /><span style="font-family: Tahoma;">The  <strong>internship</strong> is meant to supplement  the in-class training with a relevant hand-on experience, to be carried out with  a renowned organization working in the areas of human rights  protection/promotion, conflict prevention/resolution, humanitarian assistance or  development, either in the field or at  headquarters.</span><br /><br /><span style="font-family: Tahoma;">The tuition fee is 7.250 (s</span>even  thousand two hundred fifty)<span style="font-family: Tahoma;"> EUR and includes: attendance of all lectures,  didactic material (mainly in electronic format), participation in seminars and  field trips, tutorship &amp; career counselling, access to the school's library  and computers, lunch on lesson/exam days. Travel, accommodation in Pisa and during the  internship and any other expense are the responsibility of each  participant.</span><br /><br /><span style="font-family: Tahoma;"><strong>The Master offers one scholarship, covering the full  tuition fee, to be awarded to the most deserving  applicant.</strong></span></span></span></span></p>
<p class="MsoNormal" style="margin-bottom: 12pt;"><span style="font-size: small;"><span style="font-family: Tahoma; color: #333333;"><span lang="EN-GB"><strong>Depending on financial <span style="font-family: Tahoma;">availability, reduced tuition fees might be offered  to citizens from non-OECD countries</span> who are eligible for a  study visa for Italy<span style="font-family: Tahoma;">. </span></strong></span></span></span></p>
<p class="MsoNormal"><span style="font-size: small;"><span style="font-family: Tahoma; color: #333333;"><span lang="EN-GB">For  further details, please visit</span></span><span style="font-family: Tahoma;"><span lang="EN-GB"> <a title="http://www.humanrights.sssup.it/ blocked::http://www.humanrights.sssup.it/" href="http://www.humanrights.sssup.it/" target="_blank">www.humanrights.sssup.it</a> <span style="color: #333333;">, or contact:</span></span></span></span></p>
<p class="MsoNormal"><span style="font-size: small;"><strong><span style="font-family: Tahoma; color: green;"><span lang="EN-GB">Master of Arts in &ldquo;Human Rights and Conflict  Management&rdquo;</span></span></strong><span style="font-family: Tahoma; color: green;"><span lang="EN-GB"><br /></span></span><span style="font-family: Tahoma; color: #333333;"><span lang="EN-GB">Scuola  Superiore Sant'Anna</span></span><span style="font-family: Tahoma; color: #333333;"><span lang="EN-GB"><br /><span style="font-family: Tahoma;">Via  Cardinale Maffi, 27 56126 Pisa - ITALY</span><br /><span style="font-family: Tahoma;">Tel. <a title="tel:%2B%2B39%20050%20882653%2F55" href="tel:%2B%2B39%20050%20882653%2F55" target="_blank">++39 050  882653/55</a></span><br /><span style="font-family: Tahoma;">Fax <a title="tel:%2B%2B39%20050%20882633 blocked::tel:++39 050 882633" href="tel:%2B%2B39%20050%20882633" target="_blank">++39 050  882633</a></span><br /><span style="font-family: Tahoma;">E-mail:</span></span></span><span style="font-family: Tahoma;"><span lang="EN-GB"> </span></span><span style="font-family: Tahoma;"><a title="mailto:humanrights@sssup.it blocked::mailto:humanrights@sssup.it" href="mailto:humanrights@sssup.it" target="_blank"><span title="mailto:humanrights@sssup.it" lang="EN-GB"><span title="mailto:humanrights@sssup.it">humanrights@sssup.i</span><span title="mailto:humanrights@sssup.it" lang="EN-GB">t</span></span></a></span><span style="font-family: Tahoma;"> </span></span></p>
<p>&nbsp;</p>
       		<p> <strong> <a rel="nofollow" href="http://www.humanrights.sssup.it">Further Information</a> </strong> </p>
       		<p> <strong> Application Deadline :  2 July 2012 </strong> </p> 
		<p> <a href="/getpdf.php?id=3772">Download this Call as PDF</a></p>
	    <p>Contact Adress: Master Secretariat</p>
       <p>Contact Email: humanrights@sssup.it</p>
       <p>Posted on 2012-05-30 04:52:37</p>
       <p><a href="mailto:admin@scholarships-links.com">Click here to report</a> if you think this notice is not valid or violating copyrights. Not an Official Version. <a href="/terms.php">Terms Apply</a>.</p> <p> </p>
			      
<!-- Scholarships Deadline Snippet -->

<!-- Scholarships Deadline Snippet -->


<p style="color:#0000ee; text-align:center">Please give reference of <a href="http://www.scholarships-links.com/">Scholarships-Links.com</a> when applying for above scholarship. </p>


</div>
	 */
	private Pattern getAllInfoPattern() {
		return Pattern.compile("<div class=\"centerbox\">.*?</script>.*?</div>(.*?)</div>",Pattern.DOTALL);
	}

	/*
	 <p> <a href="/getpdf.php?id=3772">Download this Call as PDF</a></p>
	*/
	private Pattern getPdfLinkPattern() {
			return Pattern.compile("<p> <a href=\"(.*?)\">Download this Call as PDF</a></p>");
	}
	
	/*
	 <p>Contact Email: humanrights@sssup.it</p>
	 */
	private Pattern getContactEmailPattern() {
		return Pattern.compile("<p>Contact Email: (.*?)</p>");
	}

	/*
	 <p>Contact Adress: Master Secretariat</p>
	 */
	private Pattern getContactAdressPattern() {
		return Pattern.compile("<p>Contact Adress: (.*?)</p>");
	}

	/*
	 <p> <strong> Application Deadline :  2 July 2012 </strong> </p> 
		
	 */
	private Pattern getApplicationDeadlinePattern() {
		return Pattern.compile("Application Deadline : (.*?) </strong>");
	}

	private Pattern getContentsPattern() {
		return Pattern.compile(">(.*?)<");
	}

	/*
	 * <title>
Italy : Master of Arts in Human Rights and Conflict Management, Scuola Sant Anna, Pisa - Scholarships-Links.com 
</title></head>
	 */
	private Pattern getTitlePattern() {
		return Pattern.compile("<title>(.*?)</title></head>",Pattern.DOTALL);
	}

	/*
	 *   <div class="centerbox" > 
 
  <h1> <a href="http://www.scholarships-links.com/viewdetail/3769/Post-doctoral-Position-in-Time-Series-Analysis-Forecasting.html"> 
   UAE  : Post-doctoral Position in Time Series Analysis-Forecasting </a> </h1>
      Posted on : 2012-05-29 12:30:31<br />
  
</div>      <!-- End of CenterBox Div-->
	 */
	private Pattern getLinksPattern() {
		return Pattern.compile("<div class=\"centerbox\" >.*?<h1> <a href=\"(.*?)\"",Pattern.DOTALL  );
	}

}
