package org.plasma.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;

public class PlasmaGoogleCloudTools {

	
	public static GoogleCredentials getCredentials(String credType) {
		
		GoogleCredentials credentials = null;
	    File credentialsPath = new File("C:\\PersonalWork\\files\\" + credType + "_key.json");  // Key path
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

	}

}
