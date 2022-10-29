package com.RedRose.gemaslist;

import junit.framework.TestCase;

public class series_descriptionTest extends TestCase {

    public void testValidation (){
        series_description s1 = new series_description();
        assertEquals(true, s1.validateInformatin(05));
    }

    public void testValidateFalse(){
        series_description s2 = new series_description();
        assertEquals(false, s2.validateInformatin(25));
    }

}