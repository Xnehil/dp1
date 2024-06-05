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
                // console.log("paquete: ", paquete);
                for (let i = 0; i<paquete.ruta.length; i++) {
                    const idVuelo = paquete.ruta[i];
                    const fechaVuelo = new Date(paquete.fechasRuta[i]);
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