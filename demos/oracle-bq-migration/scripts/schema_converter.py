import csv
import json
import os


def map_oracle_type_to_bq_type(tpe):
    if tpe.startswith("VARCHAR2") or tpe.startswith("NVARCHAR2")\
            or tpe.startswith("NCHAR"):
        return "STRING"

    if tpe.startswith("TIMESTAMP"):
        return "TIMESTAMP"

    if tpe == "INTEGER" or tpe == "SHORTINTEGER" or tpe == "LONGINTEGER":
        return "INTEGER"

    if tpe.startswith("NUMBER"):
        return "NUMERIC"

    if tpe == "DATE":
        return "DATE"

    raise Exception("unrecognized type: {}".format(tpe))


def load_schema(filename):
    schemas = []
    with open(filename) as csv_file:
        csv_reader = csv.DictReader(csv_file, delimiter=",")
        for row in csv_reader:
            if row["NULLABLE"] == "Yes":
                mode = "Nullable"
            else:
                mode = "Required"
            if row["DATA_TYPE"].startswith("NUMBER("):
                print(row["DATA_TYPE"])
            tpe = map_oracle_type_to_bq_type(row["DATA_TYPE"])
            description = row["COMMENTS"]
            schema = {
                "description": description,
                "mode": mode,
                "name": row["COLUMN_NAME"],
                "type": tpe
            }
            schemas.append(schema)
    t = filename.split(".")[0] + ".json"
    with open(filename.split(".")[0] + ".json", "w") as output:
        json.dump(schemas, output, indent=4)


for f in os.listdir("../schema"):
    if f.endswith(".csv"):
        load_schema("schema/{}".format(f))

