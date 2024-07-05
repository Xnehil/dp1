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
                        // console.log("El vuelo ya ha salido, no se puede agregar el paquete");
                        // console.log("horaSalidaVuelo: ", horaSalidaVuelo);
                        // console.log("vue: ", vuelos.current?.get(idVuelo)?.vuelo);
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

export function procesarDataReal(
    messageData: any,
    programacionVuelos: React.MutableRefObject<Map<string, ProgramacionVuelo>>,
    envios: React.MutableRefObject<Map<string, Envio>>,
    aeropuertos: React.MutableRefObject<Map<string, {aeropuerto: Aeropuerto; pointFeature: any}>>,
    simulationTime: Date | null,
    cargaInicial: boolean,
    auxiliarVuelos: React.RefObject<Map<number, Vuelo>>
): void {
    console.log("Procesando data real");
    if (!simulationTime) return;
    for (let key in messageData) {
        // console.log("Key: ", key);
        if (messageData.hasOwnProperty(key)) {
            let envio = messageData[key] as Envio;
            // console.log("Envio: ", envio);
            envios.current.set(envio.codigoEnvio, envio);
            for (let paquete of envio.paquetes) {
                //Añadir paquete a aeropuerto de origen
                const aeropuertoOrigen: Aeropuerto | undefined =aeropuertos.current.get(envio.origen)?.aeropuerto;
                let dondeEsta:string="";
                // console.log("paquete: ", paquete);
                for (let i = 0; i < paquete.fechasRuta?.length ?? 0; i++) {
                    const idVuelo = paquete.ruta[i];
                    const auxFechaRuta = paquete.fechasRuta[i];


                    let fechaVuelo = new Date();
                    if(auxFechaRuta < 10) {
                        fechaVuelo = new Date(envio!.fechaHoraSalida*1000 + auxFechaRuta * 24 * 60 * 60 * 1000);
                        //Añadir offset de la hora de salida del vuelo
                        fechaVuelo = new Date(fechaVuelo.getTime() + 
                        (aeropuertos.current.get(auxiliarVuelos.current?.get(idVuelo)?.origen ?? "SPIM")?.aeropuerto.gmt?? 0) 
                        * 60 * 60 * 1000); 
                    }
                    else{
                        fechaVuelo = new Date(paquete.fechasRuta[i] * 1000);
                    }
                    console.log("Fecha hora salida vuelo: ", auxiliarVuelos.current?.get(idVuelo)?.fechaHoraSalida);
                    let horaSalidaVuelo =new Date(auxiliarVuelos.current?.get(idVuelo)?.fechaHoraSalida ?? 0);
                    console.log("Hora salida vuelo: ", horaSalidaVuelo);
                    fechaVuelo.setHours(horaSalidaVuelo.getHours(), horaSalidaVuelo.getMinutes(), horaSalidaVuelo.getSeconds());
                    if (fechaVuelo < new Date(envio!.fechaHoraSalida*1000) ) {
                        fechaVuelo.setDate(fechaVuelo.getDate() + 1);
                    }
                    
                    console.log("Fecha vuelo: ", fechaVuelo);
                    const fechaVueloFormatted = fechaVuelo
                        .toISOString()
                        .slice(0, 10);
                    const claveProgramacion =
                        idVuelo + "-" + fechaVueloFormatted;
                    let programacion:ProgramacionVuelo | undefined;
                    console.log("Se insertará el paquete", paquete.id, " en la programación: ", claveProgramacion);
                    
                    //Si fecha de salida es después que la fecha vuela, es que el vuelo aún no sale y es del día siguiente
                    
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
                dondeEsta = dondeEstaPaquete(paquete, envio, auxiliarVuelos);
                console.log("Donde está: ", dondeEsta);
                if (dondeEsta !== "" && Number.isNaN(parseInt(dondeEsta))) {
                    const aeropuerto: Aeropuerto | undefined =aeropuertos.current.get(dondeEsta)?.aeropuerto;
                    // console.log("Metiendo paquete en aeropuerto: ", aeropuerto);
                    if (aeropuerto) {
                        aeropuerto.cantidadActual++;
                        aeropuerto.paquetes.push(paquete);
                    }
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

function dondeEstaPaquete(paquete: Paquete, envio: Envio, vuelos: React.RefObject<Map<number,Vuelo>>): string {
    let dondeEsta = "";
    let fechaActual = new Date();
    let fechaLlegadaAnterior = null;
    for (let i = 0; i < paquete.fechasRuta?.length ?? 0; i++) {
        const idVuelo = paquete.ruta[i];
        const auxFechaRuta = paquete.fechasRuta[i];
        let fechaVuelo = new Date();
        if(auxFechaRuta < 10) {
            fechaVuelo = new Date(envio!.fechaHoraSalida*1000 + auxFechaRuta * 24 * 60 * 60 * 1000);
            
        }
        else{
            fechaVuelo = new Date(paquete.fechasRuta[i] * 1000);
        }
        let horaSalidaVuelo =new Date(vuelos.current?.get(idVuelo)?.fechaHoraSalida ?? 0);
        fechaVuelo.setHours(horaSalidaVuelo.getHours(), horaSalidaVuelo.getMinutes(), horaSalidaVuelo.getSeconds());
        if (fechaVuelo < new Date(envio!.fechaHoraSalida*1000) ) {
            fechaVuelo.setDate(fechaVuelo.getDate() + 1);
        }

        if (fechaActual < fechaVuelo) { // Aún no ha tomado este vuelo
            console.log("Aún no ha tomado este vuelo, idVuelo: ", idVuelo);
            console.log("Se queda en: ", vuelos.current?.get(idVuelo)?.origen);
            dondeEsta = vuelos.current?.get(idVuelo)?.origen ?? "SKBO";
            break;
        }
        let horaLlegadaVuelo =new Date(vuelos.current?.get(idVuelo)?.fechaHoraLlegada ?? 0);
        fechaVuelo.setHours(horaLlegadaVuelo.getHours(), horaLlegadaVuelo.getMinutes(), horaLlegadaVuelo.getSeconds());
        fechaVuelo.setDate(fechaVuelo.getDate() + (vuelos.current?.get(idVuelo)?.cambioDeDia ?? 0));
        if (fechaActual < fechaVuelo) { // Está en este vuelo
            console.log("Está en este vuelo, idVuelo: ", idVuelo);
            dondeEsta = vuelos.current?.get(idVuelo)?.id.toString() ?? "";
            break;
        }
    }
    if (dondeEsta === "") {
        dondeEsta = envio.origen;
    }
    return dondeEsta;
}

export function actualizarDataReal(
    messageData: any,
    programacionVuelos: React.MutableRefObject<Map<string, ProgramacionVuelo>>,
    envios: React.MutableRefObject<Map<string, Envio>>,
    aeropuertos: React.MutableRefObject<Map<string, {aeropuerto: Aeropuerto; pointFeature: any}>>,
    simulationTime: Date | null,
    cargaInicial: boolean,
    vuelos: React.RefObject<Map<number,{vuelo: Vuelo;pointFeature: any;lineFeature: any;routeFeature: any;}>>,
): void {
    console.log("Actualizando data real");
    if (!simulationTime) return;
    for (let key in messageData) {
        // console.log("Key: ", key);
        if (messageData.hasOwnProperty(key)) {
            let envio = messageData[key] as Envio;
            // console.log("Envio: ", envio);
            let envioAntiguo = envios.current.get(envio.codigoEnvio);
            let index=0;
            for (let paquete of envio.paquetes) {
                if ( paquete.ruta==null || !envioAntiguo){
                     index++;
                     continue;
                }
                //Si el antiguo paquete no tiene ruta, no se elimina, solo se agrega
                let tieneRutaAntigua = envioAntiguo.paquetes[index].ruta!=null;

                const areRutasEqual = tieneRutaAntigua && (paquete.ruta.length === envioAntiguo?.paquetes[index].ruta.length) && paquete.ruta.every((rutaElement, rutaIndex) => rutaElement === envioAntiguo.paquetes[index].ruta[rutaIndex]);
                if(areRutasEqual){
                    index++;
                    continue;
                }


                console.log("Actualizando paquete porque la ruta" + paquete.ruta + " es diferente a la anterior " + envioAntiguo.paquetes[index].ruta);
                let paqueteAntiguo = envioAntiguo.paquetes[index];
                //Borrar ruta de programación de vuelo
                for(let i=0; i<paqueteAntiguo.fechasRuta.length; i++){
                    console.log("Borrando paquete de ruta");
                    const idVuelo = paqueteAntiguo.ruta[i];
                    const auxFechaRuta = paqueteAntiguo.fechasRuta[i];
                    let fechaVuelo = new Date();
                    if(auxFechaRuta < 10) {
                        fechaVuelo = new Date(simulationTime!.getTime() + auxFechaRuta * 24 * 60 * 60 * 1000);
                    }
                    else{
                        fechaVuelo = new Date(paqueteAntiguo.fechasRuta[i] * 1000);
                    }
                    const fechaVueloFormatted = fechaVuelo
                        .toISOString()
                        .slice(0, 10);
                    const claveProgramacion =
                        idVuelo + "-" + fechaVueloFormatted;
                    let programacion:ProgramacionVuelo | undefined;
                    if (programacionVuelos.current.has(claveProgramacion)) {
                        programacion = programacionVuelos.current.get(claveProgramacion);
                        if (programacion) {
                            programacion.cantPaquetes--;
                            programacion.paquetes = programacion.paquetes.filter(p => p.id !== paqueteAntiguo.id);
                            if(programacion.cantPaquetes===0){
                                programacionVuelos.current.delete(claveProgramacion);
                            }
                        }
                    }
                }
                //Reemplazar la ruta del paquete
                paqueteAntiguo.ruta=paquete.ruta;

                // Agregar la nueva ruta
                for (let i = 0; i < paquete.fechasRuta?.length ?? 0; i++) {
                    console.log("Añadiendo paquete a ruta");
                    const idVuelo = paquete.ruta[i];
                    const auxFechaRuta = paquete.fechasRuta[i];
                    let fechaVuelo = new Date();
                    if(auxFechaRuta < 10) {
                        fechaVuelo = new Date(simulationTime!.getTime() + auxFechaRuta * 24 * 60 * 60 * 1000);
                    }
                    else{
                        fechaVuelo = new Date(paquete.fechasRuta[i] * 1000);
                    }
                    // console.log("Fecha vuelo: ", fechaVuelo);
                    const fechaVueloFormatted = fechaVuelo
                        .toISOString()
                        .slice(0, 10);
                    const claveProgramacion = idVuelo + "-" + fechaVueloFormatted;
                    let programacion:ProgramacionVuelo | undefined;
                    let horaSalidaVuelo =new Date(vuelos.current?.get(idVuelo)?.vuelo.fechaHoraSalida ?? 0);
                    fechaVuelo.setHours(horaSalidaVuelo.getHours(), horaSalidaVuelo.getMinutes(), horaSalidaVuelo.getSeconds());
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
            }
        }
    }
}

export function limpiarMapasDeDatos(
    programacionVuelos: React.MutableRefObject<Map<string, ProgramacionVuelo>>,
    envios: React.MutableRefObject<Map<string, Envio>>,
    horaSimuladaActual: Date
) {
    const dayBefore = new Date(
        horaSimuladaActual.getTime() - 24 * 60 * 60 * 1000 * 2
    ); // 48 horas antes
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
                        // if(aeropuertoOrigen.aeropuerto.cantidadActual > aeropuertoOrigen.aeropuerto.capacidadMaxima * 74 / 100){
                        //     aeropuertoOrigen.aeropuerto.cantidadActual-=4;
                        //     aeropuertoOrigen.aeropuerto.paquetes.splice(0, 4);
                        // }
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
export function agregarPaquetesAlmacen(
    idVuelo: number,
    programacionVuelos: React.MutableRefObject<Map<string, ProgramacionVuelo>>,
    aeropuertos: React.MutableRefObject<Map<string, {aeropuerto: Aeropuerto; pointFeature: any}>>,
    simulationTime: Date | null,
    envios: React.MutableRefObject<Map<string, Envio>>,
    vuelos: React.RefObject<Map<number,{vuelo: Vuelo;pointFeature: any;lineFeature: any;routeFeature: any;}>>,
    setColapso: React.Dispatch<React.SetStateAction<boolean>>
): boolean {
    if (!simulationTime) return false;
    const diaDeSimulacion = simulationTime.toISOString().slice(0, 10);
    const claveProgramacion = idVuelo + "-" + diaDeSimulacion;
    const programacion: ProgramacionVuelo | undefined = programacionVuelos.current.get(claveProgramacion);
    let cuenta = 0;
    if (programacion) {
        for (let paquete of programacion.paquetes) {
            const envio = envios.current.get(paquete.codigoEnvio);
            if (!envio) {
                console.log("No se encontró el envío para sacar el destino del paquete");
                continue;
            }
            const ciudadDestino = vuelos.current?.get(idVuelo)?.vuelo.destino;
            if (ciudadDestino === undefined) {
                console.log("No se encontró la ciudad destino");
                continue;
            }
            const aeropuertoDestino = aeropuertos.current.get(ciudadDestino);
            if (aeropuertoDestino && aeropuertoDestino.aeropuerto.codigoOACI != envio.destino) {
                aeropuertoDestino.aeropuerto.cantidadActual++;
                aeropuertoDestino.aeropuerto.paquetes.push(paquete);
                cuenta++;
                if (aeropuertoDestino.aeropuerto.cantidadActual > aeropuertoDestino.aeropuerto.capacidadMaxima) {
                    setColapso(true);
                    return false;
                }
            }
        }
        return true;
    } else {
        return false;
    }
    // console.log("Se agregaron ", cuenta, " paquetes a aeropuertos");
}

export function agregarPaquetesAlmacenReal(
    idVuelo: number,
    programacionVuelos: React.MutableRefObject<Map<string, ProgramacionVuelo>>,
    aeropuertos: React.MutableRefObject<Map<string, {aeropuerto: Aeropuerto; pointFeature: any}>>,
    simulationTime: Date | null,
    envios: React.MutableRefObject<Map<string, Envio>>,
    vuelos: React.RefObject<Map<number,{vuelo: Vuelo;pointFeature: any;lineFeature: any;routeFeature: any;}>>,
    setColapso: React.Dispatch<React.SetStateAction<boolean>>
): boolean {
    if (!simulationTime) return false;
    const diaDeSimulacion = simulationTime.toISOString().slice(0, 10);
    const claveProgramacion = idVuelo + "-" + diaDeSimulacion;
    const programacion: ProgramacionVuelo | undefined = programacionVuelos.current.get(claveProgramacion);
    let cuenta = 0;
    if (programacion) {
        for (let paquete of programacion.paquetes) {
            const envio = envios.current.get(paquete.codigoEnvio);
            if (!envio) {
                console.log("No se encontró el envío para sacar el destino del paquete");
                continue;
            }
            const ciudadDestino = vuelos.current?.get(idVuelo)?.vuelo.destino;
            if (ciudadDestino === undefined) {
                console.log("No se encontró la ciudad destino");
                continue;
            }
            const aeropuertoDestino = aeropuertos.current.get(ciudadDestino);
            if (aeropuertoDestino) {
                aeropuertoDestino.aeropuerto.cantidadActual++;
                aeropuertoDestino.aeropuerto.paquetes.push(paquete);
                cuenta++;
                if (aeropuertoDestino.aeropuerto.cantidadActual > aeropuertoDestino.aeropuerto.capacidadMaxima) {
                    setColapso(true);
                    return false;
                }

                if(aeropuertoDestino.aeropuerto.codigoOACI == envio.destino){
                    //Crear un timer que borre el paquete dentro de 5 minutos
                    setTimeout(() => {
                        const index = aeropuertoDestino.aeropuerto.paquetes.findIndex(p => p.id === paquete.id);
                        if (index !== -1) {
                            aeropuertoDestino.aeropuerto.paquetes.splice(index, 1);
                        }
                    }, 300000);
                }
            }
        }
        return true;
    } else {
        return false;
    }
}

export function decidirEstiloAeropuerto(item: {aeropuerto: Aeropuerto; pointFeature: any} | undefined) {
    // console.log("Decidiendo estilo de aeropuerto", item);
    if (!item) return;
    if (item.pointFeature === null || item.pointFeature.get('seleccionado')) return;
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
    vuelos: React.RefObject<Map<number,{vuelo: Vuelo;pointFeature: any;lineFeature: any;routeFeature: any;}>>
): {cuenta:number; porcentaje:number} {
    let cuenta = 0;
    let capacidadTotal = 0;
    let capacidadUsada = 0;
    //De los vuelos en el  de vuelos, contar los que tienen una entrada en programacionVuelos para la fecha de simulación
    vuelos.current?.forEach((vuelo) => {
        const feature = vuelo.pointFeature;
        if(feature?.get("pintarAuxiliar")){
            cuenta++;
            capacidadTotal += vuelo.vuelo.capacidad;
            capacidadUsada += feature.get("cantPaquetes") ?? 0;
        }
    });
    return {cuenta, porcentaje: capacidadUsada/capacidadTotal};
}

export function capacidadAlmacenesUsada(aeropuertos: React.MutableRefObject<Map<string, {aeropuerto: Aeropuerto; pointFeature: any}>>): number {
    let capacidadUsada = 0;
    let capacidadTotal = 0;
    for (let key of aeropuertos.current.keys()) {
        // console.log("Key: ", key);
        capacidadUsada += aeropuertos.current.get(key)?.aeropuerto.cantidadActual ?? 0;
        capacidadTotal += aeropuertos.current.get(key)?.aeropuerto.capacidadMaxima ?? 0;
    }
    return capacidadUsada/capacidadTotal;
}
