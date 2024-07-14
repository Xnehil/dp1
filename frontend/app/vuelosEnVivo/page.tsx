"use client";

import React, { useEffect, useRef, useState } from "react";
import "ol/ol.css";
import Mapa from "@/components/mapa/Mapa";
import axios from "axios";
import { Vuelo } from "@/types/Vuelo";
import { Aeropuerto } from "@/types/Aeropuerto";
import { conectarAWebsocket, enviarMensaje } from "@/utils/FuncionesWebsocket";
import useWebSocket, { ReadyState } from "react-use-websocket";
import { ProgramacionVuelo } from "@/types/ProgramacionVuelo";
import { actualizarDataReal, procesarData, procesarDataReal, quitarPaquetesAlmacenados } from "@/utils/FuncionesDatos";
import { Envio } from "@/types/Envio";

type MessageData = {
    data: Array<any>;
    metadata: string;
};

const Page = () => {
    const bottomRef = useRef<HTMLDivElement>(null);
    const apiURL = process.env.REACT_APP_API_URL_BASE;
    const vuelos = useRef<
        Map<
            number,
            {
                vuelo: Vuelo;
                pointFeature: any;
                lineFeature: any;
                routeFeature: any;
            }
        >
    >(new Map());
    const programacionVuelos = useRef<Map<string, ProgramacionVuelo>>(
        new Map()
    );
    const auxiliarVuelos = useRef<Map<number, Vuelo>>(new Map());
    const envios = useRef<Map<string, Envio>>(new Map());
    const aeropuertos = useRef<Map<string, {aeropuerto: Aeropuerto; pointFeature: any}>>(new Map());
    const [cargado, setCargado] = useState(false);
    const [horaInicio, setHoraInicio] = useState(new Date());
    const [campana, setCampana] = useState(0);
    const [simulationTime, setSimulationTime] = useState<Date | null>(null);
    const { sendMessage, lastMessage, readyState, getWebSocket } = useWebSocket(
        process.env.REACT_APP_WS_URL_BASE + "/socket",
        {
            share: true,
        }
    );
    const [nuevosVuelos, setNuevosVuelos] = useState<number[]>([]);
    const [semaforo, setSemaforo] = useState(0);
    const initializedRef = useRef(false);
    const [colapso, setColapso] = useState(false);


    useEffect(() => {
        bottomRef.current?.scrollIntoView({ behavior: "smooth" });
    }, [cargado]);

    async function fetchAeropuertos() {
        const response = await axios.get(`${apiURL}/aeropuerto`);
        const auxAeropuertos = new Map<string, { aeropuerto: Aeropuerto; pointFeature: any }>();
        response.data.forEach((aeropuerto: Aeropuerto) => {
            aeropuerto.paquetes = [];
            aeropuerto.cantidadActual = 0;
            auxAeropuertos.set(aeropuerto.codigoOACI, { aeropuerto: aeropuerto, pointFeature: null });
        });
        return auxAeropuertos;
    }
    
    async function fetchVuelos() {
        const response = await axios.get(`${apiURL}/vuelo`);
        const auxVuelos = new Map<number, Vuelo>();
        response.data.forEach((vuelo: Vuelo) => {
            auxVuelos.set(vuelo.id, vuelo);
        });
        return auxVuelos;
    }

    useEffect(() => {
        if (!initializedRef.current) {
            const initializeData = async () => {
                //   22 Julio 2024 a las 5:45 am
                setHoraInicio(new Date("2024-07-22T05:45:00"));
    
                try {
                    const [auxAeropuertos, vuelos] = await Promise.all([fetchAeropuertos(), fetchVuelos()]);
                    console.log("Aeropuertos cargados: ");
                    aeropuertos.current = auxAeropuertos;
                    console.log("Vuelos auxiliares cargados: ");
                    auxiliarVuelos.current = vuelos;
    
                    setCampana((prev) => prev + 2);
                } catch (error) {
                    console.error("Error cargando datos: ", error);
                }
            };
            initializeData();
            initializedRef.current = true;
        }
    }, []);

    useEffect(() => {
        console.log("Campana: ", campana);
        if(campana ==  2) {
            let auxHoraInicio: Date = new Date("2024-07-22T05:45:00");
            sendMessage("vuelosEnVivo: tiempo: " +auxHoraInicio.toLocaleString("en-US", {timeZone: "America/Lima",}),
                    true
            );
            console.log("Enviando mensaje de tiempo con campana 2");
        }
        if (campana ==3 ) {
            console.log("Campana sonando");
            if (cargado) {
                return;
            }
            setCargado(true);
            console.log("Cargando datos");
            // console.log("Aeropuertos cargados: ", aeropuertos);
            if (typeof window !== "undefined") {
                //Limpiar la URL del query string
                window.history.replaceState(
                    {},
                    document.title,
                    window.location.pathname
                );
            }
        }
    }, [campana]);

    useEffect(() => {
        if (lastMessage) {
            //console.log("Mensaje recibido: ", lastMessage);
            //Parsear el mensaje recibido
            let message = JSON.parse(lastMessage.data) as MessageData;
            // console.log("Mensaje recibido: ", message);
            const auxNuevosVuelos: number[] = [];

            if (message.metadata.includes("dataVuelos")) {
                console.log("Actualizando vuelos");
                // console.log("Vuelos recibidos: ", message.data);
                console.log("Vuelos actuales tamaño: ", vuelos.current.size);
                if (cargado) {
                    message.data.forEach((vuelo: Vuelo) => {
                        vuelo.pintarAuxiliar = false;
                        vuelos.current.set(vuelo.id, {
                            vuelo: vuelo,
                            pointFeature: null,
                            lineFeature: null,
                            routeFeature: null,
                        });
                        auxNuevosVuelos.push(vuelo.id);
                    });
                    console.log("Vuelos luego tamaño: ", vuelos.current.size);
                    quitarPaquetesAlmacenados(auxNuevosVuelos, programacionVuelos, aeropuertos, simulationTime);
                    setNuevosVuelos(auxNuevosVuelos);
                    setSemaforo(semaforo + 1);
                    // console.log("Vuelos actualizados: ", vuelos);
                } else {
                    console.log("Cargando vuelos con datos: ", message.data);
                    message.data.forEach((vuelo: Vuelo) => {
                        vuelo.pintarAuxiliar = false;
                        vuelos.current.set(vuelo.id, {
                            vuelo: vuelo,
                            pointFeature: null,
                            lineFeature: null,
                            routeFeature: null,
                        });
                        auxNuevosVuelos.push(vuelo.id);
                    });
                    setCampana((prev) => prev + 1);
                    // setNuevosVuelos(auxNuevosVuelos);
                    // setSemaforo(semaforo + 1);
                    console.log("Vuelos cargados: ", vuelos.current.size);
                }
            }
            if(message.metadata.includes("primeraCarga")) {
                console.log("Mensaje de primera carga");
                console.log("Datos recibidos: ", message.data);
                procesarDataReal(message.data, programacionVuelos, envios, aeropuertos, simulationTime?simulationTime:horaInicio, true, auxiliarVuelos, setColapso);
            }
            if (message.metadata.includes("nuevosEnvios")) {
                console.log(message.data);
                procesarDataReal(message.data, programacionVuelos, envios, aeropuertos, simulationTime, false, auxiliarVuelos, setColapso);
            }
            if (message.metadata.includes("enviosEnOperacion")) {
                actualizarDataReal(message.data, programacionVuelos, envios, aeropuertos, simulationTime?simulationTime:horaInicio, false, vuelos);
            }
        }
    }, [lastMessage]);

    
    return (
        <>
            {cargado && (
                <div className="pb-4">
                    <Mapa
                        vuelos={vuelos}
                        aeropuertos={aeropuertos}
                        programacionVuelos={programacionVuelos}
                        envios={envios}
                        simulationInterval={1/60}
                        horaInicio={horaInicio}
                        nuevosVuelos={nuevosVuelos}
                        semaforo={semaforo}
                        setSemaforo={setSemaforo}
                        sendMessage={sendMessage}
                        onSimulationTimeChange={setSimulationTime}
                        auxiliarVuelos={auxiliarVuelos}
                        colapso={colapso}
                        setColapso={setColapso}
                    />
                    <div ref={bottomRef}></div>
                </div>
            )}
        </>
    );
};

export default Page;
