/*
 *
 * This file is part of Aiphial.
 *
 * Copyright (c) 2010 Nicolay Mitropolsky <NicolayMitropolsky@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package me.uits.aiphial.general.dataStore;

import me.uits.aiphial.general.dataStore.DataStore;
import me.uits.aiphial.general.dataStore.NDimPoint;
import me.uits.aiphial.general.dataStore.SimpleNDimPoint;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Nicolay Mitropolsky <NicolayMitropolsky@gmail.com>
 */
@Ignore
public abstract class DataStoreTest
{

    public DataStoreTest()
    {

    }

    public abstract <T extends NDimPoint> DataStore<T> getDatastoreInstance(int dimention);


    @BeforeClass
    public static void setUpClass() throws Exception
    {
    }

    @AfterClass
    public static void tearDownClass() throws Exception
    {

    }

    @Test
    public void testGetNearest()
    {

        DataStore<NDimPoint> ds = getDatastoreInstance(5);

        Float[] ar = new Float[]
        {
            1f, 2f, 3f, 4f, 5f
        };

        NDimPoint ndp = new SimpleNDimPoint(ar);

        ds.addOrGet(ndp);

        NDimPoint nearest = ds.getNearest(new SimpleNDimPoint(8f, 0f, 5f, 9f, 5f));

        assertEquals(ndp,nearest);

        System.out.println();

    }

    @Test
    public void testGetEmpty()
    {

        DataStore<SimpleNDimPoint> ds = getDatastoreInstance(5);
        //ds.setWindow(1f, 8f, 1f, 1f, 1f);


        Collection<SimpleNDimPoint> withinWindow = ds.getWithinWindow(new SimpleNDimPoint(1f, 2f, 3f, 4f, 5f),new SimpleNDimPoint(1f, 8f, 1f, 1f, 1f));


        assertTrue(withinWindow.isEmpty());

    }

    @Test
    public void testGetWithinWindow()
    {

        DataStore<SimpleNDimPoint> ds = getDatastoreInstance(5);
        //ds.setWindow(1f, 8f, 1f, 1f, 1f);

        Set<SimpleNDimPoint> inWindow = new HashSet<SimpleNDimPoint>();
        inWindow.add(new SimpleNDimPoint(
                1f, 2f, 3f, 4f, 5f));
        inWindow.add(new SimpleNDimPoint(
                1f, 3f, 3f, 4f, 5f));

        for (SimpleNDimPoint points : inWindow)
        {
            ds.addOrGet(points);
        }

        ds.addOrGet(new SimpleNDimPoint(1f, 2f, 3f, 4f, 50f));
        ds.addOrGet(new SimpleNDimPoint(1f, 30f, 3f, 4f, 5f));


        Collection<SimpleNDimPoint> withinWindow = ds.getWithinWindow(new SimpleNDimPoint(1f, 8f, 1f, 1f, 1f),new SimpleNDimPoint(1f, 2f, 3f, 4f, 5f));


        assertTrue(inWindow.containsAll(withinWindow) && withinWindow.containsAll(inWindow));

    }

    @Test
    public void testGetWithinWindow2()
    {

        DataStore<SimpleNDimPoint> ds = getDatastoreInstance(5);

        Set<SimpleNDimPoint> inWindow = new HashSet<SimpleNDimPoint>();
        inWindow.add(new SimpleNDimPoint(
                1f, 2f, 3f, 4f, 5f));
        inWindow.add(new SimpleNDimPoint(
                1f, 3f, 3f, 4f, 5f));

        for (SimpleNDimPoint points : inWindow)
        {
            ds.addOrGet(points);
        }

        ds.addOrGet(new SimpleNDimPoint(1f, 2f, 3f, 4f, 50f));
        ds.addOrGet(new SimpleNDimPoint(1f, 30f, 3f, 4f, 5f));



        Collection<SimpleNDimPoint> withinWindow = ds.getWithinWindow(new SimpleNDimPoint(1f, 8f, 1f, 1f, 1f), new SimpleNDimPoint(1f, 2f, 3f, 4f, 5f));


        assertTrue(inWindow.containsAll(withinWindow) && withinWindow.containsAll(inWindow));

    }

