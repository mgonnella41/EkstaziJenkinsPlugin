package com.pluralsight.ekstazi;

import hudson.FilePath;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

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

public class EkstaziMavenManager extends EkstaziManager implements Serializable {
    static final long serialVersionUID = 2L;
    private FilePath POMFileName;
    private transient Document POMFile;

    public EkstaziMavenManager(FilePath POMFileName, String Version)
        throws EkstaziException {
        super(Version);
        this.POMFileName = POMFileName;
        try {
            this.POMFile = openPOMFile();
        } catch (InterruptedException | ParserConfigurationException
                | SAXException | IOException e) {
            e.printStackTrace();
        }
    }

    private Node getSurefireNode() {
        if (POMFile == null) {
            return null;
        }
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

    public Node getSurefireConfigNode() throws SAXException, IOException,
            ParserConfigurationException {
        Element surefire = (Element)getSurefireNode();
        if (surefire == null) {
            return null;
        }
        NodeList configList = surefire.getElementsByTagName("configuration");
        Node configNode = null;
        for(int i = 0; i < configList.getLength(); i++) {
            if(configList.item(i).getParentNode().getTextContent().contains("maven-surefire-plugin")) {
                configNode = configList.item(i).getParentNode();
                break;
            }
        }
        if(configNode == null) {
            String ekstazistring = "<configuration></configuration>";
            Element ekstazinode = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse(new ByteArrayInputStream(ekstazistring.getBytes()))
                .getDocumentElement();
            configNode = surefire.appendChild(POMFile.importNode(ekstazinode, true));
        }

        return configNode;
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
        String ekstazistring2 = "<excludesFile>${java.io.tmpdir}/myExcludes</excludesFile>";
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
            Node surefire = getSurefireNode();
            if(surefire == null) {
                return;
            }
            Node plugins = surefire.getParentNode();
            // Insert Ekstazi elements to pom
            plugins.appendChild(POMFile.importNode(ekstazinode1, true));
            Node surefireConfig = getSurefireConfigNode();
            surefireConfig.appendChild(POMFile.importNode(ekstazinode2, true));


            // Write the output
            writePOMFile();
            this.POMFile = openPOMFile();

            // handle the copying of previous Ekstazi results to the workspace
            runDirectory = runDirectory.child("lastEkstaziBuild");
        } catch (SAXException | IOException | ParserConfigurationException |
 TransformerException | InterruptedException e1) {
    e1.printStackTrace();
                }
    }

    protected boolean checkPresent() {
        if(POMFile == null) {
            return false;
        }
        Node plugins = POMFile.getElementsByTagName("plugins").item(0);
        if(plugins == null) {
            return false;
        }
        if(plugins.getTextContent().contains("ekstazi-maven-plugin"))  {
            return true;
        } else {
            return false;
        }
    }

    protected boolean isEnabled() {
        if(POMFile == null) {
            return false;
        }
        Element plugins = (Element)POMFile.getElementsByTagName("plugins").item(0);
        if(plugins == null) {
            return false;
        }
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
        } catch (TransformerException | ParserConfigurationException
                | SAXException | IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private Document openPOMFile()
 throws ParserConfigurationException,
            SAXException, IOException, InterruptedException {
       // Load Maven pom file
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(new File(this.POMFileName.toURI()));
        return document;
    }


    private void writePOMFile() throws TransformerException, IOException,
            InterruptedException {
        // Rewrite pom file
        DOMSource source = new DOMSource(POMFile);
        StreamResult file = new StreamResult(new File(POMFileName.toURI()));
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer;
        transformer = transformerFactory.newTransformer();
        transformer.transform(source, file);
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    }
}
