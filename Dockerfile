FROM java:8
WORKDIR /app/
COPY ./* ./
RUN javac -encoding UTF-8 *.java



