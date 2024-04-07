import java.nio.file.Files;
import java.util.HashMap;

public class FuncionesLectura {

    public static HashMap<Integer, Aeropuerto> leerAeropuertos(String archivo) {
        System.out.println("Leyendo aeropuertos desde " + archivo);
        HashMap<Integer, Aeropuerto> aeropuertos = new HashMap<>();
        try (BufferedReader br = Files.newBufferedReader(Paths.get(archivo))) {
            String line;
            int lineCount = 0;
            while ((line = br.readLine()) != null) {
                lineCount++;
                if (line.trim().isEmpty() || line.contains("GMT") || lineCount <= 4) {
                    continue; // Skip empty lines and headers
                }
                String[] parts = line.split("\\s+");
                int number = Integer.parseInt(parts[0]);
                String oaciCode = parts[1];
                String city = parts[2];
                String country = parts[3];
                String shortName = parts[4];
                int gmt = Integer.parseInt(parts[5]);
                int capacity = Integer.parseInt(parts[6]);


                aeropuertos.put(number, new Aeropuerto(number, oaciCode, city, country, shortName, gmt, capacity));
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e);
        }
        return aeropuertos;
    }
    
}
