# Minimal Example

The minimal example is very close to [this guide](https://docs.aws.amazon.com/lambda/latest/dg/gettingstarted-awscli.html).

## How to use

1. Get role ARN

2. Get babashka lambda layer ARN
    - go to [serverless application repository](https://serverlessrepo.aws.amazon.com/applications/arn:aws:serverlessrepo:us-east-1:209523798522:applications~babashka-runtime)
    - deploy the app 
    - find the babashka-runtime lambda layer ARN
    
3. Deploy the lambda:

Having done (1) and (2)
```shell script
make function-name=babashka-lambda-example \
     role=arn:aws:iam::123456789012:role/lambda-ex \
     layers=arn:aws:lambda:us-east-2:123456789012:layer:my-layer:3 \
     deploy
```
4. Invoke you lambda:
```shell script
make function-name=babashka-lambda-example invoke
```
This will return:
```
START RequestId: 27f99751-a425-43dd-9659-36e3bc7f50dc Version: $LATEST
hello from lambda
END RequestId: 27f99751-a425-43dd-9659-36e3bc7f50dc
REPORT RequestId: 27f99751-a425-43dd-9659-36e3bc7f50dc  Duration: 19.85 ms      Billed Duration: 100 ms Memory Size: 128 MB     Max Memory Used: 85 MB  
Response: {"babashka":"rocks even harder"}% 
```

## Notes

The notable differences from [this guide](https://docs.aws.amazon.com/lambda/latest/dg/gettingstarted-awscli.html) are:
- handler is fully qualified Clojure function name, i.e. `namespace/function-name`
- `namespace` is expected to match the file structure of the lambda zip, therefore, it is required, e.g.
    - `handler` namespace must be stored in `handler.clj` file;
    - `my-lambda.handler` namespace must be in file `my_lambda/handler.clj`;
- the handler function is expected to take two params: `[event context]`, where `event` is the decoded body of the request, and `context` is map with environment variables.
