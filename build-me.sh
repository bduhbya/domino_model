#! /bin/bash

usage(){
    echo "USAGE $0 [<OPTION>]"
    echo "    Ex: $0"
    echo "    Ex: $0 clean"
}

if [ $# -eq 0 ]
then
    ./get-deps.sh
    javac -cp .:junit-4.12.jar:hamcrest-core-1.3.jar -Xlint:unchecked src/main/java/com/bduhbsoft/BigSixDominoes/*.java
    #jar cvf BigSixDominoes.jar src/main/java/com/bduhbsoft/BigSixDominoes/*.class
    #jar cvf src/main/java/com/bduhbsoft/BigSixDominoes/BigSixDominoes.jar src/java/com/bduhbsoft/BigSixDominoes/*.class src/java/com/bduhbsoft/BigSixDominoes/*.java
    #javac -classpath BigSixDominoes.jar:. Testing/src/com/bduhbsoft/funcTesting/*.java
    #javac -verbose -classpath src/java/com/bduhbsoft/BigSixDominoes/BigSixDominoes.jar Testing/src/com/bduhbsoft/funcTesting/*.java
    #javac -verbose -classpath src/java/com/bduhbsoft/BigSixDominoes/ src/testing/java/com/bduhbsoft/funcTesting/*.java
    exit
fi

if [ $# -eq 1 ]
then
    echo "Cleaning..."
    rm src/main/java/com/bduhbsoft/BigSixDominoes/*.class
    exit
fi

usage
exit
