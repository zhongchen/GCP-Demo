from google.cloud import storage
import click
import logging

logger = logging.getLogger(__name__)

@click.command()
@click.option('--bucket', help="gcs bucket name")
def main(bucket):
    logger.info("the bucket name is {}".format(bucket))
    # magic path in the cloudbuild
    workspace = "/workspace"
    file = workspace + "/data/input.txt"
    with open(file, "r") as stream:
        for line in stream.readlines():
            logger.error("line is {}".format(line))

if __name__ == '__main__':
    main()
