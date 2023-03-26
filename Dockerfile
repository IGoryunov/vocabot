FROM openjdk:17

RUN groupadd -r kongostream && adduser -r kongostream -g kongostream

WORKDIR /home/kongostream/app

COPY build/output/libs libs
COPY build/libs/vocabot-1.0-plain.jar vocabot.jar

USER kongostream:kongostream
EXPOSE 8080

CMD [ \
"java", \
"-XX:+UseParallelGC", \
"-XX:MaxRAMPercentage=70", \
"-cp", \
"libs/*:vocabot.jar", \
"co.kongostream.vocabularybot.VocabularyBotApplicationKt" \
]
