import { Aeropuerto } from "@/types/Aeropuerto";
import { Envio } from "@/types/Envio";
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

export function tiempoEntreAhoraYSalida(vuelo: Vuelo, simulationTime: Date, verbose?: boolean): number {
    const [ , time] = vuelo.fechaHoraSalida.split('T');
    const [ hour, minute ] = time.split(':');

    // Convert simulationTime to UTC
    const ahoraUTC = new Date(simulationTime.getTime());

    const horaInicioUTC = new Date(ahoraUTC.getTime());
    horaInicioUTC.setUTCHours(parseInt(hour));
    horaInicioUTC.setUTCMinutes(parseInt(minute));

    if (verbose) {
        console.log("hour: ", hour);
        console.log("minute: ", minute);
        console.log("ahoraUTC: ", ahoraUTC);
        console.log("horaInicioUTC: ", horaInicioUTC);
    }

    let tiempoTranscurrido = tiempoEntre(horaInicioUTC, ahoraUTC);

    // Si el tiempo transcurrido es negativo, significa que es el día siguiente
    if (tiempoTranscurrido < 0) {
        tiempoTranscurrido+= 24 * 60;
    }

    return tiempoTranscurrido;
}

export function tiempoEntre(fechaInicio: Date, fechaFin: Date): number {
    //Devuelve la diferencia en minutos entre dos fechas
    const diferencia = fechaFin.getTime() - fechaInicio.getTime();
    return diferencia / (1000 * 60);
}

export function aHoraMinutos(tiempo: number): string {
    //Devuelve un string con el tiempo en formato hh:mm
    const horas = Math.floor(tiempo / 60);
    const minutos = (tiempo % 60).toFixed(0);
    return `${horas.toString().padStart(2, "0")}:${minutos.toString().padStart(2, "0")}`;
}

export function tiempoFaltante(envio: Envio | undefined, simulationTime: Date): string{
    //Devuelve un string con el tiempo restante para la llegada del envío
    // console.log("Calculando tiempo entre: ", simulationTime, new Date((envio?.fechaHoraLlegadaPrevista ?? 0 )*1000 ));
    const tiempoRestante = tiempoEntre(simulationTime, new Date((envio?.fechaHoraLlegadaPrevista ?? 0 )*1000 ));
    if(tiempoRestante < 0) {
        return "00:00";
    }
    // console.log("Tiempo restante: ", tiempoRestante);
    return aHoraMinutos(tiempoRestante);
}

export function tiempoNumeroADiasHorasMinutos(tiempo: number): string {
    //Devuelve un string con el tiempo en formato dd hh:mm
    const dias = Math.floor(tiempo / (60 * 24));
    const horas = Math.floor((tiempo % (60 * 24)) / 60);
    const minutos = (tiempo % 60).toFixed(0);
    return `${dias}d ${horas.toString().padStart(2, "0")}:${minutos.toString().padStart(2, "0")}`;
}

export function mostrarTiempoEnZonaHoraria(fecha: Date, zonaHoraria: number): string {
    const simulationTimeInMS = new Date(fecha).getTime();
    const timezoneDifference =zonaHoraria - (-5);
    const adjustedSimulationTime = new Date(simulationTimeInMS + timezoneDifference * 60 * 60 * 1000);
    return adjustedSimulationTime.toLocaleTimeString();
}

export function utcStringToZonedDate(utcString: string, zonaHoraria: number): string{
    // console.log("utcString: ", utcString);
    // console.log("zonaHoraria: ", zonaHoraria);
    const simulationTimeInMS = new Date(utcString).getTime();
    const adjustedSimulationTime = new Date(simulationTimeInMS + zonaHoraria * 60 * 60 * 1000);
    // Extract the time portion in HH:MM:SS format
    return adjustedSimulationTime.toISOString().split("T")[1].split(".")[0];
}