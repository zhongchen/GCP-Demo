from google.cloud import storage
import click
import logging

from google.cloud.exceptions import GoogleCloudError

from google.cloud.exceptions import NotFound

logger = logging.getLogger(__name__)


@click.command()
@click.option('--bucket', help="gcs bucket name")
def main(bucket):
    logger.info("the bucket name is {}".format(bucket))
    client = storage.Client()
    try:
        bk = client.get_bucket(bucket)
        # magic path in the cloudbuild
        workspace = "/workspace"
        file = workspace + "/data/input.txt"
        blob = bk.blob("data/input.txt")
        with open(file, "r") as stream:
            try:
                blob.upload_from_file(stream)
            except GoogleCloudError as e:
                logger.error(e, exc_info=True)
    except NotFound:
        logger.error("the bucket %s doesn't exist", bucket)


if __name__ == '__main__':
    main()
