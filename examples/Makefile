build:
	zip function.zip handler.clj

function-name = babashka-lambda-example
layers = <YOUR_LAYER_ARN>
role = <YOUR_ROLE_ARN>

deploy:
	@aws lambda delete-function \
  		--function-name $(function-name)  > /dev/null 2>&1 || true
	@aws lambda create-function \
		--function-name $(function-name) \
		--layers $(layers) \
    	--zip-file fileb://function.zip \
    	--handler handler/handle \
    	--runtime provided \
    	--role $(role)

invoke:
	@aws lambda invoke \
		--function-name $(function-name) \
		--payload '{}' \
		out \
		--log-type Tail \
		--query 'LogResult' \
		--output text | base64 -d && \
		printf "Response: %s" "$$(cat out)"
