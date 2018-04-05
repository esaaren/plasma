package org.plasma.core;

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
import java.util.logging.Level;
import java.util.logging.Logger;
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
		
		Logger.getLogger(PlasmaRedditTools.class.getName()).log(Level.INFO, "Executing GET: " + request_url);
		
		OkHttpClient client = new OkHttpClient();

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
			// Return the json string 
			return response.body().string();
		} catch (IOException ex) {
			Logger.getLogger(PlasmaRedditTools.class.getName()).log(Level.SEVERE, null, ex);
			ex.printStackTrace();
			return "Get request Failed";
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
	
	public static int loadSubreddits(int numberSubreddits, RedditToken token) {
		
		
		// Determine how many subreddits we want
		
		int numberSubRedditsToGet = ((numberSubreddits + 99 ) / 100) * 100; // Round number to nearest hundred  
		int subRedditLimit = 100; // 100 per fetch is the max 
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
			
			PlasmaBigQueryTools.insertSubreddits(getTop);
			
		}
		
		return 0;
	}

	public static int loadComments(int limit, List<String> subreddits) {
		
		
		
		return 0;
	}
	
	public static void main(String[] args) {
			
		// Get reddit token object using plasma tools 
		
		Logger.getLogger(PlasmaRedditTools.class.getName()).log(Level.INFO,"Authenticating with Reddit");
		
		RedditToken token = PlasmaRedditTools.getAuthToken("gluFwvMrQLqLuA", 
				"nowLOmNuC8tS76mrc-LQUlarngw", "plasmatrendybot", "plasmafury10");
		
		Logger.getLogger(PlasmaRedditTools.class.getName()).log(Level.INFO,"Token Expires in: " + token.getExpiresIn() );
		
		// Load subreddits
		PlasmaRedditTools.loadSubreddits(101, token); //Already loaded now 

		// Get subreddits
		
		List<String> subreddits = PlasmaBigQueryTools.getSubredditNames("plasma.subrt_results");
		
	}
		 
}