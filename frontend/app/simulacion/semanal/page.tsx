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
    const [horaInicio, setHoraInicio] = useState(new Date());
    const [websocket, setWebsocket] = useState<WebSocket | null>(null);

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
        setWebsocket(conectarAWebsocket());
    }, []);

    useEffect(() => {
        if (websocket) {
            websocket.onmessage = (event: MessageEvent) => {
                //Parsear el mensaje recibido
                let message = JSON.parse(event.data);
                console.log("Mensaje recibido: ", message);
                if (message.metadata.includes("dataVuelos")) {
                    console.log("Actualizando vuelos");
                    console.log("Vuelos recibidos: ", message.data);
                    setVuelos(vuelos.concat(message.data));
                }
            };
        }
            
    }, [websocket]);

    useEffect(() => {
        if (vuelos && vuelos.length > 0 && aeropuertos.size > 0) {
            setCargado(true);
            // console.log("Aeropuertos cargados: ", aeropuertos);
            if (typeof window !== 'undefined') {
                const params = new URLSearchParams(window.location.search);
                const startDate = params.get('startDate');
                if (startDate !== null) {
                    setHoraInicio(new Date(startDate));
                }
                else {
                    setHoraInicio(new Date());
                }
                // Use startDate here
                //Limpiar la URL del query string
                window.history.replaceState({}, document.title, window.location.pathname);
            }
        }
    }, [vuelos, aeropuertos]);

    function  fetchActiveFlights () {
        axios.get(`${apiURL}/vuelo/enAire`)
            .then((response) => {
                setVuelos(response.data);
                console.log("Vuelos cargados: ", response.data);
            })
            .catch((error) => {
                console.error(error);
            });
    };

    return (
        <>
            {cargado && (
                <div className="pb-4">
                    <Mapa vuelos={vuelos} aeropuertos={aeropuertos} simulationInterval={4} setVuelos={setVuelos} horaInicio={horaInicio}
                        websocket={websocket}/>
                    <div ref={bottomRef}></div>
                </div>
            )}
        </>
    );
};



export default Page;
