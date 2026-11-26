package modelo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {

    private final String url = "jdbc:mysql://localhost:3306/arenacup";
    private final String usuario = "root";
    private final String contrasena = "Admin$1234";

    public Connection conectar() {
        Connection con = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(
                    url, usuario, contrasena);

            System.out.println("Conexion exitosa");

        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Error de conexion: " + e.getMessage());
        }

        return con;
    }
}