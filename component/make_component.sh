#!/usr/bin/env bash

COMPONENT_DIR="component_temp_dir"
LANGUAGE_PATH="$COMPONENT_DIR/jre/languages/bc"
if [[ -f ../native/bcnative ]]; then
    INCLUDE_SLNATIVE="TRUE"
fi

rm -rf COMPONENT_DIR

mkdir -p "$LANGUAGE_PATH"
cp ../target/bc-truffle-0.1.jar "$LANGUAGE_PATH"

mkdir -p "$LANGUAGE_PATH/launcher"
cp ../launcher/target/bc-launcher.jar "$LANGUAGE_PATH/launcher/"

mkdir -p "$LANGUAGE_PATH/bin"
cp ../bc $LANGUAGE_PATH/bin/
if [[ $INCLUDE_SLNATIVE = "TRUE" ]]; then
    cp ../native/bcnative $LANGUAGE_PATH/bin/
fi

mkdir -p "$COMPONENT_DIR/META-INF"
MANIFEST="$COMPONENT_DIR/META-INF/MANIFEST.MF"
touch "$MANIFEST"
echo "Bundle-Name: bc" >> "$MANIFEST"
echo "Bundle-Symbolic-Name: ch.snipy.bc-truffle" >> "$MANIFEST"
echo "Bundle-Version: 1.0.0-rc13" >> "$MANIFEST"
echo 'Bundle-RequireCapability: org.graalvm; filter:="(&(graalvm_version=1.0.0-rc13)(os_arch=amd64))"' >> "$MANIFEST"
echo "x-GraalVM-Polyglot-Part: True" >> "$MANIFEST"

cd $COMPONENT_DIR
jar cfm ../bc-component.jar META-INF/MANIFEST.MF .

echo "bin/bc = ../jre/languages/bc/bin/bc" > META-INF/symlinks
if [[ $INCLUDE_BCNATIVE = "TRUE" ]]; then
    echo "bin/bcnative = ../jre/languages/bc/bin/bcnative" >> META-INF/symlinks
fi
jar uf ../bc-component.jar META-INF/symlinks

echo "jre/languages/bc/bin/bc = rwxrwxr-x" > META-INF/permissions
echo "jre/languages/bc/bin/bcnative = rwxrwxr-x" >> META-INF/permissions
jar uf ../bc-component.jar META-INF/permissions
cd ..
rm -rf $COMPONENT_DIR