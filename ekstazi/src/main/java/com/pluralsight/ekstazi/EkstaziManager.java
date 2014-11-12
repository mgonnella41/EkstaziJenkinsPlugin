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

    protected EkstaziManager(String Version) {
        features = new ArrayList<Feature>();
        switch (Version) {
            case "4.0.1":
                // None
            case "4.1.0": 
                features.add(Feature.ENABLE);
                features.add(Feature.FORCEFAILING);
            default:
                // throw exception
        }
    }

    public abstract boolean checkForEkstazi();

    public abstract void addEkstazi(FilePath runDirectory, FilePath workspace,
            String ekstaziVersion);

    public abstract void removeEkstazi();

    public void setEkstaziForceFailing() {
        if(!features.contains(Feature.FORCEFAILING)) {
            // throw exception
        }
    }

    public void setEkstaziEnable() throws TransformerException {
        if(!features.contains(Feature.ENABLE)) {
            removeEkstazi();
            return;
        }
    }

}
