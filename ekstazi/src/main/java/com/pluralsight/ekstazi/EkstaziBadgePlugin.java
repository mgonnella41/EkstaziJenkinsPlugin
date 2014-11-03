package com.pluralsight.ekstazi;

import hudson.Plugin;
import hudson.model.Descriptor;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;
import javax.servlet.ServletException;

public class EkstaziBadgePlugin extends Plugin {

    private boolean activated = true;           //To indicate if this plugin is activated

    public EkstaziBadgePlugin() { }

    @DataBoundConstructor
    public EkstaziBadgePlugin(boolean activated) {
        this.activated = activated;
    }

    @Override
    public void configure(StaplerRequest req, JSONObject formData)
            throws IOException, ServletException, Descriptor.FormException {

        super.configure(req, formData);
        this.setActivated(formData.getBoolean(FIELD_ACTIVATED));
        this.save();
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public static final String FIELD_ACTIVATED = "ekstazibadge_activated";
}
