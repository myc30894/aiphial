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
import java.sql.Array;
import me.uits.aiphial.general.basic.MeanShiftClusterer;
import me.uits.aiphial.general.basic.IMeanShiftClusterer;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import me.uits.aiphial.general.CollectionUtls;
import static org.junit.Assert.*;
import me.uits.aiphial.general.dataStore.DataStore;
import me.uits.aiphial.general.dataStore.MultiDimMapDataStore;
import me.uits.aiphial.general.dataStore.NDimPoint;
import me.uits.aiphial.general.dataStore.SimpleNDimPoint;

/**
 *
 * @author Nicolay Mitropolsky <NicolayMitropolsky@gmail.com>
 */
@Ignore
public abstract  class AbstractMeanShiftClustererTest extends ClustererTest
{

    public AbstractMeanShiftClustererTest()
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

    @Before
    public void setUp()
    {
    }

    @After
    public void tearDown()
    {
    }

    protected Clusterer<SimpleNDimPoint> createClusterer()
    {
        //ds.setWindow(3f, 3f);
        IMeanShiftClusterer<SimpleNDimPoint> instance = createInstance();
        instance.setWindow(3f, 3f);
        return instance;
    }

    abstract protected <T extends NDimPoint> IMeanShiftClusterer<T> createInstance();

}