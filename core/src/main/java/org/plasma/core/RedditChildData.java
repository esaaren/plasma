package org.plasma.core;

import com.google.gson.annotations.SerializedName;

public class RedditChildData {

	@SerializedName(value = "permalink")
	private String permalink;
	@SerializedName(value = "url")
	private String url;
	@SerializedName(value = "body")
	private String body;
	@SerializedName(value = "title")
	private String title;
	@SerializedName(value = "created_utc")
	private int createdUtc;
	@SerializedName(value = "score")
	private int score;
	@SerializedName(value = "id")
	private String id;
	@SerializedName(value = "subreddit_id")
	private String subredditId;
	@SerializedName(value = "link_id")
	private String linkId;
	@SerializedName(value = "link_title")
	private String linkTitle;
	@SerializedName(value = "controversiality")
	private double controversiality;
	@SerializedName(value = "gilded")
	private int gilded;
	@SerializedName(value = "upvote_ratio")
	private double upvoteRatio;
	@SerializedName(value = "subreddit_name_prefixed")
	private String subredditNamePrefixed;
	@SerializedName(value = "link_flair_text")
	private String linkFlairText;
	@SerializedName(value = "num_comments")
	private int numComments;
	@SerializedName(value = "domain")
	private String domain;
	@SerializedName(value = "subreddit_subscribers")
	private int subredditSubscribers;
	
	
	public String getPermalink() {
		return permalink;
	}
	public void setPermalink(String permalink) {
		this.permalink = permalink;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getCreatedUtc() {
		return createdUtc;
	}
	public void setCreatedUtc(int createdUtc) {
		this.createdUtc = createdUtc;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getSubredditId() {
		return subredditId;
	}
	public void setSubredditId(String subredditId) {
		this.subredditId = subredditId;
	}
	public String getLinkId() {
		return linkId;
	}
	public void setLinkId(String linkId) {
		this.linkId = linkId;
	}
	public String getLinkTitle() {
		return linkTitle;
	}
	public void setLinkTitle(String linkTitle) {
		this.linkTitle = linkTitle;
	}
	public double getControversiality() {
		return controversiality;
	}
	public void setControversiality(double controversiality) {
		this.controversiality = controversiality;
	}
	public int getGilded() {
		return gilded;
	}
	public void setGilded(int gilded) {
		this.gilded = gilded;
	}
	public double getUpvoteRatio() {
		return upvoteRatio;
	}
	public void setUpvoteRatio(double upvoteRatio) {
		this.upvoteRatio = upvoteRatio;
	}
	public String getSubredditNamePrefixed() {
		return subredditNamePrefixed;
	}
	public void setSubredditNamePrefixed(String subredditNamePrefixed) {
		this.subredditNamePrefixed = subredditNamePrefixed;
	}
	public String getLinkFlairText() {
		return linkFlairText;
	}
	public void setLinkFlairText(String linkFlairText) {
		this.linkFlairText = linkFlairText;
	}
	public int getNumComments() {
		return numComments;
	}
	public void setNumComments(int numComments) {
		this.numComments = numComments;
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public int getSubredditSubscribers() {
		return subredditSubscribers;
	}
	public void setSubredditSubscribers(int subredditSubscribers) {
		this.subredditSubscribers = subredditSubscribers;
	}
	
	

	
}
