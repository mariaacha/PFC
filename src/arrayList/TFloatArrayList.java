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
import paralel.TFloatIterator;
import paralel.TFloatFunction;
import paralel.TFloatProcedure;
import paralel.HashFunctions;
import paralel.TFloatCollection;

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
 * A resizable, array-backed list of float primitives.
 */
public class TFloatArrayList implements TFloatList, Externalizable {
	static final long serialVersionUID = 1L;

    /** the data of the list */
    protected float[] _data;

    /** the index after the last entry in the list */
    protected int _pos;

    /** the default capacity for new lists */
    protected static final int DEFAULT_CAPACITY = Constants.DEFAULT_CAPACITY;

    /** the float value that represents null */
    protected float no_entry_value;
    
    protected int numProcs = 2;
    
    protected boolean boolGlobal;
    
    protected int intGlobal;
    
    protected int[] intsGlobal;
    
    protected float floatGlobal;
    
    protected float[] floatsGlobal;
    
    protected String stringGlobal;
    
    protected Thread[] threads = new Thread[numProcs];


    /**
     * Creates a new <code>TFloatArrayList</code> instance with the
     * default capacity.
     */
    @SuppressWarnings({"RedundantCast"})
    public TFloatArrayList() {
        this( DEFAULT_CAPACITY, ( float ) 0 );
    }


    /**
     * Creates a new <code>TFloatArrayList</code> instance with the
     * specified capacity.
     *
     * @param capacity an <code>float</code> value
     */
    @SuppressWarnings({"RedundantCast"})
    public TFloatArrayList( int capacity ) {
        this( capacity, ( float ) 0 );
    }


    /**
     * Creates a new <code>TFloatArrayList</code> instance with the
     * specified capacity.
     *
     * @param capacity an <code>float</code> value
     * @param no_entry_value an <code>float</code> value that represents null.
     */
    public TFloatArrayList( int capacity, float no_entry_value ) {
        _data = new float[ capacity ];
        _pos = 0;
        this.no_entry_value = no_entry_value;
    }

    /**
     * Creates a new <code>TFloatArrayList</code> instance that contains
     * a copy of the collection passed to us.
     *
     * @param collection the collection to copy
     */
    public TFloatArrayList ( TFloatCollection collection ) {
        this( collection.size() );
        addAll( collection ); 
    }


    /**
     * Creates a new <code>TFloatArrayList</code> instance whose
     * capacity is the length of <tt>values</tt> array and whose
     * initial contents are the specified values.
     * <p>
     * A defensive copy of the given values is held by the new instance.
     *
     * @param values an <code>float[]</code> value
     */
    public TFloatArrayList( float[] values ) {
        this( values.length );
        add( values );
    }

    protected TFloatArrayList(float[] values, float no_entry_value, boolean wrap) {
        if (!wrap)
            throw new IllegalStateException("Wrong call");

        if (values == null)
            throw new IllegalArgumentException("values can not be null");

        _data = values;
        _pos = values.length;
        this.no_entry_value = no_entry_value;
    }
    
