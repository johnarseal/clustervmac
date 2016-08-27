package clustervmac.realtimedb;

import java.util.Collection;
import java.util.Map;

public interface DBMap <K,V> {
	// below are the basic CRUD method
	public V get(K key);
	// return true if update success, false if no key exists
	public boolean update(K key, V value);
	// Removes the mapping for this key from this Map if present.
	public V remove(K key);
	// Insert, if key duplicate, return false, else return true
	public boolean insert(K key,V value);
	// return a collection of V with key less than provided key
	public Collection<V> smallerThan(K key);
	// return a collection of V whose key is larger or equal than provided key
	public Collection<V> largerOrEqual(K key);
	// return a sub-set of the map whose key cater to the condition
	public Collection<Map.Entry<K, V>> subMap(K fromKey, K toKey);
	// return the lowest key-value pair
	public Map.Entry<K, V> firstEnt();
	// return the highest key-value pair
	public Map.Entry<K, V> lastEnt();
	
}
