#!/bin/bash

java -jar "tiny-remapper-0.10.1+local-fat.jar" "out/artifacts/InjectableJar_jar/InjectableJar_jar.jar" "../InjectableJar.jar" "yarn-tiny-1.20.4+build.local" named intermediary "libs" "libs/mclibs"
echo "Remapped InjectableJar..."