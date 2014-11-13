package com.pluralsight.ekstazi;

import hudson.Extension;
import hudson.FilePath;
import hudson.model.AbstractBuild;
import hudson.model.Build;
import hudson.model.BuildBadgeAction;
import hudson.model.TaskListener;
import hudson.model.listeners.RunListener;
import jenkins.model.Jenkins;
import org.xml.sax.SAXException;

import javax.annotation.Nonnull;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

//Listener to all build (to add the Ekstazi badge action)
@Extension
public class RunListenerImpl extends RunListener<AbstractBuild> {
    public RunListenerImpl() {
        super(AbstractBuild.class);
    }

    @Override
    public void onCompleted(AbstractBuild build, @Nonnull TaskListener listener) {
        //Remove the EkstaziBadgeAction that was added in onStarted()
        List<BuildBadgeAction> badges = build.getBadgeActions();

        for(BuildBadgeAction badge: badges) {
            if (badge instanceof EkstaziBadgeAction) {
                build.getActions().remove(badge);
                break;                                                          //We added only one EkstaziBadgeAction, so it's safe to just break out from here
            }
        }

        badgeApply(build, listener, build.getModuleRoot(), false);             //we don't need an animated icon after the build has completed
        super.onCompleted(build, listener);
    }

    @Override
    public void onStarted(AbstractBuild build, TaskListener listener) {

        AbstractBuild lastBuild = build.getPreviousBuild();
        badgeApply(build, listener, lastBuild.getModuleRoot(), true);             //we DO need an animated icon during the build is going on (for lastBuild)

        super.onStarted(build, listener);
    }

    private void badgeApply(AbstractBuild build, TaskListener listener, FilePath buildWorkspace, boolean animeEnabled) {
        EkstaziBadgePlugin plugin = Jenkins.getInstance().getPlugin(EkstaziBadgePlugin.class);

        //If user wants Ekstazi badges for Build History, show 'em
        if(plugin.isActivated()) {

            String pomFilePath = buildWorkspace.toString() + "/pom.xml";
            boolean ekstaziEnabled = false;

            try {
                EkstaziPOMManager ekstaziPOMManager = new EkstaziPOMManager(pomFilePath);
                ekstaziEnabled = ekstaziPOMManager.checkForEkstazi();

                build.addAction(new EkstaziBadgeAction(ekstaziEnabled, animeEnabled));

                if(ekstaziEnabled) {
                    listener.getLogger().println("Adding ekstazi-enabled badge for current build.");
                } else {
                    listener.getLogger().println("Adding ekstazi-disabled badge for current build.");
                }

            } catch (ParserConfigurationException e) {
                listener.getLogger().println("Unable to detect whether ekstazi was enabled or not; adding default badge.)");
                build.addAction(new EkstaziBadgeAction(ekstaziEnabled, animeEnabled));
            } catch (SAXException e) {
                listener.getLogger().println("Unable to detect whether ekstazi was enabled or not; adding default badge.)");
                build.addAction(new EkstaziBadgeAction(ekstaziEnabled, animeEnabled));
            } catch (FileNotFoundException e) {
                listener.getLogger().println("Unable to find pom file which is a pre-requisite to detect whether ekstazi was enabled or not; adding default badge.)");
                build.addAction(new EkstaziBadgeAction(ekstaziEnabled, animeEnabled));
            } catch (IOException e) {
                listener.getLogger().println("Unable to detect whether ekstazi was enabled or not; adding default badge.)");
                build.addAction(new EkstaziBadgeAction(ekstaziEnabled, animeEnabled));
            }
        }
    }
}
