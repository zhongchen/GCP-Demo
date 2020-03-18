import os


def generate_tf(folder):
    files = os.listdir(folder)
    with open("main.tf", "w") as output:
        for f in files:
            if f.endswith(".csv"):
                filename = f.split(".")[0].lower()
                resource = """
resource "google_bigquery_table" "{resource}" {{
  dataset_id = "oracle_migration"
  table_id   = "{resource}"
  schema = file("schema/{resource}.json")
}}
""".format(resource=filename)
                output.write(resource + "\n")


generate_tf("./schema")
