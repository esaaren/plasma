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
import com.google.api.services.bigquery.model.TableRow;
import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryError;
import com.google.cloud.bigquery.BigQueryOptions;
import com.google.cloud.bigquery.DatasetInfo;
import com.google.cloud.bigquery.FieldValueList;
import com.google.cloud.bigquery.InsertAllRequest;
import com.google.cloud.bigquery.InsertAllResponse;
import com.google.cloud.bigquery.Job;
import com.google.cloud.bigquery.JobException;
import com.google.cloud.bigquery.JobId;
import com.google.cloud.bigquery.JobInfo;
import com.google.cloud.bigquery.QueryJobConfiguration;
import com.google.cloud.bigquery.QueryResponse;
import com.google.cloud.bigquery.TableId;
import com.google.cloud.bigquery.TableResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class PlasmaBigQueryTools {
	
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
	public static int insertSubreddits (String subrJson) {
		
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
		TableId tableId = TableId.of("plasma", "c_p_reddit_subrdt");
		
		// Values of the row to insert
		Map<String, Object> rowContent = new HashMap<>();
		
		// Setup the request
		InsertAllRequest.Builder bq_request = InsertAllRequest.newBuilder(tableId);
		
		// Loop over each of the returned children (Subreddits in this case) 
		
		for (int j = 0; j < childrenLength; j++) {
			
			subredditId = response.getData().getChildren().get(j).getChildData().getId();
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
	
	
	public static void main(String[] args) {
		
		
		// Sample how to use 
		
		BigQuery bqconn = PlasmaBigQueryTools.Connect();
		// Use the client.
		System.out.println("Datasets:");
		for (com.google.cloud.bigquery.Dataset dataset : bqconn.listDatasets().iterateAll()) {
		    System.out.printf("%s%n", dataset.getDatasetId().getDataset());
		}
		
		TableId tableId = TableId.of("plasma_reddit", "test_table");
		// Values of the row to insert
		Map<String, Object> rowContent = new HashMap<>();
		InsertAllRequest.Builder request = InsertAllRequest.newBuilder(tableId);
		for (int i = 0; i < 10; i++) {
			rowContent.put("field1", Integer.toString(i));
			rowContent.put("field2", Double.toString(Math.pow(i, 2)));
			request = request.addRow(rowContent);
		}
		
		InsertAllResponse response = bqconn.insertAll(request.build());
		
		
		
		if (response.hasErrors()) {
		  // If any of the insertions failed, this lets you inspect the errors
		  for (Entry<Long, List<BigQueryError>> entry : response.getInsertErrors().entrySet()) {
		    // inspect row error
		  }
		}
		
		List<String> test = PlasmaBigQueryTools.getSubredditNames("plasma.subrt_results");
		
		for (int i=0; i < test.size(); i++) {
			System.out.println(test.get(i));
		}
	}
}
