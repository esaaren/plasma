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

public class PlasmaRedditPubSubCustom {

	private static final Logger LOG = LoggerFactory.getLogger(PlasmaRedditPubSubCustom.class);
	
	public static void main(String[] args) {
	
		
		DataflowPipelineOptions options = PipelineOptionsFactory.as(DataflowPipelineOptions.class);
	    options.setProject("idyllic-kit-191017");
	    options.setRunner(DataflowRunner.class);
	    options.setStreaming(true);
	    
	    // Topic to pull data from
	    String SUB_NAME = "projects/idyllic-kit-191017/subscriptions/reddit_sub";
	    
	    // BQ data pipeline 
	    Pipeline p = Pipeline.create(options);
		p
	    .apply(PubsubIO.readStrings().fromSubscription(SUB_NAME))
	    .apply("PrintToStdout", ParDo.of(new DoFn<String, Void>() {
	        @ProcessElement
	        public void processElement(ProcessContext c) {
	        	
	        PlasmaBigQueryTools.insertComments(c.element(), "trendy", "comments",
						null);
			  
	          
	        }
	      }));
		
	    // Run the pipeline
	    p.run().waitUntilFinish();
	}

}
