#!/bin/bash
if [[ -n "$(find ./target -name 'crawler*.jar' | head -1)" ]]; then
    mvn -DskipTests exec:java -Dexec.args="$*"
else
    mvn clean install -DskipTests exec:java -Dexec.args="$*"
fi
