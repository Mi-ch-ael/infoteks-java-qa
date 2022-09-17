#! /bin/bash

javac -cp src/ src/com/example/qa/*.java -d bin/
java -cp bin/ com.example.qa.Main
