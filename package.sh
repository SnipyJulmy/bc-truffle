#!/usr/bin/env bash
LANGFILE="language/src/main/java/ch/snipy/bc/BcLanguage.java"
mvn clean &&
sed -i -e 's/RootNode root = BCParser$.MODULE$.parseExpr(this, source);/RootNode root = null;/' $LANGFILE &&
mvn compile &&
sed -i -e 's/RootNode root = null;/RootNode root = BCParser$.MODULE$.parseExpr(this, source);/' $LANGFILE &&
mvn package &&
sudo gu uninstall ch.snipy.bc-truffle &&
sudo gu -L install component/bc-component.jar