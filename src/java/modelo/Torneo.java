package modelo;

import java.sql.Date;
import java.math.BigDecimal;

public class Torneo {

    private int id;
    private String nombre;
    private String imagen;
    private Date fechaInicio;
    private Date fechaFin;
    private BigDecimal premio;
    private String estado;
    private Integer campeonId;

    public Torneo() {
    }

    public Torneo(int id, String nombre, String imagen,
            Date fechaInicio, Date fechaFin,
            BigDecimal premio, String estado,
            Integer campeonId) {

        this.id = id;
        this.nombre = nombre;
        this.imagen = imagen;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.premio = premio;
        this.estado = estado;
        this.campeonId = campeonId;
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

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    public BigDecimal getPremio() {
        return premio;
    }

    public void setPremio(BigDecimal premio) {
        this.premio = premio;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Integer getCampeonId() {
        return campeonId;
    }

    public void setCampeonId(Integer campeonId) {
        this.campeonId = campeonId;
    }
}