package org.ibm.dao;

import java.util.List;
import org.ibm.model.Vinilo;

public interface ViniloDAO {

    List<Vinilo> listar();

    List<Vinilo> buscar(String criterio);

    boolean insertar(Vinilo vinilo);

    boolean actualizar(Vinilo vinilo);
}