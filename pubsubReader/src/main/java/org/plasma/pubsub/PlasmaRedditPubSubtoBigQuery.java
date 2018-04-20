package org.plasma.pubsub;

import java.time.Instant;
import org.apache.beam.runners.dataflow.DataflowRunner;
import org.apache.beam.runners.dataflow.options.DataflowPipelineOptions;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubIO;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.plasma.core.*;

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
	          
	          PlasmaBigQueryTools.insertComments(c.element(), "trendy", "comments",
	        		  "gs://keys/bq_key.json");
	        }
	      }));
		
	    // Run the pipeline
	    p.run().waitUntilFinish();
	}

}
