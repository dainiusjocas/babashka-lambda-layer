FROM borkdude/babashka:0.0.78 as BABASHKA

FROM clojure:tools-deps-alpine as BUILDER
RUN apk add --no-cache zip
WORKDIR /var/task

RUN mkdir bin
COPY --from=BABASHKA /usr/local/bin/bb bin/bb

ENV GITLIBS=".gitlibs/"
COPY bootstrap bootstrap

COPY deps.edn deps.edn

RUN clojure -Sdeps '{:mvn/local-repo "./.m2/repository"}' -Spath > cp
COPY src/ src/

RUN ./bin/bb -cp $(cat cp) -m lambda.core --uberscript core.clj
RUN echo "#!/usr/bin/env bb" >> bin/babashka && \
    cat core.clj >> bin/babashka && \
    chmod +x bin/babashka

RUN zip -q -r babashka-runtime.zip bin/ bootstrap
