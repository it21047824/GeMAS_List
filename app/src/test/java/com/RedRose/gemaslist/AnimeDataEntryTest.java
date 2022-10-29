package com.RedRose.gemaslist;

import static org.junit.Assert.*;

import org.junit.Test;

public class AnimeDataEntryTest {

    @Test
    public void testLinkedListFind() {
        CustomLinkList list = new CustomLinkList();
        AnimeDataEntry a1 = new AnimeDataEntry("title1",
                0,0,0,false);
        AnimeDataEntry a2 = new AnimeDataEntry("title2",
                0,0,0,false);
        AnimeDataEntry a3 = new AnimeDataEntry("title3",
                0,0,0,false);
        AnimeDataEntry a4 = new AnimeDataEntry("title4",
                0,0,0,false);

        list.addItem(a1);
        list.addItem(a2);
        list.addItem(a3);
        list.addItem(a4);

        assertEquals("title1", list.find("title1").title);
        assertEquals("title2", list.find("title2").title);
        assertEquals("title3", list.find("title3").title);
        assertEquals("title4", list.find("title4").title);
    }

    @Test
    public void testLinkedListSize() {
        CustomLinkList list = new CustomLinkList();
        AnimeDataEntry a1 = new AnimeDataEntry("title1",
                0,0,0,false);
        AnimeDataEntry a2 = new AnimeDataEntry("title2",
                0,0,0,false);
        AnimeDataEntry a3 = new AnimeDataEntry("title3",
                0,0,0,false);
        AnimeDataEntry a4 = new AnimeDataEntry("title4",
                0,0,0,false);

        list.addItem(a1);
        list.addItem(a2);
        list.addItem(a3);
        list.addItem(a4);

        assertEquals(4, list.size());
    }

    @Test
    public void testLinkedListAddItem() {
        CustomLinkList list = new CustomLinkList();
        AnimeDataEntry a1 = new AnimeDataEntry("title1",
                0,0,0,false);
        list.addItem(a1);
        assertEquals("title1", list.getItem(0).title);
    }

    @Test
    public void testLinkedListGetItem() {
        CustomLinkList list = new CustomLinkList();
        AnimeDataEntry a1 = new AnimeDataEntry("title1",
                0,0,0,false);
        AnimeDataEntry a2 = new AnimeDataEntry("title2",
                0,0,0,false);

        list.addItem(a1);
        list.addItem(a2);

        assertEquals("title1", list.getItem(0).title);
        assertEquals("title2", list.getItem(1).title);
    }

    @Test
    public void testLinkedRemoveItem() {
        CustomLinkList list = new CustomLinkList();
        AnimeDataEntry a1 = new AnimeDataEntry("title1",
                0,0,0,false);
        AnimeDataEntry a2 = new AnimeDataEntry("title2",
                0,0,0,false);
        AnimeDataEntry a3 = new AnimeDataEntry("title3",
                0,0,0,false);
        AnimeDataEntry a4 = new AnimeDataEntry("title4",
                0,0,0,false);

        list.addItem(a1);
        list.addItem(a2);
        list.addItem(a3);
        list.addItem(a4);

        list.removeItem("title3");

        assertEquals("title1", list.find("title1").title);
        assertEquals("title2", list.find("title2").title);
        assertEquals("title4", list.find("title4").title);
        assertNull(list.find("title3"));
    }

}