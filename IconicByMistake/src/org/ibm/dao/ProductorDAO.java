package org.ibm.dao;

import java.util.List;
import org.ibm.model.Productor;

public interface ProductorDAO {
    List<Productor> listarTodos();
    Productor buscarPorId(String idProductor);
    boolean crear(Productor productor);
    boolean actualizar(Productor productor);
    boolean eliminar(String idProductor);
}