import { dinamicPlaneStyle, greenAirportStyle, greenPlaneStyle, redAirportStyle, redPlaneStyle, yellowAirportStyle, yellowPlaneStyle } from "@/components/mapa/EstilosMapa";
import { Aeropuerto } from "@/types/Aeropuerto";
import { Envio } from "@/types/Envio";
import { Paquete } from "@/types/Paquete";
import { ProgramacionVuelo } from "@/types/ProgramacionVuelo";
import { Vuelo } from "@/types/Vuelo";

export function procesarData(
    messageData: any,
    programacionVuelos: React.MutableRefObject<Map<string, ProgramacionVuelo>>,
    envios: React.MutableRefObject<Map<string, Envio>>,
    aeropuertos: React.MutableRefObject<Map<string, {aeropuerto: Aeropuerto; pointFeature: any}>>,
    simulationTime: Date | null,
    cargaInicial: boolean,
    vuelos: React.RefObject<Map<number,{vuelo: Vuelo;pointFeature: any;lineFeature: any;routeFeature: any;}>>,
    esSimulacion: boolean
): void {
    console.log("Procesando data");
    for (let key in messageData) {
        // console.log("Key: ", key);
        if (messageData.hasOwnProperty(key)) {
            let envio = messageData[key] as Envio;
            // console.log("Envio: ", envio);
            envios.current.set(envio.codigoEnvio, envio);
            for (let paquete of envio.paquetes) {
                if (!paquete.llegoDestino && esSimulacion) continue;
                //Añadir paquete a aeropuerto de origen
                const aeropuertoOrigen: Aeropuerto | undefined =aeropuertos.current.get(envio.origen)?.aeropuerto;
                let vueloSalio=false;
                // console.log("paquete: ", paquete);
                for (let i = 0; i < paquete.fechasRuta?.length ?? 0; i++) {
                    const idVuelo = paquete.ruta[i];
                    const auxFechaRuta = paquete.fechasRuta[i];

                    //Para rutas salvadas tenemos que calcular la fecha de vuelo 
                    let fechaVuelo = new Date();
                    if(auxFechaRuta < 10) {
                        fechaVuelo = new Date(simulationTime!.getTime() + auxFechaRuta * 24 * 60 * 60 * 1000);
                        // console.log("Fecha vuelo: ", fechaVuelo);
                    }
                    else{
                        //Java lo envía como UNIX timestamp en segundos, pero JS lo necesita en milisegundos
                        // console.log("Fecha ruta: ", paquete.fechasRuta[i]);
                        fechaVuelo = new Date(paquete.fechasRuta[i] * 1000);
                    }
                    // console.log("Fecha vuelo: ", fechaVuelo);
                    const fechaVueloFormatted = fechaVuelo
                        .toISOString()
                        .slice(0, 10);
                    const claveProgramacion =
                        idVuelo + "-" + fechaVueloFormatted;
                    let programacion:ProgramacionVuelo | undefined;

                    let horaSalidaVuelo =new Date(vuelos.current?.get(idVuelo)?.vuelo.fechaHoraSalida ?? 0);
                    fechaVuelo.setHours(horaSalidaVuelo.getHours(), horaSalidaVuelo.getMinutes(), horaSalidaVuelo.getSeconds());
                    //Si el vuelo ya ha salido, no se puede agregar el paquete
                    if (fechaVuelo < simulationTime! && !cargaInicial) {
                        console.log("El vuelo ya ha salido, no se puede agregar el paquete");
                        console.log("horaSalidaVuelo: ", horaSalidaVuelo);
                        console.log("vue: ", vuelos.current?.get(idVuelo)?.vuelo);
                        continue;
                    }
                    if (!programacionVuelos.current.has(claveProgramacion)) {
                        programacion = {
                            fechaSalida: fechaVuelo,
                            idVuelo: idVuelo,
                            cantPaquetes: 1,
                            paquetes: [paquete],
                        };
                        programacionVuelos.current.set(claveProgramacion, programacion);
                    } else {
                        programacion = programacionVuelos.current.get(claveProgramacion);
                        if (programacion) {
                            programacion.cantPaquetes++;
                            programacion.paquetes.push(paquete);
                        }
                    }
                }

                if (aeropuertoOrigen && !cargaInicial) {
                    aeropuertoOrigen.cantidadActual++;
                    aeropuertoOrigen.paquetes.push(paquete);
                }
            }
        }
    }
    for (let key of aeropuertos.current.keys()) {
        // console.log("Decidiendo estilo de aeropuerto con clave: ", key);
        decidirEstiloAeropuerto(aeropuertos.current.get(key));
    }
    console.log("Data procesada");
}

