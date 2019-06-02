#!/usr/bin/env bash
LANGFILE="language/src/main/java/ch/snipy/bc/BcLanguage.java"
mvn clean &&
sed -i -e 's/RootNode root = BcParser$.MODULE$.parse(this, source);/RootNode root = null;/' $LANGFILE &&
mvn compile &&
sed -i -e 's/RootNode root = null;/RootNode root = BcParser$.MODULE$.parse(this, source);/' $LANGFILE &&
mvn package
cd ./component/
mvn package
cd ..