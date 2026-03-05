#!/usr/bin/bash
javac -d bin -cp ".:libs/*" client/*.java server/*.java -Xdiags:verbose
(
  cd bin
  java -cp ".:../libs/*" client.ChatController
)
