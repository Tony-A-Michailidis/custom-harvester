#---- with maven 3.8.6 
FROM openjdk:25-jdk-slim

ENV MAVEN_VERSION=3.8.6 \
  MAVEN_HOME=/opt/maven

WORKDIR /usr/local/geonetwork

# install git, node, npm and download Maven 3.8.6
RUN apt-get update && \
    apt-get install -y wget git nodejs npm && \
    wget https://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz && \
    tar -xzf apache-maven-${MAVEN_VERSION}-bin.tar.gz -C /opt && \
    ln -s /opt/apache-maven-${MAVEN_VERSION} ${MAVEN_HOME} && \
    update-alternatives --install /usr/bin/mvn mvn ${MAVEN_HOME}/bin/mvn 1 && \
    rm apache-maven-${MAVEN_VERSION}-bin.tar.gz && \
    rm -rf /var/lib/apt/lists/*

# now clone & build GeoNetwork
RUN git clone https://github.com/geonetwork/core-geonetwork.git . 
RUN cd ./core-geonetwork
RUN mvn clean install -DskipTests  
RUN cd ./web-ui 
RUN npm install 
RUN npm run build
RUN cd ../
RUN cp -r ./web-ui/dist web/src/main/webapp

EXPOSE 8080

CMD ["mvn", "-f", "web/pom.xml", "jetty:run"]