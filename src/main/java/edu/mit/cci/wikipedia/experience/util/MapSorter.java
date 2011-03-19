package edu.mit.cci.wikipedia.experience.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Generic Map Sorter
 */
public class MapSorter<K, V extends Comparable<? super V>> {

	/**
	 * Returns a new Map, sorted by value
	 */
	public Map<K, V> sortByValue(Map<K, V> map) {
	     List<Entry<K, V>> list = new LinkedList<Entry<K, V>>(map.entrySet());
	     Collections.sort(list, new Comparator<Entry<K, V>>() {
			public int compare(Entry<K, V> arg0,
					Entry<K, V> arg1) {
				return arg1.getValue().compareTo(arg0.getValue());
			}
	     });

	    Map<K, V> result = new LinkedHashMap<K, V>();
	    for (Iterator<Entry<K, V>> it = list.iterator(); it.hasNext();) {
	    	Entry<K, V> entry = it.next();
	        result.put(entry.getKey(), entry.getValue());
	    }
	    return result;
	} 
}
