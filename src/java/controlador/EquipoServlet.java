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
import modelo.Equipo;
import modelo.EquipoDAO;

@WebServlet(name = "EquipoServlet",
        urlPatterns = {"/EquipoServlet"})

public class EquipoServlet extends HttpServlet {

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

        if ("ADMINISTRADOR".equals(rol)) {

            List<Equipo> equipos = equipoDAO.listar();

            int aprobados = 0;
            int pendientes = 0;

            List<Integer> bloqueados = new ArrayList<>();

            for (Equipo equipo : equipos) {

                if ("APROBADO".equals(equipo.getEstado())) {

                    aprobados++;

                } else if ("PENDIENTE".equals(
                        equipo.getEstado())) {

                    pendientes++;
                }

                if (equipoDAO.participaEnTorneoActivo(
                        equipo.getId())) {

                    bloqueados.add(equipo.getId());
                }
            }

            request.setAttribute("equipos", equipos);
            request.setAttribute("aprobados", aprobados);
            request.setAttribute("pendientes", pendientes);
            request.setAttribute("bloqueados", bloqueados);
            request.setAttribute("cupos",
                    Math.max(0, 16 - aprobados));

            request.getRequestDispatcher("equiposAdmin.jsp")
                    .forward(request, response);

        } else if ("USUARIO".equals(rol)) {

            int usuarioId = (Integer)
                    sesion.getAttribute("idUsuario");

            List<Equipo> equipos =
                    equipoDAO.listarPorUsuario(usuarioId);

            request.setAttribute("equipos", equipos);

            request.getRequestDispatcher("misEquipos.jsp")
                    .forward(request, response);

        } else {

            response.sendError(403);
        }
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

        String rol = (String) sesion.getAttribute("rol");
        String accion = request.getParameter("accion");

        if ("registrar".equals(accion)
                && "USUARIO".equals(rol)) {

            registrarEquipo(request, response, sesion);

        } else if ("aprobar".equals(accion)
                && "ADMINISTRADOR".equals(rol)) {

            cambiarEstado(request, response, "APROBADO");

        } else if ("pendiente".equals(accion)
                && "ADMINISTRADOR".equals(rol)) {

            cambiarEstado(request, response, "PENDIENTE");

        } else {

            response.sendError(403);
        }
    }

    private void registrarEquipo(
            HttpServletRequest request,
            HttpServletResponse response,
            HttpSession sesion)
            throws ServletException, IOException {

        String nombre = request.getParameter("nombre");
        String escudo = request.getParameter("escudo");
        String telefono = request.getParameter("telefono");

        if (nombre == null || escudo == null
                || telefono == null) {

            request.setAttribute("error",
                    "Debe completar todos los campos.");

            doGet(request, response);
            return;
        }

        nombre = nombre.trim();
        escudo = escudo.trim();
        telefono = telefono.trim();

        if (nombre.isEmpty()
                || escudo.isEmpty()
                || telefono.isEmpty()) {

            request.setAttribute("error",
                    "Debe completar todos los campos.");

            doGet(request, response);
            return;
        }

        if (nombre.length() > 100
                || escudo.length() > 500
                || telefono.length() > 20) {

            request.setAttribute("error",
                    "Los datos ingresados exceden "
                    + "la longitud permitida.");

            doGet(request, response);
            return;
        }

        if (!escudo.matches("(?i)^https?://[^\\s]+$")) {

            request.setAttribute("error",
                    "Ingrese una URL valida para el escudo.");

            doGet(request, response);
            return;
        }

        if (!telefono.matches("[0-9+()\\s-]{8,20}")
                || telefono.replaceAll("[^0-9]", "")
                        .length() < 8) {

            request.setAttribute("error",
                    "Ingrese un numero de telefono valido.");

            doGet(request, response);
            return;
        }

        if (equipoDAO.existeNombre(nombre)) {

            request.setAttribute("error",
                    "Ya existe un equipo con ese nombre.");

            doGet(request, response);
            return;
        }

        int usuarioId = (Integer)
                sesion.getAttribute("idUsuario");

        Equipo equipo = new Equipo();

        equipo.setNombre(nombre);
        equipo.setEscudo(escudo);
        equipo.setTelefono(telefono);
        equipo.setUsuarioId(usuarioId);
        equipo.setEstado("PENDIENTE");

        if (equipoDAO.registrar(equipo)) {

            response.sendRedirect(
                    "EquipoServlet?mensaje=registrado");

        } else {

            request.setAttribute("error",
                    "No se pudo registrar el equipo.");

            doGet(request, response);
        }
    }

    private void cambiarEstado(
            HttpServletRequest request,
            HttpServletResponse response,
            String nuevoEstado)
            throws ServletException, IOException {

        try {

            String parametroId = request.getParameter("id");

            if (parametroId == null) {

                parametroId = request.getParameter("equipoId");
            }

            int id = Integer.parseInt(parametroId);

            Equipo equipo = equipoDAO.obtenerPorId(id);

            if (equipo == null) {

                request.setAttribute("error",
                        "El equipo seleccionado no existe.");

                doGet(request, response);
                return;
            }

            if (nuevoEstado.equals(equipo.getEstado())) {

                request.setAttribute("error",
                        "El equipo ya tiene ese estado.");

                doGet(request, response);
                return;
            }

            if (equipoDAO.participaEnTorneoActivo(id)) {

                request.setAttribute("error",
                        "No se puede cambiar el estado de un "
                        + "equipo participante de un torneo activo.");

                doGet(request, response);
                return;
            }

            if ("APROBADO".equals(nuevoEstado)) {

                int cantidad = equipoDAO.contarAprobados();

                if (cantidad < 0) {

                    request.setAttribute("error",
                            "No se pudo consultar la cantidad "
                            + "de equipos aprobados.");

                    doGet(request, response);
                    return;
                }

                if (cantidad >= 16) {

                    request.setAttribute("error",
                            "Se alcanzo el limite de 16 equipos "
                            + "aprobados. Debe liberar un cupo.");

                    doGet(request, response);
                    return;
                }
            }

            boolean actualizado = equipoDAO.cambiarEstado(
                    id, nuevoEstado);

            if (actualizado) {

                if ("APROBADO".equals(nuevoEstado)) {

                    response.sendRedirect(
                            "EquipoServlet?mensaje=aprobado");

                } else {

                    response.sendRedirect(
                            "EquipoServlet?mensaje=pendiente");
                }

            } else {

                request.setAttribute("error",
                        "No se pudo cambiar el estado. "
                        + "Verifique los cupos y el estado del torneo.");

                doGet(request, response);
            }

        } catch (NumberFormatException e) {

            request.setAttribute("error",
                    "El identificador del equipo no es valido.");

            doGet(request, response);
        }
    }
}