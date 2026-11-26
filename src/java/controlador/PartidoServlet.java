package controlador;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import modelo.Partido;
import modelo.PartidoDAO;
import modelo.Torneo;
import modelo.TorneoDAO;
import modelo.Equipo;
import modelo.EquipoDAO;

@WebServlet(name = "PartidoServlet",
        urlPatterns = {"/PartidoServlet"})

public class PartidoServlet extends HttpServlet {

    PartidoDAO partidoDAO = new PartidoDAO();
    TorneoDAO torneoDAO = new TorneoDAO();
    EquipoDAO equipoDAO = new EquipoDAO();

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession sesion = request.getSession(false);

        if (sesion == null
                || sesion.getAttribute("usuario") == null) {

            response.sendRedirect("login.jsp");
            return;
        }

        String rol = (String) sesion.getAttribute("rol");

        if (!"ADMINISTRADOR".equals(rol)
                && !"USUARIO".equals(rol)) {

            response.sendError(403);
            return;
        }

        Torneo torneo = torneoDAO.obtenerTorneoActivo();

        if (torneo == null) {

            torneo = torneoDAO.obtenerUltimoTorneo();
        }

        List<Partido> partidos = new ArrayList<>();

        Equipo campeon = null;

        if (torneo != null) {

            partidos = partidoDAO.listarPorTorneo(
                    torneo.getId());

            if (torneo.getCampeonId() != null) {

                campeon = equipoDAO.obtenerPorId(
                        torneo.getCampeonId());
            }
        }

        request.setAttribute("torneo", torneo);
        request.setAttribute("partidos", partidos);
        request.setAttribute("campeon", campeon);

        request.getRequestDispatcher("llaves.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession sesion = request.getSession(false);

        if (sesion == null
                || sesion.getAttribute("usuario") == null) {

            response.sendRedirect("login.jsp");
            return;
        }

        if (!"ADMINISTRADOR".equals(
                sesion.getAttribute("rol"))) {

            response.sendError(403);
            return;
        }

        String accion = request.getParameter("accion");

        if ("registrar".equals(accion)) {

            registrarResultado(request, response);

        } else {

            response.sendError(400);
        }
    }

    private void registrarResultado(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        try {

            int partidoId = Integer.parseInt(
                    request.getParameter("partidoId"));

            int marcador1 = Integer.parseInt(
                    request.getParameter("marcador1"));

            int marcador2 = Integer.parseInt(
                    request.getParameter("marcador2"));

            if (partidoId <= 0) {

                request.setAttribute("error",
                        "El partido seleccionado no es valido.");

                doGet(request, response);
                return;
            }

            if (marcador1 < 0 || marcador2 < 0
                    || marcador1 > 99 || marcador2 > 99) {

                request.setAttribute("error",
                        "Los marcadores deben estar entre 0 y 99.");

                doGet(request, response);
                return;
            }

            if (marcador1 == marcador2) {

                request.setAttribute("error",
                        "No se permiten empates.");

                doGet(request, response);
                return;
            }

            if (partidoDAO.registrarResultado(
                    partidoId, marcador1, marcador2)) {

                response.sendRedirect(
                        "PartidoServlet?mensaje=registrado");

            } else {

                request.setAttribute("error",
                        "No se pudo registrar el resultado. "
                        + "Verifique que ambos equipos esten definidos "
                        + "y que el partido no haya finalizado.");

                doGet(request, response);
            }

        } catch (NumberFormatException e) {

            request.setAttribute("error",
                    "Ingrese marcadores validos.");

            doGet(request, response);
        }
    }
}