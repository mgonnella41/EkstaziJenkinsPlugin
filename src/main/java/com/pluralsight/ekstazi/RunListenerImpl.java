package com.pluralsight.ekstazi;

import hudson.Extension;
import hudson.FilePath;
import hudson.model.*;
import hudson.model.listeners.RunListener;
import jenkins.model.Jenkins;
import hudson.remoting.Callable;

import javax.annotation.Nonnull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.exception.ExceptionUtils;
import java.io.Serializable;

//Listener to all build (to add the Ekstazi badge action)
@Extension
public class RunListenerImpl extends RunListener<AbstractBuild> implements Serializable {
    public RunListenerImpl() {
        super(AbstractBuild.class);
    }

    @Override
    public void onCompleted(AbstractBuild build, @Nonnull TaskListener listener) {
        //Remove the EkstaziBadgeAction that was added in onStarted(), and any previously added permalinks
        List<Action> actions = build.getActions();

        for(Action action: actions) {
            if (action instanceof EkstaziBadgeAction || action instanceof EkstaziPermalinkProjectAction) {
                build.getActions().remove(action);
            }
        }

        badgeApply(build, listener, build.getModuleRoot(), false);         //we don't need an animated icon after the build has completed
        permalinkApply(build, listener);                                   //apply permalinks for Ekstazi

        super.onCompleted(build, listener);
    }

    @Override
    public void onStarted(AbstractBuild build, TaskListener listener) {
        AbstractBuild lastBuild = build.getPreviousBuild();

        if(lastBuild == null) {
            badgeApply(build, listener, null, true);                                  //if lastBuild does not exist (i.e. this is the first build for the project)
        } else {
            badgeApply(build, listener, lastBuild.getModuleRoot(), true);             //we DO need an animated icon during the build is going on (for lastBuild)
        }

        super.onStarted(build, listener);
    }

    private void badgeApply(AbstractBuild build, TaskListener listener, final FilePath buildWorkspace, boolean animeEnabled) {
        EkstaziBadgePlugin plugin = Jenkins.getInstance().getPlugin(EkstaziBadgePlugin.class);

        //If user wants Ekstazi badges for Build History, show 'em
        if(plugin.isActivated()) {
            boolean ekstaziEnabled = false;

            try {
                if (buildWorkspace != null) {

                    EkstaziBuilder.DescriptorImpl desc = (EkstaziBuilder.DescriptorImpl) build.getDescriptorByName("EkstaziBuilder");
                    final String ekstaziVersion = desc.getEkstaziVersion();

                    Callable<String, IOException> task = new Callable<String, IOException>() {
                        static final long serialVersionUID = 12L;
                        public String call() throws IOException {
                            try{
                                MavenFinder mavenFinder = new MavenFinder(buildWorkspace);
                                ArrayList<FilePath> pomFiles = mavenFinder.find();
                                for(int i = 0; i < pomFiles.size(); i++) {
                                    // We don't want to pick up the dummy pom.xml files from our resources/ directory (which gets copied to target/)
                                    if (!pomFiles.get(i).toURI().toString().contains("/dummy-project/")) {
                                        final FilePath remoteFile = pomFiles.get(i);
                                        EkstaziMavenManager ekstaziManager = new EkstaziMavenManager(remoteFile, ekstaziVersion);
                                        boolean ekstaziEnabledInner = ekstaziManager.isEnabled();
                                        if(ekstaziEnabledInner == true) {
                                            return "true";
                                        } else {
                                            return "false";
                                        }
                                    }
                                }
                            } catch (InterruptedException | EkstaziException e) {
                                return "false";
                            }
                            return("false");
                        }
                    };
                    String ekstaziEnabledString = buildWorkspace.getChannel().call(task);
                    if (ekstaziEnabledString.equals("true")) {
                        ekstaziEnabled = true;
                    } else {
                        ekstaziEnabled = false;
                    }
                }

                build.addAction(new EkstaziBadgeAction(ekstaziEnabled, animeEnabled));

                if(ekstaziEnabled) {
                    listener.getLogger().println("Adding ekstazi-enabled badge for current build.");
                } else {
                    listener.getLogger().println("Adding ekstazi-disabled badge for current build.");
                }

            } catch (InterruptedException | IOException e) {
                listener.getLogger().println("Unable to detect whether ekstazi was enabled or not; adding default badge.)");
                build.addAction(new EkstaziBadgeAction(ekstaziEnabled, animeEnabled));
            }
        }
    }

    private void permalinkApply(AbstractBuild build, TaskListener listener) {
        AbstractProject<?, ?> project = build.getProject();
        build.addAction(new EkstaziPermalinkProjectAction(project));

        try {
            project.save();
        } catch (IOException e) {
            listener.getLogger().println("Unable to save project changes for Ekstazi permalinks.");
        }
    }

}
