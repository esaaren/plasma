package org.plasma.core;

import java.util.List;

import com.google.gson.annotations.SerializedName;

// Child array under the reddit response data array, usually many children
public class RedditChild {

	@SerializedName(value = "kind")
	private String kind;
	@SerializedName(value = "data")
	private RedditChildData childData;
	
	
	public String getKind() {
		return kind;
	}
	public RedditChildData getChildData() {
		return childData;
	}
	public void setKind(String kind) {
		this.kind = kind;
	}
	public void setChildData(RedditChildData childData) {
		this.childData = childData;
	}
	
	
	
}
