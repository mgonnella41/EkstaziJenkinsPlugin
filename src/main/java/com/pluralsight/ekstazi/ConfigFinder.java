package com.pluralsight.ekstazi;

import hudson.FilePath;

import java.io.Serializable;
import java.util.ArrayList;
import java.io.IOException;


public abstract class ConfigFinder implements Serializable {
    static final long serialVersionUID = 4L;
    protected FilePath rootFolder;

    ConfigFinder(FilePath rootFolder) {
        this.rootFolder = rootFolder;
    }

    abstract public ArrayList<FilePath> find() throws IOException, InterruptedException;
}
