package com.pluralsight.ekstazi;

import hudson.FilePath;

import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.TransformerException;

public abstract class EkstaziManager {

    // Features that this Ekstazi Manager supports
    public enum Feature{
        ENABLE, // Can be disabled and enabled 
        FORCEFAILING // Force failing flag can be set
    }

    protected List<Feature> features;

    protected EkstaziManager(String Version) throws EkstaziException {
        features = new ArrayList<Feature>();
        switch (Version) {
            case "4.0.1":
                // None
            case "4.1.0": 
                features.add(Feature.ENABLE);
                features.add(Feature.FORCEFAILING);
            default:
                throw new EkstaziException("Ekstazi version not supported");
        }
    }

    protected abstract boolean checkPresent();

    // Use enable to
    protected abstract void add(FilePath runDirectory, FilePath workspace,
            String ekstaziVersion);

    protected abstract void remove();

    public void enable(FilePath runDirectory, FilePath workspace,
            String ekstaziVersion) {
        if(!features.contains(Feature.ENABLE)) {
            if(checkPresent()) {
                remove();
            }
            add(runDirectory, workspace, ekstaziVersion);
        }
    }

    public void disable() throws TransformerException{
        if(!features.contains(Feature.ENABLE)) {
            if(checkPresent()) {
                remove();
            }
        }
    }
}
