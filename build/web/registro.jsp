<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport"
              content="width=device-width, initial-scale=1.0">

        <title>ArenaCup | Registro de usuario</title>

        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
              rel="stylesheet">

        <link rel="stylesheet" href="css/estilos.css">
    </head>

    <body>

        <div class="pantalla-auth">

            <div class="tarjeta-auth">

                <div class="marca">ARENA CUP</div>

                <p class="subtitulo">
                    Sistema de Gestión de Torneos
                </p>

                <h4 class="mb-4">Crear cuenta</h4>

                <% if (request.getAttribute("error") != null) { %>

                <div class="alert alert-danger">
                    <%= request.getAttribute("error") %>
                </div>

                <% } %>

                <form action="RegistroServlet" method="POST">

                    <div class="mb-3">

                        <label class="form-label" for="nombre">
                            Nombre completo
                        </label>

                        <input type="text"
                               id="nombre"
                               name="nombre"
                               class="form-control"
                               placeholder="Ingrese su nombre completo"
                               maxlength="100"
                               required>

                    </div>

                    <div class="mb-3">

                        <label class="form-label" for="correo">
                            Correo electrónico
                        </label>

                        <input type="email"
                               id="correo"
                               name="correo"
                               class="form-control"
                               placeholder="correo@ejemplo.com"
                               maxlength="150"
                               required>

                    </div>

                    <div class="mb-4">

                        <label class="form-label" for="contrasena">
                            Contraseña
                        </label>

                        <input type="password"
                               id="contrasena"
                               name="contrasena"
                               class="form-control"
                               placeholder="Mínimo 8 caracteres"
                               minlength="8"
                               required>

                    </div>

                    <button type="submit" class="btn-arena">
                        Crear cuenta
                    </button>

                </form>

                <div class="text-center mt-4">

                    <span class="texto-secundario">
                        ¿Ya tenés una cuenta?
                    </span>

                    <a href="login.jsp" class="enlace">
                        Iniciar sesión
                    </a>

                </div>

                <div class="pie-pagina">
                    ArenaCup 2026 | Programación II
                </div>

            </div>

        </div>

    </body>
</html>