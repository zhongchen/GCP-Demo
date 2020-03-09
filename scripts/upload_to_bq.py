from google.cloud import bigquery
import os

print(os.environ['GOOGLE_APPLICATION_CREDENTIALS'])
client = bigquery.Client()

datasets = client.list_datasets()
table = client.get_table('zhong-gcp.samples.data')



