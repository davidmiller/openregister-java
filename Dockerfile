FROM java:8


# RUN wget https://downloads.gradle.org/distributions/gradle-2.6-bin.zip
# RUN unzip gradle-2.6-bin.zip
# RUN mv gradle-2.6 /opt/
# RUN rm gradle-2.6-bin.zip

RUN mkdir -p /openregister
WORKDIR /openregister

#sudo apt-get install ruby-full rubygems
#sudo gem install sass
#RUN ./build-styles.sh

#ADD build/libs/openregister-java-all.jar /openregister

# CMD gradle run
# CMD java -jar build/libs/openregister-java-all.jar server config.yaml
CMD ./gradlew run