export function limpiarMapasDeDatos(
    programacionVuelos: React.MutableRefObject<Map<string, ProgramacionVuelo>>,
    envios: React.MutableRefObject<Map<string, Envio>>,
    horaSimuladaActual: Date
) {
    const dayBefore = new Date(
        horaSimuladaActual.getTime() - 24 * 60 * 60 * 1000 * 1
    ); // 24 hours before the current simulated time

    let cuenta = 0;
    programacionVuelos.current.forEach((programacionVuelo, key) => {
        // console.log("Fecha salida: ", programacionVuelo.fechaSalida);
        // console.log("Fecha antes de: ", dayBefore);
        if (programacionVuelo.fechaSalida < dayBefore) {
            programacionVuelos.current.delete(key);
            cuenta++;
        }
    });
    console.log("Se eliminaron ", cuenta, " programaciones de vuelo");
    cuenta = 0;
    envios.current.forEach((envio, key) => {
        let fechaFin = new Date(envio.fechaHoraLlegadaPrevista * 1000);
        // console.log("Fecha fin: ", fechaFin);
        // console.log("Fecha antes de: ", dayBefore);
        if (fechaFin < dayBefore) {
            envios.current.delete(key);
            cuenta++;
        }
    });
    console.log("Se eliminaron ", cuenta, " envíos");
}

export function quitarPaquetesAlmacenados(
    nuevosVuelos: number[],
    programacionVuelos: React.MutableRefObject<Map<string, ProgramacionVuelo>>,
    aeropuertos: React.MutableRefObject<Map<string, {aeropuerto: Aeropuerto; pointFeature: any}>>,
    simulationTime: Date | null
) {
    if (!simulationTime) return;
    // console.log("Simulation time: ", simulationTime);
    const diaDeSimulacion = simulationTime.toISOString().slice(0, 10);
    let cuenta = 0;
    for (let idVuelo of nuevosVuelos) {
        const claveProgramacion = idVuelo + "-" + diaDeSimulacion;
        const programacion = programacionVuelos.current.get(claveProgramacion);
        if (programacion) {
            for (let paquete of programacion.paquetes) {
                const aeropuertoOrigen = aeropuertos.current.get(
                    paquete.codigoEnvio.slice(0, 4)
                );
                if (aeropuertoOrigen) {
                    let packageExists = aeropuertoOrigen.aeropuerto.paquetes.some(p => p.id === paquete.id);

                    if (packageExists) {
                        aeropuertoOrigen.aeropuerto.cantidadActual--;
                        aeropuertoOrigen.aeropuerto.paquetes = aeropuertoOrigen.aeropuerto.paquetes.filter(p => p.id !== paquete.id);
                        cuenta++;
                    }
                    else{
                        //console.log("No se encontró el paquete en el aeropuerto");
                        if(aeropuertoOrigen.aeropuerto.cantidadActual > aeropuertoOrigen.aeropuerto.capacidadMaxima * 74 / 100){
                            aeropuertoOrigen.aeropuerto.cantidadActual-=4;
                            aeropuertoOrigen.aeropuerto.paquetes.splice(0, 4);
                        }
                    }
                }
            }
        }
    }

    for (let key in aeropuertos.current) {
        decidirEstiloAeropuerto(aeropuertos.current.get(key));
    }

    console.log("Se eliminaron ", cuenta, " paquetes de aeropuertos");
}

