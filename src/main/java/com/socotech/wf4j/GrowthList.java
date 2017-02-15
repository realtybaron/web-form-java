package com.socotech.wf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: marc
 * Date: 2/15/17
 * Time: 1:49 PM
 */
public class GrowthList<E> {
    /**
     * Factory method to create a growth list.
     *
     * @param <E> the type of the elements in the list
     * @return a new growth list
     * @throws NullPointerException if list is null
     * @since 4.0
     */
    public static <E> List<E> get() {
        return new ArrayList<E>() {
            @Override
            public void add(int index, E element) {
                final int size = size();
                if (index > size) {
                    super.addAll(Collections.<E>nCopies(index - size, null));
                }
                super.add(index, element);
            }

            @Override
            public boolean addAll(final int index, final Collection<? extends E> coll) {
                final int size = size();
                boolean result = false;
                if (index > size) {
                    super.addAll(Collections.<E>nCopies(index - size, null));
                    result = true;
                }
                return super.addAll(index, coll) | result;
            }

            @Override
            public E set(final int index, final E element) {
                final int size = size();
                if (index >= size) {
                    super.addAll(Collections.<E>nCopies(index - size + 1, null));
                }
                return super.set(index, element);
            }
        };
    }
}
