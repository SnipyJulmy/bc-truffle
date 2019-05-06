#!/usr/bin/env bash
if [[ $BC_BUILD_NATIVE == "false" ]]; then
    echo "Skipping the native image build because BC_BUILD_NATIVE is set to false."
    exit 0
fi
"$JAVA_HOME"/bin/native-image --tool:truffle -H:MaxRuntimeCompileMethods=1200 \
    -cp ../language/target/bc-truffle-0.1-jar-with-dependencies.jar:../launcher/target/launcher-0.1.jar \
    ch.snipy.bc.launcher.BcMain \
    bcnative
