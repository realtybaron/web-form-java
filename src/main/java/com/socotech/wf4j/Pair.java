/*
 * Pair class
 */

package com.socotech.wf4j;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * <p/> Simple class that contains a pair of Strings. </p>
 */
public class Pair<K, V> {
    /**
     * Constructor. Throws a null pointer exception if you try to construct with null strings.
     *
     * @param left  The left string
     * @param right The right string
     */
    public Pair(K left, V right) {
        Validate.notNull(left, "Left string is null");
        this.left = left;
        this.right = right;
    }

    /**
     * <p/> Returns the leftmost string of the pair. </p>
     *
     * @return the left string
     */
    public K getLeft() {
        return this.left;
    }

    /**
     * <p/> Returns the rightmost string of the pair. </p>
     *
     * @return the right string
     */
    public V getRight() {
        return this.right;
    }

    /**
     * Return a string form of the pair
     */
    public String toString() {
        return "Pair[" + this.left + "," + this.right + "]";
    }

    /**
     * Returns true if the the two pairs are equal.
     *
     * @param o some other pair
     * @return true, if equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Pair that = (Pair) o;
        return new EqualsBuilder().append(this.left, that.left).append(this.right, that.right).isEquals();
    }

    /**
     * Returns a hash code for this pair
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.left).append(this.right).toHashCode();
    }

    /**
     * The left of the pair
     */
    protected K left;
    /**
     * The right of the pair
     */
    protected V right;
}
