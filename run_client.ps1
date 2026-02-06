# compile
javac -cp ".;libs/gson-2.13.2.jar" client/*.java server/*.java -Xdiags:verbose

# run
java -cp ".;libs/gson-2.13.2.jar" client.ChatController

# remove class files
Get-ChildItem -Recurse -Filter "*.class" | Remove-Item