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
    const [vuelos, setVuelos] = useState<Map<number, { vuelo: Vuelo, pointFeature: any, lineFeature: any}>>(new Map());
    const [aeropuertos, setAeropuertos] = useState<Map<string, Aeropuerto>>(new Map());
    const [cargado, setCargado] = useState(false);
    const [horaInicio, setHoraInicio] = useState(new Date());
    const [websocket, setWebsocket] = useState<WebSocket | null>(null);
    const [nuevosVuelos, setNuevosVuelos] = useState<number[]>([]);

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
                    const recienVuelos= new Map<number, { vuelo: Vuelo, pointFeature: any, lineFeature: any}>();
                    response.data.forEach((vuelo: Vuelo) => {
                        recienVuelos.set(vuelo.id, { vuelo: vuelo, pointFeature: null, lineFeature: null });
                    });
                    console.log("Vuelos cargados: ", response.data);
                    setVuelos(recienVuelos);
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
                const auxNuevosVuelos: number[] = [];
                if (message.metadata.includes("dataVuelos")) {
                    console.log("Actualizando vuelos");
                    console.log("Vuelos recibidos: ", message.data);
                    message.data.forEach((vuelo: Vuelo) => {
                        vuelos.set(vuelo.id, { vuelo: vuelo, pointFeature: null, lineFeature: null });
                        auxNuevosVuelos.push(vuelo.id);
                    });
                    setNuevosVuelos(auxNuevosVuelos);
                }
            };
        }
            
    }, [websocket]);

    useEffect(() => {
        if (vuelos && vuelos.size > 0 && aeropuertos.size > 0) {
            console.log("Vuelos cargados: ", vuelos);
            if(cargado) {
                return;
            }
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
    return (
        <>
            {cargado && (
                <div className="pb-4">
                    <Mapa vuelos={vuelos} aeropuertos={aeropuertos} simulationInterval={4} setVuelos={setVuelos} horaInicio={horaInicio}
                        websocket={websocket} nuevosVuelos={nuevosVuelos} />
                    <div ref={bottomRef}></div>
                </div>
            )}
        </>
    );
};



export default Page;
