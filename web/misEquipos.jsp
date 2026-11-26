<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="modelo.Equipo"%>

<%
    if (session.getAttribute("usuario") == null
            || !"USUARIO".equals(session.getAttribute("rol"))) {

        response.sendRedirect("login.jsp");
        return;
    }

    List<Equipo> equipos =
            (List<Equipo>) request.getAttribute("equipos");

    if (equipos == null) {
        response.sendRedirect("EquipoServlet");
        return;
    }
%>

<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport"
              content="width=device-width, initial-scale=1.0">

        <title>ArenaCup | Mis equipos</title>

        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
              rel="stylesheet">

        <link rel="stylesheet" href="css/estilos.css">
    </head>

    <body>

        <nav class="navbar navbar-dark bg-dark border-bottom border-secondary">

            <div class="container">

                <a class="navbar-brand text-success fw-bold"
                   href="usuario.jsp">
                    ARENA CUP
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

            <a href="usuario.jsp"
               class="btn btn-outline-secondary mb-4">
                Volver al inicio
            </a>

            <h2>Mis equipos</h2>

            <p class="texto-secundario">
                Registra tus equipos para participar en el torneo.
            </p>

            <% if (request.getAttribute("error") != null) { %>

            <div class="alert alert-danger">
                <%= request.getAttribute("error") %>
            </div>

            <% } %>

            <% if ("registrado".equals(
                    request.getParameter("mensaje"))) { %>

            <div class="alert alert-success">
                Equipo registrado correctamente.
                Pendiente de aprobación.
            </div>

            <% } %>

            <div class="row g-4">

                <div class="col-lg-4">

                    <div class="card bg-dark text-light border-secondary">

                        <div class="card-body p-4">

                            <h4 class="mb-4">Registrar equipo</h4>

                            <form action="EquipoServlet" method="POST">

                                <input type="hidden"
                                       name="accion"
                                       value="registrar">

                                <div class="mb-3">

                                    <label class="form-label">
                                        Nombre del equipo
                                    </label>

                                    <input type="text"
                                           name="nombre"
                                           class="form-control"
                                           maxlength="100"
                                           required>

                                </div>

                                <div class="mb-3">

                                    <label class="form-label">
                                        URL del escudo
                                    </label>

                                    <input type="url"
                                           name="escudo"
                                           class="form-control"
                                           placeholder="https://..."
                                           maxlength="500"
                                           required>

                                </div>

                                <div class="mb-4">

                                    <label class="form-label">
                                        Número de teléfono
                                    </label>

                                    <input type="tel"
                                           name="telefono"
                                           class="form-control"
                                           maxlength="20"
                                           minlength="8"
                                           pattern="[0-9+() \-]{8,20}"
                                           required>

                                </div>

                                <button type="submit"
                                        class="btn-arena">
                                    Registrar equipo
                                </button>

                            </form>

                        </div>

                    </div>

                </div>

                <div class="col-lg-8">

                    <div class="card bg-dark text-light border-secondary">

                        <div class="card-body p-4">

                            <h4 class="mb-4">Equipos registrados</h4>

                            <% if (equipos.isEmpty()) { %>

                            <div class="alert alert-secondary">
                                Todavía no tienes equipos registrados.
                            </div>

                            <% } else { %>

                            <div class="table-responsive">

                                <table class="table table-dark table-hover align-middle">

                                    <thead>

                                        <tr>
                                            <th>Escudo</th>
                                            <th>Equipo</th>
                                            <th>Teléfono</th>
                                            <th>Estado</th>
                                        </tr>

                                    </thead>

                                    <tbody>

                                        <% for (Equipo equipo : equipos) { %>

                                        <tr>

                                            <td>

                                                <img src="${''}<%= equipo.getEscudo().replace("&", "&amp;").replace("\"", "&quot;").replace("<", "&lt;").replace(">", "&gt;") %>"
                                                     alt="Escudo"
                                                     width="50"
                                                     height="50"
                                                     style="object-fit: contain;">

                                            </td>

                                            <td>
                                                <%= equipo.getNombre().replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;") %>
                                            </td>

                                            <td>
                                                <%= equipo.getTelefono() %>
                                            </td>

                                            <td>

                                                <% if ("APROBADO".equals(
                                                        equipo.getEstado())) { %>

                                                <span class="badge bg-success">
                                                    Aprobado
                                                </span>

                                                <% } else { %>

                                                <span class="badge bg-warning text-dark">
                                                    Pendiente
                                                </span>

                                                <% } %>

                                            </td>

                                        </tr>

                                        <% } %>

                                    </tbody>

                                </table>

                            </div>

                            <% } %>

                        </div>

                    </div>

                </div>

            </div>

            <div class="pie-pagina">
                ArenaCup 2026 | Programación II
            </div>

        </div>

    </body>
</html>