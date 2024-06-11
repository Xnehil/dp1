import { dinamicPlaneStyle } from "@/components/mapa/EstilosMapa";
import { Aeropuerto } from "@/types/Aeropuerto";
import { Envio } from "@/types/Envio";
import { Paquete } from "@/types/Paquete";
import { ProgramacionVuelo } from "@/types/ProgramacionVuelo";
import { Vuelo } from "@/types/Vuelo";

export function procesarData(
    messageData: any,
    programacionVuelos: React.MutableRefObject<Map<string, ProgramacionVuelo>>,
    envios: React.MutableRefObject<Map<string, Envio>>,
    aeropuertos: React.MutableRefObject<Map<string, Aeropuerto>>,
    simulationTime: Date | null
): void {
    console.log("Procesando data");
    for (let key in messageData) {
        // console.log("Key: ", key);
        if (messageData.hasOwnProperty(key)) {
            let envio = messageData[key] as Envio;
            // console.log("Envio: ", envio);
            envios.current.set(envio.codigoEnvio, envio);
            for (let paquete of envio.paquetes) {
                if (!paquete.llegoDestino) continue;
                //Añadir paquete a aeropuerto de origen
                const aeropuertoOrigen: Aeropuerto | undefined =
                    aeropuertos.current.get(envio.origen);
                if (aeropuertoOrigen) {
                    aeropuertoOrigen.cantidadActual++;
                    aeropuertoOrigen.paquetes.push(paquete);
                }

                // console.log("paquete: ", paquete);
                for (let i = 0; i < paquete.fechasRuta.length; i++) {
                    const idVuelo = paquete.ruta[i];
                    const auxFechaRuta = paquete.fechasRuta[i];

                    //Para rutas salvadas tenemos que calcular la fecha de vuelo 
                    let fechaVuelo = new Date();
                    if(auxFechaRuta < 10) {
                        console.log("Se detectó una ruta guardada");
                        console.log("auxFechaRuta: ", auxFechaRuta);
                        // auxFechaRuta es la cantidad de días desde la fecha de simulación

                        fechaVuelo = new Date(simulationTime!.getTime() + auxFechaRuta * 24 * 60 * 60 * 1000);
                        console.log("Fecha vuelo: ", fechaVuelo);
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
                    if (!programacionVuelos.current.has(claveProgramacion)) {
                        programacionVuelos.current.set(claveProgramacion, {
                            fechaSalida: fechaVuelo,
                            idVuelo: idVuelo,
                            cantPaquetes: 1,
                            paquetes: [paquete],
                        });
                    } else {
                        const programacion =
                            programacionVuelos.current.get(claveProgramacion);
                        if (programacion) {
                            programacion.cantPaquetes++;
                            programacion.paquetes.push(paquete);
                        }
                    }
                }
            }
        }
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
    aeropuertos: React.MutableRefObject<Map<string, Aeropuerto>>,
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
                    aeropuertoOrigen.cantidadActual--;
                    aeropuertoOrigen.paquetes =
                        aeropuertoOrigen.paquetes.filter(
                            (p) => p.id !== paquete.id
                        );
                    cuenta++;
                }
            }
        }
    }
    console.log("Se eliminaron ", cuenta, " paquetes de aeropuertos");
}

//agregarPaquetesAlmacen(idVuelo, programacionVuelos, aeropuertos, simulationTime);
export function agregarPaquetesAlmacen(
    idVuelo: number,
    programacionVuelos: React.MutableRefObject<Map<string, ProgramacionVuelo>>,
    aeropuertos: React.MutableRefObject<Map<string, Aeropuerto>>,
    simulationTime: Date | null,
    envios: React.MutableRefObject<Map<string, Envio>>
) {
    if (!simulationTime) return;
    // console.log("Simulation time: ", simulationTime);
    const diaDeSimulacion = simulationTime.toISOString().slice(0, 10);
    const claveProgramacion = idVuelo + "-" + diaDeSimulacion;
    const programacion = programacionVuelos.current.get(claveProgramacion);
    let cuenta = 0;
    //Si es que existe la programación de vuelo para ese día
    if (programacion) {
        for (let paquete of programacion.paquetes) {
            const envio = envios.current.get(paquete.codigoEnvio);
            if (!envio) {
                console.log("No se encontró el envío para sacar el destino del paquete");
                continue;
            }
            const aeropuertoDestino = aeropuertos.current.get(envio.destino);
            if (aeropuertoDestino) {
                aeropuertoDestino.cantidadActual++;
                aeropuertoDestino.paquetes.push(paquete);
                cuenta++;
            }
        }
    }
    // console.log("Se agregaron ", cuenta, " paquetes a aeropuertos");
}
