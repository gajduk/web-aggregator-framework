package finki.ukim.agggregator.scholarships.data_readers;

public class Scholarshipportal_SP {
	private String title;
	private String provider;
	private String summary;
	private String duration;
	private String purpose;
	private String level;
	private String quota;
	private String application_deadline;
	private String benefits_summary;
	private String description;
	private String eligibility;
	private String duration_1;
	private String benefits;
	private String application_information;
	private String relevant_links;
	private String additional_information;
	
	/*
	 * these two had to do with eligibility
	 */
	private String come_from;
	private String go_to;
	
	
	
	
	public Scholarshipportal_SP(String title, String provider, String summary,
			String duration, String purpose, String level, String quota,
			String application_deadline, String benefits_summary,
			String description, String eligibility, String duration_1,
			String benefits, String application_information,
			String relevant_links, String additional_information,
			String come_from, String go_to) {
		super();
		this.title = title;
		this.provider = provider;
		this.summary = summary;
		this.duration = duration;
		this.purpose = purpose;
		this.level = level;
		this.quota = quota;
		this.application_deadline = application_deadline;
		this.benefits_summary = benefits_summary;
		this.description = description;
		this.eligibility = eligibility;
		this.duration_1 = duration_1;
		this.benefits = benefits;
		this.application_information = application_information;
		this.relevant_links = relevant_links;
		this.additional_information = additional_information;
		this.come_from = come_from;
		this.go_to = go_to;
	}
	
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getProvider() {
		return provider;
	}
	public void setProvider(String provider) {
		this.provider = provider;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public String getDuration() {
		return duration;
	}
	public void setDuration(String duration) {
		this.duration = duration;
	}
	public String getPurpose() {
		return purpose;
	}
	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public String getQuota() {
		return quota;
	}
	public void setQuota(String quota) {
		this.quota = quota;
	}
	public String getApplication_deadline() {
		return application_deadline;
	}
	public void setApplication_deadline(String application_deadline) {
		this.application_deadline = application_deadline;
	}
	public String getBenefits_summary() {
		return benefits_summary;
	}
	public void setBenefits_summary(String benefits_summary) {
		this.benefits_summary = benefits_summary;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getEligibility() {
		return eligibility;
	}
	public void setEligibility(String eligibility) {
		this.eligibility = eligibility;
	}
	public String getDuration_1() {
		return duration_1;
	}
	public void setDuration_1(String duration_1) {
		this.duration_1 = duration_1;
	}
	public String getBenefits() {
		return benefits;
	}
	public void setBenefits(String benefits) {
		this.benefits = benefits;
	}
	public String getApplication_information() {
		return application_information;
	}
	public void setApplication_information(String application_information) {
		this.application_information = application_information;
	}
	public String getRelevant_links() {
		return relevant_links;
	}
	public void setRelevant_links(String relevant_links) {
		this.relevant_links = relevant_links;
	}
	public String getAdditional_information() {
		return additional_information;
	}
	public void setAdditional_information(String additional_information) {
		this.additional_information = additional_information;
	}
	public String getCome_from() {
		return come_from;
	}
	public void setCome_from(String come_from) {
		this.come_from = come_from;
	}
	public String getGo_to() {
		return go_to;
	}
	public void setGo_to(String go_to) {
		this.go_to = go_to;
	}
	
	
	
}
