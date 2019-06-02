#!/usr/bin/env bash

COMPONENT_DIR="component_temp_dir"
LANGUAGE_PATH="$COMPONENT_DIR/jre/languages/bc"
INCLUDE_BCNATIVE="FALSE"

if [[ -f ../native/bcnative ]]; then
    INCLUDE_BCNATIVE="TRUE"
fi

rm -rf COMPONENT_DIR

mkdir -p "$LANGUAGE_PATH"
cp ../language/target/bc-truffle-0.1-jar-with-dependencies.jar "$LANGUAGE_PATH"

mkdir -p "$LANGUAGE_PATH/launcher"
cp ../launcher/target/bc-launcher.jar "$LANGUAGE_PATH/launcher/"

mkdir -p "$LANGUAGE_PATH/bin"
cp ../bc ${LANGUAGE_PATH}/bin/
if [[ ${INCLUDE_BCNATIVE} = "TRUE" ]]; then
    cp ../native/bcnative ${LANGUAGE_PATH}/bin/
fi

mkdir -p "$COMPONENT_DIR/META-INF"
{
    echo "Bundle-Name: bc" ;
    echo "Bundle-Symbolic-Name: ch.snipy.bc" ;
    echo "Bundle-Version: 19.0.0" ;
    echo 'Bundle-RequireCapability: org.graalvm; filter:="(&(graalvm_version=19.0.0)(os_arch=amd64))"' ;
    echo "x-GraalVM-Polyglot-Part: True"
} > "$COMPONENT_DIR/META-INF/MANIFEST.MF"

(
cd ${COMPONENT_DIR} || exit 1
jar cfm ../bc-component.jar META-INF/MANIFEST.MF .

echo "bin/bc = ../jre/languages/bc/bin/bc" > META-INF/symlinks
if [[ ${INCLUDE_BCNATIVE} = "TRUE" ]]; then
    echo "bin/bcnative = ../jre/languages/bc/bin/bcnative" >> META-INF/symlinks
fi
jar uf ../bc-component.jar META-INF/symlinks

{
    echo "jre/languages/bc/bin/bc = rwxrwxr-x"
    echo "jre/languages/bc/bin/bcnative = rwxrwxr-x"
} > META-INF/permissions

jar uf ../bc-component.jar META-INF/permissions
)
rm -rf ${COMPONENT_DIR}
