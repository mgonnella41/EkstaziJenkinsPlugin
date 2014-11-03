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

public class EkstaziPOMManager {
    private String POMFileName;
    private Document POMFile;

    public EkstaziPOMManager(String POMFileName)
        throws ParserConfigurationException, SAXException, IOException {
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

    private Node getSurefireExecution() {
        Node surefire = getSurefireNode();
        System.out.println(surefire.getTextContent());
        NodeList children = surefire.getChildNodes();
        for(int i = 0; i < children.getLength(); i++) {
            System.out.println(children.item(i).getTextContent());
        } 
        return null;
    }

    void addEkstaziToPOM(String ekstaziVersion) throws TransformerException, 
           SAXException, IOException, ParserConfigurationException {
               // Build Ekstazi elements to insert
               String ekstazistring1 = "<plugin><groupId>org.ekstazi</groupId><artifactId>ekstazi-maven-plugin</artifactId><version>"+ ekstaziVersion+"</version><executions><execution><id>doit</id><goals><goal>select</goal><goal>restore</goal></goals></execution></executions></plugin>";
               String ekstazistring2 = "<configuration><excludesFile>myExcludes</excludesFile></configuration>";
               Element ekstazinode1 =  DocumentBuilderFactory
                   .newInstance()
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
    }

    public void addEkstazi(FilePath runDirectory, FilePath workspace, String ekstaziVersion) throws TransformerException,
           SAXException, IOException, ParserConfigurationException, InterruptedException {
               addEkstaziToPOM(ekstaziVersion);
               runDirectory = runDirectory.child("lastSuccessfulBuild");
               runDirectory = runDirectory.child("archive");
               runDirectory.copyRecursiveTo(".ekstazi/*", "", workspace);
    }

    public boolean checkForEkstazi() {
        Node plugins = POMFile.getElementsByTagName("plugins").item(0);
        if(plugins.getTextContent().contains("ekstazi-maven-plugin")) {
            return true;
        } else {
            return false;
        }
    }

    public void removeEkstazi() throws TransformerException {
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

        // Write the output
        writePOMFile();
    }
    public void setEkstaziForceFailing() {

    }

    public void setEkstaziEnable() {
        getSurefireExecution();
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
