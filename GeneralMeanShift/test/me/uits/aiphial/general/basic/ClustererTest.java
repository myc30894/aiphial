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

package me.uits.aiphial.general.basic;

import me.uits.aiphial.general.basic.Clusterer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.junit.Ignore;
import org.junit.Test;
import me.uits.aiphial.general.CollectionUtls;
import me.uits.aiphial.general.dataStore.DataStore;
import me.uits.aiphial.general.dataStore.MultiDimMapDataStore;
import me.uits.aiphial.general.dataStore.SimpleNDimPoint;
import org.junit.Test;
import me.uits.aiphial.general.CollectionUtls;
import static org.junit.Assert.*;
import me.uits.aiphial.general.dataStore.DataStore;
import me.uits.aiphial.general.dataStore.MultiDimMapDataStore;

/**
 *
 * @author Nicolay Mitropolsky <NicolayMitropolsky@gmail.com>
 */
@Ignore
public abstract class ClustererTest {

    public ClustererTest()
    {
    }

    protected abstract Clusterer<SimpleNDimPoint> createClusterer();

    @Test
    public void testDoClustering1()
    {
        Clusterer<SimpleNDimPoint> instance = createClusterer();
        test1(instance);
    }

    protected void test1(Clusterer<SimpleNDimPoint> instance)
    {
        System.out.println("doClustering");
        DataStore<SimpleNDimPoint> ds = new MultiDimMapDataStore<SimpleNDimPoint>(2);
        Set<SimpleNDimPoint> firstCulster = new HashSet<SimpleNDimPoint>();
        firstCulster.add(new SimpleNDimPoint(1.0F, 2.0F));
        firstCulster.add(new SimpleNDimPoint(1.1F, 2.1F));
        firstCulster.add(new SimpleNDimPoint(1.2F, 2.2F));
        firstCulster.add(new SimpleNDimPoint(1.3F, 2.3F));
        firstCulster.add(new SimpleNDimPoint(1.4F, 1.9F));
        firstCulster.add(new SimpleNDimPoint(1.0F, 1.8F));
        ds.addAll(firstCulster);
        Set<SimpleNDimPoint> secondCulster = new HashSet<SimpleNDimPoint>();
        secondCulster.add(new SimpleNDimPoint(5.0F, 4.0F));
        secondCulster.add(new SimpleNDimPoint(5.1F, 4.0F));
        secondCulster.add(new SimpleNDimPoint(5.2F, 4.0F));
        secondCulster.add(new SimpleNDimPoint(5.3F, 4.0F));
        secondCulster.add(new SimpleNDimPoint(5.4F, 4.0F));
        ds.addAll(secondCulster);
        instance.setDataStore(ds);
        instance.doClustering();
        assertTrue(CollectionUtls.checkInCollectionEquality(instance.getClusters(), Arrays.asList(firstCulster, secondCulster)));
    }

}
