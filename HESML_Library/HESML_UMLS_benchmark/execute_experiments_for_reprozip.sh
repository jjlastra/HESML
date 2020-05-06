#!/bin/sh

# Stop the MySQL service if is started

service mysql stop

# Start the MySQL server

service mysql start        

# Don't exit the whole script on Ctrl+C

trap ' ' INT                        

# Execute the experiment

java -jar -Xms4096m ./dist/HESML_UMLS_benchmark.jar

trap - INT

# Stop MySQL
service mysql stop         
