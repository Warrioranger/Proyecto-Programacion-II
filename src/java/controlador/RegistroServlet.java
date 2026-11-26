package controlador;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import modelo.Usuario;
import modelo.UsuarioDAO;

@WebServlet(name = "RegistroServlet",
        urlPatterns = {"/RegistroServlet"})

public class RegistroServlet extends HttpServlet {

    UsuarioDAO dao = new UsuarioDAO();

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher("registro.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String nombre = request.getParameter("nombre");
        String correo = request.getParameter("correo");
        String contrasena = request.getParameter("contrasena");

        if (nombre == null || nombre.trim().isEmpty()
                || correo == null || correo.trim().isEmpty()
                || contrasena == null || contrasena.length() < 8) {

            request.setAttribute("error",
                    "Complete todos los campos correctamente.");

            request.getRequestDispatcher("registro.jsp")
                    .forward(request, response);

            return;
        }

        nombre = nombre.trim();
        correo = correo.trim().toLowerCase();

        if (!correo.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {

            request.setAttribute("error",
                    "Ingrese un correo valido.");

            request.getRequestDispatcher("registro.jsp")
                    .forward(request, response);

            return;
        }

        if (dao.existeCorreo(correo)) {

            request.setAttribute("error",
                    "El correo ya se encuentra registrado.");

            request.getRequestDispatcher("registro.jsp")
                    .forward(request, response);

            return;
        }

        Usuario usuario = new Usuario();

        usuario.setNombre(nombre);
        usuario.setCorreo(correo);
        usuario.setContrasena(contrasena);
        usuario.setRol("USUARIO");

        if (dao.registrar(usuario)) {

            response.sendRedirect("login.jsp?registro=exitoso");

        } else {

            request.setAttribute("error",
                    "No se pudo registrar el usuario.");

            request.getRequestDispatcher("registro.jsp")
                    .forward(request, response);
        }
    }
}