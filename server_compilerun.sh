javac -cp .:libs/gson-2.13.2.jar:libs/sqlite-jdbc-3.51.1.0.jar server/*.java client/*.java -Xdiags:verbose
java -cp .:libs/sqlite-jdbc-3.51.1.0.jar:libs/gson-2.13.2.jar server.Server