package org.ibm.dao;

import java.util.List;
import org.ibm.model.Artista;

public interface ArtistaDAO {
    List<Artista> listarTodos();
    Artista buscarPorId(Integer idArtista);
    boolean crear(Artista artista);
    boolean actualizar(Artista artista);
    boolean eliminar(Integer idArtista);
}