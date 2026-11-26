package modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class UsuarioDAO {

    Conexion conexion = new Conexion();

    public boolean registrar(Usuario usuario) {

        String sal = UUID.randomUUID().toString();

        String sql = "INSERT INTO usuarios "
                + "(nombre, correo, contrasena, rol) "
                + "VALUES (?, ?, CONCAT(?, ':', "
                + "SHA2(CONCAT(?, ?), 256)), ?)";

        try (Connection con = conexion.conectar()) {

            if (con == null) {
                return false;
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setString(1, usuario.getNombre());
                ps.setString(2, usuario.getCorreo());
                ps.setString(3, sal);
                ps.setString(4, sal);
                ps.setString(5, usuario.getContrasena());
                ps.setString(6, "USUARIO");

                return ps.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            System.out.println("Error al registrar: "
                    + e.getMessage());
            return false;
        }
    }

    public boolean existeCorreo(String correo) {

        String sql = "SELECT id FROM usuarios WHERE correo = ?";

        try (Connection con = conexion.conectar()) {

            if (con == null) {
                return false;
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setString(1, correo);

                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al consultar correo: "
                    + e.getMessage());
            return false;
        }
    }

    public Usuario iniciarSesion(String correo, String contrasena) {

        String sql = "SELECT id, nombre, correo, rol FROM usuarios "
                + "WHERE correo = ? "
                + "AND SUBSTRING_INDEX(contrasena, ':', -1) = "
                + "SHA2(CONCAT(SUBSTRING_INDEX(contrasena, ':', 1), "
                + "?), 256)";

        try (Connection con = conexion.conectar()) {

            if (con == null) {
                return null;
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setString(1, correo);
                ps.setString(2, contrasena);

                try (ResultSet rs = ps.executeQuery()) {

                    if (rs.next()) {

                        Usuario usuario = new Usuario();

                        usuario.setId(rs.getInt("id"));
                        usuario.setNombre(rs.getString("nombre"));
                        usuario.setCorreo(rs.getString("correo"));
                        usuario.setRol(rs.getString("rol"));

                        return usuario;
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al iniciar sesion: "
                    + e.getMessage());
        }

        return null;
    }
}