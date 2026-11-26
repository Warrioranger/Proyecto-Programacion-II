<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="modelo.Equipo"%>

<%!
    private String escapar(String texto) {

        if (texto == null) {
            return "";
        }

        return texto.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
%>

<%
    if (session.getAttribute("usuario") == null
            || !"ADMINISTRADOR".equals(
                    session.getAttribute("rol"))) {

        response.sendRedirect("login.jsp");
        return;
    }

    List<Equipo> equipos =
            (List<Equipo>) request.getAttribute("equipos");

    List<Integer> bloqueados =
            (List<Integer>) request.getAttribute("bloqueados");

    if (equipos == null || bloqueados == null) {

        response.sendRedirect("EquipoServlet");
        return;
    }

    int aprobados = (Integer)
            request.getAttribute("aprobados");

    int pendientes = (Integer)
            request.getAttribute("pendientes");

    int cupos = (Integer)
            request.getAttribute("cupos");

    String mensaje = request.getParameter("mensaje");
%>

<!DOCTYPE html>
<html lang="es">

    <head>

        <meta charset="UTF-8">

        <meta name="viewport"
              content="width=device-width, initial-scale=1.0">

        <title>ArenaCup | Gestión de equipos</title>

        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
              rel="stylesheet">

        <link rel="stylesheet" href="css/estilos.css">

    </head>

    <body>

        <nav class="navbar navbar-dark bg-dark border-bottom border-secondary">

            <div class="container">

                <a class="navbar-brand text-success fw-bold"
                   href="admin.jsp">

                    ARENA CUP | ADMIN

                </a>

                <form action="CerrarSesionServlet" method="POST">

                    <button type="submit"
                            class="btn btn-outline-light btn-sm">

                        Cerrar sesión

                    </button>

                </form>

            </div>

        </nav>

        <div class="container mt-4">

            <a href="admin.jsp"
               class="btn btn-outline-secondary mb-4">

                Volver al inicio

            </a>

            <h2>Gestión de equipos</h2>

            <p class="texto-secundario">

                Administra los equipos registrados
                y los cupos disponibles para el torneo.

            </p>

            <% if ("aprobado".equals(mensaje)) { %>

            <div class="alert alert-success">

                Equipo aprobado correctamente.

            </div>

            <% } else if ("pendiente".equals(mensaje)) { %>

            <div class="alert alert-success">

                El equipo pasó a estado pendiente.
                Se ha liberado un cupo.

            </div>

            <% } %>

            <% if (request.getAttribute("error") != null) { %>

            <div class="alert alert-danger">

                <%= escapar(request.getAttribute("error")
                        .toString()) %>

            </div>

            <% } %>

            <div class="row g-3 mb-4 text-center">

                <div class="col-6 col-md-3">

                    <div class="card bg-dark text-light border-secondary h-100">

                        <div class="card-body">

                            <p class="texto-secundario">

                                Total registrados

                            </p>

                            <h3>

                                <%= equipos.size() %>

                            </h3>

                        </div>

                    </div>

                </div>

                <div class="col-6 col-md-3">

                    <div class="card bg-dark text-light border-secondary h-100">

                        <div class="card-body">

                            <p class="texto-secundario">

                                Equipos aprobados

                            </p>

                            <h3 class="text-success">

                                <%= aprobados %>/16

                            </h3>

                        </div>

                    </div>

                </div>

                <div class="col-6 col-md-3">

                    <div class="card bg-dark text-light border-secondary h-100">

                        <div class="card-body">

                            <p class="texto-secundario">

                                Equipos pendientes

                            </p>

                            <h3 class="text-warning">

                                <%= pendientes %>

                            </h3>

                        </div>

                    </div>

                </div>

                <div class="col-6 col-md-3">

                    <div class="card bg-dark text-light border-secondary h-100">

                        <div class="card-body">

                            <p class="texto-secundario">

                                Cupos disponibles

                            </p>

                            <h3 class="<%= cupos > 0
                                    ? "text-success"
                                    : "text-warning" %>">

                                <%= cupos %>

                            </h3>

                        </div>

                    </div>

                </div>

            </div>

            <div class="progress mb-4"
                 style="height: 10px;">

                <div class="progress-bar bg-success"
                     style="width: <%= Math.min(
                             100, aprobados * 100 / 16) %>%;">

                </div>

            </div>

            <% if (aprobados >= 16) { %>

            <div class="alert alert-warning">

                <strong>Límite alcanzado.</strong>

                Solo se permiten 16 equipos aprobados.
                Para aprobar otro equipo, primero debe
                devolver uno a estado pendiente.

            </div>

            <% } else { %>

            <div class="alert alert-success">

                Hay <strong><%= cupos %></strong>
                cupo(s) disponible(s) para aprobar equipos.

            </div>

            <% } %>

            <% if (!bloqueados.isEmpty()) { %>

            <div class="alert alert-info">

                Los equipos participantes de un torneo
                activo no pueden cambiar de estado hasta
                que el torneo finalice.

            </div>

            <% } %>

            <div class="card bg-dark text-light border-secondary">

                <div class="card-header border-secondary">

                    <h5 class="mb-0">

                        Equipos registrados

                    </h5>

                </div>

                <div class="card-body">

                    <div class="table-responsive">

                        <table class="table table-dark table-hover align-middle mb-0">

                            <thead>

                                <tr>

                                    <th>ID</th>
                                    <th>Escudo</th>
                                    <th>Nombre</th>
                                    <th>Teléfono</th>
                                    <th>Usuario dueño</th>
                                    <th>Estado</th>
                                    <th class="text-center">Acción</th>

                                </tr>

                            </thead>

                            <tbody>

                                <% if (equipos.isEmpty()) { %>

                                <tr>

                                    <td colspan="7"
                                        class="text-center py-4">

                                        No hay equipos registrados.

                                    </td>

                                </tr>

                                <% } %>

                                <% for (Equipo equipo : equipos) {

                                    boolean aprobado =
                                            "APROBADO".equals(
                                                    equipo.getEstado());

                                    boolean bloqueado =
                                            bloqueados.contains(
                                                    equipo.getId());
                                %>

                                <tr>

                                    <td>

                                        <%= equipo.getId() %>

                                    </td>

                                    <td>

                                        <img src="<%= escapar(
                                                equipo.getEscudo()) %>"
                                             alt="Escudo"
                                             width="45"
                                             height="45"
                                             style="object-fit: contain;">

                                    </td>

                                    <td class="fw-medium">

                                        <%= escapar(equipo.getNombre()) %>

                                    </td>

                                    <td>

                                        <%= escapar(equipo.getTelefono()) %>

                                    </td>

                                    <td>

                                        <% if (equipo.getNombreUsuario() != null
                                                && !equipo.getNombreUsuario()
                                                        .trim().isEmpty()) { %>

                                        <%= escapar(equipo.getNombreUsuario()) %>

                                        <% } else { %>

                                        <span class="text-warning">

                                            Nombre no disponible

                                        </span>

                                        <% } %>

                                    </td>

                                    <td>

                                        <% if (aprobado) { %>

                                        <span class="badge bg-success">

                                            Aprobado

                                        </span>

                                        <% } else { %>

                                        <span class="badge bg-warning text-dark">

                                            Pendiente

                                        </span>

                                        <% } %>

                                    </td>

                                    <td class="text-center">

                                        <% if (bloqueado) { %>

                                        <button type="button"
                                                class="btn btn-outline-secondary btn-sm"
                                                disabled>

                                            En torneo activo

                                        </button>

                                        <% } else if (aprobado) { %>

                                        <form action="EquipoServlet"
                                              method="POST"
                                              onsubmit="return confirm(
                                                  '¿Desea devolver este equipo a pendiente?');">

                                            <input type="hidden"
                                                   name="accion"
                                                   value="pendiente">

                                            <input type="hidden"
                                                   name="id"
                                                   value="<%= equipo.getId() %>">

                                            <button type="submit"
                                                    class="btn btn-outline-warning btn-sm">

                                                Volver a pendiente

                                            </button>

                                        </form>

                                        <% } else if (aprobados < 16) { %>

                                        <form action="EquipoServlet"
                                              method="POST"
                                              onsubmit="return confirm(
                                                  '¿Desea aprobar este equipo?');">

                                            <input type="hidden"
                                                   name="accion"
                                                   value="aprobar">

                                            <input type="hidden"
                                                   name="id"
                                                   value="<%= equipo.getId() %>">

                                            <button type="submit"
                                                    class="btn btn-success btn-sm">

                                                Aprobar

                                            </button>

                                        </form>

                                        <% } else { %>

                                        <button type="button"
                                                class="btn btn-outline-secondary btn-sm"
                                                disabled>

                                            Sin cupos disponibles

                                        </button>

                                        <% } %>

                                    </td>

                                </tr>

                                <% } %>

                            </tbody>

                        </table>

                    </div>

                </div>

            </div>

            <div class="pie-pagina mb-4">

                ArenaCup 2026 | Programación II

            </div>

        </div>

    </body>

</html>