from google.cloud import pubsub_v1
from google.auth import jwt
import json
import os

file = os.path.expanduser('~/Downloads/gred-ptddtalak-sb-001-e4372d8c-22cf1599e5f4.json')
service_account_info = json.load(open(file))
audience = "https://pubsub.googleapis.com/google.pubsub.v1.Publisher"
credentials = jwt.Credentials.from_service_account_info(service_account_info, audience=audience)
publisher = pubsub_v1.PublisherClient(credentials=credentials)

topic_name = 'projects/gred-ptddtalak-sb-001-e4372d8c/topics/testing-cumulocity'
publisher.publish(topic_name, data=b'Testing Msg', attr1='attr1', attr2='attr2')


