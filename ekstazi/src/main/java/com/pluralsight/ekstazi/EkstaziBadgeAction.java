package com.pluralsight.ekstazi;

import hudson.PluginWrapper;
import hudson.model.BuildBadgeAction;
import hudson.model.Cause;
import jenkins.model.Jenkins;

public class EkstaziBadgeAction implements BuildBadgeAction {

    private final Cause cause;

    public EkstaziBadgeAction(Cause cause) {
        this.cause = cause;
    }

    public String getTooltip() {
        return cause.getShortDescription();
    }

    public String getIcon() {

        String path = null;

        if(true) {                                  //TODO: This needs to be derived from 'EkstaziBuilder.enableEkstazi'
            path = "ekstazi-enabled.png";
        } else {
            path = "ekstazi-disabled.png";
        }

        return getIconPath(path);
    }

    private static String getIconPath(String iconName) {
        PluginWrapper wrapper = Jenkins.getInstance().getPluginManager().getPlugin(EkstaziBadgePlugin.class);
        return "/plugin/" + wrapper.getShortName() + "/images/" + iconName;
    }

    public static EkstaziBadgePlugin getPlugin() {
        return (EkstaziBadgePlugin) Jenkins.getInstance().getPlugin(EkstaziBadgePlugin.class);
    }

    @Override
    public String getIconFileName() {
        return null;
    }

    @Override
    public String getDisplayName() {
        return "Ekstazi: " + getTooltip();
    }

    @Override
    public String getUrlName() {
        return "";
    }

    /*
     * Helper method to check if user enabled Ekstazi in the global config or not
     */
    /*public boolean isEkstaziEnabled() {
    //TODO::

    }*/
}