    private TFloatArrayList (float[] values, int from, int to)	{
    	_data = new float[to-from];
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
    public static TFloatArrayList wrap(float[] values) {
        return wrap(values, ( float ) 0);
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
    public static TFloatArrayList wrap(float[] values, float no_entry_value) {
        return new TFloatArrayList(values, no_entry_value, true) {
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
    public float getNoEntryValue() {
        return no_entry_value;
    }


    // sizing

    /**
     * Grow the internal array as needed to accommodate the specified number of elements.
     * The size of the array floats on each resize unless capacity requires more than twice
     * the current capacity.
     */
    public void ensureCapacity( int capacity ) {
        if ( capacity > _data.length ) {
            int newCap = Math.max( _data.length << 1, capacity );
            float[] tmp = new float[ newCap ];
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
            float[] tmp = new float[ size() ];
            toArray( tmp, 0, tmp.length );
            _data = tmp;
        }
    }


    // modifying

    /** {@inheritDoc} */
    public boolean add( float val ) {
        ensureCapacity( _pos + 1 );
        _data[ _pos++ ] = val;
        return true;
    }


    /** {@inheritDoc} */
    public void add( float[] vals ) {
        add( vals, 0, vals.length );
    }


    /** {@inheritDoc} */
    public void add( float[] vals, int offset, int length ) {
        ensureCapacity( _pos + length );
        System.arraycopy( vals, offset, _data, _pos, length );
        _pos += length;
    }


    /** {@inheritDoc} */
    public void insert( int offset, float value ) {
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
    public void insert( int offset, float[] values ) {
        insert( offset, values, 0, values.length );
    }


    /** {@inheritDoc} */
    public void insert( int offset, float[] values, int valOffset, int len ) {
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
    public float get( int offset ) {
        if ( offset >= _pos ) {
            throw new ArrayIndexOutOfBoundsException( offset );
        }
        return _data[ offset ];
    }


    /**
     * Returns the value at the specified offset without doing any bounds checking.
     */
    public float getQuick( int offset ) {
        return _data[ offset ];
    }


    /** {@inheritDoc} */
    public float set( int offset, float val ) {
        if ( offset >= _pos ) {
            throw new ArrayIndexOutOfBoundsException( offset );
        }

		float prev_val = _data[ offset ];
        _data[ offset ] = val;
		return prev_val;
    }


    /** {@inheritDoc} */
    public float replace( int offset, float val ) {
        if ( offset >= _pos ) {
            throw new ArrayIndexOutOfBoundsException( offset );
        }
        float old = _data[ offset ];
        _data[ offset ] = val;
        return old;
    }
    
    @SuppressWarnings("unchecked")
    public void replaceAll(UnaryOperator<Float> operator) {
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
    public void set( int offset, float[] values ) {
        set( offset, values, 0, values.length );
    }


    /** {@inheritDoc} */
    public void set( int offset, float[] values, int valOffset, int length ) {
        if ( offset < 0 || offset + length > _pos ) {
            throw new ArrayIndexOutOfBoundsException( offset );
        }
        System.arraycopy( values, valOffset, _data, offset, length );
    }


    /**
     * Sets the value at the specified offset without doing any bounds checking.
     */
    public void setQuick( int offset, float val ) {
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
        _data = new float[ capacity ];
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
    	float value;
    	Remove1 (int a, int b, float c)	{
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
    public boolean removePar (float value, int numProcs)	{
    	int numElemProc = _pos/numProcs;
    	boolGlobal = false;
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
    
    public boolean remove( float value ) {
        for ( int index = 0; index < _pos; index++ ) {
            if ( value == _data[index]  ) {
            	remove( index, 1 );
                return true;
            }
        }
        return false;
    }


    /** {@inheritDoc} */
    public float removeAt( int offset ) {
        float old = get( offset );
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
    public TFloatIterator iterator() {
        return new TFloatArrayIterator( 0 );
    }

    private class ContainsAll1 implements Runnable	{
    	int min, max;
    	Float[] array;
    	ContainsAll1 (int a, int b, Float[] c)	{
    		min = a; max = b; array = c;
    	}
    	public void run ()	{
    		for (int i=min; i<max; i++) {
                if (!boolGlobal)	return;
                if (array[i] instanceof Float ) {
                    float c = array[i].floatValue();
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
    	Float[] array = collection.toArray(new Float[collection.size()]);
    	int numElemProc = collection.size()/numProcs;
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
            if ( element instanceof Float ) {
                float c = ( ( Float ) element ).floatValue();
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
            if ( element instanceof Float ) {
                float c = ( ( Float ) element ).floatValue();
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
    	float[] array;
    	ContainsAll2 (int a, int b, float[] c)	{
    		min = a; max = b; array = c;
    	}
    	public void run ()	{
    		for (int i=min; i<max; i++) {
    			if (!boolGlobal)	return;
                float element = array[i];
                if ( ! contains( element ) ) {
                    boolGlobal = false;
                    return;
                }
            }
    	}
    }
    /** {@inheritDoc} */
    public boolean containsAllPar (TFloatCollection collection, int numProcs)	{
    	if (this == collection)	return true;
    	boolGlobal = true;
    	float[] array = collection.toArray(new float[collection.size()]);
    	int numElemProc = collection.size()/numProcs;
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
    public boolean containsAllPar2 ( TFloatCollection collection, int numProcs) {
        if ( this == collection ) {
            return true;
        }
        TFloatIterator iter = collection.iterator();
        while ( iter.hasNext() ) {
            float element = iter.next();
            if ( ! containsPar( element, numProcs) ) {
                return false;
            }
        }
        return true;
    }
    public boolean containsAll( TFloatCollection collection ) {
        if ( this == collection ) {
            return true;
        }
        TFloatIterator iter = collection.iterator();
        while ( iter.hasNext() ) {
            float element = iter.next();
            if ( ! contains( element ) ) {
                return false;
            }
        }
        return true;
    }


    /** {@inheritDoc} */
    public boolean containsAllPar (float[] array, int numProcs)	{
    	int numElemProc = array.length/numProcs;
    	boolGlobal = true;
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
    public boolean containsAllPar2( float[] array, int numProcs) {
        for ( int i = array.length; i-- > 0; ) {
            if ( ! containsPar ( array[i], numProcs) ) {
                return false;
            }
        }
        return true;
    }
    public boolean containsAll( float[] array ) {
        for ( int i = array.length; i-- > 0; ) {
            if ( ! contains( array[i] ) ) {
                return false;
            }
        }
        return true;
    }

    private class AddAll1 implements Runnable	{
    	int min, max, posInicial;
    	Float[] array;
    	AddAll1 (int a, int b, Float[] c, int d)	{
    		min = a; max = b;
    		array = c;
    		posInicial = d;
    	}
    	public void run ()	{
    		for (int i=min; i<max; i++) {
    			_data[i+posInicial] = array[i].floatValue();
                boolGlobal = true;
            }
    	}
    }
    /** {@inheritDoc} */
    public boolean addAllPar (Collection<? extends Float> collection, int numProcs)	{
    	boolGlobal = false;
    	Float[] array = collection.toArray (new Float[collection.size()]);
    	int numElemProc = array.length/numProcs;
    	int posInicial = _pos;
    	ensureCapacity (_pos+array.length);
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
    public boolean addAll( Collection<? extends Float> collection ) {
        boolean changed = false;
        for ( Float element : collection ) {
            float e = element.floatValue();
            if ( add( e ) ) {
                changed = true;
            }
        }
        return changed;
    }

    private class AddAll2 implements Runnable	{
    	int min, max;
    	float[] array;
    	AddAll2 (int a, int b, float[] c)	{
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
    public boolean addAllPar (TFloatCollection collection, int numProcs)	{
    	boolGlobal = false;
    	ensureCapacity (_pos+collection.size());
    	float[] array = collection.toArray(new float[collection.size()]);
    	int numElemProc = array.length/numProcs;
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
    public boolean addAll ( TFloatCollection collection ) {
        boolean changed = false;
        TFloatIterator iter = collection.iterator();
        while ( iter.hasNext() ) {
            float element = iter.next();
            if ( add( element ) ) {
                changed = true;
            }
        }
        return changed;
    }
    public boolean addAll2( TFloatCollection collection ) {
        boolean changed = false;
        float[] floats = collection.toArray(new float[collection.size()]);
        float[] tmp = new float[_pos + collection.size()];
        System.arraycopy(_data, 0, tmp, 0, _pos);
        System.arraycopy(floats, 0, tmp, _pos, collection.size());
        _pos += collection.size();
        _data = tmp;
        return changed;
    }


    /** {@inheritDoc} */
    public boolean addAllPar (float[] array, int numProcs)	{
    	boolGlobal = false;
    	ensureCapacity (_pos+array.length);
    	int numElemProc = array.length/numProcs;
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
    public boolean addAll( float[] array ) {
        boolean changed = false;
        for ( float element : array ) {
            if ( add( element ) ) {
                changed = true;
            }
        }
        return changed;
    }
    public boolean addAll2( float[] array ) {
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
    		float[] arrayLocal = new float[_data.length];
    		int j=0;
    		for (int i=min; i<max; i++) {
    	        if (collection.contains( Float.valueOf (_data[i]) ) ) {
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
    		System.arraycopy(arrayLocal, 0, intsGlobal, intGlobal, j);
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
    	_data = floatsGlobal;
    	_pos = intGlobal;
    	return boolGlobal;
    }
    public boolean retainAll( Collection<?> collection ) {
        boolean modified = false;
	    TFloatIterator iter = iterator();
	    while ( iter.hasNext() ) {
	        if ( ! collection.contains( Float.valueOf ( iter.next() ) ) ) {
		        iter.remove();
		        modified = true;
	        }
	    }
	    return modified;
    }


    private class RetainAll2 implements Runnable	{
    	int min, max;
    	TFloatCollection collection;
    	RetainAll2 (int a, int b, TFloatCollection c)	{
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
    public boolean retainAllPar (TFloatCollection collection, int numProcs)	{
    	if (this == collection)	return false;
    	boolGlobal = false;
    	int numElemProc = _pos/numProcs;
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
    public boolean retainAll( TFloatCollection collection ) {
        if ( this == collection ) {
            return false;
        }
        boolean modified = false;
	    TFloatIterator iter = iterator();
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
    	float[] array;
    	RetainAll3 (int a, int b, float[] c)	{
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
    public boolean retainAllPar (float[] array, int numProcs)	{
    	boolGlobal = false;
    	Arrays.sort (array);
    	int numElemProc = _pos/numProcs;
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
    public boolean retainAll( float[] array ) {
        boolean changed = false;
        Arrays.sort( array );
        float[] data = _data;

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
    		float[] arrayLocal = new float[_data.length];
    		int j=0;
    		for (int i=min; i<max; i++) {
    	        if (!collection.contains( Float.valueOf (_data[i]) ) ) {
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
    		System.arraycopy(arrayLocal, 0, intsGlobal, intGlobal, j);
    		intGlobal += j;
    	}
    }
    /** {@inheritDoc} */
    @SuppressWarnings({"SuspiciousMethodCalls"})
    public boolean removeAllPar (Collection<?> collection, int numProcs)	{
    	boolGlobal = false;
    	intsGlobal = new int[_data.length];
    	intGlobal = 0;
    	int numElemProc = _pos/numProcs;
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
    	_data = floatsGlobal;
    	_pos = intGlobal;
    	return boolGlobal;
    }
    public boolean removeAll( Collection<?> collection ) {
        boolean changed = false;
        for ( Object element : collection ) {
            if ( element instanceof Float ) {
                float c = ( ( Float ) element ).floatValue();
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
            if ( element instanceof Float ) {
                float c = ( ( Float ) element ).floatValue();
                if ( removePar( c, numProcs) ) {
                    changed = true;
                }
            }
        }
        return changed;
    }


    /** {@inheritDoc} */
    public boolean removeAll( TFloatCollection collection ) {
        if ( collection == this ) {
            clear();
            return true;
        }
        boolean changed = false;
        TFloatIterator iter = collection.iterator();
        while ( iter.hasNext() ) {
            float element = iter.next();
            if ( remove( element ) ) {
                changed = true;
            }
        }
        return changed;
    }


    /** {@inheritDoc} */
    public boolean removeAll( float[] array ) {
        boolean changed = false;
        for ( int i = array.length; i-- > 0; ) {
            if ( remove(array[i]) ) {
                changed = true;
            }
        }
        return changed;
    }
    
    public boolean removeIf(TFloatProcedure filter) {
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
            final Float element = (Float) _data[i];
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
    	TFloatFunction function;
    	TransformValues1 (int a, int b, TFloatFunction c)	{
    		min = a; max = b;
    	}
    	public void run ()	{
    		for (int i=min; i<max; i++) {
                _data[ i ] = function.execute( _data[ i ] );
            }
    	}
    }
    /** {@inheritDoc} */
    public void transformValuesPar (TFloatFunction function, int numProcs)	{
    	int numElemProc = _pos/numProcs;
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
    public void transformValues( TFloatFunction function ) {
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
    	floatsGlobal = new float[to-from];
    	//int numElemProc = (to-from)/numProcs;
    	int numElemProc = (to-from)/(numProcs*2);
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
        float tmp = _data[ i ];
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
    		float[] floats = new float[max-min];
    		int j = 0;
    		for (int i=min; i<max; i++)	{
    			floats[j] = _data[i]; 
    		}
   			if (numProc > 0)	try {
				threads[numProc-1].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
   			System.arraycopy(floats, 0, floatsGlobal, intGlobal, max-min);
   			intGlobal += (max-min);
    	}
    }
    /** {@inheritDoc} */
    public TFloatList subListPar( int begin, int end, int numProcs) {
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
        TFloatArrayList list = new TFloatArrayList( end - begin );
        floatsGlobal = new float[end-begin];
        intGlobal = 0;
        int numElemProc = (end-begin)/numProcs;
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
        list._data = floatsGlobal;
        return list;
    }
    public TFloatList subList( int begin, int end ) {
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
        TFloatArrayList list = new TFloatArrayList( end - begin );
        for ( int i = begin; i < end; i++ ) {
        	list.add( _data[ i ] );
        }
        return list;
    }
    public TFloatList subList2( int begin, int end ) {
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
		/**float[] arrayLocal = new float[end-begin];
		System.arraycopy(_data, begin, arrayLocal, 0, end-begin);
        return new TFloatArrayList (arrayLocal);*/
		return new TFloatArrayList (_data, begin, end);
    }


    /** {@inheritDoc} */
    public float[] toArrayPar(int numProcs) {
        return toArrayPar( 0, _pos, numProcs);
    }
    public float[] toArray() {
        return toArray( 0, _pos );
    }


    /** {@inheritDoc} */
    public float[] toArrayPar( int offset, int len, int numProcs) {
        float[] rv = new float[ len ];
        toArrayPar( rv, offset, len, numProcs);
        return rv;
    }
    public float[] toArray( int offset, int len ) {
        float[] rv = new float[ len ];
        toArray( rv, offset, len );
        return rv;
    }


    /** {@inheritDoc} */
    public float[] toArrayPar( float[] dest, int numProcs) {
        int len = dest.length;
        if ( dest.length > _pos ) {
            len = _pos;
            dest[len] = no_entry_value;
        }
        toArrayPar( dest, 0, len, numProcs);
        return dest;
    }
    public float[] toArray( float[] dest ) {
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
    		float[] floatsLocal = new float[max-min];
    		for (int i=min; i<max; i++)	{
    			floatsLocal[j++] = _data[i];
    		}
    		try {
    			if (numThread>0)
				threads[numThread-1].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    		System.arraycopy(floatsLocal, 0, floatsGlobal, intGlobal, j);
    	}
    }
    private class ToArray2 implements Runnable	{
    	int min, max;
    	ToArray2 (int a, int b)	{
    		min = a; max = b;
    	}
    	public void run ()	{
    		System.arraycopy(_data, min, floatsGlobal, min+intGlobal, max-min);
    		/**for (int i=min; i<max; i++)	{
    			floatsGlobal[i-intGlobal] = _data[i];
    		}*/
    	}
    }
    /** {@inheritDoc} */
    public float[] toArrayPar (float[] dest, int offset, int len, int numProcs)	{
    	if (len == 0)	{
    		return dest;
    	}
    	if (offset < 0 || offset >= _pos)	{
    		throw new ArrayIndexOutOfBoundsException (offset);
    	}
    	intGlobal = offset;
    	floatsGlobal = dest;
    	int numElemProc = len/numProcs;
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
    	return floatsGlobal;
    }
    public float[] toArray( float[] dest, int offset, int len ) {
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
    public float[] toArrayPar (float[] dest, int source_pos, int dest_pos, int len, int numProcs)	{
    	if (len == 0)	return dest;
    	if (source_pos < 0 || source_pos >= _pos)	{
    		throw new ArrayIndexOutOfBoundsException (source_pos);
    	}
    	floatsGlobal = new float[dest_pos - source_pos];
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
    	return floatsGlobal;
    }
    public float[] toArray( float[] dest, int source_pos, int dest_pos, int len ) {
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
    	TFloatArrayList that;
    	Equals1 (int a, int b, TFloatArrayList c)	{
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
    	else if (other instanceof TFloatArrayList)	{
    		TFloatArrayList that = (TFloatArrayList) other;
    		if (that.size() != this.size())	{
    			return false;
    		}
    		else	{
    			boolGlobal = true;
    			int numElemProc = _pos/numProcs;
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
        else if ( other instanceof TFloatArrayList ) {
            TFloatArrayList that = ( TFloatArrayList )other;
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
    	if (! (otherObj instanceof TFloatArrayList)) return super.equals(otherObj);
    	if (this==otherObj) return true;
    	if (otherObj==null) return false;
    	TFloatArrayList other = (TFloatArrayList) otherObj;
    	if (size()!=other.size()) return false;

    	float[] theElements = _data;
    	float[] otherElements = other._data;
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
    	TFloatProcedure procedure;
    	ForEach1 (int a, int b, TFloatProcedure c)	{
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
    public boolean forEachPar (TFloatProcedure procedure, int numProcs)	{
    	boolGlobal = true;
    	int numElemProc = _pos/numProcs;
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
    public boolean forEach( TFloatProcedure procedure ) {
        for ( int i = 0; i < _pos; i++ ) {
            if ( !procedure.execute( _data[ i ] ) ) {
                return false;
            }
        }
        return true;
    }

    private class ForEachDescending1 implements Runnable	{
    	int min, max;
    	TFloatProcedure procedure;
    	ForEachDescending1 (int a, int b, TFloatProcedure c)	{
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
    public boolean forEachDescendingPar (TFloatProcedure procedure, int numProcs)	{
    	boolGlobal = true;
    	int numElemProc = _pos/numProcs;
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
    public boolean forEachDescending( TFloatProcedure procedure ) {
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
    public void fill( float val ) {
        Arrays.fill( _data, 0, _pos, val );
    }


    /** {@inheritDoc} */
    public void fill( int fromIndex, int toIndex, float val ) {
        if ( toIndex > _pos ) {
          ensureCapacity( toIndex );
          _pos = toIndex;
        }
        Arrays.fill( _data, fromIndex, toIndex, val );
    }


    // searching

    /** {@inheritDoc} */
    public int binarySearch( float value ) {
        return binarySearch( value, 0, _pos );
    }


    
    /** {@inheritDoc} */
    public int binarySearch(float value, int fromIndex, int toIndex) {
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
            float midVal = _data[ mid ];

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
    public int indexOfPar( float value, int numProcs) {
        return indexOfPar( 0, value, numProcs);
    }
    public int indexOf( float value ) {
        return indexOf( 0, value );
    }

    private class IndexOf1 implements Runnable	{
    	int min, max;
    	float value;
    	IndexOf1 (int a, int b, float c)	{
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
    public int indexOfPar (int offset, float value, int numProcs)	{
    	intGlobal = -1;
    	int numElemProc = (_pos-offset)/numProcs;
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
    public int indexOf( int offset, float value ) {
        for ( int i = offset; i < _pos; i++ ) {
            if ( _data[ i ] == value ) {
                return i;
            }
        }
        return -1;
    }


    /** {@inheritDoc} */
    public int lastIndexOfPar( float value, int numProcs) {
        return lastIndexOfPar( _pos, value, numProcs);
    }
    public int lastIndexOf( float value ) {
        return lastIndexOf( _pos, value );
    }

    private class LastIndexOf1 implements Runnable	{
    	int min, max;
    	float value;
    	LastIndexOf1 (int a, int b, float c)	{
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
    public int lastIndexOfPar (int offset, float value, int numProcs)	{
    	intGlobal = -1;
    	int numElemProc = offset/numProcs;
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
    public int lastIndexOf( int offset, float value ) {
        for ( int i = offset; i-- > 0; ) {
            if ( _data[ i ] == value ) {
                return i;
            }
        }
        return -1;
    }


    /** {@inheritDoc} */
    public boolean containsPar ( float value, int numProcs) {
        return indexOfPar( value, numProcs) >= 0;
    }
    public boolean contains( float value ) {
        return lastIndexOf( value ) >= 0;
    }

    private class Grep1 implements Runnable	{
    	int min, max;
    	TFloatProcedure condition;
    	Grep1 (int a, int b, TFloatProcedure c)	{
    		min = a; max = b; condition = c;
    	}
    	public void run ()	{
    		float[] floats = new float[_pos];
    		int j = 0;
    		for (int i=min; i<max; i++) {
                if ( condition.execute( _data[ i ] ) ) {
                    floats[j] = _data[i];
                    j++;
                }
            }
    		synchronized (this)	{
    			System.arraycopy(floats, 0, floatsGlobal, intGlobal, j);
    			intGlobal += j;
    		}
    	}
    }
    /** {@inheritDoc} */
    public TFloatList grepPar (TFloatProcedure condition, int numProcs)	{
    	TFloatArrayList list = new TFloatArrayList();
    	floatsGlobal = new float[_pos];
    	intGlobal = 0;
    	int numElemProc = _pos/numProcs;
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
    	list.addAll(floatsGlobal);
    	return list;
    }
    public TFloatList grep( TFloatProcedure condition ) {
        TFloatArrayList list = new TFloatArrayList();
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
    	TFloatProcedure condition;
    	InverseGrep1 (int a, int b, TFloatProcedure c)	{
    		min = a; max = b; condition = c;
    	}
    	public void run ()	{
    		float[] floats = new float[_pos];
    		int j = 0;
    		for (int i=max-1; i>=min; i--) {
                if ( condition.execute( _data[ i ] ) ) {
                    floats[j] = _data[i];
                    j++;
                }
            }
    		synchronized (this)	{
    			System.arraycopy(floats, 0, floatsGlobal, intGlobal, j);
    			intGlobal += j;
    		}
    	}
    }
    /** {@inheritDoc} */
    public TFloatList inverseGrepPar (TFloatProcedure condition, int numProcs)	{
    	TFloatArrayList list = new TFloatArrayList();
    	floatsGlobal = new float[_pos];
    	intGlobal = 0;
    	int numElemProc = _pos/numProcs;
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
    	list.addAll(floatsGlobal);
    	return list;
    }
    public TFloatList inverseGrep( TFloatProcedure condition ) {
        TFloatArrayList list = new TFloatArrayList();
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
    		float maximum = floatGlobal;
    		for (int i=min; i<max; i++) {
            	if ( _data[ i ] > maximum ) {
            		maximum = _data[ i ];
            	}
            }
    		synchronized (this)	{
    			if (maximum > intGlobal)	floatGlobal = maximum;
    		}
    	}
    }
    /** {@inheritDoc} */
    public int maxPar(int numProcs)	{
    	if (size() == 0)	{
    		throw new IllegalStateException ("cannot find maximum of an empty list");
    	}
    	floatGlobal = Float.MIN_VALUE;
    	int numElemProc = _pos/numProcs;
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
    public float max() {
        if ( size() == 0 ) {
            throw new IllegalStateException("cannot find maximum of an empty list");
        }
        float max = Float.MIN_VALUE;
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
    		float minimum = floatGlobal;
    		for (int i=min; i<max; i++) {
            	if ( _data[ i ] < minimum ) {
            		minimum = _data[ i ];
            	}
            }
    		synchronized (this)	{
    			if (minimum < floatGlobal)	floatGlobal = minimum;
    		}
    	}
    }
    /** {@inheritDoc} */
    public float minPar(int numProcs)	{
    	if (size() == 0)	{
    		throw new IllegalStateException ("cannot find maximum of an empty list");
    	}
    	floatGlobal = Float.MAX_VALUE;
    	int numElemProc = _pos/numProcs;
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
    	return floatGlobal;
    }
    public float min() {
        if ( size() == 0 ) {
            throw new IllegalStateException( "cannot find minimum of an empty list" );
        }
        float min = Float.MAX_VALUE;
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
    		float suma = 0;
    		for (int i=min; i<max; i++)	{
    			suma += _data[i];
    		}
    		synchronized (this)	{
    			floatGlobal += suma;
    		}
    	}
    }
    /** {@inheritDoc} */
    public float sumPar (int numProcs)	{
    	floatGlobal = 0;
    	int numElemProc = _pos/numProcs;
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
    	return floatGlobal;
    }
    public float sum() {
        float sum = 0;
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


    /** TFloatArrayList iterator */
    class TFloatArrayIterator implements TFloatIterator {

        /** Index of element to be returned by subsequent call to next. */
        private int cursor = 0;

        /**
         * Index of element returned by most recent call to next or
         * previous.  Reset to -1 if this element is deleted by a call
         * to remove.
         */
        int lastRet = -1;


        TFloatArrayIterator( int index ) {
            cursor = index;
        }


        /** {@inheritDoc} */
        public boolean hasNext() {
            return cursor < size();
	    }


        /** {@inheritDoc} */
        public float next() {
            try {
                float next = get( cursor );
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
                TFloatArrayList.this.remove( lastRet, 1);
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
						out.writeFloat( _data[ i ] );
					} catch (IOException e) {
						e.printStackTrace();
					}
    			}
        	}
    	}
    }
    public void writeExternalPar (ObjectOutput out, int numProcs) throws IOException	{
    	//VERSION
    	out.writeFloat (0);
    	//POSITION
    	out.writeFloat (_pos);
    	//NO_ENTRY_VALUE
    	out.writeFloat (no_entry_value);
    	//ENTRIES
    	int len = _data.length;
    	out.writeFloat (len);
    	int numElemProc = len/numProcs;
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
    	out.writeFloat( 0 );

    	// POSITION
    	out.writeFloat( _pos );

    	// NO_ENTRY_VALUE
    	out.writeFloat( no_entry_value );

    	// ENTRIES
    	int len = _data.length;
    	out.writeFloat( len );
    	for( int i = 0; i < len; i++ ) {
    		out.writeFloat( _data[ i ] );
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
					_data[i] = in.readFloat();
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
    	}
    }
    public void readExternalPar (ObjectInput in, int numProcs)
    	throws IOException, ClassNotFoundException	{
    	//VERSION
    	in.readByte();
    	//POSITION
    	_pos = in.readInt();
    	//No_ENTRY_VALUE
    	no_entry_value = in.readFloat();
    	//ENTRIES
    	int len = in.readInt();
    	_data = new float[len];
    	int numElemProc = _pos/numProcs;
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
    	in.readByte();

    	// POSITION
    	_pos = in.readInt();

    	// NO_ENTRY_VALUE
    	no_entry_value = in.readFloat();

    	// ENTRIES
    	int len = in.readInt();
    	_data = new float[ len ];
    	for( int i = 0; i < len; i++ ) {
    		_data[ i ] = in.readFloat();
    	}
    }
} // TFloatArrayList