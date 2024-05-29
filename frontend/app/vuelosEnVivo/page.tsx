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
        const fetchActiveFlights = () => {
            axios.get(`${apiURL}/vuelo/enAire`)
                .then((response) => {
                    setVuelos(response.data);
                    console.log("Vuelos cargados: ", response.data);
                })
                .catch((error) => {
                    console.error(error);
                });
        };
    
        // Fetch active flights immediately
        fetchActiveFlights();
    
        // Then fetch active flights every minute
        const intervalId = setInterval(fetchActiveFlights, 60 * 1000);
    
        // Clean up the interval on component unmount
        return () => clearInterval(intervalId);
    }, []);
    
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
        if (vuelos && vuelos.length > 0 && aeropuertos.size > 0) {
            setCargado(true);
            // console.log("Aeropuertos cargados: ", aeropuertos);
        }
    }, [vuelos, aeropuertos]);

    return (
        <>
            {cargado && (
                <div className="pb-4">
                    <Mapa vuelos={vuelos.slice(0,50)} aeropuertos={aeropuertos} simulationInterval={10}/>
                    <div ref={bottomRef}></div>
                </div>
            )}
        </>
    );
};



export default Page;
