package com.pluralsight.ekstazi;


import hudson.FilePath;
import org.junit.Assert;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class MavenFinderTest {

    public static final String PROJECT_NAME = "dummy-project";

    private String workspaceDir;

    @Before
    public void setUp() {
        URL url = Thread.currentThread().getContextClassLoader().getResource(".");
        workspaceDir = url.getPath();

        // Create nested pom files inside the testResources folder
        File pomOne = new File(workspaceDir + "/" + PROJECT_NAME + "/module-one/pom.xml");
        File pomTwo = new File(workspaceDir + "/" + PROJECT_NAME + "/module-two/pom.xml");

        pomOne.getParentFile().mkdirs();
        pomTwo.getParentFile().mkdirs();

        try {
            pomTwo.createNewFile();
            pomOne.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @After
    public void tearDown() {
        try {
            FileUtils.deleteDirectory(new File(workspaceDir + "/" + PROJECT_NAME + "/module-one"));
            FileUtils.deleteDirectory(new File(workspaceDir + "/" + PROJECT_NAME + "/module-two"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void pomDirectoriesTest() {
        MavenFinder mavenFinder = new MavenFinder(new FilePath(new File(workspaceDir + "/" + PROJECT_NAME)));
        ArrayList<FilePath> pomDirectories = mavenFinder.find();

        Assert.assertEquals(3, pomDirectories.size());
    }

}
