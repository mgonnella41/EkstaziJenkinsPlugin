package com.pluralsight.ekstazi;

import hudson.FilePath;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class EkstaziMavenManager extends EkstaziManager {
    private String POMFileName;
    private Document POMFile;

    public EkstaziMavenManager(String POMFileName, String Version)
        throws ParserConfigurationException, SAXException, IOException, EkstaziException {
        super(Version);
        this.POMFileName = POMFileName;
        this.POMFile = openPOMFile();
    }

    private Node getSurefireNode() {
        NodeList artifacts = POMFile.getElementsByTagName("plugin");
        Node surefire = null;
        for(int i = 0; i < artifacts.getLength(); i++) {
            if(artifacts.item(i).getTextContent().contains("maven-surefire-plugin"))
            {
                surefire = artifacts.item(i);
            }
        }
        return surefire;
    }

    protected void add(FilePath runDirectory, FilePath workspace,
            String ekstaziVersion, boolean skipMe, boolean forceFailing) {
        // Insert Ekstazi into POM
        // Build Ekstazi elements to insert
        String skipMeString = "";
        String forceFailingString = "";
        if(!skipMe) {
            skipMeString = "<skipme>true</skipme>";
        }
        if(forceFailing) {
            forceFailingString = "<forcefailing>true</forcefailing>";
        }
        String ekstazistring1 = "<plugin><groupId>org.ekstazi</groupId><artifactId>ekstazi-maven-plugin</artifactId><version>"+ ekstaziVersion+"</version><configuration>"+skipMeString+""+forceFailingString+"</configuration><executions><execution><id>doit</id><goals><goal>select</goal><goal>restore</goal></goals></execution></executions></plugin>";
        String ekstazistring2 = "<configuration><excludesFile>myExcludes</excludesFile></configuration>";
        Element ekstazinode1;
        try {
            ekstazinode1 = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse(new ByteArrayInputStream(ekstazistring1.getBytes()))
                .getDocumentElement();
            Element ekstazinode2 =  DocumentBuilderFactory
                .newInstance()
                .newDocumentBuilder()
                .parse(new ByteArrayInputStream(ekstazistring2.getBytes()))
                .getDocumentElement();
            // Get elements to modify
            Node plugins = POMFile.getElementsByTagName("plugins").item(0);
            Node surefire = getSurefireNode();

            // Insert Ekstazi elements to pom
            plugins.appendChild(POMFile.importNode(ekstazinode1, true));
            surefire.appendChild(POMFile.importNode(ekstazinode2, true));


            // Write the output
            writePOMFile();
            this.POMFile = openPOMFile();

            // handle the copying of previous Ekstazi results to the workspace
            runDirectory = runDirectory.child("lastSuccessfulEkstaziBuild");
            runDirectory = runDirectory.child("archive");
            try {
                runDirectory.copyRecursiveTo(".ekstazi/*", "", workspace);
            } catch (InterruptedException | IOException e) {
                throw new IOException("No previous Ekstazi results found.");
            }
        } catch (SAXException | IOException | ParserConfigurationException |
                TransformerException e1) {
            e1.printStackTrace();
                }
    }

    protected boolean checkPresent() {
        Node plugins = POMFile.getElementsByTagName("plugins").item(0);
        if(plugins.getTextContent().contains("ekstazi-maven-plugin"))  {
            return true;
        } else {
            return false;
        }
    }

    protected boolean isEnabled() {
        Element plugins = (Element)POMFile.getElementsByTagName("plugins").item(0);
        NodeList skipme = plugins.getElementsByTagName("skipme");
        if(checkPresent() && skipme.getLength() == 0)  {
            return true;
        } else {
            return false;
        }
    }

    protected void remove() {
        // Get elements to modify
        Node surefire = getSurefireNode();
        NodeList children = surefire.getChildNodes();
        for(int i = 0; i < children.getLength(); i++) {
            if(children.item(i).getTextContent().contains("myExcludes")) {
                surefire.removeChild(children.item(i));
                break;
            }
        }
        Node plugins = POMFile.getElementsByTagName("plugins").item(0);
        children = plugins.getChildNodes();
        for(int i = 0; i < children.getLength(); i++) {
            if(children.item(i).getTextContent().contains("org.ekstazi")) {
                plugins.removeChild(children.item(i));
                break;
            }
        }

        try {
            writePOMFile();
            this.POMFile = openPOMFile();
        } catch (TransformerException |ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }

    private Document openPOMFile()
        throws ParserConfigurationException, SAXException, IOException {
        // Load Maven pom file
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(new File(this.POMFileName));
        return document;
    }


    private void writePOMFile() throws TransformerException {
        // Rewrite pom file
        DOMSource source = new DOMSource(POMFile);
        StreamResult file = new StreamResult(new File(POMFileName));
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer;
        transformer = transformerFactory.newTransformer();
        transformer.transform(source, file);
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    }
}
