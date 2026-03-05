javac -d bin_s -cp ".:libs/*" server/*.java client/*.java -Xdiags:verbose
(
  cd bin_s && 
  java -cp ".:../libs/*" server.Server
)
