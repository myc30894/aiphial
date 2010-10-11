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

package me.uits.aiphial.imaging.searching;

import java.awt.image.BufferedImage;
import java.util.Collection;
import ru.nickl.meanShift.direct.LUV;
import ru.nickl.meanShift.direct.LUVConverter;
import me.uits.aiphial.general.basic.Cluster;
import me.uits.aiphial.imaging.ImgUtls;
import me.uits.aiphial.imaging.LuvPoint;
import me.uits.aiphial.imaging.Region;

/**
 *
 * @author Nicolay Mitropolsky <NicolayMitropolsky@gmail.com>
 */
public class HistogramClusterComparer implements RegionComparer
{

    LUVHistorgam pattern;
    Collection<LuvPoint> patternCluster;

    public double compareCluster(Region cluster)
    {
        if(cluster.size()<0.1*patternCluster.size())
        {
            return Double.POSITIVE_INFINITY;
        }
        LUVHistorgam clusterHustogram = LUVHistorgam.build(cluster);
        return pattern.distcompare(clusterHustogram);
    }

    public void setPattern(Region pattern)
    {
        this.patternCluster = pattern;
        this.pattern = LUVHistorgam.build(this.patternCluster);
    }

    public void setPattern(BufferedImage orig)
    {
        LUV[][] toLUVDArray = new LUVConverter().toLUVDArray(orig);
        Collection<LuvPoint> luvDArraytoLuvPoints = ImgUtls.luvDArraytoLuvPoints(toLUVDArray);
        this.patternCluster = luvDArraytoLuvPoints;
        this.pattern = LUVHistorgam.build(this.patternCluster);
    }

    public LUVHistorgam getPattern()
    {
        return pattern;
    }
}
