import java.util.HashMap;

public class Main {
    public static void main(String[] args) {

        HashMap<String, Aeropuerto> aeropuertos = FuncionesLectura.leerAeropuertos("algoritmos/data/Aeropuerto.husos.v1.incompleto.txt");
        for (Aeropuerto a : aeropuertos.values()) {
            if (a.getGmt() <0 ){
                a.setContinente("América del Sur");
            }
            else{
                a.setContinente("Europa");
            }
        }

        HashMap<Integer, Vuelo> vuelos = FuncionesLectura.leerVuelos("algoritmos/data/Planes.vuelo.v1.incompleto.txt", aeropuertos);
        HashMap<Integer, Envio> envios = FuncionesLectura.leerEnvios("algoritmos/data/pack_enviado/pack_enviado_EBCI.txt", aeropuertos);

        double[][] owo= Auxiliares.levy(10, 10, 1.5);
        for (int i = 0; i < owo.length; i++) {
            for (int j = 0; j < owo[i].length; j++) {
                System.out.print(owo[i][j] + " ");
            }
            System.out.println();
        }

    }
}
