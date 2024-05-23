"use client";

import React, { useEffect, useRef, useState } from "react";
import "ol/ol.css";
import Mapa from "@/components/mapa/Mapa";
import axios from "axios";
import { Vuelo } from "@/types/Vuelo";
import { Aeropuerto } from "@/types/Aeropuerto";
import { conectarAWebsocket, enviarMensaje } from "@/utils/FuncionesWebsocket";

const Page = () => {
    const bottomRef = useRef<HTMLDivElement>(null);
    const apiURL = process.env.REACT_APP_API_URL_BASE;
    const [vuelos, setVuelos] = useState<Vuelo[]>([]);
    const [aeropuertos, setAeropuertos] = useState<Map<string, Aeropuerto>>(new Map());
    const [cargado, setCargado] = useState(false);

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
        fetchActiveFlights();
    
        let websocket: WebSocket = conectarAWebsocket();
    
        // Then fetch active flights every minute
        const intervalId = setInterval(() => enviarMensaje("cocacola", "DP1 Front", websocket), 5 * 1000);
    
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
                    <Mapa vuelos={vuelos} aeropuertos={aeropuertos} simulationInterval={1/60} setVuelos={setVuelos} />
                    <div ref={bottomRef}></div>
                </div>
            )}
        </>
    );
};

export default Page;
