FROM jetty
COPY ./*-standalone.jar .
CMD java -jar ./*-standalone.jar
# TODO: Use this Jetty server in the Github Actions build pipeline -> note that ports 3000 and 3001 need to be exposed:
# docker run -d -p 80:8080 -p 443:8443 -p 3000:3000 -p 3001:3001 my-jetty