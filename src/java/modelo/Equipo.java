package modelo;

public class Equipo {

    private int id;
    private String nombre;
    private String escudo;
    private String telefono;
    private int usuarioId;
    private String estado;
    private String nombreUsuario;

    public Equipo() {
    }

    public Equipo(int id, String nombre, String escudo,
            String telefono, int usuarioId, String estado) {

        this.id = id;
        this.nombre = nombre;
        this.escudo = escudo;
        this.telefono = telefono;
        this.usuarioId = usuarioId;
        this.estado = estado;
        this.nombreUsuario = nombreUsuario;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEscudo() {
        return escudo;
    }

    public void setEscudo(String escudo) {
        this.escudo = escudo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }
}