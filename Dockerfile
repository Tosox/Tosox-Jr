# Use Java image
FROM eclipse-temurin:17-jre

# Set workdir
WORKDIR /app

# Copy the java file
COPY target/tosox-jr.jar .

# Set default command to run the bot
CMD ["java", "-jar", "tosox-jr.jar"]
