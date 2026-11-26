<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="modelo.Partido"%>
<%@page import="modelo.Torneo"%>
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
    if (session.getAttribute("usuario") == null) {

        response.sendRedirect("login.jsp");
        return;
    }

    String rol = (String) session.getAttribute("rol");

    boolean administrador = "ADMINISTRADOR".equals(rol);

    if (!administrador && !"USUARIO".equals(rol)) {

        response.sendError(403);
        return;
    }

    Torneo torneo = (Torneo) request.getAttribute("torneo");

    Equipo campeon = (Equipo) request.getAttribute("campeon");

    List<Partido> partidos =
            (List<Partido>) request.getAttribute("partidos");

    if (partidos == null) {

        response.sendRedirect("PartidoServlet");
        return;
    }

    int finalizados = 0;

    for (Partido partido : partidos) {

        if ("FINALIZADO".equals(partido.getEstado())) {
            finalizados++;
        }
    }

    String inicio = administrador
            ? "admin.jsp" : "usuario.jsp";

    String[] nombresRondas = {
        "Ronda preliminar",
        "Cuartos de final",
        "Semifinal",
        "Gran final"
    };
%>

<!DOCTYPE html>
<html lang="es">

    <head>

        <meta charset="UTF-8">

        <meta name="viewport"
              content="width=device-width, initial-scale=1.0">

        <title>ArenaCup | Cuadro eliminatorio</title>

        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
              rel="stylesheet">

        <link rel="stylesheet" href="css/estilos.css">

    </head>

    <body>

        <nav class="navbar navbar-dark bg-dark border-bottom border-secondary">

            <div class="container-fluid px-4">

                <a class="navbar-brand text-success fw-bold"
                   href="<%= inicio %>">

                    ARENA CUP

                    <% if (administrador) { %>
                    | ADMIN
                    <% } %>

                </a>

                <form action="CerrarSesionServlet" method="POST">

                    <button type="submit"
                            class="btn btn-outline-light btn-sm">

                        Cerrar sesión

                    </button>

                </form>

            </div>

        </nav>

        <div class="container-fluid px-4 mt-4">

            <a href="<%= inicio %>"
               class="btn btn-outline-secondary mb-4">

                Volver al inicio

            </a>

            <% if (request.getAttribute("error") != null) { %>

            <div class="alert alert-danger">

                <%= escapar(request.getAttribute("error").toString()) %>

            </div>

            <% } %>

            <% if ("registrado".equals(
                    request.getParameter("mensaje"))) { %>

            <div class="alert alert-success">

                Resultado registrado correctamente.

            </div>

            <% } %>

            <% if (torneo == null) { %>

            <div class="alert alert-secondary">

                Actualmente no hay un torneo disponible.

            </div>

            <% } else { %>

            <div class="text-center mb-4">

                <img src="<%= escapar(torneo.getImagen()) %>"
                     alt="Imagen del torneo"
                     width="90"
                     height="90"
                     style="object-fit: contain;">

                <h2 class="mt-3">

                    <%= escapar(torneo.getNombre()) %>

                </h2>

                <p class="texto-secundario">

                    Cuadro eliminatorio

                </p>

                <% if ("ACTIVO".equals(torneo.getEstado())) { %>

                <span class="badge bg-success">

                    Torneo activo

                </span>

                <% } else { %>

                <span class="badge bg-secondary">

                    Torneo finalizado

                </span>

                <% } %>

            </div>

            <% if (campeon != null) { %>

            <div class="card tarjeta-campeon mb-4">

                <div class="card-body text-center p-5">

                    <div class="trofeo-campeon">

                        🏆

                    </div>

                    <h3 class="text-warning fw-bold">

                        CAMPEÓN DEL TORNEO

                    </h3>

                    <img src="<%= escapar(campeon.getEscudo()) %>"
                         alt="Escudo del campeón"
                         width="120"
                         height="120"
                         class="my-4"
                         style="object-fit: contain;">

                    <h1 class="fw-bold">

                        <%= escapar(campeon.getNombre()) %>

                    </h1>

                    <p class="texto-secundario">

                        <%= escapar(torneo.getNombre()) %>

                    </p>

                    <h4 class="text-warning mt-4">

                        Premio: ₡<%= String.format(
                                "%,.2f", torneo.getPremio()) %>

                    </h4>

                    <span class="badge bg-success mt-3">

                        Torneo finalizado

                    </span>

                </div>

            </div>

            <% } %>

            <div class="row g-3 mb-4 text-center">

                <div class="col-6 col-md-3">

                    <div class="card bg-dark text-light border-secondary">

                        <div class="card-body">

                            <p class="texto-secundario">Participantes</p>

                            <h3 class="text-success">16</h3>

                        </div>

                    </div>

                </div>

                <div class="col-6 col-md-3">

                    <div class="card bg-dark text-light border-secondary">

                        <div class="card-body">

                            <p class="texto-secundario">Partidos jugados</p>

                            <h3 class="text-success">

                                <%= finalizados %>/15

                            </h3>

                        </div>

                    </div>

                </div>

                <div class="col-6 col-md-3">

                    <div class="card bg-dark text-light border-secondary">

                        <div class="card-body">

                            <p class="texto-secundario">Pendientes</p>

                            <h3 class="text-warning">

                                <%= partidos.size() - finalizados %>

                            </h3>

                        </div>

                    </div>

                </div>

                <div class="col-6 col-md-3">

                    <div class="card bg-dark text-light border-secondary">

                        <div class="card-body">

                            <p class="texto-secundario">Premio</p>

                            <h3 class="text-success">

                                ₡<%= String.format(
                                        "%,.2f", torneo.getPremio()) %>

                            </h3>

                        </div>

                    </div>

                </div>

            </div>

            <div class="progress mb-4"
                 style="height: 10px;">

                <div class="progress-bar bg-success"
                     style="width: <%= finalizados * 100 / 15 %>%;">

                </div>

            </div>

            <div class="cuadro-contenedor">

                <div class="cuadro-eliminatorio">

                    <% for (int ronda = 1; ronda <= 4; ronda++) { %>

                    <div class="columna-ronda ronda-<%= ronda %>">

                        <h5 class="titulo-ronda">

                            <%= nombresRondas[ronda - 1] %>

                        </h5>

                        <div class="partidos-ronda">

                            <% for (Partido partido : partidos) {

                                if (partido.getNumeroRonda() != ronda) {
                                    continue;
                                }

                                boolean terminado =
                                        "FINALIZADO".equals(
                                                partido.getEstado());

                                boolean disponible =
                                        partido.getEquipo1Id() != null
                                        && partido.getEquipo2Id() != null;

                                boolean ganador1 =
                                        terminado
                                        && partido.getGanadorId() != null
                                        && partido.getGanadorId().equals(
                                                partido.getEquipo1Id());

                                boolean ganador2 =
                                        terminado
                                        && partido.getGanadorId() != null
                                        && partido.getGanadorId().equals(
                                                partido.getEquipo2Id());
                            %>

                            <div class="partido-contenedor">

                                <div class="partido">

                                    <div class="partido-encabezado">

                                        <span>

                                            Partido <%= partido.getNumero() %>

                                        </span>

                                        <% if (terminado) { %>

                                        <span class="badge bg-success">

                                            Finalizado

                                        </span>

                                        <% } else if (disponible) { %>

                                        <span class="badge bg-warning text-dark">

                                            Pendiente

                                        </span>

                                        <% } else { %>

                                        <span class="badge bg-secondary">

                                            Por definir

                                        </span>

                                        <% } %>

                                    </div>

                                    <div class="equipo-partido
                                         <%= ganador1 ? "equipo-ganador" : "" %>">

                                        <% if (partido.getEscudoEquipo1() != null) { %>

                                        <img src="<%= escapar(
                                                partido.getEscudoEquipo1()) %>"
                                             alt="Escudo">

                                        <% } else { %>

                                        <span class="escudo-vacio">?</span>

                                        <% } %>

                                        <span class="nombre-equipo">

                                            <%= partido.getNombreEquipo1() != null
                                                    ? escapar(partido.getNombreEquipo1())
                                                    : "Por definir" %>

                                        </span>

                                        <strong class="marcador">

                                            <%= partido.getMarcador1() != null
                                                    ? partido.getMarcador1()
                                                    : "-" %>

                                        </strong>

                                    </div>

                                    <div class="equipo-partido
                                         <%= ganador2 ? "equipo-ganador" : "" %>">

                                        <% if (partido.getEscudoEquipo2() != null) { %>

                                        <img src="<%= escapar(
                                                partido.getEscudoEquipo2()) %>"
                                             alt="Escudo">

                                        <% } else { %>

                                        <span class="escudo-vacio">?</span>

                                        <% } %>

                                        <span class="nombre-equipo">

                                            <%= partido.getNombreEquipo2() != null
                                                    ? escapar(partido.getNombreEquipo2())
                                                    : "Por definir" %>

                                        </span>

                                        <strong class="marcador">

                                            <%= partido.getMarcador2() != null
                                                    ? partido.getMarcador2()
                                                    : "-" %>

                                        </strong>

                                    </div>

                                    <% if (administrador
                                            && disponible
                                            && !terminado
                                            && "ACTIVO".equals(torneo.getEstado())) { %>

                                    <button type="button"
                                            class="btn btn-outline-success btn-sm w-100 mt-2"
                                            data-bs-toggle="modal"
                                            data-bs-target="#resultado<%= partido.getId() %>">

                                        Registrar resultado

                                    </button>

                                    <% } %>

                                </div>

                            </div>

                            <% } %>

                        </div>

                    </div>

                    <% } %>

                </div>

            </div>

            <% if (administrador
                    && "ACTIVO".equals(torneo.getEstado())) {

                for (Partido partido : partidos) {

                    if (partido.getEquipo1Id() == null
                            || partido.getEquipo2Id() == null
                            || !"PENDIENTE".equals(partido.getEstado())) {

                        continue;
                    }
            %>

            <div class="modal fade"
                 id="resultado<%= partido.getId() %>"
                 tabindex="-1">

                <div class="modal-dialog modal-dialog-centered">

                    <div class="modal-content bg-dark text-light border-secondary">

                        <div class="modal-header border-secondary">

                            <h5 class="modal-title">

                                Registrar resultado

                            </h5>

                            <button type="button"
                                    class="btn-close btn-close-white"
                                    data-bs-dismiss="modal">

                            </button>

                        </div>

                        <form action="PartidoServlet"
                              method="POST"
                              onsubmit="return validarMarcador(this);">

                            <div class="modal-body">

                                <input type="hidden"
                                       name="accion"
                                       value="registrar">

                                <input type="hidden"
                                       name="partidoId"
                                       value="<%= partido.getId() %>">

                                <p class="text-center texto-secundario">

                                    <%= escapar(partido.getNombreRonda()) %>

                                </p>

                                <div class="row g-3 text-center">

                                    <div class="col-6">

                                        <img src="<%= escapar(
                                                partido.getEscudoEquipo1()) %>"
                                             width="75"
                                             height="75"
                                             alt="Escudo"
                                             style="object-fit: contain;">

                                        <h6 class="mt-3">

                                            <%= escapar(partido.getNombreEquipo1()) %>

                                        </h6>

                                        <input type="number"
                                               name="marcador1"
                                               class="form-control text-center"
                                               min="0"
                                               max="99"
                                               step="1"
                                               required>

                                    </div>

                                    <div class="col-6">

                                        <img src="<%= escapar(
                                                partido.getEscudoEquipo2()) %>"
                                             width="75"
                                             height="75"
                                             alt="Escudo"
                                             style="object-fit: contain;">

                                        <h6 class="mt-3">

                                            <%= escapar(partido.getNombreEquipo2()) %>

                                        </h6>

                                        <input type="number"
                                               name="marcador2"
                                               class="form-control text-center"
                                               min="0"
                                               max="99"
                                               step="1"
                                               required>

                                    </div>

                                </div>

                                <div class="alert alert-warning mt-4 mb-0">

                                    No se permiten empates.
                                    El ganador avanzará automáticamente.

                                </div>

                            </div>

                            <div class="modal-footer border-secondary">

                                <button type="button"
                                        class="btn btn-secondary"
                                        data-bs-dismiss="modal">

                                    Cancelar

                                </button>

                                <button type="submit"
                                        class="btn btn-success">

                                    Guardar resultado

                                </button>

                            </div>

                        </form>

                    </div>

                </div>

            </div>

            <% }
                } %>

            <% } %>

            <div class="pie-pagina mb-4">

                ArenaCup 2026 | Programación II

            </div>

        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js">
        </script>

        <script>

            function validarMarcador(formulario) {

                var marcador1 = Number(
                        formulario.marcador1.value);

                var marcador2 = Number(
                        formulario.marcador2.value);

                if (marcador1 === marcador2) {

                    alert("No se permiten empates.");

                    return false;
                }

                if (marcador1 < 0 || marcador2 < 0
                        || marcador1 > 99 || marcador2 > 99) {

                    alert("Los marcadores deben estar entre 0 y 99.");

                    return false;
                }

                return confirm(
                        "¿Desea guardar este resultado? "
                        + "No podrá modificarlo posteriormente.");
            }

        </script>

    </body>

</html>