    @Test
    public void testRemoveAndIsEmmpty()
    {
        DataStore<SimpleNDimPoint> ds = getDatastoreInstance(4);

        Set<SimpleNDimPoint> firstCulster = new HashSet<SimpleNDimPoint>();
        firstCulster.add(new SimpleNDimPoint(1f, 2f, 3f, 7f));
        firstCulster.add(new SimpleNDimPoint(1f, 2f, 4f, 5f));
        firstCulster.add(new SimpleNDimPoint(3f, 6f, 2f, 4f));
        firstCulster.add(new SimpleNDimPoint(5f, 20f, 7f, 3f));
        firstCulster.add(new SimpleNDimPoint(3f, 1f, 2f, 6f));
        firstCulster.add(new SimpleNDimPoint(13f, 2f, 3f, 7f));


        ds.addAll(firstCulster);

        Set<SimpleNDimPoint> secondCulster = new HashSet<SimpleNDimPoint>();
        secondCulster.add(new SimpleNDimPoint(2f, 2f, 3f, 7f));
        secondCulster.add(new SimpleNDimPoint(2f, 2f, 4f, 5f));
        secondCulster.add(new SimpleNDimPoint(4f, 6f, 2f, 4f));
        secondCulster.add(new SimpleNDimPoint(6f, 20f, 7f, 3f));
        secondCulster.add(new SimpleNDimPoint(4f, 1f, 2f, 6f));
        secondCulster.add(new SimpleNDimPoint(14f, 2f, 3f, 7f));

        ds.addAll(secondCulster);

        for (SimpleNDimPoint simpleNDimPoint : firstCulster)
        {
            ds.remove(simpleNDimPoint);
        }
        
        List<SimpleNDimPoint> asList = ds.asList();
        
        assertTrue(asList.containsAll(secondCulster) && secondCulster.containsAll(asList));


        for (SimpleNDimPoint simpleNDimPoint : secondCulster)
        {
            ds.remove(simpleNDimPoint);
        }

        assertTrue(ds.isEmpty());

    }

    @Test
    public void removeWithinWindow()
    {
        DataStore<SimpleNDimPoint> ds = getDatastoreInstance(4);

        //ds.setWindow(2f,2f,2f,2f);

        Collection<SimpleNDimPoint> firstCulster = new ArrayList<SimpleNDimPoint>();
        firstCulster.add(new SimpleNDimPoint(1f, 2f, 3f, 4f));
        firstCulster.add(new SimpleNDimPoint(1f, 2f, 4f, 4f));
        firstCulster.add(new SimpleNDimPoint(1f, 3f, 3f, 4f));
        firstCulster.add(new SimpleNDimPoint(1f, 1f, 4f, 4f));
        firstCulster.add(new SimpleNDimPoint(1f, 1f, 4f, 4f)); // туц
        firstCulster.add(new SimpleNDimPoint(1f, 2f, 3f, 5f));
        firstCulster.add(new SimpleNDimPoint(2f, 2f, 3f, 4f));


        ds.addAll(firstCulster);

        Collection<SimpleNDimPoint> secondCulster = new ArrayList<SimpleNDimPoint>();
        secondCulster.add(new SimpleNDimPoint(20f, 20f, 30f, 70f));
        secondCulster.add(new SimpleNDimPoint(20f, 2f, 4f, 5f));
        secondCulster.add(new SimpleNDimPoint(4f, 60f, 2f, 4f));
        secondCulster.add(new SimpleNDimPoint(6f, 20f, 7f, 3f));
        secondCulster.add(new SimpleNDimPoint(40f, 1f, 2f, 6f));
        secondCulster.add(new SimpleNDimPoint(140f, 2f, 3f, 7f));

        ds.addAll(secondCulster);



        Collection<SimpleNDimPoint> withinWindow = ds.removeWithinWindow(new SimpleNDimPoint(1f, 2f, 3f, 4f),new SimpleNDimPoint(2f,2f,2f,2f));

        assertTrue(withinWindow.containsAll(firstCulster) && firstCulster.containsAll(withinWindow));

        List asList = ds.asList();

        for (SimpleNDimPoint simpleNDimPoint : firstCulster)
        {
            assertFalse(asList.contains(simpleNDimPoint));
        }
        

        for (SimpleNDimPoint simpleNDimPoint : secondCulster)
        {
            ds.remove(simpleNDimPoint);
        }

        assertTrue(ds.isEmpty());

    }


    @Test
    public void testIterate()
    {

        DataStore<SimpleNDimPoint> ds = getDatastoreInstance(5);
//        ds.setWindow(1f, 8f, 1f, 1f, 1f);

        Set<SimpleNDimPoint> srcData = new HashSet<SimpleNDimPoint>();
        srcData.add(new SimpleNDimPoint(
                1f, 2f, 3f, 4f, 5f));
        srcData.add(new SimpleNDimPoint(
                1f, 2f, 3f, 4f, 5.1f));
        srcData.add(new SimpleNDimPoint(
                1f, 2f, 3f, 4f, 4.99f));
        srcData.add(new SimpleNDimPoint(
                1f, 3f, 3f, 4f, 5f));
        /* srcData.addOrGet(new SimpleNDimPoint(
        1f, 3f, 3f, 4f, 5f));*/
        srcData.add(new SimpleNDimPoint(
                7f, 8f, 3f, 2f, 8f));
        srcData.add(new SimpleNDimPoint(
                8f, 2f, -3f, 4f, 2f));
        srcData.add(new SimpleNDimPoint(
                8f, 5f, 4f, 7f, 25f));

        for (SimpleNDimPoint points : srcData)
        {
            ds.addOrGet(points);
        }


        Set<SimpleNDimPoint> finData = new HashSet<SimpleNDimPoint>();

        for (SimpleNDimPoint point : ds)
        {
            finData.add(point);
        }




        assertTrue(srcData.containsAll(finData) && finData.containsAll(srcData));
    }
}