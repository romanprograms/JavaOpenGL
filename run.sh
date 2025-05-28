#!/bin/bash
mvn clean compile

java \
  --add-exports java.desktop/sun.awt=ALL-UNNAMED \
  -cp "target/classes:lib/*" \
  -Djava.library.path=lib \
  org.example.Main
