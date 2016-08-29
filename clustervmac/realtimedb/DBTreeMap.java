package clustervmac.realtimedb;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;


public class DBTreeMap<K,V> implements DBMap<K,V> {
	private TreeMap<K,V> treeMap;
	public DBTreeMap(){
		treeMap = new TreeMap<K,V>();
	}
	public DBTreeMap(Comparator<? super K> comparator){
		treeMap = new TreeMap<K,V>(comparator);
	}
	public V get(K key){
		return treeMap.get(key);
	}
	// return true if update success, false if no key exists
	public boolean update(K key, V value){
		if (treeMap.containsKey(key)){
			treeMap.remove(key);
			treeMap.put(key, value);
			return true;
		}
		else{
			return false;
		}
	}
	// Removes the mapping for this key from this TreeMap if present.
	public V remove(K key){
		return treeMap.remove(key);
	}
	// Insert, if key duplicate, return false, else return true
	public boolean insert(K key,V value){
		if (treeMap.containsKey(key)){
			return false;
		}
		else{
			treeMap.put(key, value);
			return true;
		}
	}
	public Collection<V> smallerThan(K key){
		return treeMap.headMap(key).values();
	}
	public Collection<V> largerOrEqual(K key){
		return treeMap.tailMap(key).values();
	}
	public Map.Entry<K, V> firstEnt(){
		return treeMap.firstEntry();
	}
	public Map.Entry<K, V> lastEnt(){
		return treeMap.lastEntry();
	}
	public Collection<Map.Entry<K, V>> subMap(K fromKey, K toKey){
		SortedMap<K,V> subSortedMap = treeMap.subMap(fromKey,toKey);
		return subSortedMap.entrySet();
	}
	// return all the mappings in the map
	public Set<Map.Entry<K,V>> allMaps(){
		return treeMap.entrySet();
	}
	
	// Returns a key-value mapping associated with the least key greater than or equal to the given key, or null if there is no such key.
	public Map.Entry<K,V> ceilingEntry(K key){
		return treeMap.ceilingEntry(key);
	}
	// Returns a key-value mapping associated with the greatest key less than or equal to the given key, or null if there is no such key.
	public Map.Entry<K,V> floorEntry(K key){
		return treeMap.floorEntry(key);
	}
	
}
