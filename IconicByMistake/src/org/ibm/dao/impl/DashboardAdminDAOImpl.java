package org.ibm.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.ibm.dao.DashboardAdminDAO;
import org.ibm.utils.Conexion;

public class DashboardAdminDAOImpl implements DashboardAdminDAO {

    @Override
    public double obtenerVentasDia() {

        String sql = """
            SELECT COALESCE(SUM(total), 0) AS total_dia
            FROM ventas
            WHERE DATE(fecha_venta) = CURDATE()
              AND estado = 'COMPLETADA'
            """;

        try (Connection conexion = Conexion.getInstancia().conectar();
             PreparedStatement consulta = conexion.prepareStatement(sql);
             ResultSet resultado = consulta.executeQuery()) {

            if (resultado.next()) {
                return resultado.getDouble("total_dia");
            }

        } catch (SQLException e) {
            System.err.println(
                    "Error al obtener ventas del día: "
                    + e.getMessage()
            );
        }

        return 0.0;
    }

    @Override
    public double obtenerVentasMes() {

        String sql = """
            SELECT COALESCE(SUM(total), 0) AS total_mes
            FROM ventas
            WHERE YEAR(fecha_venta) = YEAR(CURDATE())
              AND MONTH(fecha_venta) = MONTH(CURDATE())
              AND estado = 'COMPLETADA'
            """;

        try (Connection conexion = Conexion.getInstancia().conectar();
             PreparedStatement consulta = conexion.prepareStatement(sql);
             ResultSet resultado = consulta.executeQuery()) {

            if (resultado.next()) {
                return resultado.getDouble("total_mes");
            }

        } catch (SQLException e) {
            System.err.println(
                    "Error al obtener ventas del mes: "
                    + e.getMessage()
            );
        }

        return 0.0;
    }

    @Override
    public String obtenerViniloMasVendido() {

        String sql = """
            SELECT v.titulo_album,
                   SUM(d.cantidad) AS cantidad_vendida
            FROM detalle_venta d
            INNER JOIN ventas ve
                ON ve.id_venta = d.id_venta
            INNER JOIN vinilos v
                ON v.codigo_barras = d.codigo_barras
            WHERE ve.estado = 'COMPLETADA'
            GROUP BY v.codigo_barras, v.titulo_album
            ORDER BY cantidad_vendida DESC
            LIMIT 1
            """;

        try (Connection conexion = Conexion.getInstancia().conectar();
             PreparedStatement consulta = conexion.prepareStatement(sql);
             ResultSet resultado = consulta.executeQuery()) {

            if (resultado.next()) {
                return resultado.getString("titulo_album");
            }

        } catch (SQLException e) {
            System.err.println(
                    "Error al obtener el vinilo más vendido: "
                    + e.getMessage()
            );
        }

        return "Sin datos";
    }
}