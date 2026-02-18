javac -cp .:\
libs/gson-2.13.2.jar:\
libs/sqlite-jdbc-3.51.1.0.jar:\
libs/Java-WebSocket-1.6.0.jar:\
libs/slf4j-api-2.0.9.jar\
 server/*.java client/*.java -Xdiags:verbose
java -cp .:\
libs/gson-2.13.2.jar:\
libs/sqlite-jdbc-3.51.1.0.jar:\
libs/Java-WebSocket-1.6.0.jar:\
libs/slf4j-api-2.0.9.jar\
 server.Server

