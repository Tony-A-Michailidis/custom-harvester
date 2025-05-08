
FROM openjdk:11-jdk-slim

# Set working directory
WORKDIR /usr/local/geonetwork

# Install required packages
RUN apt-get update && apt-get install -y maven git nodejs npm && rm -rf /var/lib/apt/lists/*

# Clone and build GeoNetwork with the custom harvester
RUN git clone https://github.com/geonetwork/core-geonetwork.git . && \
    mvn clean install -DskipTests && \
    cd web-ui && npm install && npm run build && \
    cd .. && cp -r web-ui/dist web/src/main/webapp

# Expose GeoNetwork port
EXPOSE 8080

# Start GeoNetwork
CMD ["mvn", "-f", "web/pom.xml", "jetty:run"]
