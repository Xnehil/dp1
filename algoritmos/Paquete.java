//Un paquete es parte de un envío

import java.time.Duration;
import java.util.ArrayList;

public class Paquete {
    private int idPaquete;
    private int idEnvío;

    //Se almacena la lista de ids de los vuelos a seguir
    private ArrayList<Integer> ruta;

    //Tiempo restante para que el paquete llegue a su destino
    private Duration tiempoRestante;


    public Paquete(int idPaquete, int idEnvío, ArrayList<Integer> ruta, Duration tiempoRestante) {
        this.idPaquete = idPaquete;
        this.idEnvío = idEnvío;
        this.ruta = ruta;
        this.tiempoRestante = tiempoRestante;
    }

    public Paquete() {
        this.idPaquete = 0;
        this.idEnvío = 0;
        this.ruta = new ArrayList<Integer>();
        this.tiempoRestante = Duration.ZERO;
    }
}
