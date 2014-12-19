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

package paralel;


//////////////////////////////////////////////////
// THIS IS A GENERATED CLASS. DO NOT HAND EDIT! //
//////////////////////////////////////////////////

import paralel.TIntIntMap;
import paralel.TIntFunction;
import paralel.TIntProcedure;
import paralel.TIntIntProcedure;
import paralel.TIntSet;
import paralel.TIntIntIterator;
import paralel.THashPrimitiveIterator;
import paralel.HashFunctions;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.BiFunction;

/**
 * An open addressed Map implementation for #k# keys and #v# values.
 *
 * @author Eric D. Friedman
 * @author Rob Eden
 * @author Jeff Randall
 * @version $Id: _K__V_HashMap.template,v 1.1.2.16 2010/03/02 04:09:50 robeden Exp $
 */
public class TIntIntHashMap extends TIntIntHash implements TIntIntMap, Externalizable {
    static final long serialVersionUID = 1L;

    /** the values of the map */
    protected transient int[] _values;
    
    /** the number of processors */
    private int numProcs = 2;
    
    /**boolean used for the processors to share a boolean result*/
    private boolean boolGlobal;
    
    /** int used for the processors to share a boolean result*/
    private int intGlobal;
    
    /**array of ints used for the processor to share a int[] result*/
    private int[] intsGlobal;

    /**array with the threads*/
    private Thread[] threads = new Thread[numProcs];
    
    //string used for the processors to share a string result
    private String stringGlobal;

    /**
     * Creates a new <code>T#K##V#HashMap</code> instance with the default
     * capacity and load factor.
     */
    public TIntIntHashMap() {
        super();
    }


    /**
     * Creates a new <code>T#K##V#HashMap</code> instance with a prime
     * capacity equal to or greater than <tt>initialCapacity</tt> and
     * with the default load factor.
     *
     * @param initialCapacity an <code>int</code> value
     */
    public TIntIntHashMap( int initialCapacity ) {
        super( initialCapacity );
    }


    /**
     * Creates a new <code>T#K##V#HashMap</code> instance with a prime
     * capacity equal to or greater than <tt>initialCapacity</tt> and
     * with the specified load factor.
     *
     * @param initialCapacity an <code>int</code> value
     * @param loadFactor a <code>float</code> value
     */
    public TIntIntHashMap( int initialCapacity, float loadFactor ) {
        super( initialCapacity, loadFactor );
    }


    /**
     * Creates a new <code>T#K##V#HashMap</code> instance with a prime
     * capacity equal to or greater than <tt>initialCapacity</tt> and
     * with the specified load factor.
     *
     * @param initialCapacity an <code>int</code> value
     * @param loadFactor a <code>float</code> value
     * @param noEntryKey a <code>#k#</code> value that represents
     *                   <tt>null</tt> for the Key set.
     * @param noEntryValue a <code>#v#</code> value that represents
     *                   <tt>null</tt> for the Value set.
     */
    public TIntIntHashMap( int initialCapacity, float loadFactor,
        int noEntryKey, int noEntryValue ) {
        super( initialCapacity, loadFactor, noEntryKey, noEntryValue );
    }


    /**
     * Creates a new <code>T#K##V#HashMap</code> instance containing
     * all of the entries in the map passed in.
     *
     * @param keys a <tt>#k#</tt> array containing the keys for the matching values.
     * @param values a <tt>#v#</tt> array containing the values.
     */
    public TIntIntHashMap( int[] keys, int[] values ) {
        super( Math.max( keys.length, values.length ) );

        int size = Math.min( keys.length, values.length );
        for ( int i = 0; i < size; i++ ) {
            this.put( keys[i], values[i] );
        }
    }


    /**
     * Creates a new <code>T#K##V#HashMap</code> instance containing
     * all of the entries in the map passed in.
     *
     * @param map a <tt>T#K##V#Map</tt> that will be duplicated.
     */
    public TIntIntHashMap( TIntIntMap map ) {
        super( map.size() );
        if ( map instanceof TIntIntHashMap ) {
            TIntIntHashMap hashmap = ( TIntIntHashMap ) map;
            this._loadFactor = hashmap._loadFactor;
            this.no_entry_key = hashmap.no_entry_key;
            this.no_entry_value = hashmap.no_entry_value;
            //noinspection RedundantCast
            if ( this.no_entry_key != ( int ) 0 ) {
                Arrays.fill( _set, this.no_entry_key );
            }
            //noinspection RedundantCast
            if ( this.no_entry_value != ( int ) 0 ) {
                Arrays.fill( _values, this.no_entry_value );
            }
            setUp( (int) Math.ceil( DEFAULT_CAPACITY / _loadFactor ) );
        }
        putAll( map );
    }


    /**
     * initializes the hashtable to a prime capacity which is at least
     * <tt>initialCapacity + 1</tt>.
     *
     * @param initialCapacity an <code>int</code> value
     * @return the actual capacity chosen
     */
    protected int setUp( int initialCapacity ) {
        int capacity;

        capacity = super.setUp( initialCapacity );
        _values = new int[capacity];
        return capacity;
    }


    /**
     * rehashes the map to the new capacity.
     *
     * @param newCapacity an <code>int</code> value
     */
    
