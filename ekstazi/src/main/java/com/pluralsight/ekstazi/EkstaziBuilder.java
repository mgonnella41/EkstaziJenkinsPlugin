package com.pluralsight.ekstazi;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.ListBoxModel;
import hudson.util.ListBoxModel.Option;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import jenkins.model.ArtifactManagerConfiguration;

import net.sf.json.JSONObject;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
import org.xml.sax.SAXException;

// Builder for Ekstazi that inserts 
public class EkstaziBuilder extends Builder {

    public final boolean enableEkstazi;

    /* Fields in config.jelly must match the parameter names in the "DataBoundConstructor".  The config.jelly is used for
     * job specific config and global.jelly is used for global config
     */
    @DataBoundConstructor
    public EkstaziBuilder(boolean enableEkstazi) {
        this.enableEkstazi = enableEkstazi;
    }
    /**
     * We'll use this from the <tt>config.jelly</tt>.
     */
    public boolean getEkstaziEnabled() {
        return enableEkstazi;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher,
            BuildListener listener) throws IOException {
        // This is where you 'build' the project.
        FilePath workspace = build.getModuleRoot();
        String xmlFilePath = "";
        xmlFilePath = workspace.toString()+"/pom.xml";
        try {
            EkstaziPOMManager ekstaziPOMManager = new EkstaziPOMManager(xmlFilePath);
            if(enableEkstazi == true) {
                if(ekstaziPOMManager.checkForEkstazi() == false) {
                    ekstaziPOMManager.addEkstazi(getDescriptor().EkstaziVersion);
                    listener.getLogger().println("Modifying pom.xml located at: "+xmlFilePath+" to enable Ekstazi.");
                } else {
                    try {
                        ekstaziPOMManager.removeEkstazi();
                        ekstaziPOMManager.addEkstazi(getDescriptor().EkstaziVersion);
                        listener.getLogger().println("Modifying pom.xml located at, "+xmlFilePath+" to enable Ekstazi.");
                    } catch (Exception e) {
                        listener.getLogger().println("Ekstazi not supported for this project.");
                    }
                }
            } else {
                if(ekstaziPOMManager.checkForEkstazi()) {
                    ekstaziPOMManager.removeEkstazi();
                    listener.getLogger().println("Modifying pom.xml located at, "+xmlFilePath+" to disable Ekstazi.");
                }
            }
        } catch (TransformerException e) {
            listener.getLogger().println("Ekstazi not supported for this project.");
        } catch (SAXException e) {
            listener.getLogger().println("Ekstazi not supported for this project.");
        } catch (ParserConfigurationException e) {
            listener.getLogger().println("Ekstazi not supported for this project.");
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
            // Set default version
            if (EkstaziVersion == null) {
                EkstaziVersion = "4.0.1";
            }
            ListBoxModel items = new ListBoxModel(
                    new Option("4.0.1 - October 2014","4.0.1", EkstaziVersion.equals("4.0.1")),
                    new Option("4.0.0 - Beta", "4.0.0", EkstaziVersion.equals("4.0.0"))
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
    }
}

