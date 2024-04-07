import java.time.ZonedDateTime;

public class Vuelo {
    private int idVuelo;
    private String origen;
    private String destino;
    private ZonedDateTime fechaHoraSalida;
    private ZonedDateTime fechaHoraLlegada;
    private int capacidad;

    public Vuelo(String origen, String destino, ZonedDateTime fechaHoraSalida, ZonedDateTime fechaHoraLlegada, int capacidad) {
        this.origen = origen;
        this.destino = destino;
        this.fechaHoraSalida = fechaHoraSalida;
        this.fechaHoraLlegada = fechaHoraLlegada;
        this.capacidad = capacidad;
    }

    public Vuelo() {
        this.origen = "";
        this.destino = "";
        this.fechaHoraSalida = ZonedDateTime.now();
        this.fechaHoraLlegada = ZonedDateTime.now();
        this.capacidad = 0;
    }

    public int getIdVuelo() {
        return this.idVuelo;
    }

    public void setIdVuelo(int idVuelo) {
        this.idVuelo = idVuelo;
    }


    public String getOrigen() {
        return this.origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getDestino() {
        return this.destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public ZonedDateTime getFechaHoraSalida() {
        return this.fechaHoraSalida;
    }

    public void setFechaHoraSalida(ZonedDateTime fechaHoraSalida) {
        this.fechaHoraSalida = fechaHoraSalida;
    }

    public ZonedDateTime getFechaHoraLlegada() {
        return this.fechaHoraLlegada;
    }

    public void setFechaHoraLlegada(ZonedDateTime fechaHoraLlegada) {
        this.fechaHoraLlegada = fechaHoraLlegada;
    }

    public int getCapacidad() {
        return this.capacidad;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

}
