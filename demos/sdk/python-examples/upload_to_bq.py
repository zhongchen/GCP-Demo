from google.cloud import bigquery
import os

print(os.environ['GOOGLE_APPLICATION_CREDENTIALS'])
client = bigquery.Client()

datasets = client.list_datasets()
table = client.get_table('zhong-gcp.samples.data')

job_config = bigquery.LoadJobConfig(
    source_format=bigquery.SourceFormat.CSV
)

uri = "gs://zhong-gcp/file2.csv"

table_id = "zhong-gcp.TestData.NameVauleTable"

# load_job = client.load_table_from_uri(uri, table_id, job_config=job_config)
# print(load_job.result())
table = client.get_table(table_id)
schema_field = table.schema[0]

print(schema_field.name)
print(table.schema[0])

print(table.num_rows)
print(table)
