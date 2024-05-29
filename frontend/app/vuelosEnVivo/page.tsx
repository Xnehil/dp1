"use client";

import React, { useEffect, useRef, useState } from "react";
import "ol/ol.css";
import Mapa from "@/components/mapa/Mapa";
import axios from "axios";
import { Vuelo } from "@/types/Vuelo";
import { Aeropuerto } from "@/types/Aeropuerto";
import { conectarAWebsocket, enviarMensaje } from "@/utils/FuncionesWebsocket";
import useWebSocket, { ReadyState } from "react-use-websocket";

type MessageData = {
    data: Array<any>;
    metadata: string;
};

const Page = () => {
    const bottomRef = useRef<HTMLDivElement>(null);
    const apiURL = process.env.REACT_APP_API_URL_BASE;
    const [vuelos, setVuelos] = useState<Map<number, { vuelo: Vuelo, pointFeature: any, lineFeature: any, routeFeature: any }>>(new Map());
    const [aeropuertos, setAeropuertos] = useState<Map<string, Aeropuerto>>(new Map());
    const [cargado, setCargado] = useState(false);
    const [horaInicio, setHoraInicio] = useState(new Date());
    const {sendMessage, lastMessage, readyState, getWebSocket} = useWebSocket(process.env.REACT_APP_WS_URL_BASE+"/socket",
    {
        onOpen: () => {
            let auxHoraInicio :Date = new Date();
            if (typeof window !== 'undefined') {
                const params = new URLSearchParams(window.location.search);
                auxHoraInicio = new Date(params.get('startDate') || new Date());
            }
            console.log("Conexión abierta con tiempo: ", auxHoraInicio);
            sendMessage("tiempo: " + auxHoraInicio.toISOString(), true);
        },
        share: true,
    });
    const [nuevosVuelos, setNuevosVuelos] = useState<number[]>([]);
    const [semaforo, setSemaforo] = useState(0);

    useEffect(() => {
        bottomRef.current?.scrollIntoView({ behavior: "smooth" });
    }, [cargado]);

    useEffect(() => {
        axios.get(`${apiURL}/aeropuerto`)
            .then((response) => {
                const aeropuertos = new Map<string, Aeropuerto>();
                response.data.forEach((aeropuerto: Aeropuerto) => {
                    aeropuertos.set(aeropuerto.codigoOACI, aeropuerto);
                });
                setAeropuertos(aeropuertos);
            })
            .catch((error) => {
                console.error(error);
            });
    }, []);

    useEffect(() => {
        if (vuelos && vuelos.size > 0 && aeropuertos.size > 0 && readyState === ReadyState.OPEN) {
            if(cargado) {
                return;
            }
            setCargado(true);
            // console.log("Aeropuertos cargados: ", aeropuertos);
            setHoraInicio(new Date());
        }
    }, [vuelos, aeropuertos, readyState]);

    useEffect(() => {
        if (lastMessage) {
            // console.log("Mensaje recibido: ", lastMessage);
            //Parsear el mensaje recibido
            let message = JSON.parse(lastMessage.data) as MessageData;
            console.log("Mensaje recibido: ", message);
            const auxNuevosVuelos: number[] = [];
            if (message.metadata.includes("dataVuelos")) {
                console.log("Actualizando vuelos");
                console.log("Vuelos recibidos: ", message.data);
                console.log("Vuelos actuales tamaño: ", vuelos.size);
                if(cargado) {
                    const newVuelos = new Map(vuelos);
                    message.data.forEach((vuelo: Vuelo) => {
                        newVuelos.set(vuelo.id, { vuelo: vuelo, pointFeature: null, lineFeature: null , routeFeature: null});
                        auxNuevosVuelos.push(vuelo.id);
                    });
                    setVuelos(newVuelos);
                    console.log("Vuelos luego tamaño: ", vuelos.size);
                    setNuevosVuelos(auxNuevosVuelos);
                    setSemaforo(semaforo + 1);
                    console.log("Vuelos actualizados: ", vuelos);
                }
                else{
                    const newVuelos = new Map(vuelos);
                    message.data.forEach((vuelo: Vuelo) => {
                        newVuelos.set(vuelo.id, { vuelo: vuelo, pointFeature: null, lineFeature: null , routeFeature: null});
                    });
                    setVuelos(newVuelos);
                    console.log("Vuelos cargados: ", newVuelos);
                }
            }
        }
    }, [lastMessage]);
    return (
        <>
            {cargado && (
                <div className="pb-4">
                    <Mapa vuelos={vuelos} aeropuertos={aeropuertos} simulationInterval={1/60} horaInicio={horaInicio}
                        nuevosVuelos={nuevosVuelos} semaforo={semaforo} setSemaforo={setSemaforo} sendMessage={sendMessage} />
                    <div ref={bottomRef}></div>
                </div>
            )}
        </>
    );
};



export default Page;
