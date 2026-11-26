<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="modelo.Equipo"%>
<%@page import="modelo.Torneo"%>

<%
    if (session.getAttribute("usuario") == null
            || !"ADMINISTRADOR".equals(session.getAttribute("rol"))) {

        response.sendRedirect("login.jsp");
        return;
    }

    Torneo torneo = (Torneo) request.getAttribute("torneo");

    List<Equipo> equipos =
            (List<Equipo>) request.getAttribute("equipos");

    if (equipos == null) {
        response.sendRedirect("TorneoServlet");
        return;
    }
%>

<!DOCTYPE html>
<html lang="es">

    <head>

        <meta charset="UTF-8">

        <meta name="viewport"
              content="width=device-width, initial-scale=1.0">

        <title>ArenaCup | Gestión del torneo</title>

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

            <h2>Gestión del torneo</h2>

            <p class="texto-secundario">
                Administra el torneo eliminatorio de ArenaCup.
            </p>

            <% if (request.getAttribute("error") != null) { %>

            <div class="alert alert-danger">
                <%= request.getAttribute("error") %>
            </div>

            <% } %>

            <% if ("creado".equals(
                    request.getParameter("mensaje"))) { %>

            <div class="alert alert-success">
                Torneo creado correctamente.
                Las llaves fueron generadas automáticamente.
            </div>

            <% } %>

            <% if (torneo != null) { %>

            <div class="card bg-dark text-light border-secondary">

                <div class="card-body p-4">

                    <div class="row align-items-center g-4">

                        <div class="col-md-3 text-center">

                            <img src="<%= torneo.getImagen().replace("&", "&amp;").replace("\"", "&quot;").replace("<", "&lt;").replace(">", "&gt;") %>"
                                 alt="Imagen del torneo"
                                 class="img-fluid rounded"
                                 style="max-height: 220px;
                                 object-fit: contain;">

                        </div>

                        <div class="col-md-9">

                            <span class="badge bg-success mb-3">
                                Torneo activo
                            </span>

                            <h2>
                                <%= torneo.getNombre().replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;") %>
                            </h2>

                            <p>
                                <strong>Fecha de inicio:</strong>
                                <%= torneo.getFechaInicio() %>
                            </p>

                            <p>
                                <strong>Fecha de finalización:</strong>
                                <%= torneo.getFechaFin() %>
                            </p>

                            <p>
                                <strong>Premio:</strong>
                                ₡<%= torneo.getPremio() %>
                            </p>

                            <div class="alert alert-info">

                                Actualmente existe un torneo activo.
                                Los participantes y las llaves
                                no pueden modificarse.

                            </div>

                        </div>

                    </div>

                    <hr>

                    <div class="row g-3 text-center">

                        <div class="col-6 col-md-3">

                            <div class="border border-secondary rounded p-3">

                                <h3 class="text-success">16</h3>
                                <p class="mb-0">Participantes</p>

                            </div>

                        </div>

                        <div class="col-6 col-md-3">

                            <div class="border border-secondary rounded p-3">

                                <h3 class="text-success">4</h3>
                                <p class="mb-0">Rondas</p>

                            </div>

                        </div>

                        <div class="col-6 col-md-3">

                            <div class="border border-secondary rounded p-3">

                                <h3 class="text-success">15</h3>
                                <p class="mb-0">Partidos</p>

                            </div>

                        </div>

                        <div class="col-6 col-md-3">

                            <div class="border border-secondary rounded p-3">

                                <h3 class="text-success">1</h3>
                                <p class="mb-0">Campeón por definir</p>

                            </div>

                        </div>

                    </div>

                </div>

            </div>

            <% } else { %>

            <form action="TorneoServlet"
                  method="POST"
                  onsubmit="return validarEquipos();">

                <input type="hidden"
                       name="accion"
                       value="crear">

                <div class="card bg-dark text-light border-secondary mb-4">

                    <div class="card-body p-4">

                        <h4 class="mb-4">Información del torneo</h4>

                        <div class="row g-3">

                            <div class="col-md-6">

                                <label class="form-label">
                                    Nombre del torneo
                                </label>

                                <input type="text"
                                       name="nombre"
                                       class="form-control"
                                       maxlength="100"
                                       required>

                            </div>

                            <div class="col-md-6">

                                <label class="form-label">
                                    URL de la imagen
                                </label>

                                <input type="url"
                                       name="imagen"
                                       class="form-control"
                                       placeholder="https://..."
                                       maxlength="500"
                                       required>

                            </div>

                            <div class="col-md-4">

                                <label class="form-label">
                                    Fecha de inicio
                                </label>

                                <input type="date"
                                       name="fechaInicio"
                                       class="form-control"
                                       required>

                            </div>

                            <div class="col-md-4">

                                <label class="form-label">
                                    Fecha de finalización
                                </label>

                                <input type="date"
                                       name="fechaFin"
                                       class="form-control"
                                       required>

                            </div>

                            <div class="col-md-4">

                                <label class="form-label">
                                    Premio en colones
                                </label>

                                <input type="number"
                                       name="premio"
                                       class="form-control"
                                       min="0"
                                       max="9999999999.99"
                                       step="0.01"
                                       required>

                            </div>

                        </div>

                    </div>

                </div>

                <div class="card bg-dark text-light border-secondary mb-4">

                    <div class="card-body p-4">

                        <div class="d-flex justify-content-between align-items-center flex-wrap mb-3">

                            <h4>Seleccionar participantes</h4>

                            <h5 class="text-success">

                                <span id="contador">0</span>/16

                            </h5>

                        </div>

                        <p class="texto-secundario">
                            Selecciona los 16 equipos aprobados
                            que participarán en el torneo.
                        </p>

                        <% if (equipos.size() != 16) { %>

                        <div class="alert alert-warning">

                            Deben existir exactamente 16 equipos aprobados.

                            Actualmente existen:
                            <%= equipos.size() %>.

                        </div>

                        <% } %>

                        <div class="mb-4">

                            <button type="button"
                                    class="btn btn-outline-success btn-sm"
                                    onclick="seleccionarTodos()">

                                Seleccionar todos

                            </button>

                            <button type="button"
                                    class="btn btn-outline-secondary btn-sm"
                                    onclick="deseleccionarTodos()">

                                Deseleccionar todos

                            </button>

                        </div>

                        <div class="row g-3">

                            <% for (Equipo equipo : equipos) { %>

                            <div class="col-sm-6 col-lg-3">

                                <label class="border border-secondary rounded p-3 w-100 d-flex align-items-center gap-3">

                                    <input type="checkbox"
                                           name="equipos"
                                           value="<%= equipo.getId() %>"
                                           class="form-check-input"
                                           onchange="actualizarContador()">

                                    <img src="<%= equipo.getEscudo().replace("&", "&amp;").replace("\"", "&quot;").replace("<", "&lt;").replace(">", "&gt;") %>"
                                         alt="Escudo"
                                         width="45"
                                         height="45"
                                         style="object-fit: contain;">

                                    <span>

                                        <%= equipo.getNombre().replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;") %>

                                    </span>

                                </label>

                            </div>

                            <% } %>

                        </div>

                    </div>

                </div>

                <div class="alert alert-warning">

                    <strong>Importante:</strong>

                    Una vez creado el torneo, los participantes
                    y las llaves eliminatorias no podrán modificarse.

                </div>

                <button type="submit"
                        id="btnCrear"
                        class="btn-arena"
                        disabled>

                    Crear torneo y generar llaves

                </button>

            </form>

            <% } %>

            <div class="pie-pagina">
                ArenaCup 2026 | Programación II
            </div>

        </div>

        <script>

            function actualizarContador() {

                var seleccionados = document.querySelectorAll(
                        'input[name="equipos"]:checked');

                var total = document.querySelectorAll(
                        'input[name="equipos"]').length;

                document.getElementById("contador").textContent =
                        seleccionados.length;

                document.getElementById("btnCrear").disabled =
                        seleccionados.length !== 16 || total !== 16;
            }

            function seleccionarTodos() {

                var equipos = document.querySelectorAll(
                        'input[name="equipos"]');

                for (var i = 0; i < equipos.length; i++) {

                    equipos[i].checked = i < 16;
                }

                actualizarContador();
            }

            function deseleccionarTodos() {

                var equipos = document.querySelectorAll(
                        'input[name="equipos"]');

                for (var i = 0; i < equipos.length; i++) {

                    equipos[i].checked = false;
                }

                actualizarContador();
            }

            function validarEquipos() {

                var seleccionados = document.querySelectorAll(
                        'input[name="equipos"]:checked');

                var total = document.querySelectorAll(
                        'input[name="equipos"]').length;

                if (seleccionados.length !== 16 || total !== 16) {

                    alert("Debe seleccionar exactamente 16 equipos aprobados.");

                    return false;
                }

                return confirm(
                        "¿Está seguro de crear el torneo? "
                        + "Las llaves no podrán modificarse.");
            }

        </script>

    </body>

</html>