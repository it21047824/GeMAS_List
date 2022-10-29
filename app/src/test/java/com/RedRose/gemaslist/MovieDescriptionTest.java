package com.RedRose.gemaslist;

import static org.junit.Assert.assertEquals;

import junit.framework.TestCase;

import org.junit.Test;

public class MovieDescriptionTest extends TestCase {
    @Test
    public void testVAlidateInfoTrue() {
        
        MovieDescription m1 = new MovieDescription();

        assertEquals(true, m1.validateInfo(05));

    }

    @Test
    public void testVAlidateInfoFalse() {

        MovieDescription m2 = new MovieDescription();

        assertEquals(false, m2.validateInfo(25));

    }

}