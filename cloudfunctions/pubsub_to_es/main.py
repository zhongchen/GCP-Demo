import base64
import json
from datetime import datetime

from elasticsearch_dsl import Document, connections, Text, Date

connections.create_connection(hosts=['http://localhost:9200'])


class Message(Document):
    payload = Text(analyzer='snowball')
    attr = Text(analyzer='snowball')
    published_from = Date()

    class Index:
        name = 'datalake_v2'

    def save(self, **kwargs):
        return super(Message, self).save(**kwargs)

    def is_published(self):
        return self.published_from and datetime.now() > self.published_from


def pubsub_to_es(event, context):
    Message.init()
    msg = Message()
    if 'data' in event:
        payload = base64.b64decode(event['data']).decode('utf-8')
        msg.payload = payload
        # payload = json.loads(base64.b64decode(event['data']).decode('utf-8'))
        # print('name is {}'.format(payload['name']))
        print('the payload is {}'.format(payload))

    if 'attributes' in event:
        attr = json.dumps(event['attributes'])
        msg.attr = attr
        print('the attr is {}'.format(attr))

    msg.published_from = datetime.now()
    msg.save()


def main(event, context):
    pubsub_to_es(event, context)
