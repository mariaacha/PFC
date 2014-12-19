///////////////////////////////////////////////////////////////////////////////
// Copyright (c) 2001, Eric D. Friedman All Rights Reserved.
// Copyright (c) 2009, Rob Eden All Rights Reserved.
// Copyright (c) 2009, Jeff Randall All Rights Reserved.
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
///////////////////////////////////////////////////////////////////////////////

package arrayList;

import paralel.Constants;
import paralel.TCharIterator;
import paralel.TCharFunction;
import paralel.TCharProcedure;
import paralel.HashFunctions;
import paralel.TCharCollection;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.*;
import java.util.function.UnaryOperator;
import java.util.function.Function;


//////////////////////////////////////////////////
// THIS IS A GENERATED CLASS. DO NOT HAND EDIT! //
//////////////////////////////////////////////////


/**
 * A resizable, array-backed list of char primitives.
 */
public class TCharArrayList implements TCharList, Externalizable {
	static final long serialVersionUID = 1L;

    /** the data of the list */
    protected char[] _data;

    /** the index after the last entry in the list */
    protected int _pos;

    /** the default capacity for new lists */
    protected static final int DEFAULT_CAPACITY = Constants.DEFAULT_CAPACITY;

    /** the char value that represents null */
    protected char no_entry_value;
    
    protected int numProcs;
    
    protected boolean boolGlobal;
    
    protected int intGlobal;
    
    protected int[] intsGlobal;
    
    protected char charGlobal;
    
    protected char[] charsGlobal;
    
    protected String stringGlobal;
    
    protected Thread[] threads = new Thread[numProcs];


    /**
     * Creates a new <code>TCharArrayList</code> instance with the
     * default capacity.
     */
    @SuppressWarnings({"RedundantCast"})
    public TCharArrayList() {
        this( DEFAULT_CAPACITY, ( char ) 0 );
    }


    /**
     * Creates a new <code>TCharArrayList</code> instance with the
     * specified capacity.
     *
     * @param capacity an <code>char</code> value
     */
    @SuppressWarnings({"RedundantCast"})
    public TCharArrayList( int capacity ) {
        this( capacity, ( char ) 0 );
    }


    /**
     * Creates a new <code>TCharArrayList</code> instance with the
     * specified capacity.
     *
     * @param capacity an <code>char</code> value
     * @param no_entry_value an <code>char</code> value that represents null.
     */
    public TCharArrayList( int capacity, char no_entry_value ) {
        _data = new char[ capacity ];
        _pos = 0;
        this.no_entry_value = no_entry_value;
    }

    /**
     * Creates a new <code>TCharArrayList</code> instance that contains
     * a copy of the collection passed to us.
     *
     * @param collection the collection to copy
     */
    public TCharArrayList ( TCharCollection collection ) {
        this( collection.size() );
        addAll( collection ); 
    }


    /**
     * Creates a new <code>TCharArrayList</code> instance whose
     * capacity is the length of <tt>values</tt> array and whose
     * initial contents are the specified values.
     * <p>
     * A defensive copy of the given values is held by the new instance.
     *
     * @param values an <code>char[]</code> value
     */
    public TCharArrayList( char[] values ) {
        this( values.length );
        add( values );
    }

    protected TCharArrayList(char[] values, char no_entry_value, boolean wrap) {
        if (!wrap)
            throw new IllegalStateException("Wrong call");

        if (values == null)
            throw new IllegalArgumentException("values can not be null");

        _data = values;
        _pos = values.length;
        this.no_entry_value = no_entry_value;
    }
    
    private TCharArrayList (char[] values, int from, int to)	{
    	_data = new char[to-from];
    	System.arraycopy(values, from, _data, 0, to-from);
    	_pos = to-from;
    }

    /**
     * Returns a primitive List implementation that wraps around the given primitive array.
     * <p/>
     * NOTE: mutating operation are allowed as long as the List does not grow. In that case
     * an IllegalStateException will be thrown
     *
     * @param values
     * @return
     */
    public static TCharArrayList wrap(char[] values) {
        return wrap(values, ( char ) 0);
    }

    /**
     * Returns a primitive List implementation that wraps around the given primitive array.
     * <p/>
     * NOTE: mutating operation are allowed as long as the List does not grow. In that case
     * an IllegalStateException will be thrown
     *
     * @param values
     * @param no_entry_value
     * @return
     */
    public static TCharArrayList wrap(char[] values, char no_entry_value) {
        return new TCharArrayList(values, no_entry_value, true) {
            /**
             * Growing the wrapped external array is not allow
             */
            @Override
            public void ensureCapacity(int capacity) {
                if (capacity > _data.length)
                    throw new IllegalStateException("Can not grow ArrayList wrapped external array");
            }
        };
    }

    /** {@inheritDoc} */
    public char getNoEntryValue() {
        return no_entry_value;
    }


    // sizing

    /**
     * Grow the internal array as needed to accommodate the specified number of elements.
     * The size of the array chars on each resize unless capacity requires more than twice
     * the current capacity.
     */
    public void ensureCapacity( int capacity ) {
        if ( capacity > _data.length ) {
            int newCap = Math.max( _data.length << 1, capacity );
            char[] tmp = new char[ newCap ];
            System.arraycopy( _data, 0, tmp, 0, _data.length );
            _data = tmp;
        }
    }


    /** {@inheritDoc} */
    public int size() {
        return _pos;
    }


    /** {@inheritDoc} */
    public boolean isEmpty() {
        return _pos == 0;
    }


    /**
     * Sheds any excess capacity above and beyond the current size of the list.
     */
    public void trimToSize() {
        if ( _data.length > size() ) {
            char[] tmp = new char[ size() ];
            toArray( tmp, 0, tmp.length );
            _data = tmp;
        }
    }


    // modifying

    /** {@inheritDoc} */
    public boolean add( char val ) {
        ensureCapacity( _pos + 1 );
        _data[ _pos++ ] = val;
        return true;
    }


    /** {@inheritDoc} */
    public void add( char[] vals ) {
        add( vals, 0, vals.length );
    }


    /** {@inheritDoc} */
    public void add( char[] vals, int offset, int length ) {
        ensureCapacity( _pos + length );
        System.arraycopy( vals, offset, _data, _pos, length );
        _pos += length;
    }


    /** {@inheritDoc} */
    public void insert( int offset, char value ) {
        if ( offset == _pos ) {
            add( value );
            return;
        }
        ensureCapacity( _pos + 1 );
        // shift right
        System.arraycopy( _data, offset, _data, offset + 1, _pos - offset );
        // insert
        _data[ offset ] = value;
        _pos++;
    }


    /** {@inheritDoc} */
    public void insert( int offset, char[] values ) {
        insert( offset, values, 0, values.length );
    }


    /** {@inheritDoc} */
    public void insert( int offset, char[] values, int valOffset, int len ) {
        if ( offset == _pos ) {
            add( values, valOffset, len );
            return;
        }

        ensureCapacity( _pos + len );
        // shift right
        System.arraycopy( _data, offset, _data, offset + len, _pos - offset );
        // insert
        System.arraycopy( values, valOffset, _data, offset, len );
        _pos += len;
    }


    /** {@inheritDoc} */
    public char get( int offset ) {
        if ( offset >= _pos ) {
            throw new ArrayIndexOutOfBoundsException( offset );
        }
        return _data[ offset ];
    }


    /**
     * Returns the value at the specified offset without doing any bounds checking.
     */
    public char getQuick( int offset ) {
        return _data[ offset ];
    }


    /** {@inheritDoc} */
    public char set( int offset, char val ) {
        if ( offset >= _pos ) {
            throw new ArrayIndexOutOfBoundsException( offset );
        }

		char prev_val = _data[ offset ];
        _data[ offset ] = val;
		return prev_val;
    }


    /** {@inheritDoc} */
    public char replace( int offset, char val ) {
        if ( offset >= _pos ) {
            throw new ArrayIndexOutOfBoundsException( offset );
        }
        char old = _data[ offset ];
        _data[ offset ] = val;
        return old;
    }
    
    @SuppressWarnings("unchecked")
    public void replaceAll(UnaryOperator<Character> operator) {
        Objects.requireNonNull(operator);
        //final int expectedModCount = modCount;
        final int size = this._pos;
        /**for (int i=0; modCount == expectedModCount && i < size; i++) {
            _data[i] = operator.apply((E) _data[i]);
        }
        if (modCount != expectedModCount) {
            throw new ConcurrentModificationException();
        }
        modCount++;*/
        for (int i=0; i<size; i++)	{
        	_data[i] = operator.apply(_data[i]);
        }
    }


