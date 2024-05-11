"use client";

import React, { useEffect, useRef, useState } from "react";
import "ol/ol.css";
import Mapa from "@/components/mapa/Mapa";
import axios from "axios";
import { Vuelo } from "@/types/Vuelo";
import { Aeropuerto } from "@/types/Aeropuerto";

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
                })
                .catch((error) => {
                    console.error(error);
                });
            console.log("Vuelos cargados: ", vuelos);
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
                    <Mapa vuelos={vuelos} aeropuertos={aeropuertos} />
                    <div ref={bottomRef}></div>
                </div>
            )}
        </>
    );
};

export default Page;
