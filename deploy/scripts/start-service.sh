#!/bin/bash

set -eu

INSTANCE_ID=$(curl 169.254.169.254/2014-11-05/meta-data/instance-id)
REGISTER_NAME=$(aws ec2 describe-tags --filters Name=resource-id,Values=$INSTANCE_ID Name=key,Values=Register --region eu-west-1 --query 'Tags[0].Value' --output text)
ENV=$(aws ec2 describe-tags --filters Name=resource-id,Values=$INSTANCE_ID Name=key,Values=Environment --region eu-west-1 --query 'Tags[0].Value' --output text)
CONFIG_BUCKET=openregister.${ENV}.config

aws s3 cp s3://${CONFIG_BUCKET}/${REGISTER_NAME}/openregister/config.yaml /srv/openregister-java --region eu-west-1
aws s3 cp s3://${CONFIG_BUCKET}/registers.yaml /srv/openregister-java --region eu-west-1
aws s3 cp s3://${CONFIG_BUCKET}/fields.yaml /srv/openregister-java --region eu-west-1

docker run --name=openregister-migrate \
    --volume /srv/openregister-java:/srv/openregister-java \
    jstepien/openjdk8 java -Xmx8g -Dfile.encoding=UTF-8 \
    -jar /srv/openregister-java/openregister-java.jar \
    db migrate /srv/openregister-java/config.yaml
docker run -d --name=openregister -p 80:8080 \
    --volume /srv/openregister-java:/srv/openregister-java \
    jstepien/openjdk8 java -Xmx8g -Dfile.encoding=UTF-8 -DregistersYaml=/srv/openregister-java/registers.yaml -DfieldsYaml=/srv/openregister-java/fields.yaml \
    -jar /srv/openregister-java/openregister-java.jar \
    server /srv/openregister-java/config.yaml
