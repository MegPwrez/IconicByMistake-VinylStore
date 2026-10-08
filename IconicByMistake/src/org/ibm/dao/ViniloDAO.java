package org.ibm.dao;

import java.util.List;
import org.ibm.model.Vinilo;

public interface ViniloDAO {

    List<Vinilo> listarTodos();

    Vinilo buscarPorId(String codigoBarras); // Agregado para mantener consistencia

    List<Vinilo> buscar(String criterio);

    boolean crear(Vinilo vinilo);

    boolean actualizar(Vinilo vinilo);
    
    boolean eliminar(String id);
}