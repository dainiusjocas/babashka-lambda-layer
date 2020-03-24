stack = babashka-lambda-layer
s3-bucket = my-bucket

build:
	docker build --target BUILDER -t babashka-lambda-archiver .
	docker rm build || true
	docker create --name build babashka-lambda-archiver
	docker cp build:/var/task/babashka-runtime.zip babashka-runtime.zip

package: build
	aws cloudformation package \
        --template-file template.yaml \
        --s3-bucket $(s3-bucket) \
        --s3-prefix babashka \
        --output-template-file /tmp/template.yaml

deploy: package
	aws cloudformation deploy \
        --template-file /tmp/template.yaml \
        --stack-name $(stack) \
        --capabilities CAPABILITY_IAM \
        --no-fail-on-empty-changeset

publish-layer: build
	aws lambda publish-layer-version \
		--layer-name babashka-runtime \
		--principal "*" \
		--zip-file fileb://babashka-runtime.zip
