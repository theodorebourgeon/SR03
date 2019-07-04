package dao;

import java.util.List;

public interface ApiDao<T> {
	T get(int id);
	List<T> getAll(T t);
	Integer save(T t); 	// return new id or null => Integer type (nullable)
	boolean update(T t);
	boolean delete(int id);
}
