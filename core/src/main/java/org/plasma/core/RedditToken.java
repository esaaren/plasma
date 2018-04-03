package org.plasma.core;

import com.google.gson.annotations.SerializedName;


/*
 * Returns a reddit token object containing fields. Use Gson to parse. Sample Json:
 * {
    	"access_token": "ma0PtpaGiN1PoZlyAjilCHOiUGY",
    	"token_type": "bearer",
    	"expires_in": 3600,
    	"scope": "*"
	}
 */
public class RedditToken {
	
	@SerializedName(value = "access_token")
	private String token;
	@SerializedName(value = "token_type")
	private String tokenType;
	@SerializedName(value = "expires_in")
	private String expiresIn;
	@SerializedName(value = "scope")
	private String scope;
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getTokenType() {
		return tokenType;
	}
	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}
	public String getExpiresIn() {
		return expiresIn;
	}
	public void setExpiresIn(String expiresIn) {
		this.expiresIn = expiresIn;
	}
	public String getScope() {
		return scope;
	}
	public void setScope(String scope) {
		this.scope = scope;
	}
	
	
	
}
