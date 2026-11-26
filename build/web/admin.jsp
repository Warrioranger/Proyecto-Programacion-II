<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="modelo.Torneo"%>
<%@page import="modelo.TorneoDAO"%>
<%@page import="modelo.Equipo"%>
<%@page import="modelo.EquipoDAO"%>

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
            || !"ADMINISTRADOR".equals(session.getAttribute("rol"))) {

        response.sendRedirect("login.jsp");
        return;
    }

    TorneoDAO torneoDAO = new TorneoDAO();
    EquipoDAO equipoDAO = new EquipoDAO();

    Torneo torneo = torneoDAO.obtenerTorneoActivo();

    if (torneo == null) {

        torneo = torneoDAO.obtenerUltimoTorneo();
    }

    Equipo campeon = null;

    if (torneo != null && torneo.getCampeonId() != null) {

        campeon = equipoDAO.obtenerPorId(
                torneo.getCampeonId());
    }
%>

<!DOCTYPE html>
<html lang="es">

    <head>

        <meta charset="UTF-8">

        <meta name="viewport"
              content="width=device-width, initial-scale=1.0">

        <title>ArenaCup | Administración</title>

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

        <div class="container mt-5">

            <h2>Panel de administración</h2>

            <p class="texto-secundario">

                Administración general del torneo.

            </p>

            <hr>

            <% if (campeon != null) { %>

            <div class="card tarjeta-campeon mb-4">

                <div class="card-body text-center p-4">

                    <div class="trofeo-campeon">🏆</div>

                    <h3 class="text-warning fw-bold">

                        CAMPEÓN DEL TORNEO

                    </h3>

                    <img src="<%= escapar(campeon.getEscudo()) %>"
                         width="100"
                         height="100"
                         alt="Escudo del campeón"
                         class="my-3"
                         style="object-fit: contain;">

                    <h2>

                        <%= escapar(campeon.getNombre()) %>

                    </h2>

                    <p class="texto-secundario">

                        <%= escapar(torneo.getNombre()) %>

                    </p>

                    <h5 class="text-warning">

                        Premio: ₡<%= String.format(
                                "%,.2f", torneo.getPremio()) %>

                    </h5>

                    <a href="PartidoServlet"
                       class="btn btn-outline-warning mt-3">

                        Consultar resultados finales

                    </a>

                </div>

            </div>

            <% } else if (torneo != null) { %>

            <div class="card bg-dark text-light border-secondary mb-4">

                <div class="card-body d-flex align-items-center gap-4">

                    <img src="<%= escapar(torneo.getImagen()) %>"
                         width="70"
                         height="70"
                         alt="Imagen del torneo"
                         style="object-fit: contain;">

                    <div>

                        <span class="badge bg-success mb-2">

                            Torneo activo

                        </span>

                        <h4>

                            <%= escapar(torneo.getNombre()) %>

                        </h4>

                        <a href="PartidoServlet" class="enlace">

                            Ver cuadro eliminatorio

                        </a>

                    </div>

                </div>

            </div>

            <% } %>

            <div class="row g-4">

                <div class="col-md-4">

                    <div class="card bg-dark text-light border-secondary h-100">

                        <div class="card-body">

                            <h5>Gestión de equipos</h5>

                            <p class="texto-secundario">

                                Consulta y aprueba los equipos registrados.

                            </p>

                            <a href="EquipoServlet"
                               class="btn btn-success w-100">

                                Administrar equipos

                            </a>

                        </div>

                    </div>

                </div>

                <div class="col-md-4">

                    <div class="card bg-dark text-light border-secondary h-100">

                        <div class="card-body">

                            <h5>Gestión del torneo</h5>

                            <p class="texto-secundario">

                                Crea el torneo y genera las llaves.

                            </p>

                            <a href="TorneoServlet"
                               class="btn btn-success w-100">

                                Administrar torneo

                            </a>

                        </div>

                    </div>

                </div>

                <div class="col-md-4">

                    <div class="card bg-dark text-light border-secondary h-100">

                        <div class="card-body">

                            <h5>Registro de resultados</h5>

                            <p class="texto-secundario">

                                Registra marcadores y consulta
                                los resultados del torneo.

                            </p>

                            <a href="PartidoServlet"
                               class="btn btn-success w-100">

                                Administrar resultados

                            </a>

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