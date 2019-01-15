# Bililiu Son Sqs Worker


### Purpose


The worker is an application tied to the parent project "Bililiu Father", created to consume records of the teacher's application, interpret the necessary events, and direct them to different endpoints.

### How to use

#### Setup environment variables

```
export APPLICATION=bilili-son-sqs
export ENV=<env>
export PORT=<server-port>
export APP_CORE_SIZE=<app_core_size>
export APP_MAX_SIZE=<app_max_size>
export APP_CAPACITY=<app_capacity>
export APP_CONCURRENCY=<app_concurrency>
export SQS_QUEUE_NAME=<name-queue-sqs>
export SQS_QUEUE_NAME_FALLBACK=<name-queue-sqs-fallback>
export SQS_QUEUE_NAME_UNKNOWN=<name-queue-sqs-unknown>
export SQS_REGION=<region-sqs>
export BILILIU_ENDPOINT=<endpoint-with-port>
export BILILIU_ENDPOINT_TOKEN=<token>
export BILILIU_ENDPOINT_CONNECTIONS=<number of connections>
export BILILIU_ENDPOINT_TIMEOUT=<number in ms>
export BILILIU_ENDPOINT_RETRIES=<defaut 5>
export BILILIU_ENDPOINT_CAPTURED_DELAY=<ddealy to send captured orders>
export BILILIU_ENDPOINT_CAPTURED_ATTEMPTS=<attempts to send captured orders>
export CREDENTIALS_ACESSKEY=<endpoint-with-port>
export CREDENTIALS_SECRETKEY=<token>
export SLACK_ENDPOINT=<endpoint slack notification>
export SLACK_ENABLED=<true|false>
```

#### Install libraries

```
mvn clean install
```

#### Compile Project

```
mvn clean package
```

#### Run Project

```
mvn spring-boot:run
```