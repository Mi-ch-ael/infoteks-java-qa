#! /bin/bash

mkdir -p bin
javac -cp src/ src/com/example/qa/*.java -d bin/
java -cp bin/ com.example.qa.Main $1 $2 $3
