package com.pluralsight.ekstazi;


import hudson.FilePath;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.ProminentProjectAction;
import jenkins.model.Jenkins;

import java.io.File;
import java.io.IOException;

public class EkstaziProminentProjectAction implements ProminentProjectAction {

    public static final String DISPLAY_NAME = "Last Build";


    @SuppressWarnings("rawtypes")
    private AbstractProject<? extends AbstractProject, ? extends AbstractBuild> project;

    public EkstaziProminentProjectAction(AbstractProject<? extends AbstractProject, ? extends AbstractBuild> project) {
        this.project = project;
    }

    public boolean lastBuildExists() {

        if (project.getLastBuild() != null) {
            return true;
        }

        return false;
    }

    public boolean ekstaziEnabledForLastBuild() {

        boolean ekstaziEnabledforLastBuild = false;

        if (lastBuildExists()) {
            FilePath ekstaziDir = new FilePath(new File(project.getLastBuild().getArtifactsDir() + "/.ekstazi"));

            try {
                if (ekstaziDir.exists()) {
                    ekstaziEnabledforLastBuild = true;
                }

            } catch (IOException e) {
                //Swallow exception
            } catch (InterruptedException e) {
                //Swallow exception
            }
        }

        return ekstaziEnabledforLastBuild;
    }

    @Override
    public String getIconFileName() {
        String iconPath = null;

        if (lastBuildExists()) {
            if (ekstaziEnabledForLastBuild()) {
                iconPath = EkstaziBadgeAction.getIconPath(EkstaziBadgeAction.EKSTAZI_ENABLED_ICON_FILENAME);
            } else {
                iconPath = EkstaziBadgeAction.getIconPath(EkstaziBadgeAction.EKSTAZI_DISABLED_ICON_FILENAME);
            }
        }

        return iconPath;

    }

    @Override
    public String getDisplayName() {
        return DISPLAY_NAME;
    }

    @Override
    public String getUrlName() {
        //return project.getLastBuild().getAbsoluteUrl();
        return Jenkins.getInstance().getRootUrl() + project.getLastBuild().getUrl();

    }
}
