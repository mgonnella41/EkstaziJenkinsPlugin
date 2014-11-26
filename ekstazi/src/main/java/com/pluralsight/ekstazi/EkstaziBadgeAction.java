package com.pluralsight.ekstazi;

import hudson.PluginWrapper;
import hudson.model.BuildBadgeAction;
import jenkins.model.Jenkins;

public class EkstaziBadgeAction implements BuildBadgeAction {

    public static final String EKSTAZI_ENABLED_ICON_FILENAME        = "ekstazi-enabled.png";
    public static final String EKSTAZI_DISABLED_ICON_FILENAME       = "ekstazi-disabled.png";
    public static final String EKSTAZI_ENABLED_ANIME_ICON_FILENAME  = "ekstazi-enabled-anime.gif";
    public static final String EKSTAZI_DISABLED_ANIME_ICON_FILENAME = "ekstazi-disabled-anime.gif";
    public static final String EKSTAZI_ENABLED_TOOLTIP              = "Ekstazi was enabled for this build";
    public static final String EKSTAZI_DISABLED_TOOLTIP             = "Ekstazi was disabled for this build";
    public static final String EKSTAZI_ENABLED_ANIME_TOOLTIP        = "Build is in progress";
    public static final String EKSTAZI_DISABLED_ANIME_TOOLTIP       = "Build is in progress";


    private final boolean ekstaziEnabled;
    private final boolean animeEnabled;
    private final String  urlName;

    public EkstaziBadgeAction(boolean ekstaziEnabled, boolean animeEnabled) {
        this.ekstaziEnabled = ekstaziEnabled;
        this.animeEnabled   = animeEnabled;
        this.urlName        = "";
    }

    public EkstaziBadgeAction(boolean ekstaziEnabled, boolean animeEnabled, String urlName) {
        this.ekstaziEnabled = ekstaziEnabled;
        this.animeEnabled   = animeEnabled;
        this.urlName        = urlName;
    }

    public String getTooltip() {

        if(ekstaziEnabled) {
            if (animeEnabled) {
                return EKSTAZI_ENABLED_ANIME_TOOLTIP;
            } else {
                return EKSTAZI_ENABLED_TOOLTIP;
            }
        }

        if (animeEnabled) {
            return EKSTAZI_DISABLED_ANIME_TOOLTIP;
        } else {
            return EKSTAZI_DISABLED_TOOLTIP;
        }
    }

    public String getIcon() {

        if(ekstaziEnabled) {
            if (animeEnabled) {
                return getIconPath(EKSTAZI_ENABLED_ANIME_ICON_FILENAME);
            } else {
                return getIconPath(EKSTAZI_ENABLED_ICON_FILENAME);
            }
        }

        if (animeEnabled) {
            return getIconPath(EKSTAZI_DISABLED_ANIME_ICON_FILENAME);
        } else {
            return getIconPath(EKSTAZI_DISABLED_ICON_FILENAME);
        }
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
        return urlName;
    }

}
