javac -cp .:libs/gson-2.13.2.jar client/*.java server/*.java -Xdiags:verbose
java -cp .:libs/gson-2.13.2.jar client.ConnectionHandler