    /** {@inheritDoc} */
    public void set( int offset, char[] values ) {
        set( offset, values, 0, values.length );
    }


    /** {@inheritDoc} */
    public void set( int offset, char[] values, int valOffset, int length ) {
        if ( offset < 0 || offset + length > _pos ) {
            throw new ArrayIndexOutOfBoundsException( offset );
        }
        System.arraycopy( values, valOffset, _data, offset, length );
    }


    /**
     * Sets the value at the specified offset without doing any bounds checking.
     */
    public void setQuick( int offset, char val ) {
        _data[ offset ] = val;
    }


    /** {@inheritDoc} */
    public void clear() {
        clear( DEFAULT_CAPACITY );
    }


    /**
     * Flushes the internal state of the list, setting the capacity of the empty list to
     * <tt>capacity</tt>.
     */
    public void clear( int capacity ) {
        _data = new char[ capacity ];
        _pos = 0;
    }


    /**
     * Sets the size of the list to 0, but does not change its capacity. This method can
     * be used as an alternative to the {@link #clear()} method if you want to recycle a
     * list without allocating new backing arrays.
     */
    public void reset() {
        _pos = 0;
        Arrays.fill( _data, no_entry_value );
    }


    /**
     * Sets the size of the list to 0, but does not change its capacity. This method can
     * be used as an alternative to the {@link #clear()} method if you want to recycle a
     * list without allocating new backing arrays. This method differs from
     * {@link #reset()} in that it does not clear the old values in the backing array.
     * Thus, it is possible for getQuick to return stale data if this method is used and
     * the caller is careless about bounds checking.
     */
    public void resetQuick() {
        _pos = 0;
    }

    private class Remove1 implements Runnable	{
    	int min, max;
    	char value;
    	Remove1 (int a, int b, char c)	{
    		min = a; max = b; value = c;
    	}
    	public void run ()	{
    		for (int i=min; i<max; i++) {
    			if (boolGlobal)	return;
                if ( value == _data[i]  ) {
                	if ((i>=0)&&(i<(_pos-1)))
                		System.arraycopy(_data, i+1, _data, i, _pos-i-1);
                	_pos--;
                    boolGlobal = true;
                    return;
                }
            }
    	}
    }
    
