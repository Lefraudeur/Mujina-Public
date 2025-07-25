@ECHO OFF
cd libMujina\InjectableJar


if exist InjectableJar.jar rm InjectableJar.jar
if exist InjectableJar.jar.hpp rm InjectableJar.jar.hpp

cd InjectableJar

echo Starting java build...

call ant -q

echo Remapping jar...
java -jar "tiny-remapper-0.10.1+local-fat.jar" "build\libs\InjectableJar.jar" "..\InjectableJar.jar" "1.20.4.tiny" yarn-named mojang "libs" "libs\mclibs"
cd ..
echo Running ignore_File2Hex...
ignore_File2Hex.exe
cd ../..
echo Starting CMake...
cmake -B ./Build
cmake --build Build --target libMujina --config Release

echo Done!