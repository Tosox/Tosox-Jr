# Use Java slim image
FROM openjdk:17-slim

# Set workdir
WORKDIR /app

# Copy the java file
COPY target/tosox-jr.jar .

# Set default command to run the bot
CMD ["java", "-jar", "tosox-jr.jar"]
