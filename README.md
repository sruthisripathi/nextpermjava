# Part of this code and folder structure is taken from [aws-sam-java-rest](https://github.com/aws-samples/aws-sam-java-rest) repository

This is a sample application to demonstrate how to build an application on DynamoDB using the
DynamoDBMapper ORM framework to map Order items in a DynamoDB table to a RESTful API for order
management.

```bash
.
├── README.md                               <-- This instructions file
├── LICENSE.txt                             <-- Apache Software License 2.0
├── NOTICE.txt                              <-- Copyright notices
├── pom.xml                                 <-- Java dependencies, Docker integration test orchestration
├── src
│   ├── main
│   │   └── java
│   │       ├── com.amazonaws.exception             <-- Source code for custom exceptions
│   │       ├── com.amazonaws.handler               <-- Source code for lambda functions
│   │       │   ├── GetNextPermNumHandler.java      <-- Lambda function code for getting next permutation
│   │       └── com.amazonaws.model                 <-- Source code for model classes
│   │           ├── request                         <-- Source code for request model classes
│   │           │   ├── GetNextPermNumRequest.java  <-- POJO shape for getting a page of orders
│   │           ├── response                        <-- Source code for response model classes
│   │           │   ├── GatewayResponse.java        <-- Generic POJO shape for the APIGateway integration
│   │           │   └── GetNextPermNumResponse.java <-- POJO shape for a page of orders
│   └── test                                        <-- Unit and integration tests
│       └── java
│           ├── com.amazonaws.handler             <-- Unit and integration tests for handlers
│           │   ├── GetNextPermNumHandlerTest.java   <-- Unit tests for getting next permutation
│           └── com.amazonaws.services.lambda.runtime <-- Unit and integration tests for handlers
│               └── TestContext.java              <-- Context implementation for use in tests
└── template.yaml                                 <-- Contains SAM API Gateway + Lambda definitions
```

## Requirements

* AWS CLI already configured with at least PowerUser permission
* [Java SE Development Kit 8 installed](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
* [Docker installed](https://www.docker.com/community-edition)
* [Maven](https://maven.apache.org/install.html)
* [SAM CLI](https://github.com/awslabs/aws-sam-cli)
* [Python 3](https://docs.python.org/3/)

## Setup process

### Installing dependencies

We use `maven` to install our dependencies and package our application into a JAR file:

```bash
mvn package
```

### Local development

**Invoking function locally through local API Gateway**
1. Start the SAM local API.
 - On a Mac: `sam local start-api --env-vars src/test/resources/test_environment_mac.json`
 - On Windows: `sam local start-api --env-vars src/test/resources/test_environment_windows.json`
 - On Linux: `sam local start-api --env-vars src/test/resources/test_environment_linux.json`

If the previous command ran successfully you should now be able to hit the following local endpoint to
invoke the functions rooted at `http://localhost:3000/orders`

**SAM CLI** is used to emulate both Lambda and API Gateway locally and uses our `template.yaml` to
understand how to bootstrap this environment (runtime, where the source code is, etc.) - The
following excerpt is what the CLI will read in order to initialize an API and its routes:

```yaml
...
Events:
    GetOrders:
        Type: Api # More info about API Event Source: https://github.com/awslabs/serverless-application-model/blob/master/versions/2016-10-31.md#api
        Properties:
            Path: /nextperm
            Method: get
```

## Packaging and deployment

AWS Lambda Java runtime accepts either a zip file or a standalone JAR file - We use the latter in
this example. SAM will use `CodeUri` property to know where to look up for both application and
dependencies:

```yaml
...
    GetNextPermNumFunction:
        Type: AWS::Serverless::Function
        Properties:
            CodeUri: target/aws-sam-java-rest-1.0.0.jar
            Handler: com.amazonaws.handler.GetNextPermNum::handleRequest
```

Firstly, we need a `S3 bucket` where we can upload our Lambda functions packaged as ZIP before we
deploy anything - If you don't have a S3 bucket to store code artifacts then this is a good time to
create one:

```bash
export BUCKET_NAME=my_cool_new_bucket
aws s3 mb s3://$BUCKET_NAME
```

Next, run the following command to package our Lambda function to S3:

```bash
sam package \
    --template-file template.yaml \
    --output-template-file packaged.yaml \
    --s3-bucket $BUCKET_NAME
```

Next, the following command will create a Cloudformation Stack and deploy your SAM resources.

```bash
sam deploy \
    --template-file packaged.yaml \
    --stack-name sam-nextPerm \
    --capabilities CAPABILITY_IAM
```

> **See [Serverless Application Model (SAM) HOWTO Guide](https://github.com/awslabs/serverless-application-model/blob/master/HOWTO.md) for more details in how to get started.**

After deployment is complete you can run the following command to retrieve the API Gateway Endpoint URL:

```bash
aws cloudformation describe-stacks \
    --stack-name sam-nextPerm \
    --query 'Stacks[].Outputs'
```

## Testing

### Running unit tests
We use `JUnit` for testing our code.
You can run unit tests with the following command:

```bash
mvn test
```

### Running integration tests
```bash
mvn verify
```

### Running end to end tests through the SAM CLI Local endpoint
Running the following end-to-end tests requires Python 3 and the `requests` pip
package to be installed. For these tests to succeed,
```bash
pip3 install requests
python3 src/test/resources/api_tests.py 3
```

For these tests to work, you must follow the steps for [local development](#local-development).  

# Appendix

## AWS CLI commands

AWS CLI commands to package, deploy and describe outputs defined within the cloudformation stack:

```bash
sam package \
    --template-file template.yaml \
    --output-template-file packaged.yaml \
    --s3-bucket REPLACE_THIS_WITH_YOUR_S3_BUCKET_NAME

sam deploy \
    --template-file packaged.yaml \
    --stack-name sam-nextPerm \
    --capabilities CAPABILITY_IAM \
    --parameter-overrides MyParameterSample=MySampleValue

aws cloudformation describe-stacks \
    --stack-name sam-nextPerm --query 'Stacks[].Outputs'
```
