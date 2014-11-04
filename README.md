To run in debug mode:  
1. cd ekstazi  
2. ./debug.sh  
3. Open localhost:8080/jenkins in your web browser  
4. Currently the common-lang-3 project is configured to use the Ekstazi plugin.  The project will pull HEAD from the remote common-lang3 repo, modify the pom.xml, and run a small subset of the tests. The Ekstazi plugin is enabled under the pre-build steps.  

<b>Note:</b> Before running the build, please check the path for your JDK installation by navigating to:  
Manage Jenkins -> Configure System -> JDK
