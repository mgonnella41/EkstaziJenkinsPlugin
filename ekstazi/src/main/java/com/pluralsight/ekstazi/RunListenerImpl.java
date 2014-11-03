package com.pluralsight.ekstazi;

import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.Cause;
import hudson.model.TaskListener;
import hudson.model.listeners.RunListener;
import jenkins.model.Jenkins;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

//Listener to all build (to add the Ekstazi badge action)
@Extension
public class RunListenerImpl extends RunListener<AbstractBuild> {
    public RunListenerImpl() {
        super(AbstractBuild.class);
    }

    @Override
    public void onStarted(AbstractBuild build, TaskListener listener) {
        EkstaziBadgePlugin plugin = Jenkins.getInstance().getPlugin(EkstaziBadgePlugin.class);

        if(plugin.isActivated()) {
            Set<String> causeClasses =  new HashSet<String>();

            //List<Cause> causes = CauseFilter.filter((List<Cause>)build.getCauses());
            List<Cause> causes = (List<Cause>)build.getCauses();

            if (causes != null) {
                for (Cause cause : causes) {
                    build.addAction(new EkstaziBadgeAction(cause));
                    listener.getLogger().println("Adding cause: " + cause);
                }
            } else {
                listener.getLogger().println("Apologies, couldn't find any causes.");
            }

        }
        super.onStarted(build, listener);
    }
}
