#! /bin/bash

echo "run with the \"-g\" option to see graphical tests"
cd src/main/java
java com.bduhbsoft.BigSixDominoes.FunctionalTesting $@
