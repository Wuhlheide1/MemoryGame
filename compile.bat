@echo off
javac -cp ".;IServ 5.7.jar" Main.java MemoryGame.java
java -cp ".;IServ 5.7.jar" Main %1