//agregarPaquetesAlmacen(idVuelo, programacionVuelos, aeropuertos, simulationTime);
export async function agregarPaquetesAlmacen(
    idVuelo: number,
    programacionVuelos: React.MutableRefObject<Map<string, ProgramacionVuelo>>,
    aeropuertos: React.MutableRefObject<Map<string, {aeropuerto: Aeropuerto; pointFeature: any}>>,
    simulationTime: Date | null,
    envios: React.MutableRefObject<Map<string, Envio>>,
    vuelos: React.RefObject<Map<number,{vuelo: Vuelo;pointFeature: any;lineFeature: any;routeFeature: any;}>>
) {
    let verbose = false;
    if (!simulationTime) return;
    // console.log("Simulation time: ", simulationTime);
    const diaDeSimulacion = simulationTime.toISOString().slice(0, 10);
    const claveProgramacion = idVuelo + "-" + diaDeSimulacion;
    const programacion: ProgramacionVuelo | undefined = programacionVuelos.current.get(claveProgramacion);
    let cuenta = 0;
    if(idVuelo == 624){
        verbose = true;
        // console.log("Programación: ", programacion);
        // console.log("claveProgramacion: ", claveProgramacion);
        // console.log("diaDeSimulacion: ", diaDeSimulacion);
    }
    //Si es que existe la programación de vuelo para ese día
    if (programacion) {
        return new Promise<boolean>((resolve, reject) => {
            try {
                for (let paquete of programacion.paquetes) {
                    const envio = envios.current.get(paquete.codigoEnvio);
                    if (!envio) {
                        console.log("No se encontró el envío para sacar el destino del paquete");
                        continue;
                    }
                    if (verbose) {
                        console.log("Envío: ", envio);
                    }
                    const ciudadDestino = vuelos.current?.get(idVuelo)!.vuelo.destino;
                    if (ciudadDestino === undefined) {
                        console.log("No se encontró la ciudad destino");
                        continue;
                    }
                    const aeropuertoDestino = aeropuertos.current.get(ciudadDestino);
                    if (verbose) {
                        // console.log("Aeropuerto destino: ", aeropuertoDestino);
                    }
                    if (verbose) {
                        // console.log("Paquete: ", paquete);
                    }
                    if (aeropuertoDestino) {
                        aeropuertoDestino.aeropuerto.cantidadActual++;
                        aeropuertoDestino.aeropuerto.paquetes.push(paquete);
                        if (verbose) {
                            // console.log("Paquete: ", paquete);
                            // console.log("Aeropuerto destino: ", aeropuertoDestino);
                        }
                        cuenta++;
                    }
                }
                resolve(true);
            } catch (error) {
                reject(error);
            }
        });
    }
    else {
        return new Promise<boolean>((resolve, reject) => {
            resolve(false);
        });
    }
    // console.log("Se agregaron ", cuenta, " paquetes a aeropuertos");
}

export function decidirEstiloAeropuerto(item: {aeropuerto: Aeropuerto; pointFeature: any} | undefined) {
    // console.log("Decidiendo estilo de aeropuerto", item);
    if (!item) return;
    if (item.pointFeature === null) return;
    let razon = item.aeropuerto.cantidadActual / item.aeropuerto.capacidadMaxima;
    // console.log("Razón de ocupación: ", razon);
    // console.log("Aeropuerto: ", item.aeropuerto);
    //Verde, menos del 33% de la capacidad
    if (razon < 0.33) {
        item.pointFeature.setStyle(greenAirportStyle);
    }
    //Amarillo, entre 33% y 66% de la capacidad
    else if (razon < 0.66) {
        item.pointFeature.setStyle(yellowAirportStyle);
    }
    //Rojo, más del 66% de la capacidad
    else {
        item.pointFeature.setStyle(redAirportStyle);
    }
}

export function contarVuelos(
    vuelos: React.RefObject<Map<number,{vuelo: Vuelo;pointFeature: any;lineFeature: any;routeFeature: any;}>>,
    programacionVuelos: React.MutableRefObject<Map<string, ProgramacionVuelo>>,
    simulationTime: Date | null
): number {
    let cuenta = 0;
    const fechaVueloFormatted = simulationTime?.toISOString().slice(0, 10);
    //De los vuelos en el  de vuelos, contar los que tienen una entrada en programacionVuelos para la fecha de simulación
    vuelos.current?.forEach((vuelo) => {
        const claveProgramacion = vuelo.vuelo.id + "-" + fechaVueloFormatted;
        if (programacionVuelos.current.has(claveProgramacion)) {
            cuenta++;
        }
    });
    return cuenta;
}
