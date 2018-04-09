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
	public static BigQuery Connect () {
		
		  // Load credentials from JSON key file. If you can't set the GOOGLE_APPLICATION_CREDENTIALS
		  // environment variable, you can explicitly load the credentials file to construct the
		  // credentials.
		  GoogleCredentials credentials = null;
		  File credentialsPath = new File("C:\\PersonalWork\\files\\bq_key.json");  // Key path
		  try (FileInputStream serviceAccountStream = new FileInputStream(credentialsPath)) {
		      credentials = ServiceAccountCredentials.fromStream(serviceAccountStream);
		  } catch (FileNotFoundException ex) {
			  Logger.getLogger(PlasmaBigQueryTools.class.getName()).log(Level.SEVERE, "File not found", ex);
		  } catch (IOException ex) {
			  Logger.getLogger(PlasmaBigQueryTools.class.getName()).log(Level.SEVERE, "IOException", ex);
		  }

		  // Instantiate a client.
		  BigQuery bigquery =
		      BigQueryOptions.newBuilder().setProjectId("idyllic-kit-191017").setCredentials(credentials).build().getService();

		return bigquery;
	}
	
	
	// Requires a json string of subreddit data 
	public static int insertSubreddits (String subrJson, String databaseId, String tableName) {
		
		// Inserts subreddits in pools of 100 
		
		// Initialize
		Gson gson = null;
		RedditResponse response = null;
		int childrenLength = 0;
		String subredditId = null;
		String subredditName = null;
		int subredditSubscribers = 0;
		String subredditDesc = null;
		String subredditTarget = null;
		
		
		// Build new gson object each cycle
		gson = new GsonBuilder().setPrettyPrinting().create();
		
		// Get the response into the RedditResponse class
		response = gson.fromJson(subrJson, RedditResponse.class);
		
		childrenLength = response.getData().getDist();
		
		subredditId = null;
		subredditName = null;
		subredditSubscribers = 0;
		
		// Acquire a big query connection, setup the insert 
		
		BigQuery bqconn = PlasmaBigQueryTools.Connect();
		TableId tableId = TableId.of(databaseId, tableName);
		
		// Values of the row to insert
		Map<String, Object> rowContent = new HashMap<>();
		
		// Setup the request
		InsertAllRequest.Builder bq_request = InsertAllRequest.newBuilder(tableId);
		
		// Loop over each of the returned children (Subreddits in this case) 
		
		for (int j = 0; j < childrenLength; j++) {
			
			subredditId = "t5_" + response.getData().getChildren().get(j).getChildData().getId();
			subredditName = response.getData().getChildren().get(j).getChildData().getSubrDisplayName();
			subredditSubscribers = response.getData().getChildren().get(j).getChildData().getSubredditSubscribers();
			subredditDesc = response.getData().getChildren().get(j).getChildData().getSubrPublicDesc();
			subredditTarget = response.getData().getChildren().get(j).getChildData().getSubrTarget();
			
			/*  Useful print statement to see the subreddit data 
			System.out.println(subredditId);
			System.out.println(subredditName);
			System.out.println(subredditSubscribers);
			System.out.println(subredditDesc);
			System.out.println(subredditTarget); */
			
			
			// Prepare the row 
			
			rowContent.put("subr_id", subredditId);
			rowContent.put("subr_name", subredditName);
			rowContent.put("num_subscribers", subredditSubscribers);
			rowContent.put("description", subredditDesc);
			rowContent.put("audience_target", subredditTarget);
			
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

	// Requires a json string of post data
	
	public static int insertPosts (String subrJson, String databaseId, String tableName) {
		
		// Inserts subreddits in pools of 100 
		
		// Initialize
		Gson gson = null;
		RedditResponse response = null;
		int childrenLength = 0;
		String linkId = null;
		String title = null;
		String subredditFk = null;
		String permaLink = null;
		int numComments = 0;
		int createdUTC = 0;
		int score = 0;
		String linkFlair = null;
		Double controversiality = 0.0;
		
		
		// Build new gson object each cycle
		gson = new GsonBuilder().setPrettyPrinting().create();
		
		// Get the response into the RedditResponse class
		response = gson.fromJson(subrJson, RedditResponse.class);
		
		childrenLength = response.getData().getDist();
		
		// Acquire a big query connection, setup the insert 
		
		BigQuery bqconn = PlasmaBigQueryTools.Connect();
		TableId tableId = TableId.of(databaseId, tableName);
		
		// Values of the row to insert
		Map<String, Object> rowContent = new HashMap<>();
		
		// Setup the request
		InsertAllRequest.Builder bq_request = InsertAllRequest.newBuilder(tableId);
		
		// Loop over each of the returned children (Subreddits in this case) 
		
		for (int j = 0; j < childrenLength; j++) {
				
			linkId = response.getData().getChildren().get(j).getChildData().getId();
			title = response.getData().getChildren().get(j).getChildData().getTitle();
			subredditFk = response.getData().getChildren().get(j).getChildData().getSubredditId();
			permaLink = response.getData().getChildren().get(j).getChildData().getPermalink();
			createdUTC = response.getData().getChildren().get(j).getChildData().getCreatedUtc();
			score = response.getData().getChildren().get(j).getChildData().getScore();
			numComments = response.getData().getChildren().get(j).getChildData().getNumComments();
			linkFlair = response.getData().getChildren().get(j).getChildData().getLinkFlairText();
			controversiality = response.getData().getChildren().get(j).getChildData().getControversiality();
			
			
			// Prepare the row data
			
			rowContent.put("link_id", linkId);
			rowContent.put("title", title);
			rowContent.put("subreddit_id", subredditFk);
			rowContent.put("permalink", permaLink);
			rowContent.put("num_comments", numComments);
			rowContent.put("created_utc", createdUTC);
			rowContent.put("score", score);
			rowContent.put("link_flair", linkFlair);
			rowContent.put("controversiality", controversiality);
			
			
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
	
	// Returns a list of subreddits 
	public static List<String> getSubredditNames(String tablename) {
		
		List<String> subreddits = new ArrayList<String>();
		
		String query = "SELECT "
	              + "subr_name "
	              + "FROM `" 
	              + tablename
	              + "` ORDER BY num_subscribers desc";
		
		// Connect & Authenticate
		BigQuery bqconn = PlasmaBigQueryTools.Connect();
		
		QueryJobConfiguration queryConfig =
		        QueryJobConfiguration.newBuilder(query)
		            // Use standard SQL syntax for queries.
		            // See: https://cloud.google.com/bigquery/sql-reference/
		            .setUseLegacySql(false)
		            .build();

	    // Create a job ID so that we can safely retry.
	    JobId jobId = JobId.of(UUID.randomUUID().toString());
	    Job queryJob = bqconn.create(JobInfo.newBuilder(queryConfig).setJobId(jobId).build());

	    // Wait for the query to complete.
	    try {
			queryJob = queryJob.waitFor();
		} catch (InterruptedException ex) {
			Logger.getLogger(PlasmaBigQueryTools.class.getName()).log(Level.SEVERE, "Interrupted", ex);
		}
	    
	    // Check for errors
	    if (queryJob == null) {
	      throw new RuntimeException("Job no longer exists");
	    } else if (queryJob.getStatus().getError() != null) {
	      // You can also look at queryJob.getStatus().getExecutionErrors() for all
	      // errors, not just the latest one.
	      throw new RuntimeException(queryJob.getStatus().getError().toString());
	    }
	    
	    // Get the response from the job.
	    QueryResponse response = bqconn.getQueryResults(jobId);

	    // Get the results
	    TableResult result=null;
		try {
			result = queryJob.getQueryResults();
		} catch (JobException ex) {
			Logger.getLogger(PlasmaBigQueryTools.class.getName()).log(Level.SEVERE, "JobException", ex);
			ex.printStackTrace();
		} catch (InterruptedException ex) {
			Logger.getLogger(PlasmaBigQueryTools.class.getName()).log(Level.SEVERE, "Interrupted", ex);
			ex.printStackTrace();
		}

	    // Add results to an array list
	    for (FieldValueList row : result.iterateAll()) {
	    	String name = row.get("subr_name").getStringValue();
	    	//long viewCount = row.get("view_count").getLongValue(); Use this for integers/longs etc
	    	subreddits.add(name);
	    }
	    
		return subreddits;
		
	}
	
	public static int createTables (String projectId, String datasetId, String subrTableName, 
			String postTableName, String commentTableName, int skipSubrTableCreate) { 
		
		BigQuery bqconn = PlasmaBigQueryTools.Connect(); // Get big query connection

		if (skipSubrTableCreate == 0) {
			
			// ***************************** SUBR TABLE *******************************
			TableId subrTable = TableId.of(datasetId, subrTableName);
			// Table field definition
			Field subr_id = Field.of("subr_id", LegacySQLTypeName.STRING);
			Field subr_name = Field.of("subr_name", LegacySQLTypeName.STRING);
			Field num_subscribers =  Field.of("num_subscribers", LegacySQLTypeName.INTEGER);
			Field subr_cat = Field.of("subr_cat", LegacySQLTypeName.STRING);
			Field description = Field.of("description", LegacySQLTypeName.STRING);
			Field audience_target = Field.of("audience_target", LegacySQLTypeName.STRING);
			
			// Table schema definition
			Schema subrSchema;
			subrSchema = Schema.of(subr_id,subr_name, num_subscribers, subr_cat, description, audience_target);
			
			TableDefinition subrTableDefinition = StandardTableDefinition.of(subrSchema);
			TableInfo tableInfo = TableInfo.newBuilder(subrTable, subrTableDefinition).build();
			com.google.cloud.bigquery.Table table = bqconn.create(tableInfo);
		}
		
		
		// *********************** POSTS TABLE *************************
		TableId postsTable = TableId.of(datasetId, postTableName);
		// Table field definition
		Field subr_id = Field.of("subr_id", LegacySQLTypeName.STRING);
		Field subr_name = Field.of("subr_name", LegacySQLTypeName.STRING);
		Field num_subscribers =  Field.of("num_subscribers", LegacySQLTypeName.INTEGER);
		Field subr_cat = Field.of("subr_cat", LegacySQLTypeName.STRING);
		Field description = Field.of("description", LegacySQLTypeName.STRING);
		Field audience_target = Field.of("audience_target", LegacySQLTypeName.STRING);
		
		// Table schema definition
		Schema postsSchema;
		postsSchema = Schema.of(subr_id,subr_name, num_subscribers, subr_cat, description, audience_target);
		
		// Allow the table to partition
		TimePartitioning timePartitioning = TimePartitioning.of(TimePartitioning.Type.DAY);
		
		TableDefinition subrTableDefinition = StandardTableDefinition.newBuilder().setSchema(postsSchema).
				setTimePartitioning(timePartitioning).build();
		
		TableInfo tableInfo = TableInfo.newBuilder(postsTable, subrTableDefinition).build();
		com.google.cloud.bigquery.Table table = bqconn.create(tableInfo);
		  
		
		
		return 0;
		
	}
	
	public static void main(String[] args) {
		
		
		// Sample how to use 
		
		BigQuery bqconn = PlasmaBigQueryTools.Connect();
		// Use the client.
		System.out.println("Datasets:");
		for (com.google.cloud.bigquery.Dataset dataset : bqconn.listDatasets().iterateAll()) {
		    System.out.printf("%s%n", dataset.getDatasetId().getDataset());
		}
		
		PlasmaBigQueryTools.createTables("idyllic-kit-191017", "trendy", "subreddits",
				"posts", "comments", 0);
		
	
	}
}
