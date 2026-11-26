package controlador;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import modelo.Usuario;
import modelo.UsuarioDAO;

@WebServlet(name = "LoginServlet",
        urlPatterns = {"/LoginServlet"})

public class LoginServlet extends HttpServlet {

    UsuarioDAO dao = new UsuarioDAO();

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher("login.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String correo = request.getParameter("correo");
        String contrasena = request.getParameter("contrasena");

        if (correo == null || correo.trim().isEmpty()
                || contrasena == null || contrasena.isEmpty()) {

            request.setAttribute("error",
                    "Complete todos los campos.");

            request.getRequestDispatcher("login.jsp")
                    .forward(request, response);

            return;
        }

        correo = correo.trim().toLowerCase();

        Usuario usuario = dao.iniciarSesion(correo, contrasena);

        if (usuario != null) {

            HttpSession sesionAnterior = request.getSession(false);

            if (sesionAnterior != null) {
                sesionAnterior.invalidate();
            }

            HttpSession sesion = request.getSession();

            sesion.setAttribute("usuario", usuario);
            sesion.setAttribute("idUsuario", usuario.getId());
            sesion.setAttribute("nombre", usuario.getNombre());
            sesion.setAttribute("rol", usuario.getRol());

            if ("ADMINISTRADOR".equals(usuario.getRol())) {

                response.sendRedirect("admin.jsp");

            } else {

                response.sendRedirect("usuario.jsp");
            }

        } else {

            request.setAttribute("error",
                    "Correo o contrasena incorrectos.");

            request.getRequestDispatcher("login.jsp")
                    .forward(request, response);
        }
    }
}