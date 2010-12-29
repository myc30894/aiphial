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

package me.uits.aiphial.general;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Nicolay Mitropolsky <NicolayMitropolsky@gmail.com>
 */
public class CollectionUtls {

    private static boolean checkArrayEquality(Float[] a, Float[] b)
    {
        for (int i = 0; i < b.length; i++)
        {
            if (!(Math.abs(a[i] - b[i]) < 0.001))
            {
                return false;
            }
        }
        return true;

    }

    public static boolean checkCollectionContentEquality(Collection<Float[]> a, Collection<Float[]> b)
    {
        ArrayDeque<Float[]> ad = new ArrayDeque<Float[]>(a);
        List<Float[]> bd = new LinkedList<Float[]>(b);

        for (Iterator<Float[]> it1 = ad.iterator(); it1.hasNext();)
        {
            Float[] floats = it1.next();

            for (Iterator<Float[]> it2 = bd.iterator(); it2.hasNext();)
            {
                Float[] floats1 = it2.next();

                if (checkArrayEquality(floats, floats1))
                {
                    ad.pollFirst();
                    it2.remove();
                    break;
                }

            }

        }

        return ad.isEmpty() && bd.isEmpty();
    }

    public static boolean checkInCollectionEquality(Collection<? extends Collection<?>> a, Collection<? extends Collection<?>> b)
    {
        ArrayDeque<Collection<?>> ad = new ArrayDeque<Collection<?>>(a);
        List<Collection<?>> bd = new LinkedList<Collection<?>>(b);

        for (Iterator<Collection<?>> it1 = ad.iterator(); it1.hasNext();)
        {
            Collection<?> floats = it1.next();

            for (Iterator<Collection<?>> it2 = bd.iterator(); it2.hasNext();)
            {
                Collection<?> floats1 = it2.next();

                if (floats1.containsAll(floats) && floats.containsAll(floats1))
                {
                    ad.pollFirst();
                    it2.remove();
                    break;
                }

            }

        }

        return ad.isEmpty() && bd.isEmpty();
    }


}
