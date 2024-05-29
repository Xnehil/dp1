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

export function tiempoEntreAhoraYSalida(vuelo: Vuelo, aeropuertos: Map<String, Aeropuerto>, simulationTime: Date): number {
    //Devuelve el tiempo en minutos entre el momento actual y la hora de inicio del vuelo
    // console.log("vuelo: ", vuelo);
    const ciudadSalida = aeropuertos.get(vuelo.origen);
    // console.log("ciudadSalida: ", ciudadSalida);
    const ahora = simulationTime;
    const [ , time] = vuelo.fechaHoraSalida.split('T');
    const [ hour, minute ] = time.split(':');

    const horaInicio = new Date();
    horaInicio.setHours(parseInt(hour));
    horaInicio.setMinutes(parseInt(minute));

    // Poner ambos el mismo día
    horaInicio.setFullYear(ahora.getFullYear());
    horaInicio.setMonth(ahora.getMonth());
    horaInicio.setDate(ahora.getDate());

    // Convertir la horaactual a la zona horaria de la ciudad de salida
    const timeZone = ciudadSalida?.zoneId;
    const ahoraEnZonaHorariaSalida = new Date(ahora.toLocaleString("en-US", {timeZone: timeZone}));
    // console.log("ahoraEnZonaHorariaSalida: ", ahoraEnZonaHorariaSalida);
    // console.log("horaInicio: ", horaInicio);
    // Calcular la diferencia en minutos
    let tiempoTranscurrido = tiempoEntre(horaInicio, ahoraEnZonaHorariaSalida);
    // console.log("tiempoTranscurrido: ", tiempoTranscurrido);
    // Si el tiempo transcurrido es negativo, sumar 24 horas
    if (tiempoTranscurrido < 0) {
        tiempoTranscurrido += 24 * 60;
    }

    return tiempoTranscurrido;
}

export function tiempoEntre(fechaInicio: Date, fechaFin: Date): number {
    //Devuelve la diferencia en minutos entre dos fechas
    const diferencia = fechaFin.getTime() - fechaInicio.getTime();
    return diferencia / (1000 * 60);
}