package org.ibm.dao;

import java.util.List; // Cambiar ArrayList por List

public interface Crud<T, K> {
    boolean crear(T entidad);
    boolean actualizar(T entidad);
    boolean eliminar(K id);
    T buscarPorId(K id);
    List<T> listarTodos(); // Cambiado de ArrayList a List
}