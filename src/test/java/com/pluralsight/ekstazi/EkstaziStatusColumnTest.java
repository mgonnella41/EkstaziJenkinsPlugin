package com.pluralsight.ekstazi;


import hudson.model.Job;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public class EkstaziStatusColumnTest {

    @Mock
    private Job mockJob;

    @Before
    public void setUp() {
        mockJob = Mockito.mock(Job.class);
    }

    @Test
    public void getDescriptorTest() {
        EkstaziStatusColumn column = new EkstaziStatusColumn();
        Assert.assertNotNull(column.getDescriptor());
    }

    @Test
    public void getLastBuildEkstaziStatusWithNoLastBuildTest() {
        EkstaziStatusColumn column = new EkstaziStatusColumn();
        Assert.assertNull(column.getLastBuildEkstaziStatus(mockJob));
    }

}
