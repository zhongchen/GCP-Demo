import csv
from collections import defaultdict
import json


def convert_type(t):
    if t.startswith("STRING"):
        return "STRING"

    if t == 'TIMESTAMP':
        return t

    if t == 'BOOL':
        return "BOOLEAN"

    if t.startswith('ARRAY<STRING'):
        return "STRING"

    if t == "INT64":
        return "INTEGER"

    if t == "FLOAT64":
        return "FLOAT"

    raise Exception(f"wrong type: {t}")


def is_repeated(t):
    if t.startswith('ARRAY<'):
        return True
    else:
        return False


# spanner schema file in csv format
def process(filename):
    with open(filename, 'r') as f:
        reader = csv.reader(f, delimiter='\t')
        table_to_schema = defaultdict(list)
        for row in reader:
            fields = row[0].split(" ")
            d = [e for e in fields if len(e) != 0]
            if d[0] != 'TABLE_NAME':
                table_to_schema[d[0]].append(d)

        for table, fields in table_to_schema.items():
            generate_schema_file(table, fields)


def generate_schema_file(table, fields):
    schemas = []
    for field in fields:
        if field[3] == 'NO':
            mode = "REQUIRED"
        else:
            mode = "NULLABLE"
        col_name = field[1]
        tpe = convert_type(field[4])
        if is_repeated(tpe):
            mode = "REPEATED"
        schema = {
            "mode": mode,
            "name": col_name,
            "type": tpe
        }
        schemas.append(schema)
    with open(f"schemas/spanner/{table.upper()}.json", "w") as f:
        json.dump(schemas, f, indent=4)

