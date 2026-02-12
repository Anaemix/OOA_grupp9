# Use Eclipse Temurin JDK 21 (LTS) as the base
FROM eclipse-temurin:21-jdk

# Create an application directory
WORKDIR /app

# Copy your source code into the container
# (Assumes your .java files are in the same directory as the Dockerfile)
COPY . .

# Compile your Java code (e.g., Main.java)
# This confirms javac is working
RUN javac -cp .:libs/gson-2.13.2.jar:libs/sqlite-jdbc-3.51.1.0.jar server/*.java client/*.java -Xdiags:verbose
CMD ["java", "-cp", ".:libs/sqlite-jdbc-3.51.1.0.jar:libs/gson-2.13.2.jar", "server.Server"]

# Run the compiled program
