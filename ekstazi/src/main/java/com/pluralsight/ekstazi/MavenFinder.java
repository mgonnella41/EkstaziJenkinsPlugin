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

    MavenFinder(FilePath rootFolder) {
        super(rootFolder);
    }

    @SuppressWarnings("unchecked")
    public ArrayList<FilePath> find() {
        ArrayList<FilePath> pomDirectories = new ArrayList<FilePath>();
        ArrayList<String> pomFilter = new ArrayList<String>();
        pomFilter.add("pom.xml");
        Iterator<File> it;
        try {
            it = FileUtils.iterateFiles(new File(rootFolder.toURI()),
                    new NameFileFilter(pomFilter), TrueFileFilter.INSTANCE);
        while(it.hasNext()) {
            File file = (File)it.next();
            FilePath filePath = new FilePath(file);
            if(!file.toString().contains("archive-tmp")) {
                pomDirectories.add(filePath);
            }
        }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return pomDirectories;
    }
}
