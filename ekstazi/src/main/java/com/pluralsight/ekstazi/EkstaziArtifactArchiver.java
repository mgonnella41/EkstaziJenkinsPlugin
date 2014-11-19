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
        super("**/.ekstazi/*, **/.ekstazi/test-results/*", "", false, false);
    }

    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher,
            BuildListener listener) {
        boolean result = true;
        try {
            // Remove Ekstazi from list of post-build actions since it is automatically added
            build.getProject().getPublishersList().remove(EkstaziArtifactArchiver.class);
            FilePath workspacePath = build.getWorkspace();
            FilePath ekstaziPath = workspacePath.child(".ekstazi");
            // Check if there are Ekstazi build artifacts
            if(ekstaziPath.exists()) {
                // Archive Ekstazi build artifacts
                result = super.perform(build, launcher, listener);
                // Remove Ekstazi from workspace
                ekstaziPath.deleteRecursive();
                FilePath buildDir = new FilePath(build.getProject().getBuildDir());
                buildDir = buildDir.child("lastSuccessfulEkstaziBuild");
                // Add a symlink for the last successful Ekstazi build
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
            // Never show this build step as available since it is automatically added
            return false;
        }

        public String getDisplayName() {
            return "Archive Ekstazi Artifacts";
        }
    }
}
