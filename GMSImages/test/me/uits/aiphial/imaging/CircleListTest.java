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

package me.uits.aiphial.imaging;

import me.uits.aiphial.imaging.CircleList;
import java.lang.Integer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Nicolay Mitropolsky <NicolayMitropolsky@gmail.com>
 */
public class CircleListTest
{

    public CircleListTest()
    {
    }

    @BeforeClass
    public static void setUpClass() throws Exception
    {
    }

    @AfterClass
    public static void tearDownClass() throws Exception
    {
    }

    /**
     * Test of add method, of class CircleList.
     */
    @Test
    public void test1()
    {
        List<Integer> list = new ArrayList<Integer>();

        list.add(1);
        list.add(3);
        list.add(4);
        list.add(3);
        list.add(7);
        list.add(7);
        list.add(2);
        list.add(0);

        CircleList<Integer> instance = new CircleList<Integer>();
        assertTrue(instance.isEmpty());

        for (Integer integer : list)
        {
            instance.add(integer);
        }

        assertTrue(list.size() == instance.size());

        Iterator<Integer> iterator = list.iterator();
        Iterator<Integer> iterator1 = instance.iterator();

        while (iterator.hasNext())
        {
            assertTrue(iterator.next() == iterator1.next());

        }

        assertFalse(iterator1.hasNext());
    }

     @Test
    public void test2()
    {
        List<Integer> list = new ArrayList<Integer>();

        list.add(3);
        list.add(4);
        list.add(1);
        list.add(2);

        CircleList<Integer> instance = new CircleList<Integer>();

        instance.add(1);
        instance.add(2);
        instance.add(3);
        instance.add(4);

        assertTrue(list.size() == instance.size());

        Iterator<Integer> iterator = list.iterator();
        Iterator<Integer> iterator1 = instance.iterator(3);

        while (iterator.hasNext())
        {
            assertTrue(iterator.next() == iterator1.next());

        }

        assertFalse(iterator1.hasNext());
    }

}
