package org.plasma.core;

import java.util.List;

import com.google.gson.annotations.SerializedName;

// Data array under the main reddit response
public class RedditResponseData {

	@SerializedName(value = "after")
	private String after;
	@SerializedName(value = "dist")
	private int dist;
	@SerializedName(value = "modhash")
	private String modhash;
	@SerializedName(value = "whitelist_status")
	private String whitelistStatus;
	@SerializedName(value = "children")
	private List<RedditChild> children;
	public String getAfter() {
		return after;
	}
	public int getDist() {
		return dist;
	}
	public String getModhash() {
		return modhash;
	}
	public String getWhitelistStatus() {
		return whitelistStatus;
	}
	public List<RedditChild> getChildren() {
		return children;
	}
	public void setAfter(String after) {
		this.after = after;
	}
	public void setDist(int dist) {
		this.dist = dist;
	}
	public void setModhash(String modhash) {
		this.modhash = modhash;
	}
	public void setWhitelistStatus(String whitelistStatus) {
		this.whitelistStatus = whitelistStatus;
	}
	public void setChildren(List<RedditChild> children) {
		this.children = children;
	}
	
	
	
}
