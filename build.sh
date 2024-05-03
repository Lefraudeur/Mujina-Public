#!/bin/bash

# Check if both files exist
if [ -e "./libMujina/InjectableJar/InjectableJar.jar" ] && [ -e "./libMujina/InjectableJar/InjectableJar.jar.hpp" ]; then
    # If both files exist, delete them
    rm "./libMujina/InjectableJar/InjectableJar.jar" "./libMujina/InjectableJar/InjectableJar.jar.hpp"
    echo "Deleting files from last build..."
else
    # If any of the files do not exist, print a message
    echo "Starting frsh build..."
fi
cd ./libMujina/InjectableJar/InjectableJar
echo "Cd'd in to $PWD"
java -jar "tiny-remapper-0.10.1+local-fat.jar" "out/artifacts/InjectableJar_jar/InjectableJar.jar" "../InjectableJar.jar" "yarn-tiny-1.20.4+build.local" named intermediary "libs" "libs/mclibs"
echo "Remapping jar..."
cd ..
echo "Cd'd in to $PWD"

./ignore_File2Hex
echo "Running ignore_File2Hex..."

cd ../..
echo "Cd'd in to $PWD"
echo "Starting CMake..."
cmake -B ./Build
cmake --build Build --target libMujina --config Release

echo "done"
