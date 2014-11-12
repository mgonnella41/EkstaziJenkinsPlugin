package com.pluralsight.ekstazi;

import hudson.FilePath;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

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
                break;
            case "4.1.0":
                features.add(Feature.ENABLE);
                features.add(Feature.FORCEFAILING);
                break;
            default:
                throw new EkstaziException("Ekstazi version not supported");
        }
    }

    protected abstract boolean checkPresent();

    // Use enable to
    protected abstract void add(FilePath runDirectory, FilePath workspace,
            String ekstaziVersion);

    protected abstract void remove();

    protected abstract void setDisable() throws SAXException, IOException, ParserConfigurationException;

    protected abstract void setForceFailing() throws SAXException, IOException, ParserConfigurationException;


    public void enable(FilePath runDirectory, FilePath workspace,
            String ekstaziVersion) {
            if(checkPresent()) {
                remove();
            }
            add(runDirectory, workspace, ekstaziVersion);
    }

    public void disable(FilePath runDirectory, FilePath workspace, 
            String ekstaziVersion) throws TransformerException, SAXException,
           IOException, ParserConfigurationException {
               if(checkPresent()) {
                   remove();
               }
               if(features.contains(Feature.ENABLE)) {
                   add(runDirectory, workspace, ekstaziVersion);
                   setDisable();
               }
    }
}
