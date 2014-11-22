package com.pluralsight.ekstazi;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
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

    public ArrayList<FilePath> ekstaziFolders;

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
                // Archive Ekstazi build artifacts
                result = super.perform(build, launcher, listener);
                // Remove Ekstazi from workspace
                // ekstaziPath.deleteRecursive();
                FilePath buildDir = new FilePath(build.getProject().getBuildDir());
                buildDir = buildDir.child("lastEkstaziBuild");
                // Add a symlink for the last successful Ekstazi build
                buildDir.symlinkTo(Integer.toString(build.number), listener);
                listener.getLogger().println("Archiving Ekstazi results.");
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
        File root = new File(rootFolder.toURI());
        File[] filesAndFolders = root.listFiles();
        for(File file : filesAndFolders) {
            if(file.isDirectory()) {
                if(file.toString().endsWith(".ekstzi")) {
                    ekstaziFolders.add(new FilePath(file));
                }
                getEkstaziFolders(new FilePath(file));
            }
        }
    }
}