    /** {@inheritDoc} */
    public boolean removePar (char value, int numProcs)	{
    	int numElemProc = _pos/numProcs;
    	boolGlobal = false;
    	threads = new Thread[numProcs];
    	for (int i=0; i<numProcs; i++)	{
    		int numMax = (i+1) * numElemProc;
    		if ((i+1) == numProcs)	numMax = _pos;
    		threads[i] = new Thread (new Remove1 (i*numElemProc, numMax, value));
    		threads[i].start();
    	}
    	for (int i=0; i<numProcs; i++)	{
    		try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    	return boolGlobal;
    }
    
    public boolean remove( char value ) {
        for ( int index = 0; index < _pos; index++ ) {
            if ( value == _data[index]  ) {
            	remove( index, 1 );
                return true;
            }
        }
        return false;
    }


    /** {@inheritDoc} */
    public char removeAt( int offset ) {
        char old = get( offset );
        remove( offset, 1 );
        return old;
    }


    /** {@inheritDoc} */
    public void remove( int offset, int length ) {
		if ( length == 0 ) return;
        if ( offset < 0 || offset >= _pos ) {
            throw new ArrayIndexOutOfBoundsException(offset);
        }

        if ( offset == 0 ) {
            // data at the front
            System.arraycopy( _data, length, _data, 0, _pos - length );
        }
        else if ( _pos - length == offset ) {
            // no copy to make, decrementing pos "deletes" values at
            // the end
        }
        else {
            // data in the middle
            System.arraycopy( _data, offset + length, _data, offset,
                _pos - ( offset + length ) );
        }
        _pos -= length;
        // no need to clear old values beyond _pos, because this is a
        // primitive collection and 0 takes as much room as any other
        // value
    }


    /** {@inheritDoc} */
    public TCharIterator iterator() {
        return new TCharArrayIterator( 0 );
    }

    private class ContainsAll1 implements Runnable	{
    	int min, max;
    	Character[] array;
    	ContainsAll1 (int a, int b, Character[] c)	{
    		min = a; max = b; array = c;
    	}
    	public void run ()	{
    		for (int i=min; i<max; i++) {
                if (!boolGlobal)	return;
                if (array[i] instanceof Character ) {
                    char c = array[i].charValue();
                    if ( ! contains( c ) ) {
                        boolGlobal = false;
                        return;
                    }
                } else {
                    boolGlobal = false;
                    return;
                }

            }
    	}
    }
    /** {@inheritDoc} */
    public boolean containsAllPar (Collection<?> collection, int numProcs)	{
    	boolGlobal = true;
    	Character[] array = collection.toArray(new Character[collection.size()]);
    	int numElemProc = collection.size()/numProcs;
    	threads = new Thread[numProcs];
    	for (int i=0; i<numProcs; i++)	{
    		int numMax = (i+1) * numElemProc;
    		if ((i+1) == numProcs)	numMax = collection.size();
    		threads[i] = new Thread (new ContainsAll1 (i*numElemProc, numMax, array));
    		threads[i].start();
    	}
    	for (int i=0; i<numProcs; i++)	{
    		try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    	return boolGlobal;
    }
    public boolean containsAllPar2 ( Collection<?> collection, int numProcs) {
        for ( Object element : collection ) {
            if ( element instanceof Character ) {
                char c = ( ( Character ) element ).charValue();
                if ( ! containsPar( c, numProcs) ) {
                    return false;
                }
            } else {
                return false;
            }

        }
        return true;
    }
    public boolean containsAll( Collection<?> collection ) {
        for ( Object element : collection ) {
            if ( element instanceof Character ) {
                char c = ( ( Character ) element ).charValue();
                if ( ! contains( c ) ) {
                    return false;
                }
            } else {
                return false;
            }

        }
        return true;
    }

    private class ContainsAll2 implements Runnable	{
    	int min, max;
    	char[] array;
    	ContainsAll2 (int a, int b, char[] c)	{
    		min = a; max = b; array = c;
    	}
    	public void run ()	{
    		for (int i=min; i<max; i++) {
    			if (!boolGlobal)	return;
                char element = array[i];
                if ( ! contains( element ) ) {
                    boolGlobal = false;
                    return;
                }
            }
    	}
    }
    /** {@inheritDoc} */
    public boolean containsAllPar (TCharCollection collection, int numProcs)	{
    	if (this == collection)	return true;
    	boolGlobal = true;
    	char[] array = collection.toArray(new char[collection.size()]);
    	int numElemProc = collection.size()/numProcs;
    	threads = new Thread[numProcs];
    	for (int i=0; i<numProcs; i++)	{
    		int numMax = (i+1) * numElemProc;
    		if ((i+1) == numProcs)	numMax = collection.size();
    		threads[i] = new Thread (new ContainsAll2 (i*numElemProc, numMax, array));
    		threads[i].start();
    	}
    	for (int i=0; i<numProcs; i++)	{
    		try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    	return boolGlobal;
    }
    public boolean containsAllPar2 ( TCharCollection collection, int numProcs) {
        if ( this == collection ) {
            return true;
        }
        TCharIterator iter = collection.iterator();
        while ( iter.hasNext() ) {
            char element = iter.next();
            if ( ! containsPar( element, numProcs) ) {
                return false;
            }
        }
        return true;
    }
    public boolean containsAll( TCharCollection collection ) {
        if ( this == collection ) {
            return true;
        }
        TCharIterator iter = collection.iterator();
        while ( iter.hasNext() ) {
            char element = iter.next();
            if ( ! contains( element ) ) {
                return false;
            }
        }
        return true;
    }


    /** {@inheritDoc} */
    public boolean containsAllPar (char[] array, int numProcs)	{
    	int numElemProc = array.length/numProcs;
    	boolGlobal = true;
    	threads = new Thread[numProcs];
    	for (int i=0; i<numProcs; i++)	{
    		int numMax = (i+1) * numElemProc;
    		if ((i+1) == numProcs)	numMax = array.length;
    		threads[i] = new Thread (new ContainsAll2 (i*numElemProc, numMax, array));
    		threads[i].start();
    	}
    	for (int i=0; i<numProcs; i++)	{
    		try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    	return boolGlobal;
    }
    public boolean containsAllPar2( char[] array, int numProcs) {
        for ( int i = array.length; i-- > 0; ) {
            if ( ! containsPar ( array[i], numProcs) ) {
                return false;
            }
        }
        return true;
    }
    public boolean containsAll( char[] array ) {
        for ( int i = array.length; i-- > 0; ) {
            if ( ! contains( array[i] ) ) {
                return false;
            }
        }
        return true;
    }

    private class AddAll1 implements Runnable	{
    	int min, max, posInicial;
    	Character[] array;
    	AddAll1 (int a, int b, Character[] c, int d)	{
    		min = a; max = b;
    		array = c;
    		posInicial = d;
    	}
    	public void run ()	{
    		for (int i=min; i<max; i++) {
    			_data[i+posInicial] = array[i].charValue();
                boolGlobal = true;
            }
    	}
    }
    /** {@inheritDoc} */
    public boolean addAllPar (Collection<? extends Character> collection, int numProcs)	{
    	boolGlobal = false;
    	Character[] array = collection.toArray (new Character[collection.size()]);
    	int numElemProc = array.length/numProcs;
    	int posInicial = _pos;
    	ensureCapacity (_pos+array.length);
    	threads = new Thread[numProcs];
    	for (int i=0; i<numProcs; i++)	{
    		int numMax = (i+1) * numElemProc;
    		if ((i+1) == numProcs)	numMax = array.length;
    		threads[i] = new Thread (new AddAll1 (i*numElemProc, numMax, array, posInicial));
    		threads[i].start();
    	}
    	for (int i=0; i<numProcs; i++)	{
    		try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    	_pos += array.length;
    	return boolGlobal;
    }
    public boolean addAll( Collection<? extends Character> collection ) {
        boolean changed = false;
        for ( Character element : collection ) {
            char e = element.charValue();
            if ( add( e ) ) {
                changed = true;
            }
        }
        return changed;
    }

    private class AddAll2 implements Runnable	{
    	int min, max;
    	char[] array;
    	AddAll2 (int a, int b, char[] c)	{
    		min = a; max = b; array = c;
    	}
    	public void run ()	{
    		System.arraycopy(array, min, _data, min+_pos, max-min);
    		if ((max-min) != 0) boolGlobal = true;
    		/**for (int i=min; i<max; i++) {
                _data[i+posInicial] = array[i];
                boolGlobal = true;
            }*/
    	}
    }
    /** {@inheritDoc} */
    public boolean addAllPar (TCharCollection collection, int numProcs)	{
    	boolGlobal = false;
    	ensureCapacity (_pos+collection.size());
    	char[] array = collection.toArray(new char[collection.size()]);
    	int numElemProc = array.length/numProcs;
    	threads = new Thread[numProcs];
    	for (int i=0; i<numProcs; i++)	{
    		int numMax = (i+1) * numElemProc;
    		if ((i+1) == numProcs)	numMax = array.length;
    		threads[i] = new Thread (new AddAll2 (i*numElemProc, numMax, array));
    		threads[i].start();
    	}
    	for (int i=0; i<numProcs; i++)	{
    		try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    	_pos += array.length;
    	return boolGlobal;
    }
    public boolean addAll ( TCharCollection collection ) {
        boolean changed = false;
        TCharIterator iter = collection.iterator();
        while ( iter.hasNext() ) {
            char element = iter.next();
            if ( add( element ) ) {
                changed = true;
            }
        }
        return changed;
    }
    public boolean addAll2( TCharCollection collection ) {
        boolean changed = false;
        char[] chars = collection.toArray(new char[collection.size()]);
        char[] tmp = new char[_pos + collection.size()];
        System.arraycopy(_data, 0, tmp, 0, _pos);
        System.arraycopy(chars, 0, tmp, _pos, collection.size());
        _pos += collection.size();
        _data = tmp;
        return changed;
    }


    /** {@inheritDoc} */
    public boolean addAllPar (char[] array, int numProcs)	{
    	boolGlobal = false;
    	ensureCapacity (_pos+array.length);
    	int numElemProc = array.length/numProcs;
    	threads = new Thread[numProcs];
    	for (int i=0; i<numProcs; i++)	{
    		int numMax = (i+1) * numElemProc;
    		if ((i+1) == numProcs)	numMax = array.length;
    		threads[i] = new Thread (new AddAll2 (i*numElemProc, numMax, array));
    		threads[i].start();
    	}
    	for (int i=0; i<numProcs; i++)	{
    		try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    	_pos += array.length;
    	return boolGlobal;
    }
    public boolean addAll( char[] array ) {
        boolean changed = false;
        for ( char element : array ) {
            if ( add( element ) ) {
                changed = true;
            }
        }
        return changed;
    }
    public boolean addAll2( char[] array ) {
        int numNew = array.length;
        ensureCapacity (_pos + numNew);
        System.arraycopy(array, 0, _data, _pos, numNew);
        _pos += numNew;
        return numNew != 0;
    }
    
    private class RetainAll1 implements Runnable	{
    	int min, max, idProc;
    	Collection<?> collection;
    	RetainAll1 (int a, int b, Collection<?> c, int d)	{
    		min = a; max = b; collection = c; idProc = d;
    	}
    	public void run ()	{
    		char[] arrayLocal = new char[_data.length];
    		int j=0;
    		for (int i=min; i<max; i++) {
    	        if (collection.contains( Character.valueOf (_data[i]) ) ) {
    	        	arrayLocal[j++] = _data[i];
    		        boolGlobal = true;
    	        }
    	    }
    		if (idProc > 0)	{
    			try {
					threads[idProc-1].join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
    		}
    		System.arraycopy(arrayLocal, 0, charsGlobal, intGlobal, j);
    		intGlobal += j;
    	}
    }
    /** {@inheritDoc} */
    @SuppressWarnings({"SuspiciousMethodCalls"})
    public boolean retainAllPar (Collection<?> collection, int numProcs)	{
    	boolGlobal = false;
    	intsGlobal = new int[_data.length];
    	intGlobal = 0;
    	int numElemProc = _pos/numProcs;
    	threads = new Thread[numProcs];
    	for (int i=0; i<numProcs; i++)	{
    		int numMax = (i+1) * numElemProc;
    		if ((i+1) == numProcs)	numMax = _pos;
    		threads[i] = new Thread (new RetainAll1 (i*numElemProc, numMax, collection, i));
    		threads[i].start();
    	}
    	try {
			threads[numProcs-1].join();
		} catch (InterruptedException e) {
			e.printStackTrace();
    	}
    	_data = charsGlobal;
    	_pos = intGlobal;
    	return boolGlobal;
    }
    public boolean retainAll( Collection<?> collection ) {
        boolean modified = false;
	    TCharIterator iter = iterator();
	    while ( iter.hasNext() ) {
	        if ( ! collection.contains( Character.valueOf ( iter.next() ) ) ) {
		        iter.remove();
		        modified = true;
	        }
	    }
	    return modified;
    }


    private class RetainAll2 implements Runnable	{
    	int min, max;
    	TCharCollection collection;
    	RetainAll2 (int a, int b, TCharCollection c)	{
    		min = a; max = b; collection = c;
    	}
    	public void run ()	{
    		for (int i=min; i<max; i++) {
    	        if ( ! collection.contains ((_data[i]) ) ) {
    		        //removeAt (i);
    	        	synchronized (this)	{
    	        		System.arraycopy(_data, i+1, _data, i, _pos-i-1);
    	        		_pos--;
    	        	}
    		        boolGlobal = true;
    	        }
    	    }
    	}
    }
    /** {@inheritDoc} */
    @SuppressWarnings({"SuspiciousMethodCalls"})
    public boolean retainAllPar (TCharCollection collection, int numProcs)	{
    	if (this == collection)	return false;
    	boolGlobal = false;
    	int numElemProc = _pos/numProcs;
    	threads = new Thread[numProcs];
    	for (int i=0; i<numProcs; i++)	{
    		int numMax = (i+1) * numElemProc;
    		if ((i+1) == numProcs)	numMax = _pos;
    		threads[i] = new Thread (new RetainAll2 (i*numElemProc, numMax, collection));
    		threads[i].start();
    	}
    	for (int i=0; i<numProcs; i++)	{
    		try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    	return boolGlobal;
    }
    public boolean retainAll( TCharCollection collection ) {
        if ( this == collection ) {
            return false;
        }
        boolean modified = false;
	    TCharIterator iter = iterator();
	    while ( iter.hasNext() ) {
	        if ( ! collection.contains( iter.next() ) ) {
		        iter.remove();
		        modified = true;
	        }
	    }
	    return modified;
    }

    private class RetainAll3 implements Runnable	{
    	int min, max;
    	char[] array;
    	RetainAll3 (int a, int b, char[] c)	{
    		min = a; max = b; array = c;
    	}
    	public void run ()	{
    		for (int i=min; i<max; i++) {
                if ( Arrays.binarySearch( array, _data[i] ) < 0 ) {
                	synchronized (this)	{
                		remove( i, 1 );
                	}
                    boolGlobal = true;
                }
            }
    	}
    }
    /** {@inheritDoc} */
    public boolean retainAllPar (char[] array, int numProcs)	{
    	boolGlobal = false;
    	Arrays.sort (array);
    	int numElemProc = _pos/numProcs;
    	threads = new Thread[numProcs];
    	for (int i=0; i<numProcs; i++)	{
    		int numMax = (i+1) * numElemProc;
    		if ((i+1) == numProcs)	numMax = _pos;
    		threads[i] = new Thread (new RetainAll3 (i*numElemProc, numMax, array));
    		threads[i].start();
    	}
    	for (int i=0; i<numProcs; i++)	{
    		try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    	return boolGlobal;
    }
    public boolean retainAll( char[] array ) {
        boolean changed = false;
        Arrays.sort( array );
        char[] data = _data;

        for ( int i = _pos; i-- > 0; ) {
            if ( Arrays.binarySearch( array, data[i] ) < 0 ) {
                remove( i, 1 );
                changed = true;
            }
        }
        return changed;
    }

    private class RemoveAll1 implements Runnable	{
    	int min, max, idProc;
    	Collection<?> collection;
    	RemoveAll1 (int a, int b, Collection<?> c, int d)	{
    		min = a; max = b; collection = c; idProc = d;
    	}
    	public void run ()	{
    		char[] arrayLocal = new char[_data.length];
    		int j=0;
    		for (int i=min; i<max; i++) {
    	        if (!collection.contains( Character.valueOf (_data[i]) ) ) {
    	        	arrayLocal[j++] = _data[i];
    		        boolGlobal = true;
    	        }
    	    }
    		if (idProc > 0)	{
    			try {
					threads[idProc-1].join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
    		}
    		System.arraycopy(arrayLocal, 0, charsGlobal, intGlobal, j);
    		intGlobal += j;
    	}
    }
    /** {@inheritDoc} */
    @SuppressWarnings({"SuspiciousMethodCalls"})
    public boolean removeAllPar (Collection<?> collection, int numProcs)	{
    	boolGlobal = false;
    	charsGlobal = new char[_data.length];
    	intGlobal = 0;
    	int numElemProc = _pos/numProcs;
    	threads = new Thread[numProcs];
    	for (int i=0; i<numProcs; i++)	{
    		int numMax = (i+1) * numElemProc;
    		if ((i+1) == numProcs)	numMax = _pos;
    		threads[i] = new Thread (new RemoveAll1 (i*numElemProc, numMax, collection, i));
    		threads[i].start();
    	}
    	try {
			threads[numProcs-1].join();
		} catch (InterruptedException e) {
			e.printStackTrace();
    	}
    	_data = charsGlobal;
    	_pos = intGlobal;
    	return boolGlobal;
    }
    public boolean removeAll( Collection<?> collection ) {
        boolean changed = false;
        for ( Object element : collection ) {
            if ( element instanceof Character ) {
                char c = ( ( Character ) element ).charValue();
                if ( remove( c ) ) {
                    changed = true;
                }
            }
        }
        return changed;
    }
    public boolean removeAllPar2( Collection<?> collection, int numProcs) {
        boolean changed = false;
        for ( Object element : collection ) {
            if ( element instanceof Character ) {
                char c = ( ( Character ) element ).charValue();
                if ( removePar( c, numProcs) ) {
                    changed = true;
                }
            }
        }
        return changed;
    }


    /** {@inheritDoc} */
    public boolean removeAll( TCharCollection collection ) {
        if ( collection == this ) {
            clear();
            return true;
        }
        boolean changed = false;
        TCharIterator iter = collection.iterator();
        while ( iter.hasNext() ) {
            char element = iter.next();
            if ( remove( element ) ) {
                changed = true;
            }
        }
        return changed;
    }


    /** {@inheritDoc} */
    public boolean removeAll( char[] array ) {
        boolean changed = false;
        for ( int i = array.length; i-- > 0; ) {
            if ( remove(array[i]) ) {
                changed = true;
            }
        }
        return changed;
    }
    
    public boolean removeIf(TCharProcedure filter) {
        Objects.requireNonNull(filter);
        // figure out which elements are to be removed
        // any exception thrown from the filter predicate at this stage
        // will leave the collection unmodified
        int removeCount = 0;
        final BitSet removeSet = new BitSet(_pos);
        //final int expectedModCount = modCount;
        final int size = _pos;
        for (int i=0; /**modCount == expectedModCount &&*/ i < size; i++) {
            @SuppressWarnings("unchecked")
            final Character element = (Character) _data[i];
            if (filter.execute(element)) {
                removeSet.set(i);
                removeCount++;
            }
        }
        /**if (modCount != expectedModCount) {
            throw new ConcurrentModificationException();
        }*/

        // shift surviving elements left over the spaces left by removed elements
        final boolean anyToRemove = removeCount > 0;
        if (anyToRemove) {
            final int newSize = size - removeCount;
            for (int i=0, j=0; (i < size) && (j < newSize); i++, j++) {
                i = removeSet.nextClearBit(i);
                _data[j] = _data[i];
            }
            /**for (int k=newSize; k < size; k++) {
                _data[k] = null;  // Let gc do its work
            }*/
            this._pos = newSize;
            /**if (modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }*/
            //modCount++;
        }

        return anyToRemove;
    }

    private class TransformValues1 implements Runnable	{
    	int min, max;
    	TCharFunction function;
    	TransformValues1 (int a, int b, TCharFunction c)	{
    		min = a; max = b;
    	}
    	public void run ()	{
    		for (int i=min; i<max; i++) {
                _data[ i ] = function.execute( _data[ i ] );
            }
    	}
    }
    /** {@inheritDoc} */
    public void transformValuesPar (TCharFunction function, int numProcs)	{
    	int numElemProc = _pos/numProcs;
    	threads = new Thread[numProcs];
    	for (int i=0; i<numProcs; i++)	{
    		int numMax = (i+1) * numElemProc;
    		if ((i+1) == numProcs)	numMax = _pos;
    		threads[i] = new Thread (new TransformValues1 (i*numElemProc, numMax, function));
    	}
    	for (int i=0; i<numProcs; i++)	{
    		try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    }
    public void transformValues( TCharFunction function ) {
        for ( int i = _pos; i-- > 0; ) {
            _data[ i ] = function.execute( _data[ i ] );
        }
    }

    /** {@inheritDoc} */
    public void reversePar(int numProcs) {
        reversePar( 0, _pos, numProcs);
    }
    public void reverse() {
        reverse( 0, _pos );
    }

    private class Reverse1 implements Runnable	{
    	int min, max;
    	Reverse1 (int a, int b)	{
    		min = a; max = b;
    	}
    	public void run ()	{
    		int j=_pos-min-1;
    		for (int i = min; i<max; i++) {
                swap( i, j-- );
            }
    		/**for (int i=min; i<max; i++)	{
    			intsGlobal[_pos-i-1] = _data[i];
    		}*/
    	}
    }
    /** {@inheritDoc} */
    public void reversePar (int from, int to, int numProcs)	{
    	if (from == to)	return;
    	if (from > to)	throw new IllegalArgumentException ("from cannot be greater than to");
    	charsGlobal = new char[to-from];
    	//int numElemProc = (to-from)/numProcs;
    	int numElemProc = (to-from)/(numProcs*2);
    	threads = new Thread[numProcs];
    	for (int i=0; i<numProcs; i++)	{
    		int numMax = (i+1) * numElemProc + from;
    		if ((i+1) == numProcs)	numMax = to/2;
    		threads[i] = new Thread (new Reverse1 (i*numElemProc+from, numMax));
    		threads[i].start();
    	}
    	for (int i=0; i<numProcs; i++)	{
    		try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    	//_data = intsGlobal;
    }
    public void reverse( int from, int to ) {
        if ( from == to ) {
            return;             // nothing to do
        }
        if ( from > to ) {
            throw new IllegalArgumentException( "from cannot be greater than to" );
        }
        for ( int i = from, j = to - 1; i < j; i++, j-- ) {
            swap( i, j );
        }
    }

    private class Shuffle1 implements Runnable	{
    	int min, max;
    	Random rand;
    	Shuffle1 (int a, int b, Random c)	{
    		min = a; max = b; rand = c;
    	}
    	public void run ()	{
    		System.out.println ("Límites: " + min + " - " + max);
    		for (int i=min; i<max; i++)	{
    			if (i>0)
    				synchronized (this)	{
    					swap (i, rand.nextInt(i));
    				}
    		}
    	}
    }
    /** {@inheritDoc} */
    public void shufflePar (Random rand, int numProcs)	{
    	int numElemProc = _pos/numProcs;
    	threads = new Thread[numProcs];
    	for (int i=0; i<numProcs; i++)	{
    		int numMax = (i+1) * numElemProc;
    		if ((i+1) == numProcs)	numMax = _pos;
    		threads[i] = new Thread (new Shuffle1 (i*numElemProc, numMax, rand));
    		threads[i].start();
    	}
    	for (int i=0; i<numProcs; i++)	{
    		try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    }
    public void shuffle( Random rand ) {
        for ( int i = _pos; i-- > 1; ) {
            swap( i, rand.nextInt( i ) );
        }
    }


    /**
     * Swap the values at offsets <tt>i</tt> and <tt>j</tt>.
     *
     * @param i an offset into the data array
     * @param j an offset into the data array
     */
    private void swap( int i, int j ) {
        char tmp = _data[ i ];
        _data[ i ] = _data[ j ];
        _data[ j ] = tmp;
    }


    // copying

    private class SubList1 implements Runnable	{
    	int min, max, numProc;
    	SubList1 (int a, int b, int c)	{
    		min = a; max = b; numProc = c;
    	}
    	public void run ()	{
    		char[] chars = new char[max-min];
    		int j = 0;
    		for (int i=min; i<max; i++)	{
    			chars[j] = _data[i]; 
    		}
   			if (numProc > 0)	try {
				threads[numProc-1].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
   			System.arraycopy(chars, 0, charsGlobal, intGlobal, max-min);
   			intGlobal += (max-min);
    	}
    }
    /** {@inheritDoc} */
    public TCharList subListPar( int begin, int end, int numProcs) {
    	if ( end < begin ) {
			throw new IllegalArgumentException( "end index " + end +
				" greater than begin index " + begin );
		}
		if ( begin < 0 ) {
			throw new IndexOutOfBoundsException( "begin index can not be < 0" );
		}
		if ( end > _data.length ) {
			throw new IndexOutOfBoundsException( "end index < " + _data.length );
		}
        TCharArrayList list = new TCharArrayList( end - begin );
        charsGlobal = new char[end-begin];
        intGlobal = 0;
        int numElemProc = (end-begin)/numProcs;
    	threads = new Thread[numProcs];
        for (int i=0; i<numProcs; i++)	{
        	int numMax = (i+1) * numElemProc + begin;
        	if ((i+1) == numProcs)	numMax = end; 
        	threads[i] = new Thread (new SubList1 (i*numElemProc+begin, numMax, i));
        	threads[i].start();
        }
        try {
			threads[numProcs-1].join();
		} catch (InterruptedException e) {
			e.printStackTrace();
        }
        list._data = charsGlobal;
        return list;
    }
    public TCharList subList( int begin, int end ) {
    	if ( end < begin ) {
			throw new IllegalArgumentException( "end index " + end +
				" greater than begin index " + begin );
		}
		if ( begin < 0 ) {
			throw new IndexOutOfBoundsException( "begin index can not be < 0" );
		}
		if ( end > _data.length ) {
			throw new IndexOutOfBoundsException( "end index < " + _data.length );
		}
        TCharArrayList list = new TCharArrayList( end - begin );
        for ( int i = begin; i < end; i++ ) {
        	list.add( _data[ i ] );
        }
        return list;
    }
    public TCharList subList2( int begin, int end ) {
    	if ( end < begin ) {
			throw new IllegalArgumentException( "end index " + end +
				" greater than begin index " + begin );
		}
		if ( begin < 0 ) {
			throw new IndexOutOfBoundsException( "begin index can not be < 0" );
		}
		if ( end > _data.length ) {
			throw new IndexOutOfBoundsException( "end index < " + _data.length );
		}
		/**char[] arrayLocal = new char[end-begin];
		System.arraycopy(_data, begin, arrayLocal, 0, end-begin);
        return new TCharArrayList (arrayLocal);*/
		return new TCharArrayList (_data, begin, end);
    }


    /** {@inheritDoc} */
    public char[] toArrayPar(int numProcs) {
        return toArrayPar( 0, _pos, numProcs);
    }
    public char[] toArray() {
        return toArray( 0, _pos );
    }


    /** {@inheritDoc} */
    public char[] toArrayPar( int offset, int len, int numProcs) {
        char[] rv = new char[ len ];
        toArrayPar( rv, offset, len, numProcs);
        return rv;
    }
    public char[] toArray( int offset, int len ) {
        char[] rv = new char[ len ];
        toArray( rv, offset, len );
        return rv;
    }


    /** {@inheritDoc} */
    public char[] toArrayPar( char[] dest, int numProcs) {
        int len = dest.length;
        if ( dest.length > _pos ) {
            len = _pos;
            dest[len] = no_entry_value;
        }
        toArrayPar( dest, 0, len, numProcs);
        return dest;
    }
    public char[] toArray( char[] dest ) {
        int len = dest.length;
        if ( dest.length > _pos ) {
            len = _pos;
            dest[len] = no_entry_value;
        }
        toArray( dest, 0, len );
        return dest;
    }

    private class ToArray1 implements Runnable	{
    	int min, max, numThread;
    	ToArray1 (int a, int b, int c)	{
    		min = a; max = b; numThread = c;
    	}
    	public void run ()	{
    		int j=0;
    		char[] charsLocal = new char[max-min];
    		for (int i=min; i<max; i++)	{
    			charsLocal[j++] = _data[i];
    		}
    		try {
    			if (numThread>0)
				threads[numThread-1].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    		System.arraycopy(charsLocal, 0, charsGlobal, intGlobal, j);
    	}
    }
    private class ToArray2 implements Runnable	{
    	int min, max;
    	ToArray2 (int a, int b)	{
    		min = a; max = b;
    	}
    	public void run ()	{
    		System.arraycopy(_data, min, charsGlobal, min+intGlobal, max-min);
    		/**for (int i=min; i<max; i++)	{
    			charsGlobal[i-intGlobal] = _data[i];
    		}*/
    	}
    }
    /** {@inheritDoc} */
    public char[] toArrayPar (char[] dest, int offset, int len, int numProcs)	{
    	if (len == 0)	{
    		return dest;
    	}
    	if (offset < 0 || offset >= _pos)	{
    		throw new ArrayIndexOutOfBoundsException (offset);
    	}
    	intGlobal = offset;
    	charsGlobal = dest;
    	int numElemProc = len/numProcs;
    	threads = new Thread[numProcs];
    	for (int i=0; i<numProcs; i++)	{
    		int numMax = (i+1) * numElemProc;
    		if ((i+1) == numProcs)	numMax = len;
    		threads[i] = new Thread (new ToArray2 (i*numElemProc, numMax));
    		threads[i].start();
    	}
    	for (int i=0; i<numProcs; i++)	{
    		try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    	return charsGlobal;
    }
    public char[] toArray( char[] dest, int offset, int len ) {
        if ( len == 0 ) {
            return dest;             // nothing to copy
        }
        if ( offset < 0 || offset >= _pos ) {
            throw new ArrayIndexOutOfBoundsException( offset );
        }
        System.arraycopy( _data, offset, dest, 0, len );
        return dest;
    }


    /** {@inheritDoc} */
    public char[] toArrayPar (char[] dest, int source_pos, int dest_pos, int len, int numProcs)	{
    	if (len == 0)	return dest;
    	if (source_pos < 0 || source_pos >= _pos)	{
    		throw new ArrayIndexOutOfBoundsException (source_pos);
    	}
    	charsGlobal = new char[dest_pos - source_pos];
    	intGlobal = source_pos;
    	int numElemProc = (dest_pos - source_pos)/numProcs;
    	for (int i=0; i<numProcs; i++)	{
    		int numMax = (i+1) * numElemProc + source_pos;
    		if ((i+1) == numProcs)	numMax = dest_pos;
    		threads[i] = new Thread (new ToArray2 (i*numElemProc+source_pos, numMax));
    	}
    	for (int i=0; i<numProcs; i++)	{
    		try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    	return charsGlobal;
    }
    public char[] toArray( char[] dest, int source_pos, int dest_pos, int len ) {
        if ( len == 0 ) {
            return dest;             // nothing to copy
        }
        if ( source_pos < 0 || source_pos >= _pos ) {
            throw new ArrayIndexOutOfBoundsException( source_pos );
        }
        System.arraycopy( _data, source_pos, dest, dest_pos, len );
        return dest;
    }


    // comparing
    private class Equals1 implements Runnable	{
    	int min, max;
    	TCharArrayList that;
    	Equals1 (int a, int b, TCharArrayList c)	{
    		min = a; max = b; that = c;
    	}
    	public void run ()	{
    		for (int i=min; i<max; i++) {
    			if (!boolGlobal)	return;
                if (_data[i] != that._data[i]) {
                    boolGlobal = false;
                    return;
                }
            }
    	}
    }
    /** {@inheritDoc} */
    public boolean equalsPar (Object other, int numProcs)	{
    	if (other == this)	return true;
    	else if (other instanceof TCharArrayList)	{
    		TCharArrayList that = (TCharArrayList) other;
    		if (that.size() != this.size())	{
    			return false;
    		}
    		else	{
    			boolGlobal = true;
    			int numElemProc = _pos/numProcs;
    	    	threads = new Thread[numProcs];
    			for (int i=0; i<numProcs; i++)	{
    				int numMax = (i+1) * numElemProc;
    				if ((i+1) == numProcs)	numMax = _pos;
    				threads[i] = new Thread (new Equals1 (i*numElemProc, numMax, that));
    				threads[i].start();
    			}
    			for (int i=0; i<numProcs; i++)	{
    				try {
						threads[i].join();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
    			}
    			return boolGlobal;
    		}
    	}
    	else return false;
    }
    @Override
    public boolean equals( Object other ) {
        if ( other == this ) {
            return true;
        }
        else if ( other instanceof TCharArrayList ) {
            TCharArrayList that = ( TCharArrayList )other;
            if ( that.size() != this.size() ) return false;
            else {
                for ( int i = _pos; i-- > 0; ) {
                    if ( this._data[ i ] != that._data[ i ] ) {
                        return false;
                    }
                }
                return true;
            }
        }
        else return false;
    }
    public boolean equals2(Object otherObj) { //delta
    	// overridden for performance only.
    	if (! (otherObj instanceof TCharArrayList)) return super.equals(otherObj);
    	if (this==otherObj) return true;
    	if (otherObj==null) return false;
    	TCharArrayList other = (TCharArrayList) otherObj;
    	if (size()!=other.size()) return false;

    	char[] theElements = _data;
    	char[] otherElements = other._data;
    	for (int i=size(); --i >= 0; ) {
    	    if (theElements[i] != otherElements[i]) return false;
    	}
    	return true;
    }

    private class HashCode1 implements Runnable	{
    	int min, max;
    	HashCode1 (int a, int b)	{
    		min = a; max = b;
    	}
    	public void run ()	{
    		int hash = 0;
    		for (int i=min; i<max; i++)	{
    			hash += HashFunctions.hash(_data[i]);
    		}
    		synchronized (this)	{
    			intGlobal += hash;
    		}
    	}
    }
    /** {@inheritDoc} */
    public int hashCodePar(int numProcs)	{
    	intGlobal = 0;
    	int numElemProc = _pos/numProcs;
    	threads = new Thread[numProcs];
    	for (int i=0; i<numProcs; i++)	{
    		int numMax = (i+1) * numElemProc;
    		if ((i+1) == numProcs)	numMax = _pos;
    		threads[i] = new Thread (new HashCode1 (i*numElemProc, numMax));
    		threads[i].start();
    	}
    	for (int i=0; i<numProcs; i++)	{
    		try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    	return intGlobal;
    }
    @Override
    public int hashCode() {
        int h = 0;
        for ( int i = _pos; i-- > 0; ) {
            h += HashFunctions.hash( _data[ i ] );
        }
        return h;
    }


    // procedures
    private class ForEach1 implements Runnable	{
    	int min, max;
    	TCharProcedure procedure;
    	ForEach1 (int a, int b, TCharProcedure c)	{
    		min = a; max = b; procedure = c;
    	}
    	public void run ()	{
    		for (int i=min; i<max; i++) {
    			if (!boolGlobal)	return;
                if ( !procedure.execute(_data[i])) {
                    boolGlobal = false;
                    return;
                }
            }
    	}
    }
    /** {@inheritDoc} */
    public boolean forEachPar (TCharProcedure procedure, int numProcs)	{
    	boolGlobal = true;
    	int numElemProc = _pos/numProcs;
    	threads = new Thread[numProcs];
    	for (int i=0; i<numProcs; i++)	{
    		int numMax = (i+1) * numElemProc;
    		if ((i+1) == numProcs)	numMax = _pos;
    		threads[i] = new Thread (new ForEach1 (i*numElemProc, numMax, procedure));
    		threads[i].start();
    	}
    	for (int i=0; i<numProcs; i++)	{
    		try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    	return boolGlobal;
    }
    public boolean forEach( TCharProcedure procedure ) {
        for ( int i = 0; i < _pos; i++ ) {
            if ( !procedure.execute( _data[ i ] ) ) {
                return false;
            }
        }
        return true;
    }

    private class ForEachDescending1 implements Runnable	{
    	int min, max;
    	TCharProcedure procedure;
    	ForEachDescending1 (int a, int b, TCharProcedure c)	{
    		min = a; max = b; procedure = c;
    	}
    	public void run ()	{
    		for (int i=max-1; i>=min; i--) {
    			if (!boolGlobal)	return;
                if ( !procedure.execute( _data[ i ] ) ) {
                    boolGlobal = false;
                    return;
                }
            }
    	}
    }
    /** {@inheritDoc} */
    public boolean forEachDescendingPar (TCharProcedure procedure, int numProcs)	{
    	boolGlobal = true;
    	int numElemProc = _pos/numProcs;
    	threads = new Thread[numProcs];
    	for (int i=0; i<numProcs; i++)	{
    		int numMax = (i+1) * numElemProc;
    		if ((i+1) == numProcs)	numMax = _pos;
    		threads[i] = new Thread (new ForEachDescending1 (i*numElemProc, numMax, procedure));
    		threads[i].start();
    	}
    	for (int i=0; i<numProcs; i++)	{
    		try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    	return boolGlobal;
    }
    public boolean forEachDescending( TCharProcedure procedure ) {
        for ( int i = _pos; i-- > 0; ) {
            if ( !procedure.execute( _data[ i ] ) ) {
                return false;
            }
        }
        return true;
    }


    // sorting

    /** {@inheritDoc} */
    public void sort() {
        Arrays.sort( _data, 0, _pos );
    }


    /** {@inheritDoc} */
    public void sort( int fromIndex, int toIndex ) {
        Arrays.sort( _data, fromIndex, toIndex );
    }


    // filling

    /** {@inheritDoc} */
    public void fill( char val ) {
        Arrays.fill( _data, 0, _pos, val );
    }


    /** {@inheritDoc} */
    public void fill( int fromIndex, int toIndex, char val ) {
        if ( toIndex > _pos ) {
          ensureCapacity( toIndex );
          _pos = toIndex;
        }
        Arrays.fill( _data, fromIndex, toIndex, val );
    }


    // searching

    /** {@inheritDoc} */
    public int binarySearch( char value ) {
        return binarySearch( value, 0, _pos );
    }


    
    /** {@inheritDoc} */
    public int binarySearch(char value, int fromIndex, int toIndex) {
        if ( fromIndex < 0 ) {
            throw new ArrayIndexOutOfBoundsException( fromIndex );
        }
        if ( toIndex > _pos ) {
            throw new ArrayIndexOutOfBoundsException( toIndex );
        }

        int low = fromIndex;
        int high = toIndex - 1;

        while ( low <= high ) {
            int mid = ( low + high ) >>> 1;
            char midVal = _data[ mid ];

            if ( midVal < value ) {
                low = mid + 1;
            }
            else if ( midVal > value ) {
                high = mid - 1;
            }
            else {
                return mid; // value found
            }
        }
        return -( low + 1 );  // value not found.
    }


    /** {@inheritDoc} */
    public int indexOfPar( char value, int numProcs) {
        return indexOfPar( 0, value, numProcs);
    }
    public int indexOf( char value ) {
        return indexOf( 0, value );
    }

    private class IndexOf1 implements Runnable	{
    	int min, max;
    	char value;
    	IndexOf1 (int a, int b, char c)	{
    		min = a; max = b; value = c;
    	}
    	public void run ()	{
    		for ( int i = min; i < max; i++ ) {
    			if (intGlobal != -1)	return;
                if ( _data[ i ] == value ) {
                    intGlobal = i;
                    return;
                }
            }
    	}
    }
    /** {@inheritDoc} */
    public int indexOfPar (int offset, char value, int numProcs)	{
    	intGlobal = -1;
    	int numElemProc = (_pos-offset)/numProcs;
    	threads = new Thread[numProcs];
    	for (int i=0; i<numProcs; i++)	{
    		int numMax = (i+1) * numElemProc + offset;
    		if ((i+1) == numProcs)	numMax = _pos;
    		threads[i] = new Thread (new IndexOf1 (i*numElemProc + offset, numMax, value));
    		threads[i].start();
    	}
    	for (int i=0; i<numProcs; i++)	{
    		try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    	return intGlobal;
    }
    public int indexOf( int offset, char value ) {
        for ( int i = offset; i < _pos; i++ ) {
            if ( _data[ i ] == value ) {
                return i;
            }
        }
        return -1;
    }


    /** {@inheritDoc} */
    public int lastIndexOfPar( char value, int numProcs) {
        return lastIndexOfPar( _pos, value, numProcs);
    }
    public int lastIndexOf( char value ) {
        return lastIndexOf( _pos, value );
    }

    private class LastIndexOf1 implements Runnable	{
    	int min, max;
    	char value;
    	LastIndexOf1 (int a, int b, char c)	{
    		min = a; max = b; value = c;
    	}
    	public void run ()	{
    		for (int i=max-1; i>=min; i--) {
    			if (intGlobal != -1)	return;
                if (_data[i] == value) {
                    intGlobal = i;
                    return;
                }
            }
    	}
    }
    /** {@inheritDoc} */
    public int lastIndexOfPar (int offset, char value, int numProcs)	{
    	intGlobal = -1;
    	int numElemProc = offset/numProcs;
    	threads = new Thread[numProcs];
    	for (int i=0; i<numProcs; i++)	{
    		int numMax = (i+1) * numElemProc;
    		if ((i+1) == numProcs)	numMax = offset;
    		threads[i] = new Thread (new LastIndexOf1 (i*numElemProc, numMax, value));
    		threads[i].start();
    	}
    	for (int i=0; i<numProcs; i++)	{
    		try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    	return intGlobal;
    }
    public int lastIndexOf( int offset, char value ) {
        for ( int i = offset; i-- > 0; ) {
            if ( _data[ i ] == value ) {
                return i;
            }
        }
        return -1;
    }


    /** {@inheritDoc} */
    public boolean containsPar ( char value, int numProcs) {
        return indexOfPar( value, numProcs) >= 0;
    }
    public boolean contains( char value ) {
        return lastIndexOf( value ) >= 0;
    }

    private class Grep1 implements Runnable	{
    	int min, max;
    	TCharProcedure condition;
    	Grep1 (int a, int b, TCharProcedure c)	{
    		min = a; max = b; condition = c;
    	}
    	public void run ()	{
    		char[] chars = new char[_pos];
    		int j = 0;
    		for (int i=min; i<max; i++) {
                if ( condition.execute( _data[ i ] ) ) {
                    chars[j] = _data[i];
                    j++;
                }
            }
    		synchronized (this)	{
    			System.arraycopy(chars, 0, charsGlobal, intGlobal, j);
    			intGlobal += j;
    		}
    	}
    }
    /** {@inheritDoc} */
    public TCharList grepPar (TCharProcedure condition, int numProcs)	{
    	TCharArrayList list = new TCharArrayList();
    	charsGlobal = new char[_pos];
    	intGlobal = 0;
    	int numElemProc = _pos/numProcs;
    	threads = new Thread[numProcs];
    	for (int i=0; i<numProcs; i++)	{
    		int numMax = (i+1) * numElemProc;
    		if ((i+1) == numElemProc)	numMax = _pos;
    		threads[i] = new Thread (new Grep1 (i*numElemProc, numMax, condition));
    		threads[i].start();
    	}
    	for (int i=0; i<numProcs; i++)	{
    		try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    	list.addAll(charsGlobal);
    	return list;
    }
    public TCharList grep( TCharProcedure condition ) {
        TCharArrayList list = new TCharArrayList();
        for ( int i = 0; i < _pos; i++ ) {
            if ( condition.execute( _data[ i ] ) ) {
                list.add( _data[ i ] );
            }
        }
        return list;
    }


    /** {@inheritDoc} */
    private class InverseGrep1 implements Runnable	{
    	int min, max;
    	TCharProcedure condition;
    	InverseGrep1 (int a, int b, TCharProcedure c)	{
    		min = a; max = b; condition = c;
    	}
    	public void run ()	{
    		char[] chars = new char[_pos];
    		int j = 0;
    		for (int i=max-1; i>=min; i--) {
                if ( condition.execute( _data[ i ] ) ) {
                    chars[j] = _data[i];
                    j++;
                }
            }
    		synchronized (this)	{
    			System.arraycopy(chars, 0, charsGlobal, intGlobal, j);
    			intGlobal += j;
    		}
    	}
    }
    /** {@inheritDoc} */
    public TCharList inverseGrepPar (TCharProcedure condition, int numProcs)	{
    	TCharArrayList list = new TCharArrayList();
    	charsGlobal = new char[_pos];
    	intGlobal = 0;
    	int numElemProc = _pos/numProcs;
    	threads = new Thread[numProcs];
    	for (int i=0; i<numProcs; i++)	{
    		int numMax = (i+1) * numElemProc;
    		if ((i+1) == numElemProc)	numMax = _pos;
    		threads[i] = new Thread (new InverseGrep1 (i*numElemProc, numMax, condition));
    		threads[i].start();
    	}
    	for (int i=0; i<numProcs; i++)	{
    		try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    	list.addAll(charsGlobal);
    	return list;
    }
    public TCharList inverseGrep( TCharProcedure condition ) {
        TCharArrayList list = new TCharArrayList();
        for ( int i = 0; i < _pos; i++ ) {
            if ( !condition.execute( _data[ i ] ) ) {
                list.add( _data[ i ] );
            }
        }
        return list;
    }

    private class Max1 implements Runnable	{
    	int min, max;
    	Max1 (int a, int b)	{
    		min = a; max = b;
    	}
    	public void run ()	{
    		char maximum = charGlobal;
    		for (int i=min; i<max; i++) {
            	if ( _data[ i ] > maximum ) {
            		maximum = _data[ i ];
            	}
            }
    		synchronized (this)	{
    			if (maximum > intGlobal)	charGlobal = maximum;
    		}
    	}
    }
    /** {@inheritDoc} */
    public int maxPar(int numProcs)	{
    	if (size() == 0)	{
    		throw new IllegalStateException ("cannot find maximum of an empty list");
    	}
    	charGlobal = Character.MIN_VALUE;
    	int numElemProc = _pos/numProcs;
    	threads = new Thread[numProcs];
    	for (int i=0; i<numProcs; i++)	{
    		int numMax = (i+1) * numElemProc;
    		if ((i+1) == numProcs)	numMax = _pos;
    		threads[i] = new Thread (new Max1 (i*numElemProc, numMax));
    		threads[i].start();
    	}
    	for (int i=0; i<numProcs; i++)	{
    		try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    	return intGlobal;
    }
    public char max() {
        if ( size() == 0 ) {
            throw new IllegalStateException("cannot find maximum of an empty list");
        }
        char max = Character.MIN_VALUE;
        for ( int i = 0; i < _pos; i++ ) {
        	if ( _data[ i ] > max ) {
        		max = _data[ i ];
        	}
        }
        return max;
    }


    private class Min1 implements Runnable	{
    	int min, max;
    	Min1 (int a, int b)	{
    		min = a; max = b;
    	}
    	public void run ()	{
    		char minimum = charGlobal;
    		for (int i=min; i<max; i++) {
            	if ( _data[ i ] < minimum ) {
            		minimum = _data[ i ];
            	}
            }
    		synchronized (this)	{
    			if (minimum < charGlobal)	charGlobal = minimum;
    		}
    	}
    }
    /** {@inheritDoc} */
    public char minPar(int numProcs)	{
    	if (size() == 0)	{
    		throw new IllegalStateException ("cannot find maximum of an empty list");
    	}
    	charGlobal = Character.MAX_VALUE;
    	int numElemProc = _pos/numProcs;
    	threads = new Thread[numProcs];
    	for (int i=0; i<numProcs; i++)	{
    		int numMax = (i+1) * numElemProc;
    		if ((i+1) == numProcs)	numMax = _pos;
    		threads[i] = new Thread (new Min1 (i*numElemProc, numMax));
    		threads[i].start();
    	}
    	for (int i=0; i<numProcs; i++)	{
    		try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    	return charGlobal;
    }
    public char min() {
        if ( size() == 0 ) {
            throw new IllegalStateException( "cannot find minimum of an empty list" );
        }
        char min = Character.MAX_VALUE;
        for ( int i = 0; i < _pos; i++ ) {
        	if ( _data[i] < min ) {
        		min = _data[i];
        	}
        }
        return min;
    }

    private class Sum1 implements Runnable	{
    	int min, max;
    	Sum1 (int a, int b)	{
    		min = a; max = b;
    	}
    	public void run ()	{
    		char suma = 0;
    		for (int i=min; i<max; i++)	{
    			suma += _data[i];
    		}
    		synchronized (this)	{
    			charGlobal += suma;
    		}
    	}
    }
    /** {@inheritDoc} */
    public char sumPar (int numProcs)	{
    	charGlobal = 0;
    	int numElemProc = _pos/numProcs;
    	threads = new Thread[numProcs];
    	for (int i=0; i<numProcs; i++)	{
    		int numMax = (i+1) * numElemProc;
    		if ((i+1) == numProcs)	numMax = _pos;
    		threads[i] = new Thread (new Sum1 (i*numElemProc, numMax));
    		threads[i].start();
    	}
    	for (int i=0; i<numProcs; i++)	{
    		try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    	return charGlobal;
    }
    public char sum() {
        char sum = 0;
        for ( int i = 0; i < _pos; i++ ) {
			sum += _data[ i ];
        }
        return sum;
    }


    // stringification
    private class ToString1 implements Runnable	{
    	int min, max, numThread;
    	ToString1 (int a, int b, int c)	{
    		min = a; max = b; numThread = c;
    	}
    	public void run ()	{
            final StringBuilder buf = new StringBuilder( "" );
    		for (int i=min; i<max; i++) {
                buf.append( _data[ i ] );
                buf.append( ", " );
            }
    		//System.out.println ("Trozo de string: " + buf.toString());
    		if (numThread>0)
				try {
					threads[numThread-1].join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
    		stringGlobal += buf.toString();
    	}
    }
    /** {@inheritDoc} */
    public String toStringPar (int numProcs)	{
    	stringGlobal = "{";
    	int numElemProc = (_pos-1)/numProcs;
    	threads = new Thread[numProcs];
    	for (int i=0; i<numProcs; i++)	{
    		int numMax = (i+1) * numElemProc;
    		if ((i+1) == numProcs)	numMax = _pos-1;
    		threads[i] = new Thread (new ToString1 (i*numElemProc, numMax, i));
    		threads[i].start();
    	}
    	try {
			threads[numProcs-1].join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	if ( size() > 0 ) {
            stringGlobal += ( _data[ _pos - 1 ] );
        }
        stringGlobal += ( "}" );
    	return stringGlobal;
    }
    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder( "{" );
        for ( int i = 0, end = _pos - 1; i < end; i++ ) {
            buf.append( _data[ i ] );
            buf.append( ", " );
        }
        if ( size() > 0 ) {
            buf.append( _data[ _pos - 1 ] );
        }
        buf.append( "}" );
        return buf.toString();
    }


    /** TCharArrayList iterator */
    class TCharArrayIterator implements TCharIterator {

        /** Index of element to be returned by subsequent call to next. */
        private int cursor = 0;

        /**
         * Index of element returned by most recent call to next or
         * previous.  Reset to -1 if this element is deleted by a call
         * to remove.
         */
        int lastRet = -1;


        TCharArrayIterator( int index ) {
            cursor = index;
        }


        /** {@inheritDoc} */
        public boolean hasNext() {
            return cursor < size();
	    }


        /** {@inheritDoc} */
        public char next() {
            try {
                char next = get( cursor );
                lastRet = cursor++;
                return next;
            } catch ( IndexOutOfBoundsException e ) {
                throw new NoSuchElementException();
            }
        }


        /** {@inheritDoc} */
        public void remove() {
            if ( lastRet == -1 )
		        throw new IllegalStateException();

            try {
                TCharArrayList.this.remove( lastRet, 1);
                if ( lastRet < cursor )
                    cursor--;
                lastRet = -1;
            } catch ( IndexOutOfBoundsException e ) {
                throw new ConcurrentModificationException();
            }
        }
    }

    private class WriteExternal1 implements Runnable	{
    	int min, max;
    	ObjectOutput out;
    	WriteExternal1 (int a, int b, ObjectOutput c)	{
    		min = a; max = b; out = c;
    	}
    	public void run ()	{
    		for (int i=min; i<max; i++) {
    			synchronized (this)	{
    				try {
						out.writeChar( _data[ i ] );
					} catch (IOException e) {
						e.printStackTrace();
					}
    			}
        	}
    	}
    }
    public void writeExternalPar (ObjectOutput out, int numProcs) throws IOException	{
    	//VERSION
    	out.writeChar (0);
    	//POSITION
    	out.writeChar (_pos);
    	//NO_ENTRY_VALUE
    	out.writeChar (no_entry_value);
    	//ENTRIES
    	int len = _data.length;
    	out.writeChar (len);
    	int numElemProc = len/numProcs;
    	threads = new Thread[numProcs];
    	for (int i=0; i<numProcs; i++)	{
    		int numMax = (i+1) * numElemProc;
    		if ((i+1) == numProcs)	numMax = len;
    		threads[i] = new Thread (new WriteExternal1 (i*numElemProc, numMax, out));
    	}
    	for (int i=0; i<numProcs; i++)	{
    		try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    }
    public void writeExternal( ObjectOutput out ) throws IOException {
    	// VERSION
    	out.writeChar( 0 );

    	// POSITION
    	out.writeChar( _pos );

    	// NO_ENTRY_VALUE
    	out.writeChar( no_entry_value );

    	// ENTRIES
    	int len = _data.length;
    	out.writeChar( len );
    	for( int i = 0; i < len; i++ ) {
    		out.writeChar( _data[ i ] );
    	}
    }

    private class ReadExternal1 implements Runnable	{
    	int min, max;
    	ObjectInput in;
    	ReadExternal1 (int a, int b, ObjectInput c)	{
    		min = a; max = b; in = c;
    	}
    	public void run ()	{
    		for (int i=min; i<max; i++) {
        		try {
					_data[i] = in.readChar();
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
    	}
    }
    public void readExternalPar (ObjectInput in, int numProcs)
    	throws IOException, ClassNotFoundException	{
    	//VERSION
    	in.readChar();
    	//POSITION
    	_pos = in.readInt();
    	//No_ENTRY_VALUE
    	no_entry_value = in.readChar();
    	//ENTRIES
    	int len = in.readInt();
    	_data = new char[len];
    	int numElemProc = _pos/numProcs;
    	threads = new Thread[numProcs];
    	for (int i=0; i<numProcs; i++)	{
    		int numMax = (i+1) * numElemProc;
    		if ((i+1) == numProcs)	numMax = _pos;
    		threads[i] = new Thread (new ReadExternal1 (i*numElemProc, numMax, in));
    		threads[i].start();
    	}
    	for (int i=0; i<numProcs; i++)	{
    		try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    }
    public void readExternal( ObjectInput in )
    	throws IOException, ClassNotFoundException {

    	// VERSION
    	in.readChar();

    	// POSITION
    	_pos = in.readInt();

    	// NO_ENTRY_VALUE
    	no_entry_value = in.readChar();

    	// ENTRIES
    	int len = in.readInt();
    	_data = new char[ len ];
    	for( int i = 0; i < len; i++ ) {
    		_data[ i ] = in.readChar();
    	}
    }
} // TCharArrayList