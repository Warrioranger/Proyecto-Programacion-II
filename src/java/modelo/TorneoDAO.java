package modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TorneoDAO {

    Conexion conexion = new Conexion();

    public boolean existeTorneoActivo() {

        String sql = "SELECT id FROM torneos "
                + "WHERE estado = 'ACTIVO'";

        try (Connection con = conexion.conectar()) {

            if (con == null) {
                return false;
            }

            try (PreparedStatement ps = con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                return rs.next();
            }

        } catch (SQLException e) {

            System.out.println("Error al consultar torneo: "
                    + e.getMessage());
        }

        return false;
    }

    public Torneo obtenerTorneoActivo() {

        String sql = "SELECT * FROM torneos "
                + "WHERE estado = 'ACTIVO' LIMIT 1";

        try (Connection con = conexion.conectar()) {

            if (con == null) {
                return null;
            }

            try (PreparedStatement ps = con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    Torneo torneo = new Torneo();

                    torneo.setId(rs.getInt("id"));
                    torneo.setNombre(rs.getString("nombre"));
                    torneo.setImagen(rs.getString("imagen"));
                    torneo.setFechaInicio(
                            rs.getDate("fecha_inicio"));
                    torneo.setFechaFin(
                            rs.getDate("fecha_fin"));
                    torneo.setPremio(rs.getBigDecimal("premio"));
                    torneo.setEstado(rs.getString("estado"));

                    int campeonId = rs.getInt("campeon_id");

                    if (!rs.wasNull()) {
                        torneo.setCampeonId(campeonId);
                    }

                    return torneo;
                }
            }

        } catch (SQLException e) {

            System.out.println("Error al obtener torneo: "
                    + e.getMessage());
        }

        return null;
    }

    public Torneo obtenerUltimoTorneo() {

        String sql = "SELECT * FROM torneos "
                + "ORDER BY id DESC LIMIT 1";

        try (Connection con = conexion.conectar()) {

            if (con == null) {
                return null;
            }

            try (PreparedStatement ps = con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    Torneo torneo = new Torneo();

                    torneo.setId(rs.getInt("id"));
                    torneo.setNombre(rs.getString("nombre"));
                    torneo.setImagen(rs.getString("imagen"));
                    torneo.setFechaInicio(
                            rs.getDate("fecha_inicio"));
                    torneo.setFechaFin(
                            rs.getDate("fecha_fin"));
                    torneo.setPremio(rs.getBigDecimal("premio"));
                    torneo.setEstado(rs.getString("estado"));

                    int campeonId = rs.getInt("campeon_id");

                    if (!rs.wasNull()) {
                        torneo.setCampeonId(campeonId);
                    }

                    return torneo;
                }
            }

        } catch (SQLException e) {

            System.out.println("Error al consultar ultimo torneo: "
                    + e.getMessage());
        }

        return null;
    }

    public boolean crearTorneo(Torneo torneo,
            List<Integer> equiposSeleccionados) {

        if (torneo == null
                || equiposSeleccionados == null
                || equiposSeleccionados.size() != 16) {

            return false;
        }

        if (torneo.getNombre() == null
                || torneo.getNombre().trim().isEmpty()
                || torneo.getNombre().length() > 100
                || torneo.getImagen() == null
                || torneo.getImagen().trim().isEmpty()
                || torneo.getImagen().length() > 500
                || torneo.getFechaInicio() == null
                || torneo.getFechaFin() == null
                || torneo.getFechaFin().before(
                        torneo.getFechaInicio())
                || torneo.getPremio() == null
                || torneo.getPremio().signum() < 0) {

            return false;
        }

        List<Integer> equipos = new ArrayList<>();

        for (Integer id : equiposSeleccionados) {

            if (id == null || id <= 0 || equipos.contains(id)) {

                return false;
            }

            equipos.add(id);
        }

        try (Connection con = conexion.conectar()) {

            if (con == null) {
                return false;
            }

            con.setAutoCommit(false);

            try {

                // Consultar y bloquear los equipos aprobados.

                String sqlEquipos = "SELECT id FROM equipos "
                        + "WHERE estado = 'APROBADO' "
                        + "ORDER BY id FOR UPDATE";

                List<Integer> aprobados = new ArrayList<>();

                try (PreparedStatement ps =
                        con.prepareStatement(sqlEquipos);
                     ResultSet rs = ps.executeQuery()) {

                    while (rs.next()) {

                        aprobados.add(rs.getInt("id"));
                    }
                }

                // Validar los 16 equipos aprobados.

                if (aprobados.size() != 16
                        || !aprobados.containsAll(equipos)) {

                    con.rollback();

                    return false;
                }

                // Verificar que no exista otro torneo activo.

                String sqlActivo = "SELECT id FROM torneos "
                        + "WHERE estado = 'ACTIVO' FOR UPDATE";

                try (PreparedStatement ps =
                        con.prepareStatement(sqlActivo);
                     ResultSet rs = ps.executeQuery()) {

                    if (rs.next()) {

                        con.rollback();

                        return false;
                    }
                }

                // Registrar el torneo.

                String sqlTorneo = "INSERT INTO torneos "
                        + "(nombre, imagen, fecha_inicio, "
                        + "fecha_fin, premio, estado) "
                        + "VALUES (?, ?, ?, ?, ?, 'ACTIVO')";

                int torneoId;

                try (PreparedStatement ps =
                        con.prepareStatement(sqlTorneo,
                                Statement.RETURN_GENERATED_KEYS)) {

                    ps.setString(1, torneo.getNombre());
                    ps.setString(2, torneo.getImagen());
                    ps.setDate(3, torneo.getFechaInicio());
                    ps.setDate(4, torneo.getFechaFin());
                    ps.setBigDecimal(5, torneo.getPremio());

                    ps.executeUpdate();

                    try (ResultSet rs = ps.getGeneratedKeys()) {

                        if (rs.next()) {

                            torneoId = rs.getInt(1);

                        } else {

                            con.rollback();

                            return false;
                        }
                    }
                }

                // Registrar los participantes.

                String sqlParticipantes =
                        "INSERT INTO torneo_equipos "
                        + "(torneo_id, equipo_id) VALUES (?, ?)";

                try (PreparedStatement ps =
                        con.prepareStatement(sqlParticipantes)) {

                    for (Integer equipoId : equipos) {

                        ps.setInt(1, torneoId);
                        ps.setInt(2, equipoId);

                        ps.executeUpdate();
                    }
                }

                // Mezclar los equipos aleatoriamente.

                Collections.shuffle(equipos);

                // Generar las rondas y los partidos.

                generarLlaves(con, torneoId, equipos);

                con.commit();

                return true;

            } catch (SQLException e) {

                con.rollback();

                System.out.println("Error al crear torneo: "
                        + e.getMessage());

                return false;
            }

        } catch (SQLException e) {

            System.out.println("Error de conexion: "
                    + e.getMessage());
        }

        return false;
    }

    private void generarLlaves(Connection con, int torneoId,
            List<Integer> equipos) throws SQLException {

        // Crear las cuatro rondas.

        int preliminar = crearRonda(
                con, torneoId, "Ronda preliminar", 1);

        int cuartos = crearRonda(
                con, torneoId, "Cuartos de final", 2);

        int semifinal = crearRonda(
                con, torneoId, "Semifinal", 3);

        int rondaFinal = crearRonda(
                con, torneoId, "Final", 4);

        // Crear el partido final.

        int partidoFinal = crearPartido(
                con, rondaFinal, 1,
                null, null, null, null);

        // Crear los partidos de semifinal.

        int[] semifinales = new int[2];

        for (int i = 0; i < 2; i++) {

            semifinales[i] = crearPartido(
                    con,
                    semifinal,
                    i + 1,
                    null,
                    null,
                    partidoFinal,
                    i + 1);
        }

        // Crear los partidos de cuartos de final.

        int[] partidosCuartos = new int[4];

        for (int i = 0; i < 4; i++) {

            partidosCuartos[i] = crearPartido(
                    con,
                    cuartos,
                    i + 1,
                    null,
                    null,
                    semifinales[i / 2],
                    (i % 2) + 1);
        }

        // Crear los partidos de la ronda preliminar.

        for (int i = 0; i < 8; i++) {

            crearPartido(
                    con,
                    preliminar,
                    i + 1,
                    equipos.get(i * 2),
                    equipos.get(i * 2 + 1),
                    partidosCuartos[i / 2],
                    (i % 2) + 1);
        }
    }

    private int crearRonda(Connection con, int torneoId,
            String nombre, int numero) throws SQLException {

        String sql = "INSERT INTO rondas "
                + "(torneo_id, nombre, numero) "
                + "VALUES (?, ?, ?)";

        try (PreparedStatement ps =
                con.prepareStatement(sql,
                        Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, torneoId);
            ps.setString(2, nombre);
            ps.setInt(3, numero);

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {

                if (rs.next()) {

                    return rs.getInt(1);
                }
            }
        }

        throw new SQLException("No se pudo crear la ronda.");
    }

    private int crearPartido(Connection con, int rondaId,
            int numero, Integer equipo1,
            Integer equipo2, Integer siguientePartido,
            Integer posicionSiguiente) throws SQLException {

        String sql = "INSERT INTO partidos "
                + "(ronda_id, numero, equipo1_id, equipo2_id, "
                + "siguiente_partido_id, posicion_siguiente, "
                + "estado) VALUES (?, ?, ?, ?, ?, ?, 'PENDIENTE')";

        try (PreparedStatement ps =
                con.prepareStatement(sql,
                        Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, rondaId);
            ps.setInt(2, numero);
            ps.setObject(3, equipo1);
            ps.setObject(4, equipo2);
            ps.setObject(5, siguientePartido);
            ps.setObject(6, posicionSiguiente);

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {

                if (rs.next()) {

                    return rs.getInt(1);
                }
            }
        }

        throw new SQLException("No se pudo crear el partido.");
    }
}