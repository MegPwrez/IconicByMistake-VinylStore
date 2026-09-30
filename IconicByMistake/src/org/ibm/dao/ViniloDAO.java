package org.ibm.dao;

import java.util.List;
import org.ibm.model.Vinilo;

public interface ViniloDAO {

    List<Vinilo> listarTodos();

    List<Vinilo> buscar(String criterio);

    boolean crear(Vinilo vinilo);

    boolean actualizar(Vinilo vinilo);
    
    boolean eliminar(String id);
}