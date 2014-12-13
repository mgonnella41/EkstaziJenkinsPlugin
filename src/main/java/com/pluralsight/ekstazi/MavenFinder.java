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

public class MavenFinder extends ConfigFinder implements Serializable {
    static final long serialVersionUID = 5L;
    ArrayList<FilePath> pomFiles;

    MavenFinder(FilePath rootFolder) {
        super(rootFolder);
    }

    @SuppressWarnings("unchecked")
    public ArrayList<FilePath> find() throws IOException, InterruptedException {
        this.pomFiles = new ArrayList<FilePath>();
        this.getPOMs(rootFolder);
        return this.pomFiles;
    }

    // Find POM files
    public void getPOMs(FilePath rootFolder)
        throws IOException, InterruptedException {
        ArrayList<FilePath> filesAndFolders = new ArrayList<FilePath>(rootFolder.list());
        for(FilePath file : filesAndFolders) {
            if(file.isDirectory()) {
                getPOMs(file);
            } else if(file.toString().endsWith("pom.xml") && !file.toString().contains("archive-tmp")) {
                this.pomFiles.add(file);
            }
        }
    }

}
