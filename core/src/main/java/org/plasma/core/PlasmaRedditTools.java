package org.plasma.core;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
// Gson and Ok Http packages
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;


// Java standard 
import java.util.Base64;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;

public class PlasmaRedditTools {

	// Returns a reddit auth token for 1 hour of use
	public static RedditToken getAuthToken(String clientid, String secret, String user, String pass) {
		
		// Using Ok Http for posting to reddit
		OkHttpClient client = new OkHttpClient();

		// Reddit authentication clientid/clientsecret as user/pass basic auth header
		
		String encodeThis = clientid + ":" + secret;
		
		String encoding = Base64.getEncoder().encodeToString((encodeThis).getBytes());
				
		// Url form encoded because needed for reddit
		MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
		
		
		String request_body = "grant_type=password&username=" + 
				user + "&password=" + pass;
				
		
		RequestBody body = RequestBody.create(mediaType, request_body);
		Request request = new Request.Builder()
		  .url("https://www.reddit.com/api/v1/access_token")
		  .post(body)
		  .addHeader("authorization", "Basic " + encoding)
		  .addHeader("content-type", "application/x-www-form-urlencoded")
		  .addHeader("cache-control", "no-cache")
		  .build();

		String tokenJson = null;
		try {
			Response response = client.newCall(request).execute();
			tokenJson = response.body().string();
						
		} catch (Exception ex) {
			Logger.getLogger(PlasmaRedditTools.class.getName()).log(Level.SEVERE, null, ex); 
		}
		
		// Parse the json token response body with Gson
		
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		
		RedditToken token = gson.fromJson(tokenJson, RedditToken.class);
		
		return token;
		
	}
	
	// Use a reddit url suffix, e.g r/toronto, top or best
	public static String getRedditData(String url_suffix, RedditToken token) {
		
		String base_url = "https://oauth.reddit.com/";
		String request_url = base_url + url_suffix;
		String response_body;
		
		Logger.getLogger(PlasmaRedditTools.class.getName()).log(Level.INFO, "Executing GET: " + request_url);
		
		OkHttpClient client = new OkHttpClient();
		
		client.setConnectTimeout(15, TimeUnit.SECONDS); // connect timeout
		client.setReadTimeout(15, TimeUnit.SECONDS);    // socket timeout
		
		Request request = new Request.Builder()
		  .url(request_url)
		  .get()
		  .addHeader("authorization", "bearer " + token.getToken())
		  .addHeader("cache-control", "no-cache")
		  .build();

		Response response;
		try {
			response = client.newCall(request).execute();
			
			// Get header
			Headers header = response.headers();
			
			// Check if auth has expired by inspecting body contents for unauthorized
			
			response_body = response.body().string();
			
			// Check if token expired
			if (header.get("www-authenticate") != null) {
				
				if (header.get("www-authenticate").contains("invalid_token")) {
					return "token_expired";
				}
				
			}
			
			// Get limit remaining and if less than 10 remaining then sleep for remaining window
			
			// Reddit requests left 
			double remaining = Double.parseDouble(header.get("x-ratelimit-remaining"));
			
			
			// Time left in the window 
			long remaining_time_seconds = Long.parseLong(header.get("x-ratelimit-reset"));
			
			Logger.getLogger(PlasmaRedditTools.class.getName()).log(Level.INFO, 
					"REDDIT requests left: " + Double.toString(remaining) +
					", duration remaining: " + Long.toString(remaining_time_seconds));
			
			if (remaining < 10.0) {
				try{
					Logger.getLogger(PlasmaRedditTools.class.getName()).log(Level.WARNING, 
							"Reddit 10 minute 600 request window exceeded. Sleeping for " +
								Long.toString(remaining_time_seconds) + " seconds");
				    Thread.sleep(remaining_time_seconds*1000);
				} 
				catch(InterruptedException ex) {
				    Thread.currentThread().interrupt();
				    Logger.getLogger(PlasmaRedditTools.class.getName()).log(Level.SEVERE, "Can't sleep", ex);
				    ex.printStackTrace();
				}
			}
			
			// Sleep for 1.5s because if no delay we will pull duplicate data 
			
			try {
				Thread.sleep(1500);
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
			    Logger.getLogger(PlasmaRedditTools.class.getName()).log(Level.SEVERE, "Can't sleep", ex);
			    ex.printStackTrace();
			}
						
			// Return the json string 
			return response_body;
		} catch (Exception ex) {
			Logger.getLogger(PlasmaRedditTools.class.getName()).log(Level.SEVERE, null, ex);
			ex.printStackTrace();
			return "request_failed";
		}
		
	}
	
