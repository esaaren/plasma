package org.plasma.pubsub;


import com.google.api.services.bigquery.model.TableRow;
import com.google.api.services.bigquery.model.TableSchema;
import com.google.api.services.bigquery.model.TableFieldSchema;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

import org.apache.beam.runners.dataflow.DataflowRunner;
import org.apache.beam.runners.dataflow.options.DataflowPipelineOptions;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.io.gcp.bigquery.BigQueryIO;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubIO;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubMessage;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.Count;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.MapElements;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.transforms.SerializableFunction;
import org.apache.beam.sdk.transforms.SimpleFunction;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlasmaRedditPubSubtoBigQuery {

	private static final Logger LOG = LoggerFactory.getLogger(PlasmaRedditPubSubtoBigQuery.class);
	
	public static void main(String[] args) {
	  
		
		DataflowPipelineOptions options = PipelineOptionsFactory.as(DataflowPipelineOptions.class);
	    options.setProject("idyllic-kit-191017");
	    options.setRunner(DataflowRunner.class);
	    options.setStreaming(true);
	    
	    // Topic to pull data from
	    String TOPIC_NAME = "projects/idyllic-kit-191017/topics/reddit_topic";
	    String SUB_NAME = "projects/idyllic-kit-191017/subscriptions/reddit_sub";
	    // Big query table location to write to
	    String BQ_DS = "comments_ps";
	   
	    Pipeline p = Pipeline.create(options);
		p
	    .apply(PubsubIO.readStrings().fromSubscription(SUB_NAME))
	    .apply("PrintToStdout", ParDo.of(new DoFn<String, Void>() {
	        @ProcessElement
	        public void processElement(ProcessContext c) {
	          System.out.printf("Received at %s : %s\n", Instant.now(), c.element()); // debug log
	        }
	      }));
		
	    // Run the pipeline
	    p.run().waitUntilFinish();
	}

}
