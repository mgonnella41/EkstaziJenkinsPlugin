package com.pluralsight.ekstazi;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.ArtifactArchiver;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class EkstaziArtifactArchiver extends ArtifactArchiver {

    private ArrayList<FilePath> ekstaziFolders;

    @DataBoundConstructor
    public EkstaziArtifactArchiver() {
        super("**/.ekstazi/*, **/.ekstazi/test-results/*", "", false, false);
        ekstaziFolders = new ArrayList<FilePath>();
    }

    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher,
            BuildListener listener) {
        boolean result = true;
        try {
            // Remove Ekstazi from list of post-build actions since it is automatically added
            build.getProject().getPublishersList().remove(EkstaziArtifactArchiver.class);
            this.getEkstaziFolders(build.getWorkspace());
            if(ekstaziFolders.size() > 0) {
                // Archive Ekstazi build artifacts
                result = super.perform(build, launcher, listener);
                // Remove Ekstazi from workspace
                for(FilePath filePath : ekstaziFolders) {
                    filePath.deleteRecursive();
                }
                // ekstaziPath.deleteRecursive();
                FilePath buildDir = new FilePath(build.getProject().getBuildDir());
                buildDir = buildDir.child("lastEkstaziBuild");
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

    public void getEkstaziFolders(FilePath rootFolder)
            throws IOException, InterruptedException {
        ArrayList<FilePath> filesAndFolders = new ArrayList<FilePath>(rootFolder.list());
        for(FilePath file : filesAndFolders) {
            if(file.isDirectory()) {
                if(file.toString().contains(".ekstazi") && !file.toString().contains("archive-tmp")) {
                    ekstaziFolders.add(file);
                }
                getEkstaziFolders(file);
            }
        }
    }
}