	public static String getKindName(String kindCd) {
		
		if (kindCd.equals("t1")) {
			return "comment";
		}
		else if (kindCd.equals("t2")) {
			return "account";
		}
		else if (kindCd.equals("t3")) {
			return "link";		
		}
		else if (kindCd.equals("t4")) {
			return "message";
		}
		else if (kindCd.equals("t5")) {
			return "subreddit";
		}
		else if (kindCd.equals("t6")) {
			return "award";
		}
		else if (kindCd.equals("Listing")) {
			return "listing";
		}
		else {
			return "null";
		}
		
	}
	
	public static int loadSubreddits(int numberSubreddits, String databaseId, String tableName, RedditToken token) {
		
		
		// Determine how many subreddits we want
		
		int numberSubRedditsToGet = ((numberSubreddits + 99 ) / 100) * 100; // Round number to nearest hundred  
		int subRedditLimit = 100; // 100 per fetch is the max from reddit
		int numSubredditLoops = 0;
		
		if (numberSubRedditsToGet == 100) {
			numSubredditLoops = 1;
		}
		else {
			numSubredditLoops = (numberSubRedditsToGet/subRedditLimit) - 1; // Number times to call reddit 
		}
		
		
		// Initialize vars 
		String getTop = ""; // Json string holding the reddit response data for 'top subreddits'
		String getAfter = ""; // String holding the after keyword for looping over reddit responses
		Gson gson = null;
		RedditResponse response = null;
		String responseKind = null;
		int childrenLength = 0;
		String childrenKind = null;
		
		Logger.getLogger(PlasmaRedditTools.class.getName()).log(Level.INFO, 
				"Number of reddits specified: " + Integer.toString(numberSubreddits) +
				", Will submit in batches of 100 " + Integer.toString(numSubredditLoops) + " times.");

		
		for (int i = 0 ; i < numSubredditLoops; i ++) {
			
			if (i==0) {
				
				// Get top 100 subreddits only on first loop
				getTop = PlasmaRedditTools.getRedditData("subreddits?limit=" + 
						Integer.toString(subRedditLimit), token);
			}
			else { 
				
				// Get next 100 subreddits after every loop
				getTop = PlasmaRedditTools.getRedditData("subreddits?limit=" + 
						Integer.toString(subRedditLimit) + "&after=" + getAfter, token);
			}
			
			// Build new gson object each cycle
			gson = new GsonBuilder().setPrettyPrinting().create();
			
			// Get the response into the RedditResponse class
			response = gson.fromJson(getTop, RedditResponse.class);
			
			// Get and output basic response data 
			responseKind= PlasmaRedditTools.getKindName(response.getKind());
			childrenLength = response.getData().getDist();
			childrenKind = PlasmaRedditTools.getKindName(response.getData().getChildren().get(0).getKind());
			getAfter = response.getData().getAfter(); // Used to query the next group 
			
			
			Logger.getLogger(PlasmaRedditTools.class.getName()).log(Level.INFO, "Response is a: " + responseKind );
			Logger.getLogger(PlasmaRedditTools.class.getName()).log(Level.INFO, "Childen are: " + childrenKind );
			Logger.getLogger(PlasmaRedditTools.class.getName()).log(Level.INFO, "Number of children returned is: " + Integer.toString(childrenLength) );
			Logger.getLogger(PlasmaRedditTools.class.getName()).log(Level.INFO, "Inserting " + Integer.toString(childrenLength) 
				+ " subreddit records into BQ");
			
			//PlasmaBigQueryTools.insertSubreddits(getTop, databaseId, tableName,
					//"C:\\PersonalWork\\files\\bq_key.json");
			
		}
		
		return 0;
	}

	
	public static int loadPostsWithUrl(int limit, String url, String databaseId, String tableName, RedditToken token) {
		
		int numberPostsToGet = ((limit + 99 ) / 100) * 100;
		
		int subRedditLimit = 100; // 100 per fetch is the max from reddit
		int numSubredditLoops = 0;
		
		if (numberPostsToGet == 100) {
			numSubredditLoops = 1;
		}
		else {
			numSubredditLoops = (numberPostsToGet/subRedditLimit) - 1; // Number times to call reddit 
		}
		
		// Initialize vars 
		String getPostJson = ""; // Json string holding the reddit response data for 'top subreddits'
		String getAfter = ""; // String holding the after keyword for looping over reddit responses
		Gson gson = null;
		RedditResponse response = null;
		String responseKind = null;
		int childrenLength = 0;
		String childrenKind = null;
		
		// Loop over based on limit given to get all posts and load them 
		for (int i = 0 ; i < numSubredditLoops; i ++) {
			
			if (i==0) {
				
				// Get top 100 posts only on first loop
				getPostJson = PlasmaRedditTools.getRedditData(url + "?limit=" +
						Integer.toString(subRedditLimit), token);
			}
			else { 
				
				// Get next 100 subreddits after every loop
				getPostJson = PlasmaRedditTools.getRedditData(url + "?limit=" + 
						Integer.toString(subRedditLimit) + "&after=" + getAfter, token);
			}
			
			// Build new gson object each cycle
			gson = new GsonBuilder().setPrettyPrinting().create();
			
			// Get the response into the RedditResponse class
			response = gson.fromJson(getPostJson, RedditResponse.class);
			
			// Get and output basic response data 
			responseKind= PlasmaRedditTools.getKindName(response.getKind());
			childrenLength = response.getData().getDist();
			childrenKind = PlasmaRedditTools.getKindName(response.getData().getChildren().get(0).getKind());
			getAfter = response.getData().getAfter(); // Used to query the next group 
			
			
			Logger.getLogger(PlasmaRedditTools.class.getName()).log(Level.INFO, "Response is a: " + responseKind );
			Logger.getLogger(PlasmaRedditTools.class.getName()).log(Level.INFO, "Childen are: " + childrenKind );
			Logger.getLogger(PlasmaRedditTools.class.getName()).log(Level.INFO, "Number of children returned is: " + Integer.toString(childrenLength) );
			Logger.getLogger(PlasmaRedditTools.class.getName()).log(Level.INFO, "Inserting " + Integer.toString(childrenLength) 
				+ " post records into BQ");
			
			//PlasmaBigQueryTools.insertPosts(getPostJson, databaseId, tableName,
					//"C:\\PersonalWork\\files\\bq_key.json");
			
		}
		
		return 0;
	}
	
	
	// Specify number of calls to make 
	public static int loadCommentsWithUrl (int numCalls, String url, String databaseId, 
			String tableName, RedditToken token, GoogleCredentials credentials) {
		
		int subRedditLimit = 100;
		
		// Initialize vars 
		String getCommentJson = ""; // Json string holding the reddit response data for comments
		String getAfter = ""; // String holding the after keyword for looping over reddit responses
		Gson gson = null;
		RedditResponse response = null;
		String responseKind = null;
		int childrenLength = 0;
		String childrenKind = null;
		
		// Loop over based on limit given to get all posts and load them. 
		for (int i = 0 ; i < numCalls; i ++) {
			
			//System.out.println(i);
			if (i==0) {
				
				// Get top 100 posts only on first loop
				getCommentJson = PlasmaRedditTools.getRedditData(url + "?limit=" +
						Integer.toString(subRedditLimit), token);
				
				// Check if return string is token expiry
				if (getCommentJson.equals("token_expired")) {
					return 2;
				}
			}
			else { 
				
				// Get next 100 subreddits after every loop
				getCommentJson = PlasmaRedditTools.getRedditData(url + "?limit=" + 
						Integer.toString(subRedditLimit), token);
				
				// Check if return string is token expiry
				if (getCommentJson.equals("token_expired")) {
					return 2;
				}
			}
			
			
			// Build new gson object each cycle
			gson = new GsonBuilder().setPrettyPrinting().create();
			
			// Get the response into the RedditResponse class
			response = gson.fromJson(getCommentJson, RedditResponse.class);
			
			
			// Get and output basic response data 
			responseKind= response.getKind();
			childrenLength = response.getData().getDist();
			childrenKind = response.getData().getChildren().get(0).getKind();
			getAfter = response.getData().getAfter(); // Used to query the next group 
			
			/*
			Logger.getLogger(PlasmaRedditTools.class.getName()).log(Level.INFO, "Response is a: " + responseKind );
			Logger.getLogger(PlasmaRedditTools.class.getName()).log(Level.INFO, "Childen are: " + childrenKind );
			Logger.getLogger(PlasmaRedditTools.class.getName()).log(Level.INFO, "Number of children returned is: " + Integer.toString(childrenLength) );
			Logger.getLogger(PlasmaRedditTools.class.getName()).log(Level.INFO, "Inserting " + Integer.toString(childrenLength) 
				+ " post records into BQ"); */
			
			//PlasmaBigQueryTools.insertComments(getCommentJson, databaseId, tableName,
					//credentials);
			PlasmaPubSubTools.publish("reddit_topic", getCommentJson);
			
		}
		
		return 0;
	}
	
