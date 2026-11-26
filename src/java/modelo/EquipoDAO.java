package modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EquipoDAO {

    Conexion conexion = new Conexion();

    public boolean registrar(Equipo equipo) {

        String sql = "INSERT INTO equipos "
                + "(nombre, escudo, telefono, usuario_id, estado) "
                + "VALUES (?, ?, ?, ?, 'PENDIENTE')";

        try (Connection con = conexion.conectar()) {

            if (con == null) {
                return false;
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setString(1, equipo.getNombre());
                ps.setString(2, equipo.getEscudo());
                ps.setString(3, equipo.getTelefono());
                ps.setInt(4, equipo.getUsuarioId());

                return ps.executeUpdate() > 0;
            }

        } catch (SQLException e) {

            System.out.println("Error al registrar equipo: "
                    + e.getMessage());
        }

        return false;
    }

    public boolean existeNombre(String nombre) {

        String sql = "SELECT id FROM equipos WHERE nombre = ?";

        try (Connection con = conexion.conectar()) {

            if (con == null) {
                return false;
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setString(1, nombre);

                try (ResultSet rs = ps.executeQuery()) {

                    return rs.next();
                }
            }

        } catch (SQLException e) {

            System.out.println("Error al consultar equipo: "
                    + e.getMessage());
        }

        return false;
    }

    public List<Equipo> listar() {

        List<Equipo> lista = new ArrayList<>();

        String sql = "SELECT e.*, u.nombre AS nombre_usuario "
                + "FROM equipos e "
                + "LEFT JOIN usuarios u ON e.usuario_id = u.id "
                + "ORDER BY e.nombre";

        try (Connection con = conexion.conectar()) {

            if (con == null) {
                return lista;
            }

            try (PreparedStatement ps = con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    Equipo equipo = new Equipo();

                    equipo.setId(rs.getInt("id"));
                    equipo.setNombre(rs.getString("nombre"));
                    equipo.setEscudo(rs.getString("escudo"));
                    equipo.setTelefono(rs.getString("telefono"));
                    equipo.setUsuarioId(rs.getInt("usuario_id"));
                    equipo.setEstado(rs.getString("estado"));

                    equipo.setNombreUsuario(
                            rs.getString("nombre_usuario"));

                    lista.add(equipo);
                }
            }

        } catch (SQLException e) {

            System.out.println("Error al listar equipos: "
                    + e.getMessage());

            e.printStackTrace();
        }

        return lista;
    }

    public List<Equipo> listarPorUsuario(int usuarioId) {

        List<Equipo> lista = new ArrayList<>();

        String sql = "SELECT * FROM equipos "
                + "WHERE usuario_id = ? ORDER BY nombre";

        try (Connection con = conexion.conectar()) {

            if (con == null) {
                return lista;
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setInt(1, usuarioId);

                try (ResultSet rs = ps.executeQuery()) {

                    while (rs.next()) {

                        lista.add(obtenerEquipo(rs));
                    }
                }
            }

        } catch (SQLException e) {

            System.out.println("Error al listar mis equipos: "
                    + e.getMessage());
        }

        return lista;
    }

    public int contarAprobados() {

        String sql = "SELECT COUNT(*) AS cantidad "
                + "FROM equipos WHERE estado = 'APROBADO'";

        try (Connection con = conexion.conectar()) {

            if (con == null) {
                return -1;
            }

            try (PreparedStatement ps = con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    return rs.getInt("cantidad");
                }
            }

        } catch (SQLException e) {

            System.out.println("Error al contar equipos: "
                    + e.getMessage());
        }

        return -1;
    }

    public boolean aprobar(int id) {

        return cambiarEstado(id, "APROBADO");
    }

    public boolean devolverPendiente(int id) {

        return cambiarEstado(id, "PENDIENTE");
    }

    public boolean cambiarEstado(int id, String nuevoEstado) {

        if (id <= 0) {
            return false;
        }

        if (!"APROBADO".equals(nuevoEstado)
                && !"PENDIENTE".equals(nuevoEstado)) {

            return false;
        }

        try (Connection con = conexion.conectar()) {

            if (con == null) {
                return false;
            }

            con.setAutoCommit(false);

            try {

                int aprobados = 0;
                String estadoActual = null;

                // Bloquear los equipos durante el cambio.

                String sqlEquipos = "SELECT id, estado "
                        + "FROM equipos ORDER BY id FOR UPDATE";

                try (PreparedStatement ps =
                        con.prepareStatement(sqlEquipos);
                     ResultSet rs = ps.executeQuery()) {

                    while (rs.next()) {

                        if ("APROBADO".equals(
                                rs.getString("estado"))) {

                            aprobados++;
                        }

                        if (rs.getInt("id") == id) {

                            estadoActual = rs.getString("estado");
                        }
                    }
                }

                // Verificar que el equipo exista.

                if (estadoActual == null) {

                    con.rollback();
                    return false;
                }

                // Evitar cambios innecesarios.

                if (nuevoEstado.equals(estadoActual)) {

                    con.rollback();
                    return false;
                }

                // Verificar si participa en un torneo activo.

                String sqlTorneo = "SELECT te.equipo_id "
                        + "FROM torneo_equipos te "
                        + "INNER JOIN torneos t "
                        + "ON te.torneo_id = t.id "
                        + "WHERE te.equipo_id = ? "
                        + "AND t.estado = 'ACTIVO' FOR UPDATE";

                try (PreparedStatement ps =
                        con.prepareStatement(sqlTorneo)) {

                    ps.setInt(1, id);

                    try (ResultSet rs = ps.executeQuery()) {

                        if (rs.next()) {

                            con.rollback();
                            return false;
                        }
                    }
                }

                // Validar el limite de equipos aprobados.

                if ("APROBADO".equals(nuevoEstado)
                        && aprobados >= 16) {

                    con.rollback();
                    return false;
                }

                // Actualizar el estado del equipo.

                String sqlActualizar = "UPDATE equipos "
                        + "SET estado = ? "
                        + "WHERE id = ? AND estado = ?";

                try (PreparedStatement ps =
                        con.prepareStatement(sqlActualizar)) {

                    ps.setString(1, nuevoEstado);
                    ps.setInt(2, id);
                    ps.setString(3, estadoActual);

                    if (ps.executeUpdate() != 1) {

                        con.rollback();
                        return false;
                    }
                }

                con.commit();

                return true;

            } catch (SQLException e) {

                con.rollback();

                System.out.println("Error al cambiar estado: "
                        + e.getMessage());

                return false;
            }

        } catch (SQLException e) {

            System.out.println("Error de conexion: "
                    + e.getMessage());
        }

        return false;
    }

    public boolean participaEnTorneoActivo(int id) {

        String sql = "SELECT te.equipo_id "
                + "FROM torneo_equipos te "
                + "INNER JOIN torneos t "
                + "ON te.torneo_id = t.id "
                + "WHERE te.equipo_id = ? "
                + "AND t.estado = 'ACTIVO'";

        try (Connection con = conexion.conectar()) {

            if (con == null) {
                return true;
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setInt(1, id);

                try (ResultSet rs = ps.executeQuery()) {

                    return rs.next();
                }
            }

        } catch (SQLException e) {

            System.out.println("Error al consultar participante: "
                    + e.getMessage());
        }

        return true;
    }

    public List<Equipo> listarAprobados() {

        List<Equipo> lista = new ArrayList<>();

        String sql = "SELECT * FROM equipos "
                + "WHERE estado = 'APROBADO' ORDER BY nombre";

        try (Connection con = conexion.conectar()) {

            if (con == null) {
                return lista;
            }

            try (PreparedStatement ps = con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    lista.add(obtenerEquipo(rs));
                }
            }

        } catch (SQLException e) {

            System.out.println("Error al listar equipos aprobados: "
                    + e.getMessage());
        }

        return lista;
    }

    public Equipo obtenerPorId(int id) {

        String sql = "SELECT * FROM equipos WHERE id = ?";

        try (Connection con = conexion.conectar()) {

            if (con == null) {
                return null;
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setInt(1, id);

                try (ResultSet rs = ps.executeQuery()) {

                    if (rs.next()) {

                        return obtenerEquipo(rs);
                    }
                }
            }

        } catch (SQLException e) {

            System.out.println("Error al consultar equipo: "
                    + e.getMessage());
        }

        return null;
    }

    private Equipo obtenerEquipo(ResultSet rs)
            throws SQLException {

        Equipo equipo = new Equipo();

        equipo.setId(rs.getInt("id"));
        equipo.setNombre(rs.getString("nombre"));
        equipo.setEscudo(rs.getString("escudo"));
        equipo.setTelefono(rs.getString("telefono"));
        equipo.setUsuarioId(rs.getInt("usuario_id"));
        equipo.setEstado(rs.getString("estado"));

        return equipo;
    }
}