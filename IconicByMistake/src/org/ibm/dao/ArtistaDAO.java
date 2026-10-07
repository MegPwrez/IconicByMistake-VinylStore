package org.ibm.dao;

import java.util.List;
import org.ibm.model.Artista;

public interface ArtistaDAO {
    List<Artista> listarTodos();
    boolean crear(Artista artista);
    boolean actualizar(Artista artista);
}