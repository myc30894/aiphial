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

package me.uits.aiphial.general.aglomerative;

import me.uits.aiphial.general.aglomerative.AglomerativeClustererStack;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import me.uits.aiphial.general.basic.Clusterer;
import me.uits.aiphial.general.basic.ClustererTest;
import me.uits.aiphial.general.basic.FastMeanShiftClusterer;
import me.uits.aiphial.general.basic.MeanShiftClusterer;
import me.uits.aiphial.general.dataStore.DataStore;
import me.uits.aiphial.general.dataStore.NDimPoint;
import me.uits.aiphial.general.dataStore.SimpleNDimPoint;

/**
 *
 * @author Nicolay Mitropolsky <NicolayMitropolsky@gmail.com>
 */
public class AglomerativeClustererTest extends ClustererTest
{

    public AglomerativeClustererTest()
    {
    }

    @Override
    protected Clusterer<SimpleNDimPoint> createClusterer()
    {
        AglomerativeClustererStack<SimpleNDimPoint> result = new AglomerativeClustererStack<SimpleNDimPoint>();

        MeanShiftClusterer<NDimPoint> clustere1 = new MeanShiftClusterer<NDimPoint>();
        clustere1.setWindow(2F, 2F);
        result.addClustererToQueue(clustere1);
        MeanShiftClusterer<NDimPoint> clustere2 = new MeanShiftClusterer<NDimPoint>();
        clustere2.setWindow(2F, 2F);
        result.addClustererToQueue(clustere2);

        return result;
    }

    public void testAglomerativeClusterer2()
    {
         AglomerativeClustererStack<SimpleNDimPoint> result = new AglomerativeClustererStack<SimpleNDimPoint>();

        MeanShiftClusterer<NDimPoint> clustere1 = new MeanShiftClusterer<NDimPoint>();
        clustere1.setWindow(2F, 2F);
        result.addClustererToQueue(clustere1);
        MeanShiftClusterer<NDimPoint> clustere2 = new MeanShiftClusterer<NDimPoint>();
        clustere2.setWindow(2F, 2F);
        result.addClustererToQueue(clustere2);
        MeanShiftClusterer<NDimPoint> clustere3 = new FastMeanShiftClusterer<NDimPoint>();
        clustere3.setWindow(2F, 2F);
        result.addClustererToQueue(clustere3);

        test1(result);

    }

}
