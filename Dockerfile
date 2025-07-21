# Use Java alpine image
FROM openjdk:17-alpine

# Set workdir
WORKDIR /app

# Copy the java file
COPY target/tosox-jr.jar .

# Set default command to run the bot
CMD ["java", "-jar", "tosox-jr.jar"]
