package com.pluralsight.ekstazi;

import hudson.FilePath;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

public abstract class EkstaziManager implements Serializable {
    static final long serialVersionUID = 3L;

    // Features that this Ekstazi Manager supports
    public enum Feature{
        ENABLE, // Can be disabled and enabled 
        FORCEFAILING, // Force failing flag can be set
        FORCEALL // Force all tests to run
    }

    protected ArrayList<Feature> features;

    protected EkstaziManager(String Version) throws EkstaziException {
        features = new ArrayList<Feature>();
        switch (Version) {
            case "4.0.1":
                // None
                break;
            case "4.1.0":
                features.add(Feature.ENABLE);
                features.add(Feature.FORCEFAILING);
                break;
            case "4.2.0":
                features.add(Feature.ENABLE);
                features.add(Feature.FORCEFAILING);
                break;
            case "4.3.0":
                features.add(Feature.ENABLE);
                features.add(Feature.FORCEFAILING);
                features.add(Feature.FORCEALL); // Not yet implemented
                break;
            default:
                throw new EkstaziException("Ekstazi version not supported");
        }
    }

    protected abstract boolean checkPresent();

    // Use enable to
    protected abstract void add(FilePath runDirectory, FilePath workspace,
            String ekstaziVersion, boolean skipMe, boolean forceFailing);

    protected abstract void remove();

    public void enable(FilePath runDirectory, FilePath workspace, String ekstaziVersion,
            boolean forceFailing) {
            if(checkPresent()) {
                remove();
            }
            add(runDirectory, workspace, ekstaziVersion, false, forceFailing);
    }

    public void disable(FilePath runDirectory, FilePath workspace, 
            String ekstaziVersion) {
               if(checkPresent()) {
                   remove();
               }
               if(features.contains(Feature.ENABLE)) {
                   add(runDirectory, workspace, ekstaziVersion, true, false);
               }
    }
}
