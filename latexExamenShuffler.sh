if [ "$#" -eq 0 ]
then
java -jar  target/LatexExamenShuffler-1.0-SNAPSHOT.jar
else
java -jar  target/LatexExamenShuffler-1.0-SNAPSHOT.jar "$@"
fi
