from __future__ import absolute_import
import argparse
import logging
import re
import apache_beam as beam
from apache_beam.options.pipeline_options import PipelineOptions

""" How to run via cloud shell

    pip install --upgrade virtualenv

    virtualenv --python=/usr/bin/python2.7 /home/<user>/sample_venv

    . /home/<user>/sample_venv/bin/activate

    cd /home/<user>/sample_venv/bin

    pip install apache-beam[gcp]

    python erik_dataflow.py

"""



class DataIngestion:
    """A helper class which contains the logic to translate the file into
    a format BigQuery will accept."""

    def parse_method(self, string_input):
        """This method translates a single line of comma separated values to a
        dictionary which can be loaded into BigQuery.

        Args:
            string_input: A comma separated list of values in the form of
            state_abbreviation,gender,year,name,count_of_babies,dataset_created_date
                example string_input: KS,F,1923,Dorothy,654,11/28/2016

        Returns:
            A dict mapping BigQuery column names as keys to the corresponding value
            parsed from string_input.  In this example, the data is not transformed, and
            remains in the same format as the CSV.  There are no date format transformations.
            example output:
              {'state': 'KS',
               'gender': 'F',
               'year': '1923', <- Keep the format of the "date" as a simple integer, with no transformations.
               'name': 'Dorothy',
               'number': '654',
               'created_date': '11/28/2016'
               }
         """
        # Strip out return characters and quote characters.
        values = re.split(",",
                          re.sub('\r\n', '', re.sub(u'"', '', string_input)))

        row = dict(zip(('id', 'random_data'),
                       values))

        return row


def run(argv=None):
    """The main function which creates the pipeline and runs it."""
    parser = argparse.ArgumentParser()
    # Here we add some specific command line arguments we expect.
    # Specifically we have the input file to load and the output table to
    # This is the final stage of the pipeline, where we define the destination
    # of the data.  In this case we are writing to BigQuery.
    parser.add_argument(
        '--input', dest='input', required=False,
        help='Input file to read.  This can be a local file or '
             'a file in a Google Storage Bucket.',
        # This example file contains a total of only 10 lines.
        # Useful for developing on a small set of data
        default='gs://erik-dataflow/test_data')
    # This defaults to the lake dataset in your bigquery project.  You'll have
    # to create the lake dataset yourself using this command:
    # bq mk lake

    parser.add_argument('--output', dest='output', required=False,
                        help='Output BQ table to write results to.',
                        default='poc.test_table')

    # Parse arguments from the command line.
    known_args, pipeline_args = parser.parse_known_args(argv)

    print known_args

    pipeline_args.extend([
        # CHANGE 2/5: (OPTIONAL) Change this to DataflowRunner to
        # run your pipeline on the Google Cloud Dataflow Service.
        '--runner=DataflowRunner',
        # CHANGE 3/5: Your project ID is required in order to run your pipeline on
        # the Google Cloud Dataflow Service.
        '--project=idyllic-kit-191017',
        # CHANGE 4/5: Your Google Cloud Storage path is required for staging local
        # files.
        '--staging_location=gs://erik-dataflow/stg',
        # CHANGE 5/5: Your Google Cloud Storage path is required for temporary
        # files.
        '--temp_location=gs://erik-dataflow/tmp',
        '--job_name=poc-job',
    ])


    # DataIngestion is a class we built in this script to hold the logic for
    # transforming the file into a BigQuery table.
    data_ingestion = DataIngestion()

    # Initiate the pipeline using the pipeline arguments passed in from the
    # command line.  This includes information including where Dataflow should
    # store temp files, and what the project id is

    p = beam.Pipeline(options=PipelineOptions(pipeline_args))

    (p
     # Read the file.  This is the source of the pipeline.  All further
     # processing starts with lines read from the file.  We use the input
     # argument from the command line.  We also skip the first line which is a
     # header row.
     | 'Read from a File' >> beam.io.ReadFromText(known_args.input,
                                                  skip_header_lines=1)
     # This stage of the pipeline translates from a CSV file single row
     # input as a string, to a dictionary object consumable by BigQuery.
     # It refers to a function we have written.  This function will
     # be run in parallel on different workers using input from the
     # previous stage of the pipeline.
     | 'String To BigQuery Row' >> beam.Map(lambda s:
                                            data_ingestion.parse_method(s))
     | 'Write to BigQuery' >> beam.io.Write(
                beam.io.BigQuerySink(
                    # The table name is a required argument for the BigQuery sink.
                    # In this case we use the value passed in from the command line.
                    known_args.output,
                    # Here we use the simplest way of defining a schema:
                    # fieldName:fieldType
                    schema='id:INTEGER,random_data:STRING',
                    # Creates the table in BigQuery if it does not yet exist.
                    create_disposition=beam.io.BigQueryDisposition.CREATE_IF_NEEDED,
                    # Deletes all data in the BigQuery table before writing.
                    write_disposition=beam.io.BigQueryDisposition.WRITE_TRUNCATE)))
    p.run().wait_until_finish()


if __name__ == '__main__':
    logging.getLogger().setLevel(logging.INFO)
    run()