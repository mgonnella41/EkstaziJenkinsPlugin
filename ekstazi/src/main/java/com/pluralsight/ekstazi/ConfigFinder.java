package com.pluralsight.ekstazi;

import hudson.FilePath;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

public class ConfigFinder implements Serializable {
    static final long serialVersionUID = 4L;

    ConfigFinder(FilePath rootFolder) {
        super(rootFolder);
    }

    abstract public ArrayList<FilePath> find();
}
