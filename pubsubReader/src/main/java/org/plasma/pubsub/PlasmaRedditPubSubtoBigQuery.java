package org.plasma.pubsub;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.apache.beam.runners.dataflow.DataflowRunner;
import org.apache.beam.runners.dataflow.options.DataflowPipelineOptions;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.gcp.bigquery.BigQueryIO;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubIO;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.services.bigquery.model.TableFieldSchema;
import com.google.api.services.bigquery.model.TableRow;
import com.google.api.services.bigquery.model.TableSchema;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.plasma.core.*;

public class PlasmaRedditPubSubtoBigQuery {

	private static final Logger LOG = LoggerFactory.getLogger(PlasmaRedditPubSubtoBigQuery.class);
	
	public static void main(String[] args) {
	
		
		DataflowPipelineOptions options = PipelineOptionsFactory.as(DataflowPipelineOptions.class);
	    options.setProject("idyllic-kit-191017");
	    options.setRunner(DataflowRunner.class);
	    options.setStreaming(true);
	    
	    // Topic to pull data from
	    String SUB_NAME = "projects/idyllic-kit-191017/subscriptions/reddit_sub";
	    // Big query table location to write to
	    String BQ_DS = "idyllic-kit-191017:trendy.comments_ps";
	   
	    // Build the table schema for the output table.
	    List<TableFieldSchema> fields = new ArrayList<>();
	    fields.add(new TableFieldSchema().setName("comment_id").setType("STRING"));
	    fields.add(new TableFieldSchema().setName("post_fk").setType("STRING"));
	    fields.add(new TableFieldSchema().setName("subreddit_fk").setType("STRING"));
	    fields.add(new TableFieldSchema().setName("body").setType("STRING"));
	    fields.add(new TableFieldSchema().setName("score").setType("INTEGER"));
	    fields.add(new TableFieldSchema().setName("created_utc").setType("INTEGER"));
	    fields.add(new TableFieldSchema().setName("subreddit_prefix").setType("STRING"));
	    fields.add(new TableFieldSchema().setName("link_title").setType("STRING"));
	    fields.add(new TableFieldSchema().setName("permalink").setType("STRING"));
	    TableSchema schema = new TableSchema().setFields(fields);
	    
	    // BQ data pipeline 
	    Pipeline p = Pipeline.create(options);
		p
	    .apply(PubsubIO.readStrings().fromSubscription(SUB_NAME))
	    .apply("ProcessRedditJsonData", ParDo.of(new DoFn<String, TableRow>() {
	        @ProcessElement
	        public void processElement(ProcessContext c) {
	        	
	          Gson gson = null;
	          RedditResponse response;
	          String commentId = null;
	  		  String postFk = null;
	  		  String subredditFk = null;
	  		  String permaLink = null;
	  		  String body = null;
	  		  String subredditPrefix = null;
	  		  String linkTitle = null;
	  		  int createdUTC = 0;
	  		  int score = 0;
	  		  int childrenLength;
	          
	          // Build new gson object each cycle
			  gson = new GsonBuilder().setPrettyPrinting().create();
				
			  // Get the response into the RedditResponse class
			  response = gson.fromJson(c.element(), RedditResponse.class);
	          
			  childrenLength = response.getData().getDist();
			  
			  for (int i = 0; i < childrenLength; i++) {
				  
				  commentId = "t1_" + response.getData().getChildren().get(i).getChildData().getId();
				  postFk = response.getData().getChildren().get(i).getChildData().getLinkId();
				  subredditFk = response.getData().getChildren().get(i).getChildData().getSubredditId();
				  permaLink = response.getData().getChildren().get(i).getChildData().getPermalink();
				  body = response.getData().getChildren().get(i).getChildData().getBody();
				  subredditPrefix = response.getData().getChildren().get(i).getChildData().getSubredditNamePrefixed();
				  linkTitle = response.getData().getChildren().get(i).getChildData().getLinkTitle();
				  createdUTC = response.getData().getChildren().get(i).getChildData().getCreatedUtc();
				  score = response.getData().getChildren().get(i).getChildData().getScore();
				  
		         // System.out.printf("Received at %s : %s\n", Instant.now(), body); // debug log
		          
		          TableRow row = new TableRow()
		        		.set("comment_id", commentId)
		      			.set("post_fk", postFk)
		    			.set("subreddit_fk", subredditFk)
		    			.set("body", body)
		    			.set("score", score)
		    			.set("created_utc", createdUTC)
		    			.set("subreddit_prefix", subredditPrefix)
		    			.set("link_title", linkTitle)
		    			.set("permalink", permaLink);
		            c.output(row);
				  
			  }
			  
			
	        }
	      })).apply("InsertTableRowsToBigQuery",
	    	      BigQueryIO.writeTableRows().to(BQ_DS)
	    	      .withSchema(schema)
	    	      .withCreateDisposition(BigQueryIO.Write.CreateDisposition.CREATE_IF_NEEDED)
	    	      .withWriteDisposition(BigQueryIO.Write.WriteDisposition.WRITE_APPEND));

		
	    // Run the pipeline
	    p.run().waitUntilFinish();
	}

}
