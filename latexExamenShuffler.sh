java -jar target/LatexExamenShuffler-1.0-SNAPSHOT.jar

if [ "$#" -eq 0 ]
then
java -jar -java -jar target/LatexExamenShuffler-1.0-SNAPSHOT.jar
else
java -jar  --add-opens=java.base/java.io=ALL-UNNAMED --add-opens=java.base/java.util=ALL-UNNAMED target/LatexExamenShuffler-1.0-SNAPSHOT.jar "$@"
fi
