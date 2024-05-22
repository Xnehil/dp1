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

export function tiempoEntreAhoraYSalida(vuelo: Vuelo, aeropuertos: Map<String, Aeropuerto>): number {
    //Devuelve el tiempo en minutos entre el momento actual y la hora de inicio del vuelo
    const ahora = new Date();
    const ciudadSalida = aeropuertos.get(vuelo.origen);

    // Convert the timezone from the format 'GMT-04:00' to a format that JavaScript's Date object can understand
    const timeZone = ciudadSalida?.zoneId ?? "GMT-05:00";
    const ahoraEnZonaHorariaSalida = new Date(ahora.toLocaleString("en-US", {timeZone: timeZone}));

    const horaInicio = new Date(vuelo.fechaHoraSalida);
    return (ahoraEnZonaHorariaSalida.getTime() - horaInicio.getTime()) / 60000;
}

export function tiempoEntre(fecha1: Date, fecha2: Date): number {
    //Devuelve el tiempo en minutos entre dos fechas
    return (fecha1.getTime() - fecha2.getTime()) / 60000;
}