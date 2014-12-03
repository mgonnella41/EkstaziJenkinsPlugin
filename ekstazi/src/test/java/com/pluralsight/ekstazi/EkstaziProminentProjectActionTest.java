package com.pluralsight.ekstazi;


import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.File;
import java.net.URL;

public class EkstaziProminentProjectActionTest {

    @Mock
    private AbstractProject<? extends AbstractProject, ? extends AbstractBuild> mockProjectWithLastBuild;
    @Mock
    private AbstractProject<? extends AbstractProject, ? extends AbstractBuild> mockProjectWithLastBuildAndEkstazi;
    @Mock
    private AbstractProject<? extends AbstractProject, ? extends AbstractBuild> mockProjectWithoutLastBuild;
    @Mock
    private AbstractBuild mockLastBuild;
    @Mock
    private AbstractBuild mockLastBuildWithEkstazi;

    private String artifactsDir;

    @Before
    public void setUp() {

        URL url = Thread.currentThread().getContextClassLoader().getResource(".");
        artifactsDir = url.getPath();

        mockLastBuild            = Mockito.mock(AbstractBuild.class);
        mockLastBuildWithEkstazi = Mockito.mock(AbstractBuild.class);

        Mockito.when(mockLastBuildWithEkstazi.getArtifactsDir()).thenReturn(new File(artifactsDir));

        mockProjectWithLastBuild           = Mockito.mock(AbstractProject.class);
        mockProjectWithLastBuildAndEkstazi = Mockito.mock(AbstractProject.class);
        mockProjectWithoutLastBuild        = Mockito.mock(AbstractProject.class);

        Mockito.when(mockProjectWithLastBuild.          getLastBuild()).thenReturn(mockLastBuild);
        Mockito.when(mockProjectWithLastBuildAndEkstazi.getLastBuild()).thenReturn(mockLastBuildWithEkstazi);
        Mockito.when(mockProjectWithoutLastBuild.       getLastBuild()).thenReturn(null);
    }

    @Test
    public void ekstaziEnabledForLastBuildTest() {
        EkstaziProminentProjectAction action = new EkstaziProminentProjectAction(mockProjectWithLastBuildAndEkstazi);
        Assert.assertTrue(action.ekstaziEnabledForLastBuild());
    }

    @Test
    public void ekstaziNotEnabledForLastBuildTest() {
        EkstaziProminentProjectAction action = new EkstaziProminentProjectAction(mockProjectWithLastBuild);
        Assert.assertFalse(action.ekstaziEnabledForLastBuild());
    }

    @Test
    public void ekstaziNotEnabledForLastBuildDoesNotExistTest() {
        EkstaziProminentProjectAction action = new EkstaziProminentProjectAction(mockProjectWithoutLastBuild);
        Assert.assertFalse(action.ekstaziEnabledForLastBuild());
    }

    @Test
    public void displayNameTest() {
        EkstaziProminentProjectAction action = new EkstaziProminentProjectAction(mockProjectWithLastBuildAndEkstazi);
        Assert.assertEquals(EkstaziProminentProjectAction.DISPLAY_NAME, action.getDisplayName());
    }

    @Test
    public void iconFileNameForLastBuidDoesNotExistTest() {
        EkstaziProminentProjectAction action = new EkstaziProminentProjectAction(mockProjectWithoutLastBuild);
        Assert.assertNull(action.getIconFileName());
    }

    @Test
    public void urlNameNameForLastBuidDoesNotExistTest() {
        EkstaziProminentProjectAction action = new EkstaziProminentProjectAction(mockProjectWithoutLastBuild);
        Assert.assertNull(action.getUrlName());
    }

}
