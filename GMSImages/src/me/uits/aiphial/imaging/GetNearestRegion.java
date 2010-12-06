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

import java.util.HashMap;
import java.util.Map.Entry;

/**
 *
 * @author nickl
 */
public class GetNearestRegion {

    public static Region getNearestRegion(Region region, Matrix<Region> matrix)
    {
        int width = matrix.width();
        int height = matrix.height();
        HashMap<Region, Integer> neibourdMap = new HashMap<Region, Integer>();
        for (LuvPoint p : region/*.getUnorderedBoundary()*/)
        {
            for (short y0 = (short) (p.getY() - 1); y0 <= p.getY() + 1; y0++)
            {
                if (y0 >= 0 && y0 < width)
                {
                    for (short x0 = (short) (p.getX() - 1); x0 <= p.getX() + 1; x0++)
                    {
                        if (x0 >= 0 && x0 < height)
                        {
                            final Region pointRegion = matrix.apply(x0, y0);
                            if (pointRegion != region)
                            {
                                Integer count = neibourdMap.get(pointRegion);
                                if (count == null)
                                {
                                    count = 0;
                                }
                                count++;
                                neibourdMap.put(pointRegion, count);
                            }
                        }
                    }
                }
            }
        }
        int max = 0;
        Region nearestRegion = null;
        for (Entry<Region, Integer> entry : neibourdMap.entrySet())
        {
            final Integer value = entry.getValue();
            if (value > max)
            {
                max = value;
                nearestRegion = entry.getKey();
            }
        }
        return nearestRegion;
    }


}
