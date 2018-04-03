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
import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryOptions;
import com.google.cloud.bigquery.DatasetInfo;
import com.google.cloud.bigquery.FieldValueList;
import com.google.cloud.bigquery.Job;
import com.google.cloud.bigquery.JobException;
import com.google.cloud.bigquery.JobId;
import com.google.cloud.bigquery.JobInfo;
import com.google.cloud.bigquery.QueryJobConfiguration;
import com.google.cloud.bigquery.QueryResponse;
import com.google.cloud.bigquery.TableResult;

public class PlasmaBigQueryTools {
	
	public static BigQuery Connect () {
		
		/*
		 // Instantiate a client. If you don't specify credentials when constructing a client, the
		  // client library will look for credentials in the environment, such as the
		  // GOOGLE_APPLICATION_CREDENTIALS environment variable.
		  BigQuery bigquery = BigQueryOptions.getDefaultInstance().getService();

		  // Use the client.
		  System.out.println("Datasets:");
		  for (com.google.cloud.bigquery.Dataset dataset : bigquery.listDatasets().iterateAll()) {
		    System.out.printf("%s%n", dataset.getDatasetId().getDataset());
		  } */
		
			// Load credentials from JSON key file. If you can't set the GOOGLE_APPLICATION_CREDENTIALS
		  // environment variable, you can explicitly load the credentials file to construct the
		  // credentials.
		  GoogleCredentials credentials = null;
		  File credentialsPath = new File("C:\\PersonalWork\\plasma\\core\\src\\main\\resources\\bq_key.json");  // TODO: update to your key path.
		  try (FileInputStream serviceAccountStream = new FileInputStream(credentialsPath)) {
		    credentials = ServiceAccountCredentials.fromStream(serviceAccountStream);
		  } catch (FileNotFoundException ex) {
			  Logger.getLogger(PlasmaBigQueryTools.class.getName()).log(Level.SEVERE, null, ex);
		  } catch (IOException ex) {
			Logger.getLogger(PlasmaBigQueryTools.class.getName()).log(Level.SEVERE, null, ex);
		  }

		  // Instantiate a client.
		  BigQuery bigquery =
		      BigQueryOptions.newBuilder().setCredentials(credentials).build().getService();

		return bigquery;
	}

	public static void main(String[] args) {
		
		BigQuery bqconn = PlasmaBigQueryTools.Connect();
		// Use the client.
		System.out.println("Datasets:");
		for (com.google.cloud.bigquery.Dataset dataset : bqconn.listDatasets().iterateAll()) {
		    System.out.printf("%s%n", dataset.getDatasetId().getDataset());
		}
	}

}
