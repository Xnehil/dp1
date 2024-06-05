import { Envio } from "@/types/Envio";
import { Paquete } from "@/types/Paquete";
import { ProgramacionVuelo } from "@/types/ProgramacionVuelo";

export function procesarData(messageData:any, programacionVuelos:React.MutableRefObject<Map<string, ProgramacionVuelo>>,
    envios:React.MutableRefObject<Map<string, Envio>>
):void {
    for (let key in messageData) {
        if (messageData.hasOwnProperty(key)) {
            let envio = messageData[key] as Envio;
            envios.current.set(envio.codigoEnvio, envio);
            for (let paquete of envio.paquetes) {
                if(!paquete.llegoDestino) continue;
                // console.log("paquete: ", paquete);
                for (let i = 0; i<paquete.ruta.length; i++) {
                    const idVuelo = paquete.ruta[i];
                    //Java lo envía como UNIX timestamp en segundos, pero JS lo necesita en milisegundos
                    const fechaVuelo = new Date(paquete.fechasRuta[i]*1000);
                    const fechaVueloFormatted = fechaVuelo.toISOString().slice(0,10);
                    const claveProgramacion = idVuelo + "-" + fechaVueloFormatted;
                    if (!programacionVuelos.current.has(claveProgramacion)) {
                        programacionVuelos.current.set(claveProgramacion, {
                            fechaSalida: fechaVuelo,
                            idVuelo: idVuelo,
                            cantPaquetes: 1,
                            paquetes: [paquete],
                        });
                    }
                    else {
                        const programacion = programacionVuelos.current.get(claveProgramacion);
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

export function limpiarMapasDeDatos(programacionVuelos:React.MutableRefObject<Map<string, ProgramacionVuelo>>,
    envios:React.MutableRefObject<Map<string, Envio>>,
horaSimuladaActual:Date){
    const dayBefore = new Date(horaSimuladaActual.getTime() - 24 * 60 * 60 * 1000*1); // 24 hours before the current simulated time

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
        let fechaFin = new Date(envio.fechaHoraLlegadaPrevista*1000);
        // console.log("Fecha fin: ", fechaFin);
        // console.log("Fecha antes de: ", dayBefore);
        if (fechaFin < dayBefore) {
            envios.current.delete(key);
            cuenta++;
        }
    });
    console.log("Se eliminaron ", cuenta, " envíos");
}