	public static void main(String[] args) {
		
		// Database names 
		
		String databaseId = "trendy";
		
		// Table names 
		String subrTableName = "subreddits";
		String postTableName = "posts";
		String commentTableName = "comments";
		
		// Job execution
		
		// Each hour the token will expire, so the number of times to execute is synonymous with hours
		int timesToExecute = 200;
		
		// Get reddit token object using plasma tools 
		
		Logger.getLogger(PlasmaRedditTools.class.getName()).log(Level.INFO,"Authenticating with Reddit");
		
		RedditToken token = PlasmaRedditTools.getAuthToken("gluFwvMrQLqLuA", 
				"nowLOmNuC8tS76mrc-LQUlarngw", "plasmatrendybot", "plasmafury10");
		
		Logger.getLogger(PlasmaRedditTools.class.getName()).log(Level.INFO,"Token Expires in: " + token.getExpiresIn() );
		
		// Google creds so authentication can happen with BQ or pubsub
		// bq for bigquery pubsub for pub/sub
		GoogleCredentials credentials = PlasmaGoogleCloudTools.getCredentials("pubsub");

		
		int loadResponse = 0;
		
		for (int i = 0; i < timesToExecute; i++) {
			try {
				System.out.println("LOADING:");
				loadResponse = PlasmaRedditTools.loadCommentsWithUrl(5000,
						"r/all/comments", databaseId, commentTableName,	token, credentials);
				if (loadResponse == 2) {
					Logger.getLogger(PlasmaRedditTools.class.getName()).log(Level.INFO,"Token Has Expired, Authenticating again");
					token = PlasmaRedditTools.getAuthToken("gluFwvMrQLqLuA", 
							"nowLOmNuC8tS76mrc-LQUlarngw", "plasmatrendybot", "plasmafury10");
					Logger.getLogger(PlasmaRedditTools.class.getName()).log(Level.INFO,"Token Expires in: " + token.getExpiresIn() );
				}
			} catch (Exception ex) {
				
				Logger.getLogger(PlasmaRedditTools.class.getName()).log(Level.INFO, "Something failed within the load job" );
				ex.printStackTrace();
				System.exit(1);
			}
		}
		
		
		
	}
		 
}