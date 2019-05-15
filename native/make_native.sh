#!/usr/bin/env bash
if [[ $BC_BUILD_NATIVE == "false" ]]; then
    echo "Skipping the native image build because BC_BUILD_NATIVE is set to false."
    exit 0
fi

if [[ $JAVA_HOME == "" ]]; then
  export JAVA_HOME="/usr/lib/jvm/java-8-graal"
fi

"$JAVA_HOME"/bin/native-image --tool:truffle -H:MaxRuntimeCompileMethods=1200 \
    -cp ../language/target/bc-truffle-0.1-jar-with-dependencies.jar:../launcher/target/bc-launcher.jar \
    ch.snipy.bc.launcher.BcMain \
    bcnative
