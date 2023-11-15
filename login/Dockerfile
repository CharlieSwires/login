FROM tomcat:9.0
ADD target/login-0.0.1-SNAPSHOT.jar /usr/local/tomcat/webapps/login.war
CMD ["npm i dotenv", "run"]
CMD ["catalina.sh", "run"]
