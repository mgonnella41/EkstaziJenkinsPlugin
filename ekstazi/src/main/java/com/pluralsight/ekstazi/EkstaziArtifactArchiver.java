package com.pluralsight.ekstazi;
import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.TaskListener;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;
import hudson.tasks.ArtifactArchiver;


public class EkstaziArtifactArchiver extends ArtifactArchiver {
    @DataBoundConstructor
    public EkstaziArtifactArchiver() {
        super(".ekstazi/*, .ekstazi/test-results/*", "", false, false);
    }

    public boolean perform(AbstractBuild build, Launcher launcher,
            BuildListener listener) throws InterruptedException {
        boolean result = super.perform(build, launcher, listener);
        listener.getLogger().println("Archiving Ekstazi results.");
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


