package com.pluralsight.ekstazi;
import java.io.IOException;

import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;
import hudson.tasks.ArtifactArchiver;


public class EkstaziArtifactArchiver extends ArtifactArchiver {
    @DataBoundConstructor
    public EkstaziArtifactArchiver() {
        super(".ekstazi/*, .ekstazi/test-results/*", "", false, false);
    }

    public boolean perform(AbstractBuild build, Launcher launcher,
            BuildListener listener) {
        boolean result = true;
        try {
            //ttest here
            FilePath workspacePath = build.getWorkspace();
            FilePath ekstaziPath = workspacePath.child(".ekstazi");
            if(ekstaziPath.exists()) {
                result = super.perform(build, launcher, listener);
                ekstaziPath.deleteRecursive();
                FilePath buildDir = new FilePath(build.getProject().getBuildDir());
                buildDir = buildDir.child("lastSuccessfulEkstaziBuild");
                buildDir.symlinkTo(Integer.toString(build.number), listener);
                listener.getLogger().println("Archiving Ekstazi results.");
            }
        } catch (IOException | InterruptedException e) {
            listener.getLogger().println("Unable to archive old Ekstazi output.");
        }
        return result;
    }

    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        public String getDisplayName() {
            return "Archive Ekstazi Artifacts";
        }
    }
}
