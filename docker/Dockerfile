FROM python:3.7
RUN pip install pipenv
COPY /scripts/Pipfile /tmp
RUN cd /tmp && pipenv lock -r > requirements.txt
RUN pip install -r /tmp/requirements.txt