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

package MyImage.utls;

import java.util.ArrayList;
import java.util.Collection;
import ru.nickl.meanShift.direct.LUV;
import me.uits.aiphial.imaging.LuvPoint;

/**
 *
 * @author Nicolay Mitropolsky <NicolayMitropolsky@gmail.com>
 */
public class PointsMap
{

    protected LuvPoint[][] points;

    protected PointsMap()
    {
    }
    protected int sx;
    protected int sy;
    protected int width;
    protected int height;


    public PointsMap(int x, int y, int w, int h)
    {
        this(x, y, w, h, 0);
    }

    public PointsMap(int x, int y, int w, int h, int add)
    {
        this.sx = x-add;
        this.sy = y-add;
        this.width = w+add;
        this.height = h+add;
        points = new LuvPoint[this.width -sx][this.height-sy];

        for (int i = 0; i < points.length; i++)
        {
            for (int j = 0; j < points[i].length; j++)
            {
                points[i][j] = new LuvPoint(i+sx, j+sy, LUV.zeroLuv());
            }

        }

    }

    public PointsMap buildMap(Iterable<LuvPoint> points)
    {
        for (LuvPoint point : points)
        {
            this.points[point.getX()-sx][point.getY()-sy] = point;
        }

        return this;
    }

    public Iterable<LuvPoint>  get8Nearest(LuvPoint point)
    {

        final int cx = point.getX();
        final int cy = point.getY();

        return get8Nearest(cx, cy);
    }

    public Iterable<LuvPoint>  get8Nearest(final int cx, final int cy)
    {
        Collection<LuvPoint> result = new ArrayList<LuvPoint>(8);
        for (int x = cx - 1; x < cx + 2; x++)
        {
            for (int y = cy - 1; y < cy + 2; y++)
            {
                if (x < width + sx && x > 0 && y < height + sy && y > 0)
                {
                    result.add(points[x - sx][y - sy]);
                }
            }
        }
        return result;
    }

    public Iterable<LuvPoint> get4Nearest(final int cx, final int cy)
    {
        Collection<LuvPoint> result = new ArrayList<LuvPoint>(8);
        for (int x = cx - 1; x < cx + 2; x++)
        {
            for (int y = cy - 1; y < cy + 2; y++)
            {
                if(Math.abs(x-cx)+Math.abs(y-cy)<=1)
                {
                if (x < width + sx && x > 0 && y < height + sy && y > 0)
                {
                    result.add(points[x - sx][y - sy]);
                }
                }
            }
        }
        return result;
    }

    public Iterable<LuvPoint> get4Nearest(LuvPoint point)
    {
        return  get4Nearest(point.getX(),point.getY());
    }


    public CircleList<LuvPoint> get4CouterClockwise(LuvPoint cur)
    {
        int cx = cur.getX();
        int cy = cur.getY();

        //Collection<LuvPoint> r = new ArrayList<LuvPoint>(4);

        CircleList<LuvPoint> r = new CircleList<LuvPoint>();

        r.add(getAt(cx, cy - 1));
        r.add(getAt(cx - 1, cy));
        r.add(getAt(cx, cy + 1));
        r.add(getAt(cx + 1, cy));

        return r;
    }

    public CircleList<LuvPoint> get8CouterClockwise(LuvPoint cur)
    {
        int cx = cur.getX();
        int cy = cur.getY();

        //Collection<LuvPoint> r = new ArrayList<LuvPoint>(8);

        CircleList<LuvPoint> r = new CircleList<LuvPoint>();

        r.add(getAt(cx, cy - 1));
        r.add(getAt(cx - 1, cy - 1));
        r.add(getAt(cx - 1, cy));
        r.add(getAt(cx - 1, cy + 1));
        r.add(getAt(cx, cy + 1));
        r.add(getAt(cx + 1, cy + 1));
        r.add(getAt(cx + 1, cy));
        r.add(getAt(cx + 1, cy - 1));

        return r;
    }

    public LuvPoint getAt(int x, int y)
    {
        return points[x-sx][y-sy];
    }

    void putAt(int x, int y, LuvPoint point)
    {
        points[x-sx][y-sy] = point;
    }

}
