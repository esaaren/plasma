package org.plasma.core;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.bigquery.Bigquery;
import com.google.api.services.bigquery.BigqueryScopes;
import com.google.api.services.bigquery.model.Dataset;
import com.google.api.services.bigquery.model.DatasetReference;
import com.google.api.services.bigquery.model.Table;
import com.google.api.services.bigquery.model.TableFieldSchema;
import com.google.api.services.bigquery.model.TableReference;
import com.google.api.services.bigquery.model.TableRow;
import com.google.api.services.bigquery.model.TableSchema;
import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryError;
import com.google.cloud.bigquery.BigQueryOptions;
import com.google.cloud.bigquery.DatasetInfo;
import com.google.cloud.bigquery.Field;
import com.google.cloud.bigquery.FieldValueList;
import com.google.cloud.bigquery.InsertAllRequest;
import com.google.cloud.bigquery.InsertAllResponse;
import com.google.cloud.bigquery.Job;
import com.google.cloud.bigquery.JobException;
import com.google.cloud.bigquery.JobId;
import com.google.cloud.bigquery.JobInfo;
import com.google.cloud.bigquery.LegacySQLTypeName;
import com.google.cloud.bigquery.QueryJobConfiguration;
import com.google.cloud.bigquery.QueryResponse;
import com.google.cloud.bigquery.Schema;
import com.google.cloud.bigquery.StandardTableDefinition;
import com.google.cloud.bigquery.TableDefinition;
import com.google.cloud.bigquery.TableId;
import com.google.cloud.bigquery.TableInfo;
import com.google.cloud.bigquery.TableResult;
import com.google.cloud.bigquery.TimePartitioning;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class PlasmaBigQueryTools {
	
	// Returns a BQ connection object 
	public static BigQuery ConnectLocal (GoogleCredentials credentials) {
		
		  // Load credentials from JSON key file. If you can't set the GOOGLE_APPLICATION_CREDENTIALS
		  // environment variable, you can explicitly load the credentials file to construct the
		  // credentials.
		
		  // Instantiate a client.
		  BigQuery bigquery =
		      BigQueryOptions.newBuilder().setProjectId("idyllic-kit-191017").setCredentials(credentials).build().getService();

		return bigquery;
	}
	
	
	// Use when connecting from GCP 
	
	public static BigQuery ConnectGCP() {
		BigQuery bigquery =
			      BigQueryOptions.getDefaultInstance().getService();

			return bigquery;
	}
	
	// Requires a json string of comment data 
	
	public static int insertComments (String json, String databaseId, String tableName,
			GoogleCredentials credentials) {
		
		// Inserts subreddits in pools of 100 
		
		// Initialize
		Gson gson = null;
		RedditResponse response = null;
		int childrenLength = 0;
		String commentId = null;
		String postFk = null;
		String subredditFk = null;
		String permaLink = null;
		String body = null;
		String subredditPrefix = null;
		String linkTitle = null;
		int createdUTC = 0;
		int score = 0;
		
		
		// Build new gson object each cycle
		gson = new GsonBuilder().setPrettyPrinting().create();
		
		// Get the response into the RedditResponse class
		response = gson.fromJson(json, RedditResponse.class);
		
		childrenLength = response.getData().getDist();
		
		// Acquire a big query connection, setup the insert 
		
		BigQuery bqconn;
		if (credentials == null) {
			bqconn = PlasmaBigQueryTools.ConnectGCP();
		}
		else {
			bqconn = PlasmaBigQueryTools.ConnectLocal(credentials);
		}
		bqconn = PlasmaBigQueryTools.ConnectLocal(credentials);
		TableId tableId = TableId.of(databaseId, tableName);
		
		// Values of the row to insert
		Map<String, Object> rowContent = new HashMap<>();
		
		// Setup the request
		InsertAllRequest.Builder bq_request = InsertAllRequest.newBuilder(tableId);
		
		// Loop over each of the returned children (Subreddits in this case) 
		
		for (int j = 0; j < childrenLength; j++) {
				
			commentId = "t1_" + response.getData().getChildren().get(j).getChildData().getId();
			postFk = response.getData().getChildren().get(j).getChildData().getLinkId();
			subredditFk = response.getData().getChildren().get(j).getChildData().getSubredditId();
			permaLink = response.getData().getChildren().get(j).getChildData().getPermalink();
			body = response.getData().getChildren().get(j).getChildData().getBody();
			subredditPrefix = response.getData().getChildren().get(j).getChildData().getSubredditNamePrefixed();
			linkTitle = response.getData().getChildren().get(j).getChildData().getLinkTitle();
			createdUTC = response.getData().getChildren().get(j).getChildData().getCreatedUtc();
			score = response.getData().getChildren().get(j).getChildData().getScore();
			
			// Prepare the row data
			
			rowContent.put("comment_id", commentId);
			rowContent.put("post_fk", postFk);
			rowContent.put("subreddit_fk", subredditFk);
			rowContent.put("body", body);
			rowContent.put("score", score);
			rowContent.put("created_utc", createdUTC);
			rowContent.put("subreddit_prefix", subredditPrefix);
			rowContent.put("link_title", linkTitle);
			rowContent.put("permalink", permaLink);
			
			
			// Add row to the request 
			
			bq_request = bq_request.addRow(rowContent);
		}
		
		// Submit the rows as bulk to BQ 
		
		InsertAllResponse bq_response = bqconn.insertAll(bq_request.build());
		
		if (bq_response.hasErrors()) {
			
			  // If any of the insertions failed, this lets you inspect the errors
			  for (Entry<Long, List<BigQueryError>> entry : bq_response.getInsertErrors().entrySet()) {
				   Logger.getLogger(PlasmaBigQueryTools.class.getName()).log(Level.SEVERE, "BigQuery bulk insert failed", entry);
			  }
		}
		
		Logger.getLogger(PlasmaBigQueryTools.class.getName()).log(Level.INFO, "BigQuery bulk insert succeeded");
		
		return 0;
			
	}
	
	
	public static void main(String[] args) {
		
		GoogleCredentials credentials = null;
	    File credentialsPath = new File("C:\\PersonalWork\\files\\pubsub_key.json");  // Key path
	    try (FileInputStream serviceAccountStream = new FileInputStream(credentialsPath)) {
	        credentials = ServiceAccountCredentials.fromStream(serviceAccountStream);
	    } catch (FileNotFoundException ex) {
		  Logger.getLogger(PlasmaPubSubTools.class.getName()).log(Level.SEVERE, "File not found", ex);
		 
	    } catch (IOException ex) {
		  Logger.getLogger(PlasmaPubSubTools.class.getName()).log(Level.SEVERE, "IOException", ex);
	    }
	    
		// Sample how to use 
		
		BigQuery bqconn = PlasmaBigQueryTools.ConnectLocal(credentials);
		// Use the client.
		System.out.println("Datasets:");
		for (com.google.cloud.bigquery.Dataset dataset : bqconn.listDatasets().iterateAll()) {
		    System.out.printf("%s%n", dataset.getDatasetId().getDataset());
		}
		
	}
}
