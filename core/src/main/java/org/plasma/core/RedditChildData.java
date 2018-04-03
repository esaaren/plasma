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
	private String createdUtc;
	@SerializedName(value = "score")
	private String score;
	@SerializedName(value = "id")
	private String id;
	@SerializedName(value = "subreddit_id")
	private String subredditId;
	@SerializedName(value = "link_id")
	private String linkId;
	@SerializedName(value = "link_title")
	private String linkTitle;
	@SerializedName(value = "controversiality")
	private String controversiality;
	@SerializedName(value = "gilded")
	private String gilded;
	@SerializedName(value = "upvote_ratio")
	private String upvoteRatio;
	@SerializedName(value = "subreddit_name_prefixed")
	private String subredditNamePrefixed;
	@SerializedName(value = "link_flair_text")
	private String linkFlairText;
	public String getPermalink() {
		return permalink;
	}
	public String getUrl() {
		return url;
	}
	public String getBody() {
		return body;
	}
	public String getTitle() {
		return title;
	}
	public String getCreatedUtc() {
		return createdUtc;
	}
	public String getScore() {
		return score;
	}
	public String getId() {
		return id;
	}
	public String getSubredditId() {
		return subredditId;
	}
	public String getLinkId() {
		return linkId;
	}
	public String getLinkTitle() {
		return linkTitle;
	}
	public String getControversiality() {
		return controversiality;
	}
	public String getGilded() {
		return gilded;
	}
	public String getUpvoteRatio() {
		return upvoteRatio;
	}
	public String getSubredditNamePrefixed() {
		return subredditNamePrefixed;
	}
	public String getLinkFlairText() {
		return linkFlairText;
	}
	public void setPermalink(String permalink) {
		this.permalink = permalink;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public void setCreatedUtc(String createdUtc) {
		this.createdUtc = createdUtc;
	}
	public void setScore(String score) {
		this.score = score;
	}
	public void setId(String id) {
		this.id = id;
	}
	public void setSubredditId(String subredditId) {
		this.subredditId = subredditId;
	}
	public void setLinkId(String linkId) {
		this.linkId = linkId;
	}
	public void setLinkTitle(String linkTitle) {
		this.linkTitle = linkTitle;
	}
	public void setControversiality(String controversiality) {
		this.controversiality = controversiality;
	}
	public void setGilded(String gilded) {
		this.gilded = gilded;
	}
	public void setUpvoteRatio(String upvoteRatio) {
		this.upvoteRatio = upvoteRatio;
	}
	public void setSubredditNamePrefixed(String subredditNamePrefixed) {
		this.subredditNamePrefixed = subredditNamePrefixed;
	}
	public void setLinkFlairText(String linkFlairText) {
		this.linkFlairText = linkFlairText;
	}
	
}
