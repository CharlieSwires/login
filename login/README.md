login
-----
<p>in git bash</p>
<p>git clone https://github.com/CharlieSwires/login

<p>This contains both the java and React</p>

build
-----
<p>cd login/login/src/main/webapp/hello</p>
<p>npm run build</p>
<p>cd ../../../..</p>
<p>you'll need an application.properties file with the DB username and password
URL</p>
<p>you'll also need an env.list file with the passwords of superuser, 
user and developer default users.</p>
<p>mvn package</p>

<p>produces login.war in target</p>

<p>7zip login.war</p>
<p>remove the hello/node-nodules</p>

deploy
------
<p>docker build --tag login:latest .</p>
<p>docker run  --env-file ./env.list --name container4 -d -p 8882:8080 login:latest</p>


browser
-------
<p>http://localhost:8882/login/hello/build/index.html</p>

RESTful
-------

<p>http://localhost:8882/login/api/V1/register POST</p>
<p>http://localhost:8882/login/api/V1/developer GET</p>
<p>http://localhost:8882/login/api/V1/user GET</p>
<p>http://localhost:8882/login/api/V1/superuser GET</p>
<p>http://localhost:8882/login/api/V1/loginAttempt POST</p>

