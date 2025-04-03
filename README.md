## INFORMATION
Java files and classes can be found in src/
DDL files can be found in tables/
CSV files can be found in data/
JAR files can be found in libs/
Javadoc documentation can be found in docs/index.html
The database is called 'database.db'

### These commands assume a terminal is opened in the P3 directory

## Add appropriate JAR files to classpath
export CLASSPATH=${CLASSPATH}:libs/sqlite-jdbc-3.45.1.0.jar:libs/slf4j-api-1.7.36.jar:libs/slf4j-nop-1.7.36.jar

## Compile all java files
javac src/*.java

## Initialise database
java src/InitialiseDB

## Populate database
java src/PopulateDB

## Run queries with integer argument
java QueryDB {integer 1-6}

## Description of each query
1. List the titles of all the movies in the database.
2. List the names of the actors who perform in some specified movie.
3. List the plots of movies with a specified actor in them and directed by some particular director.
4. List the directors of the movies that have a particular actor in them.
5. List all actors who have won at least one award and the movies they have appeared in.
6. Find the top-rated movies (IMDB rating greater than or equal to 8.0) along with their directors and lead actors.