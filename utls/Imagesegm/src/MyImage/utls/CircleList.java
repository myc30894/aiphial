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

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 *
 * @author Nicolay Mitropolsky <NicolayMitropolsky@gmail.com>
 */
public class CircleList<T> implements Collection<T>
{

    public CircleList()
    {
    }

    public CircleList(Iterable<T> iterable)
    {
        for (T object : iterable)
        {
            this.add(object);
        }
    }

    private class Item<T>
    {

        public Item(T elem, Item<T> next)
        {
            this.elem = elem;
            this.next = next;
        }
        T elem;
        Item<T> next;

        @Override
        public String toString()
        {
            return "elem: "+ elem.toString()+" next: "+next.elem.toString();
        }


    }
    private Item<T> last = null;
    private int size = 0;

    public boolean add(T elem)
    {
        if (last == null)
        {
            last = new Item<T>(elem, null);
            last.next = last;
        } else
        {
            Item<T> next = last.next;
            last.next = new Item<T>(elem, next);
            last = last.next;
        }

        size++;

        return true;
    }

    public Iterator<T> iterator(T elem)
    {
        Item<T> founded = findItem(elem);
        
        if (founded==null)
        {
            throw new NoSuchElementException();
        }

        return new CircleListIterator(founded);
    }

    public Iterator<T> iterator()
    {
        return new CircleListIterator(last.next);
    }

    private class CircleListIterator implements Iterator<T>
    {

        Item<T> start;
        Item<T> cur;
        boolean hasnext = true;

        public CircleListIterator(Item<T> start)
        {
            this.start = start;
            this.cur = start;

            hasnext = cur.next != start;
        }

        public boolean hasNext()
        {
            return hasnext;
        }

        public T next()
        {
            T r = cur.elem;
            hasnext = cur.next != start;
            cur = cur.next;
            return r;
        }

        public void remove()
        {
            throw new UnsupportedOperationException("Not supported");
        }
    }

    private Item<T> findItem(T elem)
    {
        Item<T> cur = last;
        Item<T> founded = null;
        do
        {
            cur = cur.next;
            if (cur.elem == elem)
            {
                founded = cur;
                break;
            }
        } while (cur != last);
        return founded;
    }

    public int size()
    {
        return size;
    }

    public boolean isEmpty()
    {
        return this.last == null;
    }

    public boolean contains(Object o)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Object[] toArray()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public <T> T[] toArray(T[] a)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean remove(Object o)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean containsAll(Collection<?> c)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean addAll(Collection<? extends T> c)
    {
        for (T t : c)
        {
            this.add(t);
        }

        return true;
    }

    public boolean removeAll(Collection<?> c)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean retainAll(Collection<?> c)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void clear()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
