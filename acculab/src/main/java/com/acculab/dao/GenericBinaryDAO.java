package com.acculab.dao;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;

public abstract class GenericBinaryDAO<T extends Serializable, ID> implements IGenericDAO<T, ID> {
    private final String filePath;
    private final Function<T, ID> idExtractor;
    private final Map<ID, T> cache;
    private final ReentrantReadWriteLock lock;

    public GenericBinaryDAO(String filePath, Function<T, ID> idExtractor) {
        this.filePath = filePath;
        this.idExtractor = idExtractor;
        this.cache = new ConcurrentHashMap<>();
        this.lock = new ReentrantReadWriteLock();
        loadFromFile();
    }

    @SuppressWarnings("unchecked")
    private void loadFromFile() {
        lock.writeLock().lock();
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
                return;
            }
            if (file.length() > 0) {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                    List<T> list = (List<T>) ois.readObject();
                    for (T item : list) {
                        cache.put(idExtractor.apply(item), item);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error al cargar desde el archivo binario: " + e.getMessage());
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void saveToFile() {
        lock.writeLock().lock();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            List<T> list = new ArrayList<>(cache.values());
            oos.writeObject(list);
        } catch (Exception e) {
            System.err.println("Error al guardar en el archivo binario: " + e.getMessage());
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void save(T entity) {
        cache.put(idExtractor.apply(entity), entity);
        saveToFile();
    }

    @Override
    public void update(T entity) {
        cache.put(idExtractor.apply(entity), entity);
        saveToFile();
    }

    @Override
    public void delete(ID id) {
        cache.remove(id);
        saveToFile();
    }

    @Override
    public T findById(ID id) {
        return cache.get(id);
    }

    @Override
    public List<T> findAll() {
        return new ArrayList<>(cache.values());
    }
}
