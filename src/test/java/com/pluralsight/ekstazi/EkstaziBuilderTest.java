package com.pluralsight.ekstazi;

import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import hudson.model.FreeStyleProject;
import hudson.scm.SubversionSCM;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

public class EkstaziBuilderTest {
    public static final String PROJECT_NAME = "commons-lang3";
    public static final String PROJECT_REPO = "http://svn.apache.org/repos/asf/commons/proper/lang/trunk";

    @Rule public JenkinsRule j = new JenkinsRule();

    @Before
    public void setUp() throws Exception {
        j.configureMaven3();
    }

    @Test
    public void configRoundtripTest() throws Exception {
        FreeStyleProject p = j.createFreeStyleProject();
        p.getBuildersList().add(new EkstaziBuilder(true, true, false));
        JenkinsRule.WebClient webClient = j.createWebClient();
        HtmlForm form = webClient.getPage(p, "configure").getFormByName("config");
        j.submit(form);

        EkstaziBuilder builder = p.getBuildersList().get(EkstaziBuilder.class);

        Assert.assertTrue(builder.ekstaziEnable);
        Assert.assertTrue(builder.ekstaziForceFailing);
    }

    @Test
    public void globalConfigTest() throws Exception {
        JenkinsRule.WebClient webClient = j.createWebClient();

        HtmlForm form = webClient.goTo("configure").getFormByName("config");
        HtmlSelect ekstaziVersionHTMLElement = form.getSelectByName("_.EkstaziVersion");
        String ekstaziVersion = ekstaziVersionHTMLElement.getAttribute("value");

        Assert.assertEquals(EkstaziBuilder.DEFAULT_EKSTAZI_VERSION, ekstaziVersion);
    }

    @Test
    public void buildWithEkstaziTest() throws Exception {
        FreeStyleProject p = j.createFreeStyleProject(PROJECT_NAME);
        p.setScm(new SubversionSCM(PROJECT_REPO));
        p.getBuildersList().add(new EkstaziBuilder(true, false, false));

        j.buildAndAssertSuccess(p);
    }

    @Test
    public void buildWithoutEkstaziTest() throws Exception {
        FreeStyleProject p = j.createFreeStyleProject(PROJECT_NAME);
        p.setScm(new SubversionSCM(PROJECT_REPO));
        p.getBuildersList().add(new EkstaziBuilder(false, false, false));

        j.buildAndAssertSuccess(p);
    }
}
