package org.plasma.core;

// Gson and Ok Http packages
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;


// Java standard 
import java.util.Base64;
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
			return response.body().string();
		} catch (IOException ex) {
			Logger.getLogger(PlasmaRedditTools.class.getName()).log(Level.SEVERE, null, ex);
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

	public static void main(String[] args) {
			
		// Get reddit token object using plasma tools 
		RedditToken token = PlasmaRedditTools.getAuthToken("gluFwvMrQLqLuA", 
				"nowLOmNuC8tS76mrc-LQUlarngw", "plasmatrendybot", "plasmafury10");
		
		System.out.println(token.getToken());
		System.out.println(token.getTokenType());
		System.out.println(token.getExpiresIn());
		System.out.println(token.getScope());
		
		String getTop = PlasmaRedditTools.getRedditData("top?limit=1", token);
		
		// Parse the json token response body with Gson
		
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
				
		RedditResponse response = gson.fromJson(getTop, RedditResponse.class);
		System.out.println("");
		System.out.println("Json Response");
		
		String responseKind= PlasmaRedditTools.getKindName(response.getKind());
		int childrenLength = response.getData().getDist();
		String childrenKind = PlasmaRedditTools.getKindName(response.getData().getChildren().get(0).getKind());
				
		
		System.out.println("Response is a: " + responseKind);
		System.out.println("Childen are: " + childrenKind);
		System.out.println("Number of children returned is: " + Integer.toString(childrenLength));
		
		 // Initialize connection 
	     Connection conn = PostgresConnectionManager.getConnection();
	     
	     

	}

}
