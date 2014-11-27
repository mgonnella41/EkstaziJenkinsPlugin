package com.pluralsight.ekstazi;

import hudson.FilePath;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.PermalinkProjectAction;
import hudson.model.Run;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class EkstaziPermalinkProjectAction implements PermalinkProjectAction {

    public static final String DISPLAY_NAME = "Last successful Ekstazi build";
    public static final String ID           = "lastSuccessfulEkstaziBuild";

    private AbstractProject<? extends AbstractProject, ? extends AbstractBuild> project;
    private List<Permalink> permalinks;

    public EkstaziPermalinkProjectAction(AbstractProject<? extends AbstractProject, ? extends AbstractBuild> project) {
        this.project = project;
        permalinks   = new ArrayList<Permalink>();
        addPermalinks();
    }

    @Override
    public List<Permalink> getPermalinks() {
        return permalinks;
    }

    @Override
    public String getIconFileName() {
        return null;
    }

    @Override
    public String getDisplayName() {
        return DISPLAY_NAME;
    }

    @Override
    public String getUrlName() {
        return null;
    }

    private void addPermalinks() {
        if (permalinks.size() != 0) {
            return;
        }

        Run lastSucessfulEkstaziBuild = getLastSucessfulEkstaziBuild(project.getLastSuccessfulBuild());

        if (lastSucessfulEkstaziBuild != null) {
            permalinks.add(new EkstaziPermalink(lastSucessfulEkstaziBuild.getNumber(), ID, DISPLAY_NAME));
        }
    }

    private Run getLastSucessfulEkstaziBuild(Run lastSuccessfulBuild) {

        if (lastSuccessfulBuild == null) {
            return null;
        }

        FilePath ekstaziDir = new FilePath(new File(lastSuccessfulBuild.getArtifactsDir() + "/.ekstazi"));

        boolean ekstaziEnabledforLastSuccessfulBuild = false;

        try {
            if (ekstaziDir.exists()) {
                ekstaziEnabledforLastSuccessfulBuild = true;
            }

        } catch (IOException e) {
            //Swallow exception
        } catch (InterruptedException e) {
            //Swallow exception
        }

        if(!ekstaziEnabledforLastSuccessfulBuild) {
            return getLastSucessfulEkstaziBuild(lastSuccessfulBuild.getPreviousSuccessfulBuild());
        }

        return lastSuccessfulBuild;
    }

}
