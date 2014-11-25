package com.pluralsight.ekstazi;

import org.jvnet.hudson.test.JenkinsRule;
import hudson.maven.MavenModuleSet;
import hudson.matrix.*;
import hudson.scm.*;

import org.junit.Rule;
import org.junit.Test;

public class BuildTests {
  @Rule public JenkinsRule j = new JenkinsRule();
  @Test public void commonglang3() throws Exception {
   MavenModuleSet project = j.createMavenProject("common-lang-3"); 
   j.configureMaven3();
   project.setScm(new SubversionSCM("http://svn.apache.org/repos/asf/commons/proper/lang/trunk"));
   j.buildAndAssertSuccess(project);
  }
}
