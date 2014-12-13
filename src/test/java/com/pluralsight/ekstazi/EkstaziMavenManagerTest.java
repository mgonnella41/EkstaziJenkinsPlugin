package com.pluralsight.ekstazi;


import hudson.FilePath;
import org.codehaus.plexus.util.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class EkstaziMavenManagerTest {
    public static final String PROJECT_NAME = "dummy-project";
    public static final String EKSTAZI_VERSION = "4.3.0";

    private FilePath POMFileName;
    private String projectDir;
    private EkstaziMavenManager manager;

    @Before
    public void setUp() throws EkstaziException {
        URL url = Thread.currentThread().getContextClassLoader().getResource(".");
        String resourcesDir = url.getPath();
        projectDir = resourcesDir + "/" + PROJECT_NAME;

        POMFileName = new FilePath(new File(projectDir + "/pom.xml"));

        manager = new EkstaziMavenManager(POMFileName, EKSTAZI_VERSION);
    }

    @Test
    public void getSurefireConfigNodeWithSurefireConfigNodeTest() throws EkstaziException, ParserConfigurationException, SAXException, IOException {

        Assert.assertNotNull(manager.getSurefireConfigNode());
    }

    @Test
    public void getSurefireConfigNodeWithoutSurefireConfigNodeTest() throws EkstaziException, ParserConfigurationException, SAXException, IOException {
        String pomFile = projectDir + "/pom.xml";
        String tempPomFile = projectDir + "/temp-pom.xml";

        FileUtils.copyFile(new File(pomFile), new File(tempPomFile));
        removeSurefireConfigNode(tempPomFile, false, false);

        EkstaziMavenManager manager = new EkstaziMavenManager(new FilePath(new File(tempPomFile)), EKSTAZI_VERSION);

        Assert.assertNotNull(manager.getSurefireConfigNode());

        FileUtils.fileDelete(tempPomFile);
    }

    private void removeSurefireConfigNode(String pomFile, boolean removeParentNode, boolean removeEkstaziPluginNode) {
        // If removeParentNode is true, this will remove the plugin node for maven surefire too
        // If removeEkstaziPluginNode is true, this will remove ekstazi-maven-plugin

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder parser;

        try {
            parser = factory.newDocumentBuilder();
            Document document = parser.parse(pomFile);

            NodeList artifacts = document.getElementsByTagName("plugin");
            Node surefireNode = null;

            for(int i = 0; i < artifacts.getLength(); i++) {
                if(artifacts.item(i).getTextContent().contains("maven-surefire-plugin"))
                {
                    surefireNode = artifacts.item(i);
                    break;
                }
            }

            NodeList surefireNodeChildren = surefireNode.getChildNodes();

            for(int i = 0; i < surefireNodeChildren.getLength(); i++) {
                if(surefireNodeChildren.item(i).getTextContent().contains("myExcludes")) {
                    surefireNode.removeChild(surefireNodeChildren.item(i));
                    break;
                }
            }

            if(removeParentNode || removeEkstaziPluginNode) {
                Node plugins = document.getElementsByTagName("plugins").item(0);
                NodeList pluginNodeChildren = plugins.getChildNodes();

                if (removeParentNode) {
                    for(int i = 0; i < pluginNodeChildren.getLength(); i++) {
                        if(pluginNodeChildren.item(i).getTextContent().contains("maven-surefire-plugin")) {
                            plugins.removeChild(pluginNodeChildren.item(i));
                            break;
                        }
                    }
                }

                if (removeEkstaziPluginNode) {
                    for(int i = 0; i < pluginNodeChildren.getLength(); i++) {
                        if(pluginNodeChildren.item(i).getTextContent().contains("ekstazi-maven-plugin")) {
                            plugins.removeChild(pluginNodeChildren.item(i));
                            break;
                        }
                    }
                }
            }

            savePOMFile(document, new FilePath((new File(pomFile))));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void savePOMFile(Document pomFile, FilePath POMFileName) throws TransformerException, IOException, InterruptedException {
        DOMSource source = new DOMSource(pomFile);
        StreamResult file = new StreamResult(new File(POMFileName.toURI()));
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer;
        transformer = transformerFactory.newTransformer();
        transformer.transform(source, file);
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    }

    @Test
    public void getSurefireConfigNodeWithoutSurefireNodeTest() throws EkstaziException, ParserConfigurationException, SAXException, IOException {
        String pomFile = projectDir + "/pom.xml";
        String tempPomFile = projectDir + "/temp-pom.xml";

        FileUtils.copyFile(new File(pomFile), new File(tempPomFile));
        removeSurefireConfigNode(tempPomFile, true, false);

        EkstaziMavenManager manager = new EkstaziMavenManager(new FilePath(new File(tempPomFile)), EKSTAZI_VERSION);

        Assert.assertNull(manager.getSurefireConfigNode());

        FileUtils.fileDelete(tempPomFile);
    }

    @Test
    public void checkPresentTest() {
        Assert.assertTrue(manager.checkPresent());
    }

    @Test
    public void checkPresentWithoutEkstaziMavenPluginTest() throws IOException, EkstaziException {
        String pomFile = projectDir + "/pom.xml";
        String tempPomFile = projectDir + "/temp-pom.xml";

        FileUtils.copyFile(new File(pomFile), new File(tempPomFile));
        removeSurefireConfigNode(tempPomFile, false, true);

        EkstaziMavenManager manager = new EkstaziMavenManager(new FilePath(new File(tempPomFile)), EKSTAZI_VERSION);

        Assert.assertFalse(manager.checkPresent());

        FileUtils.fileDelete(tempPomFile);
    }

    @Test
    public void isEnabledTest() throws IOException, EkstaziException{
        String pomFile = projectDir + "/pom.xml";
        String tempPomFile = projectDir + "/temp-pom.xml";

        FileUtils.copyFile(new File(pomFile), new File(tempPomFile));
        EkstaziMavenManager manager = new EkstaziMavenManager(new FilePath(new File(tempPomFile)), EKSTAZI_VERSION);
        Assert.assertTrue(manager.isEnabled());
        FileUtils.fileDelete(tempPomFile);
    }

    @Test
    public void isEnabledWithoutEkstaziTest() throws IOException, EkstaziException {
        String pomFile = projectDir + "/pom.xml";
        String tempPomFile = projectDir + "/temp-pom.xml";

        FileUtils.copyFile(new File(pomFile), new File(tempPomFile));
        removeSurefireConfigNode(tempPomFile, true, true);

        EkstaziMavenManager manager = new EkstaziMavenManager(new FilePath(new File(tempPomFile)), EKSTAZI_VERSION);

        Assert.assertFalse(manager.isEnabled());

        FileUtils.fileDelete(tempPomFile);
    }

    @Test
    public void removeTest() throws IOException, EkstaziException {
        String pomFile = projectDir + "/pom.xml";
        String tempPomFile = projectDir + "/temp-pom.xml";

        FileUtils.copyFile(new File(pomFile), new File(tempPomFile));

        EkstaziMavenManager manager = new EkstaziMavenManager(new FilePath(new File(tempPomFile)), EKSTAZI_VERSION);
        manager.remove();

        Assert.assertFalse(manager.isEnabled());

        FileUtils.fileDelete(tempPomFile);
    }

}
