package com.pluralsight.ekstazi;


import org.junit.Assert;
import org.junit.Test;

public class EkstaziBadgeActionTest {

    @Test
    public void getToolTipWithEkstaziAnimeTest() {
        EkstaziBadgeAction action = new EkstaziBadgeAction(true, true);

        Assert.assertEquals(EkstaziBadgeAction.EKSTAZI_ENABLED_ANIME_TOOLTIP, action.getTooltip());
    }

    @Test
    public void getToolTipWithEkstaziWithoutAnimeTest() {
        EkstaziBadgeAction action = new EkstaziBadgeAction(true, false);

        Assert.assertEquals(EkstaziBadgeAction.EKSTAZI_ENABLED_TOOLTIP, action.getTooltip());
    }

    @Test
    public void getToolTipWithoutEkstaziWithAnimeTest() {
        EkstaziBadgeAction action = new EkstaziBadgeAction(false, true);

        Assert.assertEquals(EkstaziBadgeAction.EKSTAZI_DISABLED_ANIME_TOOLTIP, action.getTooltip());
    }

    @Test
    public void getToolTipWithoutEkstaziWithoutAnimeTest() {
        EkstaziBadgeAction action = new EkstaziBadgeAction(false, false);

        Assert.assertEquals(EkstaziBadgeAction.EKSTAZI_DISABLED_TOOLTIP, action.getTooltip());
    }

    @Test
    public void getIconFileNameTest() {
        EkstaziBadgeAction action = new EkstaziBadgeAction(true, true);

        Assert.assertNull(action.getIconFileName());
    }

    @Test
    public void getDisplayNameTest() {
        EkstaziBadgeAction action = new EkstaziBadgeAction(true, true);

        Assert.assertEquals(action.getTooltip(), action.getDisplayName());
    }

    @Test
    public void getDefaultUrlNameTest(){
        EkstaziBadgeAction action = new EkstaziBadgeAction(true, true);

        Assert.assertEquals("", action.getUrlName());
    }

}
