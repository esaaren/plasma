package org.plasma.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;

public class PlasmaGoogleCloudTools {

	
	public static GoogleCredentials getCredentials(String credType) {
		
		GoogleCredentials credentials = null;
		
		// Read the plasma.conf file to get key path
		BufferedReader reader;
		String key_file_path = null;
		try {
			reader = new BufferedReader(new FileReader(
					"src/main/resources/plasma.conf"));
			String line = reader.readLine();
			while (line != null) {
				if (line.contains("key_file_url")) {
					key_file_path = line.split("=")[1];
				}
				line = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Load the key file based on the type needed
	    File credentialsPath = new File(key_file_path + credType + "_key.json");  // Key path
	    try (FileInputStream serviceAccountStream = new FileInputStream(credentialsPath)) {
	        credentials = ServiceAccountCredentials.fromStream(serviceAccountStream);
	    } catch (FileNotFoundException ex) {
		  Logger.getLogger(PlasmaPubSubTools.class.getName()).log(Level.SEVERE, "File not found", ex);
		 
	    } catch (IOException ex) {
		  Logger.getLogger(PlasmaPubSubTools.class.getName()).log(Level.SEVERE, "IOException", ex);
	    }
	    
	    return credentials;
	}
	
	public static void main(String[] args) {

		PlasmaGoogleCloudTools.getCredentials("bq");
	}

}
