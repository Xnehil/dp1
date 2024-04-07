import java.util.HashMap;

public class Main {
    public static void main(String[] args) {
        HashMap<Integer, Aeropuerto> aeropuertos = FuncionesLectura.leerAeropuertos("data/Aeropuerto.husos.v1.incompleto.txt");
        for (Aeropuerto a : aeropuertos.values()) {
            System.out.println(a);
        }
    }
}
