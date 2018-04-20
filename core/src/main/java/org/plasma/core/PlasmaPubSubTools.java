package org.plasma.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.ProjectTopicName;
import com.google.pubsub.v1.PubsubMessage;

public class PlasmaPubSubTools {
	
	
	public static int publish(String topic, String messageContents) {
		
	  String topicNameString = topic;
	  String messageString = messageContents;
	
	  // Setup google credentials 
	
	  GoogleCredentials credentials = PlasmaGoogleCloudTools.getCredentials("pubsub");
		  
	  // Set project name & topic name 
	  ProjectTopicName topicName = ProjectTopicName.of("idyllic-kit-191017", topicNameString);
	  Publisher publisher = null;
	  
	  // Initialize the lists 
	  List<ApiFuture<String>> messageIdFutures = new ArrayList<>();
	  List<String> messageIds = null;

	  try {
	    // Create a publisher instance bound to the topic
	    try {
			publisher = Publisher.newBuilder(topicName).setCredentialsProvider(FixedCredentialsProvider.create(credentials)).build();
		} catch (IOException ex) {
			 Logger.getLogger(PlasmaPubSubTools.class.getName()).log(Level.SEVERE, "IOException", ex);
			 ex.printStackTrace();
			 return -1;

		}

	    List<String> messages = Arrays.asList(messageString);

	    // Schedule publishing one message at a time : messages get automatically batched
	    for (String message : messages) {
	      ByteString data = ByteString.copyFromUtf8(message);
	      PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(data).build();
	      
	      // Once published, returns a server-assigned message id (unique within the topic)
	      ApiFuture<String> messageIdFuture = publisher.publish(pubsubMessage);
	      messageIdFutures.add(messageIdFuture);
	    }
	    
	  } finally {
	    // wait on any pending publish requests.
		  
		try {
			messageIds = ApiFutures.allAsList(messageIdFutures).get();
		} catch (InterruptedException ex) {
			Logger.getLogger(PlasmaPubSubTools.class.getName()).log(Level.SEVERE, "Execution exception", ex);
			 ex.printStackTrace();
			 return -1;
		} catch (ExecutionException ex) {
			Logger.getLogger(PlasmaPubSubTools.class.getName()).log(Level.SEVERE, "Execution exception", ex);
			 ex.printStackTrace();
			 return -1;
		}

	    for (String messageId : messageIds) {
	      System.out.println("published with message ID: " + messageId);
	    }

	    if (publisher != null) {
	      // When finished with the publisher, shutdown to free up resources.
	      try {
			publisher.shutdown();
		} catch (Exception ex) {
			Logger.getLogger(PlasmaPubSubTools.class.getName()).log(Level.SEVERE, "Exception", ex);
			ex.printStackTrace();
			return -1;
		}
	    }
	  }
	
	return 0;
	}

	public static void main(String[] args) {
		
		PlasmaPubSubTools.publish("reddit_topic", "Blah!");

	}

}
