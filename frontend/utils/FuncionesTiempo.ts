import { Aeropuerto } from "@/types/Aeropuerto";
import { Vuelo } from "@/types/Vuelo";

export function actualmenteEnVuelo(vuelo: Vuelo, aeropuertos: Map<string, Aeropuerto>): Boolean | undefined {
    //Devuelve si el momento actual es antes de la hora de inicio del vuelo
    const ahora = new Date();
    const ciudadSalida = aeropuertos.get(vuelo.origen);
    const ciudadLlegada = aeropuertos.get(vuelo.destino);
    let enVuelo : Boolean = true;

    if (!ciudadSalida) {
        return undefined;
    }

    // Convert the timezone from the format 'GMT-04:00' to a format that JavaScript's Date object can understand
    const timeZone = ciudadSalida.zoneId;
    const ahoraEnZonaHorariaSalida = new Date(ahora.toLocaleString("en-US", {timeZone: timeZone}));

    const horaInicio = new Date(vuelo.fechaHoraSalida);
    if (horaInicio > ahoraEnZonaHorariaSalida) {
        enVuelo 
    }
    return false;

    //Creo que esto mejor no
}