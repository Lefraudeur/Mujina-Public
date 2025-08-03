#!/bin/bash

# Check if ant exists
if ! command -v ant >/dev/null 2>&1
then
    echo "ant could not be found. Is it installed?"
    exit 1
fi

# Check if both files exist
if [ -e "./libMujina/InjectableJar/InjectableJar.jar" ] && [ -e "./libMujina/InjectableJar/InjectableJar.jar.hpp" ]; then
    # If both files exist, delete them
    rm "./libMujina/InjectableJar/InjectableJar.jar" "./libMujina/InjectableJar/InjectableJar.jar.hpp" || exit 1
    echo "Deleting files from last build..."
else
    # If any of the files do not exist, print a message
    echo "Starting fresh build..."
fi
cd ./libMujina/InjectableJar/InjectableJar || exit 1
echo "Cd'd in to $PWD"

echo "Starting java build..."
ant || exit 1

echo "Remapping jar..."
java -jar "tiny-remapper-0.10.1+local-fat.jar" "build/libs/InjectableJar.jar" "../InjectableJar.jar" "yarn-tiny-1.20.4+build.local" named intermediary "libs" "libs/mclibs" || exit 1

cd ..
echo "Cd'd in to $PWD"

echo "Running ignore_File2Hex..."
./ignore_File2Hex || exit 1

cd ../.. || exit 1
echo "Cd'd in to $PWD"
echo "Starting CMake..."
cmake -B ./Build || exit 1
cmake --build Build --target libMujina --config Release || exit 1

echo "done"
