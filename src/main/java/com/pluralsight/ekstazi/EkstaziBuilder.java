package com.pluralsight.ekstazi;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.remoting.Callable;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.ListBoxModel;
import hudson.util.ListBoxModel.Option;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;

// Builder for Ekstazi that inserts 
public class EkstaziBuilder extends Builder implements Serializable {
    public static final String DEFAULT_EKSTAZI_VERSION = "4.3.0";

    public final boolean ekstaziEnable;
    public final boolean ekstaziForceFailing;
    public final boolean ekstaziOverwrite;

    /* Fields in config.jelly must match the parameter names in the "DataBoundConstructor".  The config.jelly is used for
     * job specific config and global.jelly is used for global config
     */
    @DataBoundConstructor
    public EkstaziBuilder(boolean ekstaziEnable, boolean ekstaziForceFailing, boolean ekstaziOverwrite) {
        this.ekstaziEnable = ekstaziEnable;
        this.ekstaziForceFailing = ekstaziForceFailing;
        this.ekstaziOverwrite = ekstaziOverwrite;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean perform(final AbstractBuild<?, ?> build, Launcher launcher,
            final BuildListener listener) throws IOException, InterruptedException {

        // Declare inputs for callable that can be runo n master or slave
        final FilePath buildWorkspace = build.getWorkspace();
        final FilePath buildDir = new FilePath(build.getProject().getBuildDir());
        final String ekstaziVersion = getDescriptor().getEkstaziVersion();

        // Enable archiver for Ekstazi artifacts
        if (ekstaziEnable == true) {
            EkstaziArtifactArchiver ekstaziArchiver = new EkstaziArtifactArchiver();
            build.getProject().getPublishersList().replaceBy(Collections.singleton(ekstaziArchiver));
            FilePath previousResults = buildDir.child("lastEkstaziBuild");
            previousResults = previousResults.child("archive");
            previousResults.copyRecursiveTo(buildWorkspace);

        }

        if(ekstaziOverwrite == false) {
            // Use callable to support slave nodes
            Callable<String, IOException> task = new Callable<String, IOException>() {
                static final long serialVersionUID = 1L;
                public String call() throws IOException {
                    EkstaziManager ekstaziManager;
                    // Get the POM for this project
                    MavenFinder mavenFinder = new MavenFinder(buildWorkspace);
                    ArrayList<FilePath> pomFiles = new ArrayList<FilePath>();
                    try{
                        pomFiles = mavenFinder.find();
                    } catch (InterruptedException e) {
                        listener.getLogger().println("Unable to find POM files.");
                        e.printStackTrace();
                    }
                    try {
                        if(pomFiles.size() > 0) {
                            for( FilePath pomFile : pomFiles) {
                                if(pomFile.toString().contains("dummy-project")) {
                                    continue;
                                }
                                ekstaziManager = new EkstaziMavenManager(pomFile, ekstaziVersion);
                                if(ekstaziEnable) {

                                    // Add a post build step to collect the Ekstazi results
                                    ekstaziManager.enable(buildDir, buildWorkspace, ekstaziVersion, ekstaziForceFailing);
                                } else {
                                    // remove Ekstazi from POM if it is disabled
                                    ekstaziManager.disable(buildDir, buildWorkspace, ekstaziVersion);
                                    listener.getLogger().println("Modifying pom.xml located at, "+pomFile.toString()+" to disable Ekstazi.");
                                }
                            }
                        }
                    } catch (EkstaziException e) {
                        listener.getLogger().println("Ekstazi not supported for this project.");
                        e.printStackTrace();
                    }
                    return InetAddress.getLocalHost().getHostName();
                }
            };

            launcher.getChannel().call(task);
        }
        return true;
    }

    // Overridden for better type safety.
    // If your plugin doesn't really define any property on Descriptor,
    // you don't have to do this.
    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl)super.getDescriptor();
    }

    // Global settings for all jobs
    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {
        // Persist version number
        private String EkstaziVersion;

        // Load stored settings
        public DescriptorImpl() {
            load();

            // Set the default version if we weren't able to load it from disk
            if (EkstaziVersion == null) {
                EkstaziVersion = DEFAULT_EKSTAZI_VERSION;
            }
        }


        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            // Indicates that this builder can be used with all kinds of project types 
            return true;
        }

        /**
         * This human readable name is used in the configuration screen.
         */
        public String getDisplayName() {
            return "Ekstazi Regression Testing";
        }

        // Fill out the drop down selecting version number
        public ListBoxModel doFillEkstaziVersionItems() {

            ListBoxModel items = new ListBoxModel(
                    new Option("4.3.0 - November 2014","4.3.0", EkstaziVersion.equals("4.3.0")),
                    new Option("4.2.0 - November 2014","4.2.0", EkstaziVersion.equals("4.2.0")),
                    new Option("4.1.0 - October 2014","4.1.0", EkstaziVersion.equals("4.1.0")),
                    new Option("4.0.1 - October 2014","4.0.1", EkstaziVersion.equals("4.0.1"))
                    );

            return items;
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            // set private members and call save to persist
            EkstaziVersion = formData.getString("EkstaziVersion");
            save();
            return super.configure(req,formData);
        }

        /**
         * This function returns the Ekstazi version selected.
         *
         * The method name is bit awkward because global.jelly calls this method to determine
         * the initial state of the checkbox by the naming convention.
         */
        public String getEkstaziVersion() {
            return EkstaziVersion;
        }

        public boolean isForceFailingSupported() {

            if (getEkstaziVersion().equals("4.0.1")) {
                return false;
            }

            return true;
        }
    }
}

