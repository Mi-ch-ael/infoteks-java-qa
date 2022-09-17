#! /bin/bash

javac -cp src/ src/com/example/qa/*.java -d bin/
cd bin/
jar cfe ../app.jar com/example/qa/Main com/example/qa
cd ..
