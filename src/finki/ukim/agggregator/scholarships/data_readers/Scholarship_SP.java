package finki.ukim.agggregator.scholarships.data_readers;

/*
 * This class is according to the Scholarship-Positions.com Format
 */

public class Scholarship_SP {
	private String title;
	private String intro;
	private String studySubjects;
	private String courseLevel;
	private String scholarshipProvider;
	private String scholarshipTakenAt;
	private String eligibility;
	private String isScholarshipInternational;
	private String countries;
	private String description;
	private String howToApply;
	private String Deadline;
	private String linkFurther;
	private String tags;
	
	
	
	public Scholarship_SP() {

	}

	public Scholarship_SP(String title, String intro, String studySubjects,
			String courseLevel, String scholarshipProvider,
			String scholarshipTakenAt, String eligibility,
			String isScholarshipInternational, String countries,
			String description, String howToApply, String deadline,
			String linkFurther, String tags) {
		this.title = title;
		this.intro = intro;
		this.studySubjects = studySubjects;
		this.courseLevel = courseLevel;
		this.scholarshipProvider = scholarshipProvider;
		this.scholarshipTakenAt = scholarshipTakenAt;
		this.eligibility = eligibility;
		this.isScholarshipInternational = isScholarshipInternational;
		this.countries = countries;
		this.description = description;
		this.howToApply = howToApply;
		Deadline = deadline;
		this.linkFurther = linkFurther;
		this.tags = tags;
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getIntro() {
		return intro;
	}
	public void setIntro(String intro) {
		this.intro = intro;
	}
	public String getStudySubjects() {
		return studySubjects;
	}
	public void setStudySubjects(String studySubjects) {
		this.studySubjects = studySubjects;
	}
	public String getCourseLevel() {
		return courseLevel;
	}
	public void setCourseLevel(String courseLevel) {
		this.courseLevel = courseLevel;
	}
	public String getScholarshipProvider() {
		return scholarshipProvider;
	}
	public void setScholarshipProvider(String scholarshipProvider) {
		this.scholarshipProvider = scholarshipProvider;
	}
	public String getScholarshipTakenAt() {
		return scholarshipTakenAt;
	}
	public void setScholarshipTakenAt(String scholarshipTakenAt) {
		this.scholarshipTakenAt = scholarshipTakenAt;
	}
	public String getEligibility() {
		return eligibility;
	}
	public void setEligibility(String eligibility) {
		this.eligibility = eligibility;
	}
	public String getIsScholarshipInternational() {
		return isScholarshipInternational;
	}
	public void setIsScholarshipInternational(String isScholarshipInternational) {
		this.isScholarshipInternational = isScholarshipInternational;
	}
	public String getCountries() {
		return countries;
	}
	public void setCountries(String countries) {
		this.countries = countries;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getHowToApply() {
		return howToApply;
	}
	public void setHowToApply(String howToApply) {
		this.howToApply = howToApply;
	}
	public String getDeadline() {
		return Deadline;
	}
	public void setDeadline(String deadline) {
		Deadline = deadline;
	}
	public String getLinkFurther() {
		return linkFurther;
	}
	public void setLinkFurther(String linkFurther) {
		this.linkFurther = linkFurther;
	}
	public String getTags() {
		return tags;
	}
	public void setTags(String tags) {
		this.tags = tags;
	}
	
	
}
