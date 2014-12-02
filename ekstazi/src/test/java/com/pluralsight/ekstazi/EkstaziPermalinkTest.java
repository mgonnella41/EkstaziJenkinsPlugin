package com.pluralsight.ekstazi;


import org.junit.Test;

public class EkstaziPermalinkTest {

    public static final String DISPLAY_NAME = "Last Successful Ekstazi Build";

    @Test(expected = IllegalArgumentException.class)
    public void disallowNullName() {
        new EkstaziPermalink(3, null, DISPLAY_NAME);
    }
}
