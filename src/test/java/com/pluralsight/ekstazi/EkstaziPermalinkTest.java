package com.pluralsight.ekstazi;


import org.junit.Test;

public class EkstaziPermalinkTest {

    public static final String DISPLAY_NAME = "Last Successful Ekstazi Build";
    public static final int    BUILD_NUMER  = 3;

    @Test(expected = IllegalArgumentException.class)
    public void disallowNullName() {
        new EkstaziPermalink(BUILD_NUMER, null, DISPLAY_NAME);
    }
}