    private class Rehash1 implements Runnable	{
    	int min, max;
    	int[] oldKeys, oldValues;
    	byte[] oldStates;
    	Rehash1 (int a, int b, int[] oldK, int[] oldV, byte[] oldS){
    		min=a; max=b; oldKeys=oldK; oldValues=oldV; oldStates=oldS;
    	}
    	public void run ()	{
    		for (int i=min; i<max; i++) {
                if(oldStates[i] == FULL ) {
                    int o = oldKeys[i];
                    int index = insertKey( o );
                    _values[index] = oldValues[i];
                }
            }
    	}
    }
     /** {@inheritDoc} */
    protected void rehash (int newCapacity, int numProcs)	{
    	int oldCapacity = _set.length;
    	int oldKeys[] = _set;
    	int oldValues[] = _values;
    	byte oldStates[] = _states;
    	
    	_set = new int[newCapacity];
    	_values = new int[newCapacity];
    	_states = new byte[newCapacity];
    	int numElemProc = oldCapacity/numProcs;
    	for (int i=0; i<numProcs; i++)	{
    		int numMax = (i+1) * numElemProc;
    		if ((i+1) == numProcs)	numMax = oldCapacity;
    		threads[i] = new Thread (new Rehash1 (i*numElemProc, numMax, oldKeys, oldValues, oldStates));
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
    protected void rehash( int newCapacity ) {
        int oldCapacity = _set.length;
        
        int oldKeys[] = _set;
        int oldVals[] = _values;
        byte oldStates[] = _states;

        _set = new int[newCapacity];
        _values = new int[newCapacity];
        _states = new byte[newCapacity];

        for ( int i = oldCapacity; i-- > 0; ) {
            if( oldStates[i] == FULL ) {
                int o = oldKeys[i];
                int index = insertKey( o );
                _values[index] = oldVals[i];
            }
        }
    }
    public void pruebaRehash (int capacity, int numProcs)	{
    	rehash (capacity, numProcs);
    }
    public void pruebaRehash (int capacity)	{
    	rehash (capacity);
    }

    /** {@inheritDoc} */
    public int put( int key, int value, int numProcs ) {
        int index = insertKey( key, numProcs );
        return doPut( key, value, index );
    }
    
    /** {@inheritDoc} */
    public int put( int key, int value ) {
        int index = insertKey( key );
        return doPut( key, value, index );
    }


    /** {@inheritDoc} */
    public int putIfAbsent( int key, int value ) {
        int index = insertKey( key );
        if (index < 0)
            return _values[-index - 1];
        return doPut( key, value, index );
    }


    private int doPut( int key, int value, int index ) {
        int previous = no_entry_value;
        boolean isNewMapping = true;
        if ( index < 0 ) {
            index = -index -1;
            previous = _values[index];
            isNewMapping = false;
        }
        _values[index] = value;

        if (isNewMapping) {
            postInsertHook( consumeFreeSlot );
        }

        return previous;
    }
    
    
    private class PutAll1 implements Runnable	{
    	int min, max;
    	Entry<? extends Integer, ? extends Integer>[] entradas;
    	PutAll1 (int a, int b, Entry<? extends Integer, ? extends Integer>[] e)	{
    		min=a; max=b; entradas = e;
    	}
    	public void run ()	{
    		for (int i=min; i<max; i++)	{
    			put (entradas[i].getKey().intValue(), entradas[i].getValue().intValue());
    		}
    	}
    }
    
    /** {@inheritDoc} */
    public void putAll( Map<? extends Integer, ? extends Integer> map, int numProcs ) {
        ensureCapacity( map.size() );
        int numElemProc = map.size()/numProcs;
        Entry<? extends Integer, ? extends Integer>[] entradas;
        entradas = map.entrySet().toArray (new Entry[map.size()]);
        for (int i=0; i<numProcs; i++)	{
        	int numMax = (i+1)*numElemProc;
        	if ((i+1) == numProcs)	numMax = map.size();
        	threads[i] = new Thread (new PutAll1 (i*numElemProc, numMax, entradas));
        	threads[i].start();
        }
        for (int i=0; i<numProcs; i++)
			try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        // could optimize this for cases when map instanceof THashMap
        /**for ( Map.Entry<? extends Integer, ? extends Integer> entry : map.entrySet() ) {
            this.put( entry.getKey().intValue(), entry.getValue().intValue() );
        }*/
    }
    public void putAll( Map<? extends Integer, ? extends Integer> map ) {
        ensureCapacity( map.size() );
        // could optimize this for cases when map instanceof THashMap
        for ( Map.Entry<? extends Integer, ? extends Integer> entry : map.entrySet() ) {
            this.put( entry.getKey().intValue(), entry.getValue().intValue() );
        }
    }
    
    private class PutAll2 implements Runnable	{
    	int min, max;
    	int[] keys, values;
    	PutAll2 (int a, int b, int[] c, int[] d)	{
    		min = a; max = b; keys = c; values = d;
    	}
    	public void run ()	{
    		for (int i=min; i<max; i++)	{
    			put (keys[i], values[i]);
    		}
    	}
    }
    /** {@inheritDoc} */
    public void putAll( TIntIntMap map, int numProcs) {
        ensureCapacity( map.size() );
        int numElemProc = map.size()/numProcs;
        for (int i=0; i<numProcs; i++)	{
        	int numMax = (i+1) * numElemProc;
        	if ((i+1) == numProcs)	numMax = _values.length;
        	threads[i] = new Thread (new PutAll2 (i*numElemProc, numMax, map.keys(), map.values()));
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
    public void putAll( TIntIntMap map ) {
        ensureCapacity( map.size() );
        TIntIntIterator iter = map.iterator();
        while ( iter.hasNext() ) {
            iter.advance();
            this.put( iter.key(), iter.value() );
        }
    }


    /** {@inheritDoc} */
    public int get( int key ) {
        int index = index( key );
        return index < 0 ? no_entry_value : _values[index];
    }
    
    //@Override
    public int getOrDefault(int key, int defaultValue) {
    	int index = index(key);
        return index < 0 ? defaultValue : _values[index];
    }

    private class Clear1 implements Runnable	{
    	public void run ()	{
            Arrays.fill (_set, 0, _set.length, no_entry_key);
    	}
    }
    private class Clear2 implements Runnable	{
    	public void run ()	{
    		Arrays.fill (_values, 0, _values.length, no_entry_value);
    	}
    }
    private class Clear3 implements Runnable	{
    	public void run ()	{
    		Arrays.fill (_states, 0, _states.length, FREE);
    	}
    }
    
    public void clear ()	{
    	//if (numProcs > 2){
    		Thread t1 = new Thread (new Clear1 ());
    		t1.start();
    		Thread t2 = new Thread (new Clear2 ());
    		t2.start();
    		Thread t3 = new Thread (new Clear3 ());
    		t3.start();
    		try {
    			t1.join(); t2.join(); t3.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	//}
    	
    }
    
    public void clear3 ()	{
    	super.clear();
		Arrays.fill (_states, 0, _states.length, FREE);
    }
    	

    /** {@inheritDoc} */
    public void clear2() {
        super.clear();
        Arrays.fill( _set, 0, _set.length, no_entry_key );
        Arrays.fill( _values, 0, _values.length, no_entry_value );
        Arrays.fill( _states, 0, _states.length, FREE );
    }


    /** {@inheritDoc} */
    public boolean isEmpty() {
        return 0 == _size;
    }


    /** {@inheritDoc} */
    public int remove( int key ) {
        int prev = no_entry_value;
        int index = index( key );
        if ( index >= 0 ) {
            prev = _values[index];
            removeAt( index );    // clear key,state; adjust size
        }
        return prev;
    }

    public boolean remove (int key, int value)	{
    	int index = index (key);
    	if ((index > 0) && (_values[index] == value))	{
    		removeAt (index);
    		return true;
    	}
    	return false;
    }

    /** {@inheritDoc} */
    protected void removeAt( int index ) {
        _values[index] = no_entry_value;
        super.removeAt( index );  // clear key, state; adjust size
    }
    
    public Integer replace (int key, int value)	{
    	int index = index (key);
    	if (index > 0)	{
    		int oldValue = _values[index];
    		_values[index] = value;
    		return (Integer)oldValue;
    	}
    	return null;
    }

    public boolean replace (int key, int oldValue, int newValue)	{
    	int index = index (key);
    	if ((index > 0) && (_values[index] == oldValue))	{
    		_values[index] = newValue;
    		return true;
    	}
    	return false;
    }
    
    private class ReplaceAll1 implements Runnable	{
    	int min, max;
    	BiFunction <? super Integer, ? super Integer, ? extends Integer> function;
    	ReplaceAll1 (int a, int b, BiFunction <? super Integer, ? super Integer,
			? extends Integer> c)	{
    		min = a; max = b; function = c;
    	}
    	public void run ()	{
    		for (int i=min; i<max; i++)	{
    			if (_states[i] == FULL)	{
    				_values[i] = function.apply(_set[i], _values[i]);
    				boolGlobal = true;
    			}
    		}
    	}
    }
    public boolean replaceAll (BiFunction <? super Integer, ? super Integer,
    			? extends Integer> function, int numProcs)	{
    	boolGlobal = false;
    	int numElemProc = _values.length/numProcs;
    	for (int i=0; i<numProcs; i++)	{
    		int numMax = (i+1) * numElemProc;
    		if ((i+1) == numProcs)	numMax = _values.length;
    		threads[i] = new Thread (new ReplaceAll1 (i*numElemProc, numMax, function));
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
    
    public boolean replaceAll (BiFunction 
    			<? super Integer, ? super Integer, ? extends Integer> function)	{
    	boolean replaced = false;
    	for (int i=0; i<_values.length; i++)	{
    		if (_states[i] == FULL)	{
    			int newValue = function.apply(_set[i], _values[i]);
    			_values[i] = newValue;
    			replaced = true;
    		}
    	}
    	return replaced;
    }
    /** {@inheritDoc} */
    public TIntSet keySet() {
        return new TKeyView();
    }


    private class Keys1 implements Runnable	{
    	int min, max;
    	Keys1 (int a, int b)	{
    		min=a; max=b;
    	}
    	int[] intsLocal = new int[_size];
    	public void run()	{
    		int j=0, k;
    		for (int i=min; i<max; i++) {
    			if (_states[i] == FULL ) {
    				intsLocal[j++] = _set[i];
    			}
    		}
    		synchronized (this){
    			intGlobal += j;
    			k = intGlobal - j;
    		}
    		for (int i=0; i<j; i++)	{
    			intsGlobal[k+i] = intsLocal[i];
    		}
    	}
    }
    
    private class Keys2 implements Runnable	{
    	int min, max;
    	Keys2 (int a, int b)	{
    		min=a; max=b;
    	}
    	public void run()	{
    		int j=0, k;
    		for (int i=min; i<max; i++) {
    			if (_states[i] == FULL ) {
    				j++;
    			}
    		}
    		synchronized (this){
    			intGlobal += j;
    			k = intGlobal - j;
    		}
    		for (int i=min; i<max; i++) {
    			if (_states[i] == FULL ) {
    				intsGlobal[k] = _set[i];
    				k++;
    			}
    		}
    	}
    }
    /** {@inheritDoc} */
    public int[] keys(int numProcs)	{
    	int numElemsProc = _set.length/numProcs;
    	intGlobal = 0;
    	intsGlobal = new int[size()];
    	for (int i=0; i<numProcs; i++)	{
    		int numMax = (i+1) * numElemsProc;
    		if ((i+1) == numProcs)	numMax = _set.length;
    		threads[i] = new Thread (new Keys1 (numElemsProc*i, numMax));
    		threads[i].start();
    	}
    	for (int i=0; i<numProcs; i++)
			try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	return intsGlobal;
    }
    public int[] keys2(int numProcs)	{
    	int numElemsProc = _set.length/numProcs;
    	intGlobal = 0;
    	intsGlobal = new int[size()];
    	for (int i=0; i<numProcs; i++)	{
    		int numMax = (i+1) * numElemsProc;
    		if ((i+1) == numProcs)	numMax = _set.length;
    		threads[i] = new Thread (new Keys2 (numElemsProc*i, numMax));
    		threads[i].start();
    	}
    	for (int i=0; i<numProcs; i++)
			try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	return intsGlobal;
    }
    public int[] keys() {
        int[] keys = new int[size()];
        int[] k = _set;
        byte[] states = _states;

        for ( int i = k.length, j = 0; i-- > 0; ) {
          if ( states[i] == FULL ) {
            keys[j++] = k[i];
          }
        }
        return keys;
    }


    /** {@inheritDoc} */
    public int[] keys( int[] array ) {
        int size = size();
        if ( array.length < size ) {
            array = new int[size];
        }

        int[] keys = _set;
        byte[] states = _states;

        for ( int i = keys.length, j = 0; i-- > 0; ) {
          if ( states[i] == FULL ) {
            array[j++] = keys[i];
          }
        }
        return array;
    }


    /** {@inheritDoc} */
    public TIntCollection valueCollection() {
        return new TValueView();
    }

    private class Values1 implements Runnable	{
    	int min, max;
    	Values1 (int a, int b)	{
    		min=a; max=b;
    	}
    	int[] intsLocal;
    	public void run()	{
    		int j=0, k;
    		for (int i=min; i<max; i++) {
    			if (_states[i] == FULL ) {
    				intsLocal[j++] = _values[i];
    			}
    		}
    		synchronized (this){
    			intGlobal += j;
    			k = intGlobal - j;
    		}
    		for (int i=0; i<j; i++)	{
    			intsGlobal[k+i] = intsLocal[i];
    		}
    	}
    }
    
    private class Values2 implements Runnable	{
    	int min, max;
    	Values2 (int a, int b)	{
    		min=a; max=b;
    	}
    	public void run()	{
    		int j=0, k;
    		for (int i=min; i<max; i++) {
    			if (_states[i] == FULL ) {
    				j++;
    			}
    		}
    		synchronized (this){
    			intGlobal += j;
    			k = intGlobal - j;
    		}
    		for (int i=min; i<max; i++) {
    			if (_states[i] == FULL ) {
    				intsGlobal[k] = _values[i];
    				k++;
    			}
    		}
    	}
    }
    /** {@inheritDoc} */
    public int[] values (int numProcs)	{
    	intsGlobal = new int[size()];
    	int numElemProc = _values.length/numProcs;
    	for (int i=0; i<numProcs; i++)	{
    		int numMax = (i+1) * numElemProc;
    		if ((i+1) == numProcs)	numMax = _values.length;
    		threads[i] = new Thread (new Values1 (numElemProc*i, numMax));
    		threads[i].start();
    	}
    	for (int i=0; i<numProcs; i++)
			try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	return intsGlobal;
    }
    public int[] values2 (int numProcs)	{
    	intsGlobal = new int[size()];
    	int numElemProc = _values.length/numProcs;
    	for (int i=0; i<numProcs; i++)	{
    		int numMax = (i+1) * numElemProc;
    		if ((i+1) == numProcs)	numMax = _values.length;
    		threads[i] = new Thread (new Values2 (numElemProc*i, numMax));
    		threads[i].start();
    	}
    	for (int i=0; i<numProcs; i++)
			try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	return intsGlobal;
    }
    public int[] values() {
        int[] vals = new int[size()];
        int[] v = _values;
        byte[] states = _states;

        for ( int i = v.length, j = 0; i-- > 0; ) {
          if ( states[i] == FULL ) {
            vals[j++] = v[i];
          }
        }
        return vals;
    }

    private class Values3 implements Runnable	{
    	int min, max, idProc;
    	Values3 (int a, int b, int c)	{
    		min = a; max = b; idProc = c;
    	}
    	public void run ()	{
    		int[] intsLocal = new int[max-min];
    		int j=0;
    		for (int i=min; i<max; i++) {
    			if (_states[i] == FULL ) {
    				intsLocal[j++] = _values[i];
    			}
    	    }
    		if (idProc>0)
				try {
					threads[idProc-1].join();
				} catch (InterruptedException e) {
					e.printStackTrace();
			}
    		System.arraycopy(intsLocal, 0, intsGlobal, intGlobal, j);
    		intGlobal += j;
    	}
    }
    /** {@inheritDoc} */
    public int[] values (int[] array, int numProcs)	{
    	if (array.length < size())
    		array = new int[size()];
    	intsGlobal = new int[_values.length];
    	intGlobal = 0;
    	int numElemProc = array.length/numProcs;
    	for (int i=0; i<numProcs; i++)	{
    		int numMax = (i+1) * numElemProc;
    		threads[i] = new Thread (new Values3 (i*numElemProc, numMax, i));
    		threads[i].start();
    	}
    	array = intsGlobal;
    	return array;
    }
    public int[] values( int[] array ) {
        int size = size();
        if ( array.length < size ) {
            array = new int[size];
        }

        int[] v = _values;
        byte[] states = _states;

        for ( int i = v.length, j = 0; i-- > 0; ) {
          if ( states[i] == FULL ) {
            array[j++] = v[i];
          }
        }
        return array;
    }
    public int[] values2( int[] array ) {
        int size = size();
        if ( array.length < size ) {
            array = new int[size];
        }
        System.arraycopy(_values, 0, array, 0, _values.length);
        return array;
    }

    private class ContainsValue1 implements Runnable	{
    	private int val, min, max;
    	ContainsValue1 (int value, int a, int b)	{
    		val = value; min = a; max = b;
    	}
    	public void run ()	{
    		for (int i=min; i<max; i++)	{
    			if (boolGlobal) return;
    			if (_states[i]==FULL && val==_values[i])	{
    				boolGlobal = true;
    				return;
    			}
    		}
    	}
    }
    public boolean containsValue (int val, int numProcs){
    	int numElProc = _values.length/numProcs;
    	boolGlobal = false;
    	for (int i=0; i<numProcs; i++)	{
    		int numMax = (i+1) * numElProc;
    		if ((i+1) == numProcs)	numMax = _values.length;
    		threads[i] = new Thread (new ContainsValue1 (val, i*numElProc, numMax));
    		threads[i].start();
    	}
    	for (int i=0; i<numProcs; i++)
			try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	return boolGlobal;
    }

    /** {@inheritDoc} */
    public boolean containsValue( int val ) {
        byte[] states = _states;
        int[] vals = _values;

        for ( int i = vals.length; i-- > 0; ) {
            if ( states[i] == FULL && val == vals[i] ) {
                return true;
            }
        }
        return false;
    }


    /** {@inheritDoc} */
    public boolean containsKey( int key ) {
        return contains( key );
    }


    /** {@inheritDoc} */
    public TIntIntIterator iterator() {
        return new TIntIntHashIterator( this );
    }


    /** {@inheritDoc} */
    public boolean forEachKey( TIntProcedure procedure ) {
        return forEach( procedure );
    }


    private class ForEachValue1 implements Runnable	{
    	TIntProcedure procedure;
    	int min, max;
    	ForEachValue1 (TIntProcedure proc, int a, int b)	{
    		procedure=proc; min=a; max=b;
    	}
    	public void run ()	{
    		for (int i=min; i<max; i++) {
                if (_states[i] == FULL && ! procedure.execute(_values[i] ) ) {
                    boolGlobal = false;
                	return;
                }
            }
    	}
    }
    /** {@inheritDoc} */
    public boolean forEachValue (TIntProcedure procedure, int numProcs)	{
    	int numElemProc = _values.length/numProcs;
    	boolGlobal = true;
    	for (int i=0; i<numProcs; i++){
    		int numMax = (i+1)*numProcs;
    		if ((i+1) == numProcs)	numMax = _values.length;
    		threads[i] = new Thread (new ForEachValue1 (procedure, numElemProc*i, numMax));
    		threads[i].start();
    	}
    	for (int i=0; i<numProcs; i++)
			try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	return boolGlobal;
    }
    public boolean forEachValue( TIntProcedure procedure ) {
        byte[] states = _states;
        int[] values = _values;
        for ( int i = values.length; i-- > 0; ) {
            if ( states[i] == FULL && ! procedure.execute( values[i] ) ) {
                return false;
            }
        }
        return true;
    }
    
    private class ForEachEntry1 implements Runnable	{
    	TIntIntProcedure procedure;
    	int min, max;
    	ForEachEntry1 (TIntIntProcedure proc, int a, int b)	{
    		procedure=proc; min=a; max=b;
    	}
    	public void run ()	{
    		for (int i=min; i<max; i++)	{
    			if (_states[i] == FULL && ! procedure.execute(_set[i], _values[i] ) ) {
    				boolGlobal = false;
    				return;
    			}
            }
    	}
    }

    /** {@inheritDoc} */
    public boolean forEachEntry (TIntIntProcedure procedure, int numProcs)	{
    	int numElemProc = _set.length/numProcs;
    	boolGlobal = true;
    	for (int i=0; i<numProcs; i++)	{
    		int numMax = (i+1) * numElemProc;
    		if ((i+1) == numProcs)	numMax = _set.length;
    		threads[i] = new Thread (new ForEachEntry1 (procedure, i*numElemProc, numMax));
    		threads[i].start();
    	}
    	for (int i=0; i<numProcs; i++)
			try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	return boolGlobal;
    }
    public boolean forEachEntry( TIntIntProcedure procedure ) {
        byte[] states = _states;
        int[] keys = _set;
        int[] values = _values;
        for ( int i = keys.length; i-- > 0; ) {
            if ( states[i] == FULL && ! procedure.execute( keys[i], values[i] ) ) {
                return false;
            }
        }
        return true;
    }
    

    private class TransformValues1 implements Runnable	{
    	int min, max;
    	TIntFunction function;
    	TransformValues1 (int a, int b, TIntFunction fun){
    		min=a; max=b; function=fun;
    	}
    	public void run ()	{
    		for (int i=min; i<max; i++)	{
    			if (_states[i] == FULL)	{
    				_values[i] = function.execute (_values[i]);
    			}
    		}    		
    	}
    }
    /** {@inheritDoc} */
    public void transformValues (TIntFunction function, int numProcs)	{
    	int numElemProc = _values.length/numProcs;
    	for (int i=0; i<numProcs; i++)	{
    		int numMax = (i+1) * numElemProc;
    		if ((i+1) == numProcs)	numMax = _values.length;
    		threads[i] = new Thread (new TransformValues1 (numElemProc*i, numMax, function));
    		threads[i].start();
    	}
    	for (int i=0; i<numProcs; i++)
			try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    }
    public void transformValues( TIntFunction function ) {
        byte[] states = _states;
        int[] values = _values;
        for ( int i = values.length; i-- > 0; ) {
            if ( states[i] == FULL ) {
                values[i] = function.execute( values[i] );
            }
        }
    }
    
  //@Override
    public Integer computeIfAbsent (int key, 
    			Function<? super Integer, ? extends Integer> mappingFunction)	{
    	int index = index( key );
    	Integer value = mappingFunction.apply((Integer) key);
    	if (value == null)	{
    		return null;
    	}
        if ((index < 0) || ((index >= 0) && (_values[index] == no_entry_value))) {
           put (key, value);
           return (Integer) value;
        }
        return null;
    }
    
    public Integer computeIfPresent (int key,
    				BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction)	{
    	int index = index( key );
    	Integer value = null;;
    	if (index > 0)	value = remappingFunction.apply((Integer)key, (Integer)_values[index]);
    	if (index > 0) {
    		if (value != null)	{
    			put (key, value.intValue());
    			return (Integer) value;
    		}
    		else	removeAt (index);
        }
        return null;
    }

    //@Override
    public Integer compute (int key,
    			BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction)	{
    	int index = index( key );
    	Integer value = null;
    	if (index > 0)	value = remappingFunction.apply((Integer)key, (Integer)_values[index]);
    	if (value != null)	{
    		put (key, value.intValue());
    		return (Integer) value;
    	}
    	else if (index > 0)	removeAt (index);
        return null;
    }
    
    public Integer merge (int key, Integer value,
    			BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction)	{
    	if (value == null)
            throw new NullPointerException();
        if (remappingFunction == null)
            throw new NullPointerException();
    	int index = index (key);
    	if ((index < 0) || (_values[index] == no_entry_value))	{
    		put (key, value);
    		return value;
    	}
    	Integer newValue = remappingFunction.apply ((Integer)_values[index], value);
    	if (newValue != null)	{
    		put (key, newValue);
    		return newValue;
    	}
    	removeAt (index);
    	return null;
    }
    /**@Override
    public V merge(K key, V value,
                   BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        if (value == null)
            throw new NullPointerException();
        if (remappingFunction == null)
            throw new NullPointerException();
        int hash = hash(key);
        Node<K,V>[] tab; Node<K,V> first; int n, i;
        int binCount = 0;
        TreeNode<K,V> t = null;
        Node<K,V> old = null;
        if (size > threshold || (tab = table) == null ||
            (n = tab.length) == 0)
            n = (tab = resize()).length;
        if ((first = tab[i = (n - 1) & hash]) != null) {
            if (first instanceof TreeNode)
                old = (t = (TreeNode<K,V>)first).getTreeNode(hash, key);
            else {
                Node<K,V> e = first; K k;
                do {
                    if (e.hash == hash &&
                        ((k = e.key) == key || (key != null && key.equals(k)))) {
                        old = e;
                        break;
                    }
                    ++binCount;
                } while ((e = e.next) != null);
            }
        }
        if (old != null) {
            V v;
            if (old.value != null)
                v = remappingFunction.apply(old.value, value);
            else
                v = value;
            if (v != null) {
                old.value = v;
                afterNodeAccess(old);
            }
            else
                removeNode(hash, key, null, false, true);
            return v;
        }
        if (value != null) {
            if (t != null)
                t.putTreeVal(this, tab, hash, key, value);
            else {
                tab[i] = newNode(hash, key, value, first);
                if (binCount >= TREEIFY_THRESHOLD - 1)
                    treeifyBin(tab, hash);
            }
            ++modCount;
            ++size;
            afterNodeInsertion(true);
        }
        return value;
    }*/
    private class RetainEntries1 implements Runnable	{
    	int min, max;
    	TIntIntProcedure procedure;
    	RetainEntries1 (int a, int b, TIntIntProcedure proc)	{
    		min=a; max=b; procedure = proc;
    	}
    	public void run ()	{
    		try	{
    			for (int i=min; i<max; i++) {
                    if (_states[i] == FULL && ! procedure.execute(_set[i], _values[i])) {
                        removeAt(i);
                        boolGlobal = true;
                    }
                }
    		}
    		finally	{}
    	}
    }
    /** {@inheritDoc} */
    public boolean retainEntries (TIntIntProcedure procedure, int numProcs)	{
    	boolGlobal = false;
    	tempDisableAutoCompaction ();
    	int numElemProc = _set.length/numProcs;
    	try	{
    		for (int i=0; i<numProcs; i++)	{
    			int numMax = (i+1) * numElemProc;
    			if ((i+1) == numProcs)	numMax = _set.length;
    			threads[i] = new Thread (new RetainEntries1 (i*numElemProc, numMax, procedure));
    			threads[i].start();
    		}
    	}
    	finally	{
    		for (int i=0; i<numProcs; i++)
				try {
					threads[i].join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
    		reenableAutoCompaction (true);
    	}
    	return boolGlobal;
    }
    public boolean retainEntries( TIntIntProcedure procedure ) {
        boolean modified = false;
        byte[] states = _states;
        int[] keys = _set;
        int[] values = _values;


        // Temporarily disable compaction. This is a fix for bug #1738760
        tempDisableAutoCompaction();
        try {
            for ( int i = keys.length; i-- > 0; ) {
                if ( states[i] == FULL && ! procedure.execute( keys[i], values[i] ) ) {
                    removeAt( i );
                    modified = true;
                }
            }
        }
        finally {
            reenableAutoCompaction( true );
        }

        return modified;
    }


    /** {@inheritDoc} */
    public boolean increment( int key ) {
        return adjustValue( key, ( int ) 1 );
    }


    /** {@inheritDoc} */
    public boolean adjustValue( int key, int amount ) {
        int index = index( key );
        if (index < 0) {
            return false;
        } else {
            _values[index] += amount;
            return true;
        }
    }


    /** {@inheritDoc} */
    public int adjustOrPutValue( int key, int adjust_amount, int put_amount ) {
        int index = insertKey( key );
        final boolean isNewMapping;
        final int newValue;
        if ( index < 0 ) {
            index = -index -1;
            newValue = ( _values[index] += adjust_amount );
            isNewMapping = false;
        } else {
            newValue = ( _values[index] = put_amount );
            isNewMapping = true;
        }

        byte previousState = _states[index];

        if ( isNewMapping ) {
            postInsertHook(consumeFreeSlot);
        }

        return newValue;
    }


    /** a view onto the keys of the map. */
    protected class TKeyView implements TIntSet {

        /** {@inheritDoc} */
        public TIntIterator iterator() {
            return new TIntIntKeyHashIterator( TIntIntHashMap.this );
        }


        /** {@inheritDoc} */
        public int getNoEntryValue() {
            return no_entry_key;
        }


        /** {@inheritDoc} */
        public int size() {
            return _size;
        }


        /** {@inheritDoc} */
        public boolean isEmpty() {
            return 0 == _size;
        }


        /** {@inheritDoc} */
        public boolean contains( int entry ) {
            return TIntIntHashMap.this.contains( entry );
        }


        /** {@inheritDoc} */
        public int[] toArray() {
            return TIntIntHashMap.this.keys();
        }


        /** {@inheritDoc} */
        public int[] toArray( int[] dest ) {
            return TIntIntHashMap.this.keys( dest );
        }


        /**
         * Unsupported when operating upon a Key Set view of a T#K##V#Map
         * <p/>
         * {@inheritDoc}
         */
        public boolean add( int entry ) {
            throw new UnsupportedOperationException();
        }


        /** {@inheritDoc} */
        public boolean remove( int entry ) {
            return no_entry_value != TIntIntHashMap.this.remove( entry );
        }

        private class ContainsAll1 implements Runnable	{
        	int min, max;
        	Integer[] array;
        	ContainsAll1 (int a, int b, Integer[] c)	{
        		min = a; max = b; array = c;
        	}
        	public void run ()	{
        		for (int i=min; i<max; i++) {
        			if (!boolGlobal)	return;
                    if (array[i] instanceof Integer ) {
                        int ele = ( ( Integer ) array[i] ).intValue();
                        if ( ! TIntIntHashMap.this.containsKey( ele ) ) {
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
        public boolean containsAll (Collection<?> collection, int numProcs)	{
        	Integer[] array = collection.toArray(new Integer [collection.size()]);
        	boolGlobal = true;
        	int numElemProc = collection.size()/numProcs;
        	for (int i=0; i<numProcs; i++)	{
        		int numMax = (i+1) * numElemProc;
        		if ((i+1) == numProcs) numMax = collection.size();
        		threads[i] = new Thread (new ContainsAll1 (i*numElemProc, numMax, array));
        		threads[i].start();
        	}
        	for (int i=0; i<numProcs; i++)
				try {
					threads[i].join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
        	return boolGlobal;
        }
        public boolean containsAll( Collection<?> collection ) {
            for ( Object element : collection ) {
                if ( element instanceof Integer ) {
                    int ele = ( ( Integer ) element ).intValue();
                    if ( ! TIntIntHashMap.this.containsKey( ele ) ) {
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
        	int[] array;
        	ContainsAll2 (int a, int b, int[] c)	{
        		min = a; max = b; array = c;
        	}
        	public void run ()	{
        		for (int i=min; i<max; i++) {
        			if (!boolGlobal)	return;
                    if (!TIntIntHashMap.this.containsKey (array[i])) {
                        boolGlobal = false;
                        return;
                    }
                }
        	}
        }
        /** {@inheritDoc} */
        public boolean containsAll (TIntCollection collection, int numProcs)	{
        	boolGlobal = true;
        	int numElemProc = collection.size()/numProcs;
        	int[] array = collection.toArray (new int[collection.size()]);
        	for (int i=0; i<numProcs; i++)	{
        		int numMax = (i+1) * numElemProc;
        		if ((i+1) == numProcs) numMax = array.length;
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
        public boolean containsAll( TIntCollection collection ) {
            TIntIterator iter = collection.iterator();
            while ( iter.hasNext() ) {
                if ( ! TIntIntHashMap.this.containsKey( iter.next() ) ) {
                    return false;
                }
            }
            return true;
        }

        private class ContainsAll3 implements Runnable	{
        	int min, max;
        	int[] array;
        	ContainsAll3 (int a, int b, int[] c)	{
        		min = a; max = b; array = c;
        	}
        	public void run ()	{
        		for (int i=min; i<max; i++) {
        			if (!boolGlobal)	return;
                    if ( ! TIntIntHashMap.this.contains( array[i] ) ) {
                        boolGlobal = false;
                        return;
                    }
                }
        	}
        }
        /** {@inheritDoc} */
        public boolean containsAll (int[] array, int numProcs)	{
        	boolGlobal = false;
        	int numElemProc = array.length/numProcs;
        	for (int i=0; i<numProcs; i++)	{
        		int numMax = (i+1) * numElemProc;
        		threads[i] = new Thread (new ContainsAll3 (i*numElemProc, numMax, array));
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
        public boolean containsAll( int[] array ) {
            for ( int element : array ) {
                if ( ! TIntIntHashMap.this.contains( element ) ) {
                    return false;
                }
            }
            return true;
        }


        /**
         * Unsupported when operating upon a Key Set view of a T#K##V#Map
         * <p/>
         * {@inheritDoc}
         */
        public boolean addAll( Collection<? extends Integer> collection ) {
            throw new UnsupportedOperationException();
        }


        /**
         * Unsupported when operating upon a Key Set view of a T#K##V#Map
         * <p/>
         * {@inheritDoc}
         */
        public boolean addAll( TIntCollection collection ) {
            throw new UnsupportedOperationException();
        }


        /**
         * Unsupported when operating upon a Key Set view of a T#K##V#Map
         * <p/>
         * {@inheritDoc}
         */
        public boolean addAll( int[] array ) {
            throw new UnsupportedOperationException();
        }

        private class RetainAll1 implements Runnable	{
        	int min, max;
        	Collection<?> collection;
        	RetainAll1 (int a, int b, Collection<?> c)	{
        		min = a; max = b; collection = c;
        	}
        	public void run ()	{
        		for (int i=min; i<max; i++) {
        			if (boolGlobal)	return;
                    if (!collection.contains (Integer.valueOf (_set[i]) ) ) {
                        removeAt(i);
                        boolGlobal = true;
                        return;
                    }
                }
        	}
        }
        /** {@inheritDoc} */
        @SuppressWarnings({"SuspiciousMethodCalls"})
        public boolean retainAll (Collection<?> collection, int numProcs)	{
        	boolGlobal = false;
        	int numElemProc = _set.length/numProcs;
        	for (int i=0; i<numProcs; i++)	{
        		int numMax = (i+1) * numProcs;
        		if ((i+1) == numProcs) numMax = _set.length;
        		threads[i] = new Thread (new RetainAll1 (i*numElemProc, numMax, collection));
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
        public boolean retainAll( Collection<?> collection ) {
            boolean modified = false;
            TIntIterator iter = iterator();
            while ( iter.hasNext() ) {
                if ( ! collection.contains( Integer.valueOf ( iter.next() ) ) ) {
                    iter.remove();
                    modified = true;
                }
            }
            return modified;
        }


        private class RetainAll2 implements Runnable	{
        	int min, max;
        	TIntCollection collection;
        	RetainAll2 (int a, int b, TIntCollection c)	{
        		min = a; max = b; collection = c;
        	}
        	public void run ()	{
        		for (int i=min; i<max; i++) {
        			if (boolGlobal)	return;
                    if (!collection.contains (_set[i]) ) {
                        removeAt(i);
                        boolGlobal = true;
                        return;
                    }
                }
        	}
        }
        /** {@inheritDoc} */
        public boolean retainAll (TIntCollection collection, int numProcs)	{
        	if (this == collection)	return false;
        	boolGlobal = false;
        	int numElemProc = _set.length/numProcs;
        	for (int i=0; i<numProcs; i++)	{
        		int numMax = (i+1) * numElemProc;
        		if ((i+1) == numProcs) numMax = _set.length;
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
        public boolean retainAll( TIntCollection collection ) {
            if ( this == collection ) {
                return false;
            }
            boolean modified = false;
            TIntIterator iter = iterator();
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
        	int[] array;
        	RetainAll3 (int a, int b, int[] c)	{
        		min = a; max = b; array = c;
        	}
        	public void run()	{
        		for (int i=min; i<max; i++) {
        			if (boolGlobal)	return;
                    if (_states[i] == FULL && (Arrays.binarySearch (array, _set[i]) < 0)) {
                        removeAt (i);
                        boolGlobal = true;
                        return;
                    }
                }
        	}
        }
        /** {@inheritDoc} */
        public boolean retainAll (int[] array, int numProcs)	{
        	boolGlobal = false;
        	Arrays.sort(array);
        	int numElemProc = _set.length/numProcs;
        	for (int i=0; i<numProcs; i++)	{
        		int numMax = (i+1) * numElemProc;
        		if ((i+1) == numProcs) numMax = _set.length;
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
        public boolean retainAll( int[] array ) {
            boolean changed = false;
            Arrays.sort( array );
            int[] set = _set;
            byte[] states = _states;

            for ( int i = set.length; i-- > 0; ) {
                if ( states[i] == FULL && ( Arrays.binarySearch( array, set[i] ) < 0) ) {
                    removeAt( i );
                    changed = true;
                }
            }
            return changed;
        }

        private class RemoveAll1 implements Runnable	{
        	int min, max;
        	Integer[] array;
        	RemoveAll1 (int a, int b, Integer[] c)	{
        		min = a; max = b; array = c;
        	}
        	public void run ()	{
        		for (int i=min; i<max; i++) {
                    if (array[i] instanceof Integer ) {
                        int c = ((Integer) array[i]).intValue();
                        if ( remove( c ) ) {
                            boolGlobal = true;
                        }
                    }
                }
        	}
        }
        /** {@inheritDoc} */
        public boolean removeAll (Collection<?> collection, int numProcs)	{
        	boolGlobal = false;
        	int numElemProc = collection.size()/numProcs;
        	Integer[] array = collection.toArray (new Integer[collection.size()]);
        	for (int i=0; i<numProcs; i++)	{
        		int numMax = (i+1) * numElemProc;
        		if ((i+1) == numProcs)	numMax = collection.size();
        		threads[i] = new Thread (new RemoveAll1 (i*numElemProc, numMax, array));
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
        public boolean removeAll( Collection<?> collection ) {
            boolean changed = false;
            for ( Object element : collection ) {
                if ( element instanceof Integer ) {
                    int c = ( ( Integer ) element ).intValue();
                    if ( remove( c ) ) {
                        changed = true;
                    }
                }
            }
            return changed;
        }

        private class RemoveAll2 implements Runnable	{
        	int min, max;
        	int[] array;
        	RemoveAll2 (int a, int b, int[] c)	{
        		min = a; max = b; array = c;
        	}
        	public void run ()	{
                for (int i=min; i<max; i++) {
                    int element = array[i];
                    if ( remove( element ) ) {
                        boolGlobal = true;
                    }
                }
        	}
        }
        /** {@inheritDoc} */
        public boolean removeAll (TIntCollection collection, int numProcs)	{
        	if (this == collection)	{
        		clear();
        		return true;
        	}
        	boolGlobal = false;
        	int numElemProc = collection.size()/numProcs;
        	int[] array = collection.toArray (new int[collection.size()]);
        	for (int i=0; i<numProcs; i++)	{
        		int numMax = (i+1) * numElemProc;
        		if ((i+1) == numProcs)	numMax = collection.size();
        		threads[i] = new Thread (new RemoveAll2 (i*numElemProc, numMax, array));
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
        public boolean removeAll( TIntCollection collection ) {
            if ( this == collection ) {
                clear();
                return true;
            }
            boolean changed = false;
            TIntIterator iter = collection.iterator();
            while ( iter.hasNext() ) {
                int element = iter.next();
                if ( remove( element ) ) {
                    changed = true;
                }
            }
            return changed;
        }

        private class RemoveAll3 implements Runnable	{
        	int min, max;
        	int[] array;
        	RemoveAll3 (int a, int b, int[] c)	{
        		min = a; max = b; array = c;
        	}
        	public void run ()	{
        		for (int i=min; i<max; i++) {
                    if (remove (array[i])) {
                        boolGlobal = true;
                    }
                }
        	}
        }
        /** {@inheritDoc} */
        public boolean removeAll (int[] array, int numProcs)	{
        	boolGlobal = false;
        	int numElemProc = array.length/numProcs;
        	for (int i=0; i<numProcs;i++)	{
        		int numMax = numElemProc/numProcs;
        		if ((i+1) == 1)	numMax = array.length;
        		threads[i] = new Thread (new RemoveAll3 (i*numElemProc, numMax, array));
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
        public boolean removeAll( int[] array ) {
            boolean changed = false;
            for ( int i = array.length; i-- > 0; ) {
                if ( remove( array[i] ) ) {
                    changed = true;
                }
            }
            return changed;
        }


        /** {@inheritDoc} */
        public void clear() {
            TIntIntHashMap.this.clear();
        }


        /** {@inheritDoc} */
        public boolean forEach( TIntProcedure procedure ) {
            return TIntIntHashMap.this.forEachKey( procedure );
        }

        private class Equals1 implements Runnable	{
        	int min, max;
        	TIntSet that;
        	Equals1 (int a, int b, TIntSet c)	{
        		min = a; max = b; that = c;
        	}
        	public void run ()	{
                for (int i=min; i<max; i++) {
                	if (!boolGlobal)	return;
                    if (_states[i] == FULL) {
                        if (!that.contains(_set[i])) {
                            boolGlobal = false;
                            return;
                        }
                    }
                }
        	}
        }
        @Override
        public boolean equals (Object other, int numProcs)	{
        	if (!(other instanceof TIntSet))	return false;
        	final TIntSet that = (TIntSet) other;
        	if (that.size() != this.size())	return false;
        	boolGlobal = true;
        	int numElemProc = _states.length/numProcs;
        	for (int i=0; i<numProcs; i++)	{
        		int numMax = (i+1) * numElemProc;
        		if ((i+1) == numProcs)	numMax = _states.length;
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
        public boolean equals( Object other ) {
            if (! (other instanceof TIntSet)) {
                return false;
            }
            final TIntSet that = ( TIntSet ) other;
            if ( that.size() != this.size() ) {
                return false;
            }
            for ( int i = _states.length; i-- > 0; ) {
                if ( _states[i] == FULL ) {
                    if ( ! that.contains( _set[i] ) ) {
                        return false;
                    }
                }
            }
            return true;
        }

        private class HashCode1 implements Runnable	{
        	int min, max;
        	HashCode1 (int a, int b)	{
        		min = a; max = b;
        	}
        	public void run ()	{
        		int intLocal = 0;
        		for (int i=min; i<max; i++) {
                    if (_states[i] == FULL ) {
                        intLocal += HashFunctions.hash( _set[i] );
                    }
                }
        		synchronized (this)	{
        			intGlobal += intLocal;
        		}
        	}
        }
        @Override
        public int hashCode (int numProcs)	{
        	intGlobal = 0;
        	int numElemProc = _states.length/numProcs;
        	for (int i=0; i<numProcs; i++)	{
        		int numMax = (i+1) * numProcs;
        		if ((i+1) == numProcs)	numMax = _states.length;
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
        public int hashCode() {
            int hashcode = 0;
            for ( int i = _states.length; i-- > 0; ) {
                if ( _states[i] == FULL ) {
                    hashcode += HashFunctions.hash( _set[i] );
                }
            }
            return hashcode;
        }


        @Override
        public String toString() {
            final StringBuilder buf = new StringBuilder( "{" );
            forEachKey( new TIntProcedure() {
                private boolean first = true;


                public boolean execute( int key ) {
                    synchronized (this)	{
                    	if ( first ) {
                    		first = false;
                    	}
                    	else {
                    		buf.append( ", " );
                    	}
                    }
                    synchronized (this)	{
                    	buf.append( key );
                    }
                    return true;
                }
            } );
            synchronized (this)	{
            	buf.append( "}" );
            }
            return buf.toString();
        }
    }


    /** a view onto the values of the map. */
    protected class TValueView implements TIntCollection {

        /** {@inheritDoc} */
        public TIntIterator iterator() {
            return new TIntIntValueHashIterator( TIntIntHashMap.this );
        }


        /** {@inheritDoc} */
        public int getNoEntryValue() {
            return no_entry_value;
        }


        /** {@inheritDoc} */
        public int size() {
            return _size;
        }


        /** {@inheritDoc} */
        public boolean isEmpty() {
            return 0 == _size;
        }


        /** {@inheritDoc} */
        public boolean contains( int entry ) {
            return TIntIntHashMap.this.containsValue( entry );
        }


        /** {@inheritDoc} */
        public int[] toArray() {
            return TIntIntHashMap.this.values();
        }


        /** {@inheritDoc} */
        public int[] toArray( int[] dest ) {
            return TIntIntHashMap.this.values( dest );
        }



        public boolean add( int entry ) {
            throw new UnsupportedOperationException();
        }

        private class Remove1 implements Runnable	{
        	int min, max, entry;
        	Remove1 (int a, int b, int c)	{
        		min = a; max = b; entry = c;
        	}
        	public void run ()	{
        		for (int i=min; i<max; i++) {
        			if (boolGlobal)	return;
                    if ((_set[i] != FREE && _set[i] != REMOVED) && entry == _values[i]) {
                        removeAt (i);
                        boolGlobal = true;
                        return;
                    }
                }
        	}
        }
        /** {@inheritDoc} */
        public boolean remove (int entry, int numProcs)	{
        	boolGlobal = false;
        	int numElemProc = _values.length/numProcs;
        	for (int i=0; i<numProcs; i++)	{
        		int numMax = (i+1) * numElemProc;
        		if ((i+1) == numProcs)	numMax = _values.length;
        		threads[i] = new Thread (new Remove1 (i*numElemProc, numMax, entry));
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
        public boolean remove( int entry ) {
            int[] values = _values;
            int[] set = _set;

            for ( int i = values.length; i-- > 0; ) {
                if ( ( set[i] != FREE && set[i] != REMOVED ) && entry == values[i] ) {
                    removeAt( i );
                    return true;
                }
            }
            return false;
        }

        private class ContainsAll1 implements Runnable	{
        	int min, max;
        	Integer[] array;
        	ContainsAll1 (int a, int b, Integer[] c)	{
        		min = a; max = b; array = c;
        	}
        	public void run ()	{
        		for (int i=min; i<max; i++) {
        			if (!boolGlobal)	return;
                    if (array[i] instanceof Integer ) {
                        int ele = ((Integer) array[i]).intValue();
                        if (!TIntIntHashMap.this.containsValue (ele)) {
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
        public boolean containsAll (Collection<?> collection, int numProcs)	{
        	boolGlobal = true;
        	int numElemProc = collection.size()/numProcs;
        	Integer[] array = collection.toArray (new Integer[collection.size()]);
        	for (int i=0; i<numProcs; i++)	{
        		int numMax = (i+1) * numElemProc;
        		if ((i+1) == numProcs)	numMax = array.length;
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
        public boolean containsAll( Collection<?> collection ) {
            for ( Object element : collection ) {
                if ( element instanceof Integer ) {
                    int ele = ( ( Integer ) element ).intValue();
                    if ( ! TIntIntHashMap.this.containsValue( ele ) ) {
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
        	int[] array;
        	ContainsAll2 (int a, int b, int[] c)	{
        		min = a; max = b; array = c;
        	}
        	public void run ()	{
        		for (int i=min; i<max; i++) {
        			if (!boolGlobal)	return;
                    if (!TIntIntHashMap.this.containsValue(array[i])) {
                        boolGlobal = false;
                        return;
                    }
                }
        	}
        }
        /** {@inheritDoc} */
        public boolean containsAll (TIntCollection collection, int numProcs)	{
        	boolGlobal = true;
        	int numElemProc = collection.size()/numProcs;
        	int[] array = collection.toArray (new int[collection.size()]);
        	for (int i=0; i<numProcs; i++)	{
        		int numMax = (i+1) * numElemProc;
        		if ((i+1) == numProcs)	numMax = array.length;
        		threads[i] = new Thread (new ContainsAll2 (i*numElemProc, numMax, array));
        		threads[i].start();
        	}
        	return boolGlobal;
        }
        public boolean containsAll( TIntCollection collection ) {
            TIntIterator iter = collection.iterator();
            while ( iter.hasNext() ) {
                if ( ! TIntIntHashMap.this.containsValue( iter.next() ) ) {
                    return false;
                }
            }
            return true;
        }


        /** {@inheritDoc} */
        public boolean containsAll (int[] array, int numProcs)	{
        	boolGlobal = true;
        	int numElemProc = array.length/numProcs;
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
        public boolean containsAll( int[] array ) {
            for ( int element : array ) {
                if ( ! TIntIntHashMap.this.containsValue( element ) ) {
                    return false;
                }
            }
            return true;
        }


        /** {@inheritDoc} */
        public boolean addAll( Collection<? extends Integer> collection ) {
            throw new UnsupportedOperationException();
        }


        /** {@inheritDoc} */
        public boolean addAll( TIntCollection collection ) {
            throw new UnsupportedOperationException();
        }


        /** {@inheritDoc} */
        public boolean addAll( int[] array ) {
            throw new UnsupportedOperationException();
        }

        private class RetainAll1 implements Runnable	{
        	int min, max;
        	Collection<?> collection;
        	RetainAll1 (int a, int b, Collection<?> c)	{
        		min = a; max = b; collection = c;
        	}
        	public void run ()	{
        		for (int i=min; i<max; i++) {
                    if (!collection.contains (Integer.valueOf (_values[i]))) {
                        removeAt (i);
                        boolGlobal = true;
                    }
                }
        	}
        }
        /** {@inheritDoc} */
        @SuppressWarnings({"SuspiciousMethodCalls"})
        public boolean retainAll (Collection<?> collection, int numProcs)	{
        	boolGlobal = false;
        	int numElemProc = _values.length/numProcs;
        	for (int i=0; i<numProcs; i++)	{
        		int numMax = (i+1) * numElemProc;
        		if ((i+1) == numProcs)	numMax = _values.length;
        		threads[i] = new Thread (new RetainAll1 (i*numElemProc, numMax, collection));
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
        public boolean retainAll( Collection<?> collection ) {
            boolean modified = false;
            TIntIterator iter = iterator();
            while ( iter.hasNext() ) {
                if ( ! collection.contains( Integer.valueOf ( iter.next() ) ) ) {
                    iter.remove();
                    modified = true;
                }
            }
            return modified;
        }

        private class RetainAll2 implements Runnable	{
        	int min, max;
        	TIntCollection collection;
        	RetainAll2 (int a, int b, TIntCollection c)	{
        		min = a; max = b; collection = c;
        	}
        	public void run ()	{
        		for (int i=min; i<max; i++) {
                    if (!collection.contains(_values[i])) {
                        removeAt (i);
                        boolGlobal = true;
                    }
                }
        	}
        }
        /** {@inheritDoc} */
        public boolean retainAll (TIntCollection collection, int numProcs)	{
        	if (this == collection)	return false;
        	boolGlobal = false;
        	int numElemProc = _values.length/numProcs;
        	for (int i=0; i<numProcs; i++)	{
        		int numMax = (i+1) * numElemProc;
        		if ((i+1) == numProcs)	numMax = _values.length;
        		threads[i] = new Thread (new RetainAll2 (i*numElemProc, numMax, collection));
        		threads[i].start();
        	}
        	return boolGlobal;
        }
        public boolean retainAll( TIntCollection collection ) {
            if ( this == collection ) {
                return false;
            }
            boolean modified = false;
            TIntIterator iter = iterator();
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
        	int[] array;
        	RetainAll3 (int a, int b, int[] c)	{
        		min = a; max = b; array = c;
        	}
        	public void run ()	{
        		for (int i=min; i<max; i++) {
                    if (_states[i] == FULL && (Arrays.binarySearch (array, _values[i]) < 0)) {
                        synchronized (this)	{
                        	removeAt (i);
                        }
                        boolGlobal = true;
                    }
                }
        	}
        }
        /** {@inheritDoc} */
        public boolean retainAll (int[] array, int numProcs)	{
        	boolGlobal = false;
        	Arrays.sort (array);
        	int numElemProc = _values.length/numProcs;
        	for (int i=0; i<numProcs; i++)	{
        		int numMax = (i+1) * numElemProc;
        		if ((i+1) == numProcs) numMax = _values.length;
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
        public boolean retainAll( int[] array ) {
            boolean changed = false;
            Arrays.sort( array );
            int[] values = _values;
            byte[] states = _states;

            for ( int i = values.length; i-- > 0; ) {
                if ( states[i] == FULL && ( Arrays.binarySearch( array, values[i] ) < 0) ) {
                    removeAt( i );
                    changed = true;
                }
            }
            return changed;
        }

        private class RemoveAll1 implements Runnable	{
        	int min, max;
        	Integer[] array;
        	RemoveAll1 (int a, int b, Integer[] c)	{
        		min = a; max = b; array = c;
        	}
        	public void run ()	{
        		for (int i=min; i<max; i++) {
                    if (array[i] instanceof Integer ) {
                        int c = ((Integer) array[i]).intValue();
                        if (remove (c)) {
                            boolGlobal = true;
                        }
                    }
                }
        	}
        }
        /** {@inheritDoc} */
        public boolean removeAll (Collection<?> collection, int numProcs)	{
        	boolGlobal = false;
        	int numElemProc = collection.size()/numProcs;
        	Integer[] array = collection.toArray (new Integer[collection.size()]);
        	for (int i=0; i<numProcs; i++)	{
        		int numMax = (i+1) * numElemProc;
        		if ((i+1) == numProcs)	numMax = array.length;
        		threads[i] = new Thread (new RemoveAll1 (i*numElemProc, numMax, array));
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
        public boolean removeAll( Collection<?> collection ) {
            boolean changed = false;
            for ( Object element : collection ) {
                if ( element instanceof Integer ) {
                    int c = ( ( Integer ) element ).intValue();
                    if ( remove( c ) ) {
                        changed = true;
                    }
                }
            }
            return changed;
        }

        private class RemoveAll2 implements Runnable	{
        	int min, max;
        	int[] array;
        	RemoveAll2 (int a, int b, int[] c)	{
        		min = a; max = b; array = c;
        	}
        	public void run ()	{
        		for (int i=min; i<max; i++) {
                    int element = array[i];
                    if (remove (element)) {
                        boolGlobal = true;
                    }
                }
        	}
        }
        /** {@inheritDoc} */
        public boolean removeAll (TIntCollection collection, int numProcs)	{
        	if (this == collection)	{
        		clear();
        		return true;
        	}
        	boolGlobal = false;
        	int[] array = collection.toArray (new int[collection.size()]);
        	int numElemProc = array.length/numProcs;
        	for (int i=0; i<numProcs; i++)	{
        		int numMax = (i+1) * numElemProc;
        		if ((i+1) == numProcs)	numMax = array.length;
        		threads[i] = new Thread (new RemoveAll2 (i*numElemProc, numMax, array));
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
        public boolean removeAll( TIntCollection collection ) {
            if ( this == collection ) {
                clear();
                return true;
            }
            boolean changed = false;
            TIntIterator iter = collection.iterator();
            while ( iter.hasNext() ) {
                int element = iter.next();
                if ( remove( element ) ) {
                    changed = true;
                }
            }
            return changed;
        }


        /** {@inheritDoc} */
        public boolean removeAll (int[] array, int numProcs)	{
        	boolGlobal = false;
        	int numElemProc = array.length/numProcs;
        	for (int i=0; i<numProcs; i++)	{
        		int numMax = (i+1) * numElemProc;
        		if ((i+1) == numProcs)	numMax = array.length;
        		threads[i] = new Thread (new RemoveAll2 (i*numElemProc, numMax, array));
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
        public boolean removeAll( int[] array ) {
            boolean changed = false;
            for ( int i = array.length; i-- > 0; ) {
                if ( remove( array[i] ) ) {
                    changed = true;
                }
            }
            return changed;
        }


        /** {@inheritDoc} */
        public void clear() {
            TIntIntHashMap.this.clear();
        }


        /** {@inheritDoc} */
        public boolean forEach( TIntProcedure procedure ) {
            return TIntIntHashMap.this.forEachValue( procedure );
        }


        /** {@inheritDoc} */
        public String toString(int numProcs) {
            final StringBuilder buf = new StringBuilder( "{" );
            forEachValue( new TIntProcedure() {
                private boolean first = true;

                public boolean execute( int value ) {
                    if ( first ) {
                        first = false;
                    } else {
                        buf.append( ", " );
                    }

                    buf.append( value );
                    return true;
                }
            }, numProcs);
            buf.append( "}" );
            return buf.toString();
        }
        @Override
        public String toString() {
            final StringBuilder buf = new StringBuilder( "{" );
            forEachValue( new TIntProcedure() {
                private boolean first = true;

                public boolean execute( int value ) {
                    if ( first ) {
                        first = false;
                    } else {
                        buf.append( ", " );
                    }

                    buf.append( value );
                    return true;
                }
            } );
            buf.append( "}" );
            return buf.toString();
        }
    }


    class TIntIntKeyHashIterator extends THashPrimitiveIterator implements TIntIterator {

        /**
         * Creates an iterator over the specified map
         *
         * @param hash the <tt>TPrimitiveHash</tt> we will be iterating over.
         */
        TIntIntKeyHashIterator( TPrimitiveHash hash ) {
            super( hash );
        }

        /** {@inheritDoc} */
        public int next() {
            moveToNextIndex();
            return _set[_index];
        }

        /** @{inheritDoc} */
        public void remove() {
            if ( _expectedSize != _hash.size() ) {
                throw new ConcurrentModificationException();
            }

            // Disable auto compaction during the remove. This is a workaround for bug 1642768.
            try {
                _hash.tempDisableAutoCompaction();
                TIntIntHashMap.this.removeAt( _index );
            }
            finally {
                _hash.reenableAutoCompaction( false );
            }

            _expectedSize--;
        }
    }


   
    class TIntIntValueHashIterator extends THashPrimitiveIterator implements TIntIterator {

        /**
         * Creates an iterator over the specified map
         *
         * @param hash the <tt>TPrimitiveHash</tt> we will be iterating over.
         */
        TIntIntValueHashIterator( TPrimitiveHash hash ) {
            super( hash );
        }

        /** {@inheritDoc} */
        public int next() {
            moveToNextIndex();
            return _values[_index];
        }

        /** @{inheritDoc} */
        public void remove() {
            if ( _expectedSize != _hash.size() ) {
                throw new ConcurrentModificationException();
            }

            // Disable auto compaction during the remove. This is a workaround for bug 1642768.
            try {
                _hash.tempDisableAutoCompaction();
                TIntIntHashMap.this.removeAt( _index );
            }
            finally {
                _hash.reenableAutoCompaction( false );
            }

            _expectedSize--;
        }
    }


    class TIntIntHashIterator extends THashPrimitiveIterator implements TIntIntIterator {

        /**
         * Creates an iterator over the specified map
         *
         * @param map the <tt>T#K##V#HashMap</tt> we will be iterating over.
         */
        TIntIntHashIterator( TIntIntHashMap map ) {
            super( map );
        }

        /** {@inheritDoc} */
        public void advance() {
            moveToNextIndex();
        }

        /** {@inheritDoc} */
        public int key() {
            return _set[_index];
        }

        /** {@inheritDoc} */
        public int value() {
            return _values[_index];
        }

        /** {@inheritDoc} */
        public int setValue( int val ) {
            int old = value();
            _values[_index] = val;
            return old;
        }

        /** @{inheritDoc} */
        public void remove() {
            if ( _expectedSize != _hash.size() ) {
                throw new ConcurrentModificationException();
            }
            // Disable auto compaction during the remove. This is a workaround for bug 1642768.
            try {
                _hash.tempDisableAutoCompaction();
                TIntIntHashMap.this.removeAt( _index );
            }
            finally {
                _hash.reenableAutoCompaction( false );
            }
            _expectedSize--;
        }
    }

    private class Equals1 implements Runnable	{
    	Object other;
    	int min, max;
    	Equals1 (Object obj, int a, int b)	{
    		other=obj; min=a; max=b;
    	}
    	public void run ()	{
            TIntIntMap that = ( TIntIntMap ) other;
            int this_no_entry_value = getNoEntryValue();
            int that_no_entry_value = that.getNoEntryValue();
    		for ( int i=min; i<max; i++) {
                if ( _states[i] == FULL ) {
                    int key = _set[i];
                    int that_value = that.get( key );
                    int this_value = _values[i];
                    if ( ( this_value != that_value ) &&
                         ( this_value != this_no_entry_value ) &&
                         ( that_value != that_no_entry_value ) ) {
                        boolGlobal = false;
                        return;
                    }
                }
            }
    	}
    }

    /** {@inheritDoc} */
    public boolean equals (Object other, int numProcs)	{
    	if (! (other instanceof TIntIntMap))	{
    		return false;
    	}
    	TIntIntMap that = (TIntIntMap) other;
    	if (that.size () != this.size())	{
    		return false;
    	}
    	boolGlobal = true;
    	int elemProc = (_values.length)/numProcs;
    	for (int i=0; i<numProcs; i++)	{
    		int numMax = (i+1) * elemProc;
    		if ((i+1) == numProcs)	numMax = _values.length;
    		threads[i] = new Thread (new Equals1 (other, i*elemProc, numMax));
    		threads[i].start();
    	}
    	for (int i=0; i<numProcs; i++)
			try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	return boolGlobal;
    }
    @Override
    public boolean equals( Object other ) {
        if ( ! ( other instanceof TIntIntMap ) ) {
        	System.out.println ("Clase errónea");
            return false;
        }
        TIntIntMap that = ( TIntIntMap ) other;
        if ( that.size() != this.size() ) {
        	System.out.println("Distinto tamaño: " + that.size() + " y " + this.size());
            return false;
        }
        int[] values = _values;
        byte[] states = _states;
        int this_no_entry_value = getNoEntryValue();
        int that_no_entry_value = that.getNoEntryValue();
        for ( int i = values.length; i-- > 0; ) {
            if ( states[i] == FULL ) {
                int key = _set[i];
                int that_value = that.get( key );
                int this_value = values[i];
                if ( ( this_value != that_value ) &&
                     ( this_value != this_no_entry_value ) &&
                     ( that_value != that_no_entry_value ) ) {
                	System.out.println("Elemento distinto");
                    return false;
                }
            }
        }
        return true;
    }


    private class HashCode1 implements Runnable	{
    	int min, max;
    	int hashCode = 0;
    	HashCode1 (int a, int b)	{
    		min=a; max=b;
    	}
    	public void run ()	{
            for (int i=min; i<max; i++) {
                if (_states[i] == FULL ) {
                    hashCode += HashFunctions.hash( _set[i] ) ^
                                HashFunctions.hash( _values[i] );
                }
            }
            intGlobal += hashCode;
    	}
    }
    /** {@inheritDoc} */
    public int hashCode(int numProcs)	{
    	intGlobal=0;
    	int numElemProc = _values.length/numProcs;
    	for (int i=0; i<numProcs; i++)	{
    		int numMax = (i+1)*numElemProc;
    		if ((i+1) == numProcs)	numMax = _values.length;
    		threads[i] = new Thread (new HashCode1 (numElemProc*i, numMax));
    		threads[i].start();
    	}
    	for (int i=0; i<numProcs; i++)
			try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	return intGlobal;
    }
    @Override
    public int hashCode() {
        int hashcode = 0;
        byte[] states = _states;
        for ( int i = _values.length; i-- > 0; ) {
            if ( states[i] == FULL ) {
                hashcode += HashFunctions.hash( _set[i] ) ^
                            HashFunctions.hash( _values[i] );
            }
        }
        return hashcode;
    }


    
    private class ToString1 implements Runnable	{
    	int min, max;
    	final StringBuilder buf = new StringBuilder ("");
    	ToString1 (int a, int b)	{
    		min = a; max = b;
    	}
    	public void run ()		{
    		TIntIntProcedure procedure = new TIntIntProcedure() {
                private boolean first = true;
                public boolean execute( int key, int value ) {
                	synchronized (this)	{
                		if ( first ) first = false;
                		else buf.append( ", " );

                		buf.append(key);
                		buf.append("=");
                		buf.append(value);
                	}
                    return true;
                }
    		};
    		for (int i=min; i<max; i++)	{
    			if (_states[i] == FULL && ! procedure.execute(_set[i], _values[i] ) ) {
    				boolGlobal = false;
    				return;
    			}
    		}
    		synchronized (this)	{
    			stringGlobal.concat(buf.toString());
    		}
    	}
    }
    /** {@inheritDoc} */
    public String toString (int numProcs)	{
    	stringGlobal = "{";
    	int numElemProc = _states.length/numProcs;
    	for (int i=0; i<numProcs; i++)	{
    		int numMax = (i+1) * numElemProc;
    		if ((i+1) == numProcs)	numMax = _states.length;
    		threads[i] = new Thread (new ToString1 (i*numElemProc, numMax));
    		threads[i].start();
    	}
    	for (int i=0; i<numProcs; i++)	{
    		try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    	return stringGlobal;
    }
    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder( "{" );
        forEachEntry( new TIntIntProcedure() {
            private boolean first = true;
            public boolean execute( int key, int value ) {
                if ( first ) first = false;
                else buf.append( ", " );

                buf.append(key);
                buf.append("=");
                buf.append(value);
                return true;
            }
        });
        buf.append( "}" );
        return buf.toString();
    }

    private class WriteExternal1 implements Runnable	{
    	int min, max;
    	ObjectOutput out;
    	WriteExternal1 (int a, int b, ObjectOutput o)	{
    		min=a; max=b; out=o;
    	}
    	public void run ()	{
        	for (int i=min; i<max; i++) {
                if ( _states[i] == FULL ) {
    				try {
                    	out.writeInt( _set[i] );
                    	out.writeInt( _values[i] );
    				} catch (IOException e) {
    					e.printStackTrace();
    				}
                }
            }
    	}
    }
    /** {@inheritDoc} */
    public void writeExternal (ObjectOutput out, int numProcs) throws IOException	{
    	//VERSION
    	out.writeByte(0);
    	//SUPER
    	super.writeExternal(out);
    	//NUMBER OF ENTRIES
    	out.writeInt (_size);
    	//ENTRIES
    	int numElemProc = _size/numProcs;
    	for (int i=0; i<numProcs; i++)	{
    		int numMax = (i+1) * numElemProc;
    		if ((i+1) == numProcs)	numMax = _size;
    		threads[i] = new Thread (new WriteExternal1 (i*numElemProc, numMax, out));
    		threads[i].start();
    	}
    	for (int i=0; i<numProcs; i++)
			try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    }
    public void writeExternal(ObjectOutput out) throws IOException {
        // VERSION
    	out.writeByte( 0 );

        // SUPER
    	super.writeExternal( out );

    	// NUMBER OF ENTRIES
    	out.writeInt( _size );

    	// ENTRIES
    	for ( int i = _states.length; i-- > 0; ) {
            if ( _states[i] == FULL ) {
                out.writeInt( _set[i] );
                out.writeInt( _values[i] );
            }
        }
    }


    private class ReadExternal1 implements Runnable	{
    	int min, max;
    	ObjectInput in;
    	ReadExternal1 (int a, int b, ObjectInput input)	{
    		min=a; max=b; in=input;
    	}
    	public void run ()	{
            int i = max;
            int key = 0;
            int val = 0;
    		while (i-- > min) {
				try {
					key = in.readInt();
					val = in.readInt();
					put(key, val);
				} catch (IOException e) {
					e.printStackTrace();
				}
            }
    	}
    }
    /** {@inheritDoc} */
    public void readExternal(ObjectInput in, int numProcs) throws IOException, ClassNotFoundException {
        // VERSION
    	in.readByte();

        // SUPER
    	super.readExternal( in );

    	// NUMBER OF ENTRIES
    	int size = in.readInt();
    	setUp( size );

    	// ENTRIES
    	int numElemProc = size/numProcs;
        for (int i=0; i<numProcs; i++)	{
        	int numMax = (i+1) * numElemProc;
        	if ((i+1) == numProcs)	numMax = size;
        	threads[i] = new Thread (new ReadExternal1(numElemProc*i, numMax, in));
        	threads[i].start();
        }
        for (int i=0; i<numProcs; i++)
			try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    }
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        // VERSION
    	in.readByte();

        // SUPER
    	super.readExternal( in );

    	// NUMBER OF ENTRIES
    	int size = in.readInt();
    	setUp( size );

    	// ENTRIES
        while (size-- > 0) {
            int key = in.readInt();
            int val = in.readInt();
            put(key, val);
        }
    }
} // T#K##V#HashMap