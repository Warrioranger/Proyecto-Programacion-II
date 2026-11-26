package modelo;

public class Partido {

    private int id;
    private int rondaId;
    private int numero;
    private Integer equipo1Id;
    private Integer equipo2Id;
    private Integer marcador1;
    private Integer marcador2;
    private Integer ganadorId;
    private Integer siguientePartidoId;
    private Integer posicionSiguiente;
    private String estado;

    private String nombreRonda;
    private int numeroRonda;
    private String nombreEquipo1;
    private String nombreEquipo2;
    private String escudoEquipo1;
    private String escudoEquipo2;

    public Partido() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRondaId() {
        return rondaId;
    }

    public void setRondaId(int rondaId) {
        this.rondaId = rondaId;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public Integer getEquipo1Id() {
        return equipo1Id;
    }

    public void setEquipo1Id(Integer equipo1Id) {
        this.equipo1Id = equipo1Id;
    }

    public Integer getEquipo2Id() {
        return equipo2Id;
    }

    public void setEquipo2Id(Integer equipo2Id) {
        this.equipo2Id = equipo2Id;
    }

    public Integer getMarcador1() {
        return marcador1;
    }

    public void setMarcador1(Integer marcador1) {
        this.marcador1 = marcador1;
    }

    public Integer getMarcador2() {
        return marcador2;
    }

    public void setMarcador2(Integer marcador2) {
        this.marcador2 = marcador2;
    }

    public Integer getGanadorId() {
        return ganadorId;
    }

    public void setGanadorId(Integer ganadorId) {
        this.ganadorId = ganadorId;
    }

    public Integer getSiguientePartidoId() {
        return siguientePartidoId;
    }

    public void setSiguientePartidoId(Integer siguientePartidoId) {
        this.siguientePartidoId = siguientePartidoId;
    }

    public Integer getPosicionSiguiente() {
        return posicionSiguiente;
    }

    public void setPosicionSiguiente(Integer posicionSiguiente) {
        this.posicionSiguiente = posicionSiguiente;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getNombreRonda() {
        return nombreRonda;
    }

    public void setNombreRonda(String nombreRonda) {
        this.nombreRonda = nombreRonda;
    }

    public int getNumeroRonda() {
        return numeroRonda;
    }

    public void setNumeroRonda(int numeroRonda) {
        this.numeroRonda = numeroRonda;
    }

    public String getNombreEquipo1() {
        return nombreEquipo1;
    }

    public void setNombreEquipo1(String nombreEquipo1) {
        this.nombreEquipo1 = nombreEquipo1;
    }

    public String getNombreEquipo2() {
        return nombreEquipo2;
    }

    public void setNombreEquipo2(String nombreEquipo2) {
        this.nombreEquipo2 = nombreEquipo2;
    }

    public String getEscudoEquipo1() {
        return escudoEquipo1;
    }

    public void setEscudoEquipo1(String escudoEquipo1) {
        this.escudoEquipo1 = escudoEquipo1;
    }

    public String getEscudoEquipo2() {
        return escudoEquipo2;
    }

    public void setEscudoEquipo2(String escudoEquipo2) {
        this.escudoEquipo2 = escudoEquipo2;
    }
}