#Ekstazi Jenkins Plugin
Support for enabling Ekstazi in Jenkins Maven projects

##Build and tests
<a href='https://travis-ci.org/peterlvilim/EkstaziJenkinsPlugin'><img src='https://secure.travis-ci.org/peterlvilim/EkstaziJenkinsPlugin.png?branch=master'></a>

##Features
- Automatic POM patching to enable Ekstazi
- Support for multiple Ekstazi versions
- Per build Ekstazi options (i.e. forcefailing, skipme)
- Ekstazi status icons in Jenkins web interface
- Archival and Permalinking of Ekstazi results
- Support for large multi-module projects
- Transparent support for distributed builds

##To Run Tests
```
mvn test
```
Code coverage

The project uses JaCoCo as the code coverage tool to generate coverage reports. These coverage reports can then be analyzed by SonarQube. Assuming the developer has a sonar server up and running, the following sequence of commands can be used to generate and analyze code coverage for the project:
```
mvn clean test
mvn sonar:sonar
```

##To Install
```
mvn install
cp EkstaziJenkinsPlugin.hpi $JENKINS_HOME/plugins
http://yourjenkinsserver:8080/jenkins/reload
```

##To Debug
```
mvn hpi:run
```
