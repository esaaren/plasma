package com.plasma;

import java.util.ArrayList;
import java.util.List;

import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.io.gcp.bigquery.BigQueryIO;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.DoFn.ProcessContext;
import org.apache.beam.sdk.transforms.DoFn.ProcessElement;
import org.apache.beam.sdk.transforms.ParDo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.services.bigquery.model.TableReference;

import com.google.api.services.bigquery.model.TableRow;

import com.google.api.services.bigquery.model.TableSchema;

import com.google.api.services.bigquery.model.TableFieldSchema;

public class PlasmaStorageToBigQuery {

	private static final Logger LOG = LoggerFactory.getLogger(PlasmaStorageToBigQuery.class);
    private static String HEADERS = "id,random_data";

    public static class FormatForBigquery extends DoFn<String, TableRow> {

        private String[] columnNames = HEADERS.split(",");

        @ProcessElement
         public void processElement(ProcessContext c) {
             TableRow row = new TableRow();
             String[] parts = ((String) c.element()).split(",");

            if (!((String) c.element()).contains(HEADERS)) {
                 for (int i = 0; i < parts.length; i++) {
                     row.set(columnNames[i], parts[i]);
                 }
                 c.output(row);
             }
         }


        /** Defines the BigQuery schema used for the output. */


        static TableSchema getSchema() {
             List<TableFieldSchema> fields = new ArrayList<>();
             // Currently store all values as String
             fields.add(new TableFieldSchema().setName("id").setType("INTEGER"));
             fields.add(new TableFieldSchema().setName("random_data").setType("STRING"));
       
            return new TableSchema().setFields(fields);
         }
     }
    

	public static void main(String[] args) {
		
		// Currently hard-code the variables, this can be passed into as parameters
        String sourceFilePath = "gs://erik-dataflow/test_data";
        String tempLocationPath = "gs://erik-dataflow/tmp";
        boolean isStreaming = false;
        TableReference tableRef = new TableReference();
        // Replace this with your own GCP project id
        tableRef.setProjectId("idyllic-kit-191017");
        tableRef.setDatasetId("poc");
        tableRef.setTableId("test_table");


       PipelineOptions options = PipelineOptionsFactory.fromArgs(args).withValidation().create();
        // This is required for BigQuery
        options.setTempLocation(tempLocationPath);
        options.setJobName("csvtobq");
        Pipeline p = Pipeline.create(options);


       p.apply("Read CSV File", TextIO.read().from(sourceFilePath))
                .apply("Log messages", ParDo.of(new DoFn<String, String>() {
                    @ProcessElement
                    public void processElement(ProcessContext c) {
                        LOG.info("Processing row: " + c.element());
                        c.output(c.element());
                    }
                }))
                .apply("Convert to BigQuery TableRow", ParDo.of(new FormatForBigquery()))
                .apply("Write into BigQuery",
                        BigQueryIO.writeTableRows().to(tableRef).withSchema(FormatForBigquery.getSchema())
                                .withCreateDisposition(BigQueryIO.Write.CreateDisposition.CREATE_IF_NEEDED)
                                .withWriteDisposition(isStreaming ? BigQueryIO.Write.WriteDisposition.WRITE_APPEND
                                        : BigQueryIO.Write.WriteDisposition.WRITE_TRUNCATE));


       p.run().waitUntilFinish();

	}

}
