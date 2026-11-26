package controlador;

import java.io.IOException;
import java.sql.Date;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import modelo.Equipo;
import modelo.EquipoDAO;
import modelo.Torneo;
import modelo.TorneoDAO;

@WebServlet(name = "TorneoServlet",
        urlPatterns = {"/TorneoServlet"})

public class TorneoServlet extends HttpServlet {

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

        if (!"ADMINISTRADOR".equals(
                sesion.getAttribute("rol"))) {

            response.sendError(403);
            return;
        }

        Torneo torneo = torneoDAO.obtenerTorneoActivo();

        List<Equipo> equipos = equipoDAO.listarAprobados();

        request.setAttribute("torneo", torneo);
        request.setAttribute("equipos", equipos);

        request.getRequestDispatcher("torneoAdmin.jsp")
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

        if ("crear".equals(accion)) {

            crearTorneo(request, response);

        } else {

            response.sendError(400);
        }
    }

    private void crearTorneo(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String nombre = request.getParameter("nombre");
        String imagen = request.getParameter("imagen");
        String fechaInicio = request.getParameter("fechaInicio");
        String fechaFin = request.getParameter("fechaFin");
        String premio = request.getParameter("premio");

        String[] seleccionados =
                request.getParameterValues("equipos");

        if (torneoDAO.existeTorneoActivo()) {

            request.setAttribute("error",
                    "Ya existe un torneo activo.");

            doGet(request, response);
            return;
        }

        if (nombre == null || nombre.trim().isEmpty()
                || imagen == null || imagen.trim().isEmpty()
                || fechaInicio == null || fechaInicio.isEmpty()
                || fechaFin == null || fechaFin.isEmpty()
                || premio == null || premio.trim().isEmpty()) {

            request.setAttribute("error",
                    "Complete todos los campos del torneo.");

            doGet(request, response);
            return;
        }

        nombre = nombre.trim();
        imagen = imagen.trim();

        if (nombre.length() > 100
                || imagen.length() > 500) {

            request.setAttribute("error",
                    "Los datos superan la longitud permitida.");

            doGet(request, response);
            return;
        }

        if (!imagen.matches("(?i)^https?://[^\\s<>\"']+$")) {

            request.setAttribute("error",
                    "Ingrese una URL valida para la imagen.");

            doGet(request, response);
            return;
        }

        if (seleccionados == null
                || seleccionados.length != 16) {

            request.setAttribute("error",
                    "Debe seleccionar exactamente 16 equipos.");

            doGet(request, response);
            return;
        }

        try {

            Date inicio = Date.valueOf(fechaInicio);
            Date fin = Date.valueOf(fechaFin);

            BigDecimal montoPremio = new BigDecimal(premio);

            if (fin.before(inicio)) {

                request.setAttribute("error",
                        "La fecha final no puede ser anterior "
                        + "a la fecha de inicio.");

                doGet(request, response);
                return;
            }

            if (montoPremio.signum() < 0
                    || montoPremio.scale() > 2
                    || montoPremio.compareTo(
                            new BigDecimal("9999999999.99")) > 0) {

                request.setAttribute("error",
                        "Ingrese un monto de premio valido.");

                doGet(request, response);
                return;
            }

            List<Integer> equipos = new ArrayList<>();

            for (String seleccionado : seleccionados) {

                int id = Integer.parseInt(seleccionado);

                if (id <= 0 || equipos.contains(id)) {

                    request.setAttribute("error",
                            "La seleccion de equipos es invalida.");

                    doGet(request, response);
                    return;
                }

                equipos.add(id);
            }

            Torneo torneo = new Torneo();

            torneo.setNombre(nombre);
            torneo.setImagen(imagen);
            torneo.setFechaInicio(inicio);
            torneo.setFechaFin(fin);
            torneo.setPremio(montoPremio);
            torneo.setEstado("ACTIVO");

            if (torneoDAO.crearTorneo(torneo, equipos)) {

                response.sendRedirect(
                        "TorneoServlet?mensaje=creado");

            } else {

                request.setAttribute("error",
                        "No se pudo crear el torneo. "
                        + "Verifique los equipos aprobados "
                        + "y que no exista otro torneo activo.");

                doGet(request, response);
            }

        } catch (IllegalArgumentException e) {

            request.setAttribute("error",
                    "Los datos ingresados no son validos.");

            doGet(request, response);
        }
    }
}