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

package me.uits.aiphial.imaging.boundary;

import java.util.*;

/**
 * Collection implements a Circle List. This means that iterator
 * from arbitrary element iterates over all elements.
 * @author Nicolay Mitropolsky <NicolayMitropolsky@gmail.com>
 */
public class CircleList<T> implements Collection<T> {

    public CircleList() {
    }

    public CircleList(Iterable<T> iterable) {
        for (T object : iterable) {
            this.add(object);
        }
    }

    protected class Item<T> {

        public Item(T elem, Item<T> next,Item<T> prev ) {
            this.elem = elem;
            this.next = next;
            this.prev = prev;
        }

        T elem;
        Item<T> next;
        Item<T> prev;

        @Override
        public String toString() {
            return "elem: " + elem.toString() + " next: " + next.elem.toString()+ " prev: " + prev.elem.toString();
        }


    }

    protected  Item<T> last = null;
    private int size = 0;

    public boolean add(T elem) {
        if (last == null) {
            last = new Item<T>(elem, null,null);
            last.next = last;
            last.prev = last;
        } else {
            Item<T> next = last.next;
            last.next = new Item<T>(elem, next,last);
            last = last.next;
        }

        size++;

        return true;
    }

    /**
     * Creates iterator from first occurrence of given element
     * @param elem
     * @return
     */
    public CircleListIterator iterator(T elem) {
        Item<T> founded = findItem(elem);

        if (founded == null) {
            throw new NoSuchElementException();
        }

        return new CircleListIterator(founded);
    }

    public CircleListIterator iterator() {
        return new CircleListIterator(last.next);
    }

    public class CircleListIterator implements Iterator<T> {

        Item<T> start;
        Item<T> cur;
        boolean hasnext = true;

        public CircleListIterator(Item<T> start) {
            this.start = start;
            this.cur = start;

            hasnext = cur.next != start;
        }

        public boolean hasNext() {
            return hasnext;
        }

        public T next() {
            T r = cur.elem;
            hasnext = cur.next != start;
            cur = cur.next;
            return r;
        }

        public T prev() {
            T r = cur.elem;
            hasnext = cur != start;
            cur = cur.prev;
            return r;
        }

        public T getCurrent()
        {
            return cur.elem;
        }

        public T getNext()
        {
            return cur.next.elem;
        }

         public T getPrev()
        {
            return cur.prev.elem;
        }

        public void remove() {
            throw new UnsupportedOperationException("Not supported");
        }
    }

    private Item<T> findItem(T elem) {
        Item<T> cur = last;
        Item<T> founded = null;
        do {
            cur = cur.next;
            if (cur.elem == elem) {
                founded = cur;
                break;
            }
        } while (cur != last);
        return founded;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return this.last == null;
    }

    public boolean contains(Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Object[] toArray() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public <E> E[] toArray(E[] a) {
        if (a.length < size)
            a = (E[]) java.lang.reflect.Array.newInstance(
                    a.getClass().getComponentType(), size);
        int i = 0;
        for (T elem : this) {
            a[i++] = (E) elem;
        }
        return a;
    }

    public boolean remove(Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean containsAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean addAll(Collection<? extends T> c) {
        for (T t : c) {
            this.add(t);
        }

        return true;
    }

    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void clear() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
