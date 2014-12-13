package com.pluralsight.ekstazi;


import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.PermalinkProjectAction;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.File;
import java.net.URL;
import java.util.List;

public class EkstaziPermalinkProjectActionTest {
    public static final int BUILD_NUMER = 3;

    @Mock
    private AbstractProject<? extends AbstractProject, ? extends AbstractBuild> mockProjectWithLastSuccessfulBuild;
    @Mock
    private AbstractProject<? extends AbstractProject, ? extends AbstractBuild> mockProjectWithoutLastSuccessfulBuild;
    @Mock
    private AbstractBuild mockLastSuccessfulBuild;

    private String artifactsDir;

    @Before
    public void setUp() {

        URL url = Thread.currentThread().getContextClassLoader().getResource(".");
        artifactsDir = url.getPath();

        mockLastSuccessfulBuild = Mockito.mock(AbstractBuild.class);
        Mockito.when(mockLastSuccessfulBuild.getNumber()).thenReturn(BUILD_NUMER);
        Mockito.when(mockLastSuccessfulBuild.getArtifactsDir()).thenReturn(new File(artifactsDir));
        Mockito.when(mockLastSuccessfulBuild.getPreviousSuccessfulBuild()).thenReturn(null);

        mockProjectWithLastSuccessfulBuild    = Mockito.mock(AbstractProject.class);
        mockProjectWithoutLastSuccessfulBuild = Mockito.mock(AbstractProject.class);

        Mockito.when(mockProjectWithLastSuccessfulBuild.   getLastSuccessfulBuild()).thenReturn(mockLastSuccessfulBuild);
        Mockito.when(mockProjectWithoutLastSuccessfulBuild.getLastSuccessfulBuild()).thenReturn(null);
    }

    @Test
    public void nonZeroPermalinksWhenLastSuccessfulBuildExistsTest() {

        EkstaziPermalinkProjectAction action = new EkstaziPermalinkProjectAction(mockProjectWithLastSuccessfulBuild);
        List<PermalinkProjectAction.Permalink> permalinks = action.getPermalinks();

        Assert.assertNotEquals(0, permalinks.size());
    }

    @Test
    public void zeroPermalinksWhenLastSuccessfulBuildDoesNotExistTest() {

        EkstaziPermalinkProjectAction action = new EkstaziPermalinkProjectAction(mockProjectWithoutLastSuccessfulBuild);
        List<PermalinkProjectAction.Permalink> permalinks = action.getPermalinks();

        Assert.assertEquals(0, permalinks.size());
    }

    @Test
    public void iconFileNameTest() {
        EkstaziPermalinkProjectAction action = new EkstaziPermalinkProjectAction(mockProjectWithLastSuccessfulBuild);
        Assert.assertNull(action.getIconFileName());
    }

    @Test
    public void displayNameTest() {
        EkstaziPermalinkProjectAction action = new EkstaziPermalinkProjectAction(mockProjectWithLastSuccessfulBuild);
        Assert.assertEquals(EkstaziPermalinkProjectAction.DISPLAY_NAME, action.getDisplayName());
    }

    @Test
    public void urlNameTest() {
        EkstaziPermalinkProjectAction action = new EkstaziPermalinkProjectAction(mockProjectWithLastSuccessfulBuild);
        Assert.assertNull(action.getUrlName());
    }
}
