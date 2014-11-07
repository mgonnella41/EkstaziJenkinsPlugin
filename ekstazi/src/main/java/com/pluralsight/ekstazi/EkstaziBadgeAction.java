package com.pluralsight.ekstazi;

import hudson.PluginWrapper;
import hudson.model.BuildBadgeAction;
import jenkins.model.Jenkins;

public class EkstaziBadgeAction implements BuildBadgeAction {

    public static final String EKSTAZI_ENABLED_ICON_FILENAME  = "ekstazi-enabled.png";
    public static final String EKSTAZI_DISABLED_ICON_FILENAME = "ekstazi-disabled.png";
    public static final String EKSTAZI_ENABLED_TOOLTIP        = "Ekstazi was enabled for this build";
    public static final String EKSTAZI_DISABLED_TOOLTIP       = "Ekstazi was disabled for this build";

    private final boolean ekstaziEnabled;

    public EkstaziBadgeAction(boolean ekstaziEnabled) {
        this.ekstaziEnabled = ekstaziEnabled;
    }

    public String getTooltip() {

        if(ekstaziEnabled) {
            return EKSTAZI_ENABLED_TOOLTIP;
        }

        return EKSTAZI_DISABLED_TOOLTIP;
    }

    public String getIcon() {

        if(ekstaziEnabled) {
            return getIconPath(EKSTAZI_ENABLED_ICON_FILENAME);
        }

        return getIconPath(EKSTAZI_DISABLED_ICON_FILENAME);
    }

    public static String getIconPath(String iconFileName) {
        PluginWrapper wrapper = Jenkins.getInstance().getPluginManager().getPlugin(EkstaziBadgePlugin.class);
        return "/plugin/" + wrapper.getShortName() + "/images/" + iconFileName;
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
        return getTooltip();
    }

    @Override
    public String getUrlName() {
        return "";
    }

}
