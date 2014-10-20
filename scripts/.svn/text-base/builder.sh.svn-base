#!/bin/bash

project=commons-lang3
repo_url="http://svn.apache.org/repos/asf/commons/proper/lang/trunk"
build_system="mvn"


# Ekstazi version
version="4.0.1"

configuration="<configuration ><argLine>-javaagent:org.ekstazi.core-4.0.1.jar=mode=junit</argLine> </configuration>"

function run() {
            rev="$1"
            configuration_line="$2"
            

            rm -f ${project}"/pom.xml"
            svn checkout ${repo_url}@${rev} ${project};
            cd ${project};
            
            sed -i "${configuration_line}i\\${configuration}" pom.xml
            
            # Download Ekstazi
            url="mir.cs.illinois.edu/ekstazi/release/"
            if [ ! -e org.ekstazi.core-${version}.jar ]; then wget "${url}"org.ekstazi.core-${version}.jar; fi
            
            # Compile separate not to measure time
            # ${build_system} test-compile
            
            # Run tests
            #${build_system} test
            ${build_system} -Dtest=CharEncodingTest -DfailIfNoTests=false test
            
            cd ..
}

function step1() {
     (       
        run "1567796" "539"
     )
}


# Run tests with Ekstazi over the checked out revision
cwd=`pwd`
step1 | tee step1.txt
sed -i 's/.*'"${cwd//\//\\/}"'/USER/g' step1.txt
grep 'sec -' step1.txt | cut -f1 -d'-' > table1.txt