package com.pluralsight.ekstazi;

import org.jvnet.hudson.test.JenkinsRule;
import org.apache.commons.io.FileUtils;
import hudson.matrix.MatrixProject;
import hudson.maven.MavenModuleSet;
import hudson.model.FreeStyleProject;
import hudson.model.*;
import hudson.maven.*;
import hudson.matrix.*;
import hudson.scm.*;
import hudson.tasks.Shell;

import org.junit.Rule;
import org.junit.Test;
import static org.junit.Assert.*;
import org.jvnet.hudson.test.HudsonTestCase;

public class BuildTests {
  @Rule public JenkinsRule j = new JenkinsRule();
  @Test public void commonglang3() throws Exception {
   MavenModuleSet project = j.createMavenProject("common-lang-3"); 
   j.configureMaven3();
   project.setScm(new SubversionSCM("http://svn.apache.org/repos/asf/commons/proper/lang/trunk"));
   j.buildAndAssertSuccess(project);
   // project.getBuildClass().buildAndAssertSuccess();
    // FreeStyleProject project = j.createFreeStyleProject();
    // j.
    // project.getBuildersList().add(new Shell("echo hello"));
    // MavenBuild build = project.scheduleBuild2(0).get();

    // System.out.println(build.getDisplayName() + " completed");
    // String s = FileUtils.readFileToString(build.getLogFile());
    // System.out.println("asdf");
    // assertFalse(s.contains("+ echo hello"));
  }
}
