package com.pluralsight.ekstazi;

import hudson.model.Job;
import hudson.model.PermalinkProjectAction;
import hudson.model.Run;


public class EkstaziPermalink extends PermalinkProjectAction.Permalink {

    private int    buildNumber;
    private String id;
    private String displayName;

    public EkstaziPermalink(int buildNumber, String id, String displayName) {
        this.buildNumber = buildNumber;
        this.id          = id;
        this.displayName = displayName;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Run<?, ?> resolve(Job<?, ?> job) {
        return job.getBuildByNumber(buildNumber);
    }
}
