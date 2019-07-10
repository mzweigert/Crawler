@echo off
IF EXIST ./target/crawler*.jar (
   mvn -DskipTests exec:java -Dexec.args="%*"
) ELSE (
    mvn clean install -DskipTests exec:java -Dexec.args="%*"
)
