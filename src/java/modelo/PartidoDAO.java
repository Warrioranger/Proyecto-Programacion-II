package modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PartidoDAO {

    Conexion conexion = new Conexion();

    public List<Partido> listarPorTorneo(int torneoId) {

        List<Partido> lista = new ArrayList<>();

        String sql = "SELECT p.*, "
                + "r.nombre AS nombre_ronda, "
                + "r.numero AS numero_ronda, "
                + "e1.nombre AS nombre_equipo1, "
                + "e2.nombre AS nombre_equipo2, "
                + "e1.escudo AS escudo_equipo1, "
                + "e2.escudo AS escudo_equipo2 "
                + "FROM partidos p "
                + "INNER JOIN rondas r ON p.ronda_id = r.id "
                + "LEFT JOIN equipos e1 ON p.equipo1_id = e1.id "
                + "LEFT JOIN equipos e2 ON p.equipo2_id = e2.id "
                + "WHERE r.torneo_id = ? "
                + "ORDER BY r.numero, p.numero";

        try (Connection con = conexion.conectar()) {

            if (con == null) {
                return lista;
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setInt(1, torneoId);

                try (ResultSet rs = ps.executeQuery()) {

                    while (rs.next()) {

                        Partido partido = new Partido();

                        partido.setId(rs.getInt("id"));
                        partido.setRondaId(rs.getInt("ronda_id"));
                        partido.setNumero(rs.getInt("numero"));

                        partido.setEquipo1Id(
                                obtenerEntero(rs, "equipo1_id"));

                        partido.setEquipo2Id(
                                obtenerEntero(rs, "equipo2_id"));

                        partido.setMarcador1(
                                obtenerEntero(rs, "marcador1"));

                        partido.setMarcador2(
                                obtenerEntero(rs, "marcador2"));

                        partido.setGanadorId(
                                obtenerEntero(rs, "ganador_id"));

                        partido.setSiguientePartidoId(
                                obtenerEntero(rs, "siguiente_partido_id"));

                        partido.setPosicionSiguiente(
                                obtenerEntero(rs, "posicion_siguiente"));

                        partido.setEstado(rs.getString("estado"));

                        partido.setNombreRonda(
                                rs.getString("nombre_ronda"));

                        partido.setNumeroRonda(
                                rs.getInt("numero_ronda"));

                        partido.setNombreEquipo1(
                                rs.getString("nombre_equipo1"));

                        partido.setNombreEquipo2(
                                rs.getString("nombre_equipo2"));

                        partido.setEscudoEquipo1(
                                rs.getString("escudo_equipo1"));

                        partido.setEscudoEquipo2(
                                rs.getString("escudo_equipo2"));

                        lista.add(partido);
                    }
                }
            }

        } catch (SQLException e) {

            System.out.println("Error al listar partidos: "
                    + e.getMessage());
        }

        return lista;
    }

    public boolean registrarResultado(int partidoId,
            int marcador1, int marcador2) {

        // Validar que no existan marcadores negativos ni empates.

        if (partidoId <= 0
                || marcador1 < 0
                || marcador2 < 0
                || marcador1 == marcador2) {

            return false;
        }

        try (Connection con = conexion.conectar()) {

            if (con == null) {
                return false;
            }

            con.setAutoCommit(false);

            try {

                // Obtener los datos del partido y del torneo.

                String sqlPartido = "SELECT p.*, "
                        + "r.torneo_id, "
                        + "r.numero AS numero_ronda, "
                        + "t.estado AS estado_torneo "
                        + "FROM partidos p "
                        + "INNER JOIN rondas r "
                        + "ON p.ronda_id = r.id "
                        + "INNER JOIN torneos t "
                        + "ON r.torneo_id = t.id "
                        + "WHERE p.id = ? FOR UPDATE";

                Integer equipo1;
                Integer equipo2;
                Integer siguientePartido;
                Integer posicionSiguiente;

                int torneoId;
                int numeroRonda;

                try (PreparedStatement ps =
                        con.prepareStatement(sqlPartido)) {

                    ps.setInt(1, partidoId);

                    try (ResultSet rs = ps.executeQuery()) {

                        if (!rs.next()) {

                            con.rollback();
                            return false;
                        }

                        if (!"PENDIENTE".equals(
                                rs.getString("estado"))
                                || !"ACTIVO".equals(
                                        rs.getString("estado_torneo"))) {

                            con.rollback();
                            return false;
                        }

                        equipo1 = obtenerEntero(
                                rs, "equipo1_id");

                        equipo2 = obtenerEntero(
                                rs, "equipo2_id");

                        siguientePartido = obtenerEntero(
                                rs, "siguiente_partido_id");

                        posicionSiguiente = obtenerEntero(
                                rs, "posicion_siguiente");

                        torneoId = rs.getInt("torneo_id");

                        numeroRonda = rs.getInt("numero_ronda");
                    }
                }

                // Validar que ambos equipos estén definidos.

                if (equipo1 == null || equipo2 == null
                        || equipo1.equals(equipo2)) {

                    con.rollback();
                    return false;
                }

                // Determinar automáticamente el ganador.

                int ganador;

                if (marcador1 > marcador2) {

                    ganador = equipo1;

                } else {

                    ganador = equipo2;
                }

                // Guardar el resultado y finalizar el partido.

                String sqlResultado = "UPDATE partidos "
                        + "SET marcador1 = ?, "
                        + "marcador2 = ?, "
                        + "ganador_id = ?, "
                        + "estado = 'FINALIZADO' "
                        + "WHERE id = ? "
                        + "AND estado = 'PENDIENTE'";

                try (PreparedStatement ps =
                        con.prepareStatement(sqlResultado)) {

                    ps.setInt(1, marcador1);
                    ps.setInt(2, marcador2);
                    ps.setInt(3, ganador);
                    ps.setInt(4, partidoId);

                    if (ps.executeUpdate() != 1) {

                        con.rollback();
                        return false;
                    }
                }

                // Si existe otro partido, avanzar al ganador.

                if (siguientePartido != null) {

                    if (numeroRonda >= 4
                            || posicionSiguiente == null) {

                        con.rollback();
                        return false;
                    }

                    String sqlAvance;

                    if (posicionSiguiente == 1) {

                        sqlAvance = "UPDATE partidos "
                                + "SET equipo1_id = ? "
                                + "WHERE id = ? "
                                + "AND equipo1_id IS NULL "
                                + "AND estado = 'PENDIENTE'";

                    } else if (posicionSiguiente == 2) {

                        sqlAvance = "UPDATE partidos "
                                + "SET equipo2_id = ? "
                                + "WHERE id = ? "
                                + "AND equipo2_id IS NULL "
                                + "AND estado = 'PENDIENTE'";

                    } else {

                        con.rollback();
                        return false;
                    }

                    try (PreparedStatement ps =
                            con.prepareStatement(sqlAvance)) {

                        ps.setInt(1, ganador);
                        ps.setInt(2, siguientePartido);

                        if (ps.executeUpdate() != 1) {

                            con.rollback();
                            return false;
                        }
                    }

                } else {

                    // Si no existe siguiente partido,
                    // debe tratarse de la gran final.

                    if (numeroRonda != 4) {

                        con.rollback();
                        return false;
                    }

                    // Guardar el campeón y finalizar el torneo.

                    String sqlCampeon = "UPDATE torneos "
                            + "SET campeon_id = ?, "
                            + "estado = 'FINALIZADO' "
                            + "WHERE id = ? "
                            + "AND estado = 'ACTIVO'";

                    try (PreparedStatement ps =
                            con.prepareStatement(sqlCampeon)) {

                        ps.setInt(1, ganador);
                        ps.setInt(2, torneoId);

                        if (ps.executeUpdate() != 1) {

                            con.rollback();
                            return false;
                        }
                    }
                }

                // Confirmar todas las operaciones.

                con.commit();

                return true;

            } catch (SQLException e) {

                con.rollback();

                System.out.println("Error al registrar resultado: "
                        + e.getMessage());

                return false;
            }

        } catch (SQLException e) {

            System.out.println("Error de conexion: "
                    + e.getMessage());
        }

        return false;
    }

    private Integer obtenerEntero(ResultSet rs,
            String columna) throws SQLException {

        int valor = rs.getInt(columna);

        if (rs.wasNull()) {

            return null;
        }

        return valor;
    }
}