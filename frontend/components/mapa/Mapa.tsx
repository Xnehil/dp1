"use client";
import Leyenda from "@/components/mapa/Leyenda";
import React, { useEffect, useRef, useState } from "react";
import "ol/ol.css";
import { Map as OLMap } from "ol";
import View from "ol/View";
import TileLayer from "ol/layer/Tile";
import OSM from "ol/source/OSM";
import { Feature } from "ol";
import { LineString, Point } from "ol/geom";
import VectorSource from "ol/source/Vector";
import VectorLayer from "ol/layer/Vector";
import { Coordinate } from "ol/coordinate";
import { fromLonLat, toLonLat } from "ol/proj";

import { planeStyle, airportStyle, invisibleStyle } from "./EstilosMapa";
import { Vuelo } from "@/types/Vuelo";
import { Aeropuerto } from "@/types/Aeropuerto";
import { coordenadasIniciales, crearLineaDeVuelo, crearPuntoDeVuelo, updateCoordinates } from "@/utils/FuncionesMapa";

type MapaProps = {
    vuelos: Map<number, { vuelo: Vuelo, pointFeature: any, lineFeature: any, routeFeature: any }>;
    aeropuertos: Map<string, Aeropuerto>;
    simulationInterval: number;
    horaInicio: Date;
    nuevosVuelos: number[];
    semaforo: number;
    setSemaforo: React.Dispatch<React.SetStateAction<number>>;
    sendMessage: (message: string, keep: boolean) => void;
};

const Mapa = ({
    vuelos,
    aeropuertos,
    simulationInterval,
    horaInicio = new Date(),
    nuevosVuelos,
    semaforo,
    setSemaforo,
    sendMessage,
}: MapaProps) => {
    const mapRef = useRef<OLMap | null>(null);
    const vectorSourceRef = useRef(new VectorSource());
    const [simulationTime, setSimulationTime] = useState(new Date(horaInicio));
    const [currentTime, setCurrentTime] = useState(new Date());

    useEffect(() => {
        if (!mapRef.current) {
            const initialCoordinates = fromLonLat([-77.0428, -12.0464]);
            mapRef.current = new OLMap({
                target: "map",
                layers: [
                    new TileLayer({
                        source: new OSM(),
                    }),
                ],
                view: new View({
                    center: initialCoordinates,
                    zoom: 5,
                }),
            });
        }
    }, []);

    useEffect(() => {
        if (typeof window === "undefined" || !mapRef.current) {
            return;
        }

        let auxLineFeatures: any[] = [];
        vuelos.forEach((item) => {
            const feature = crearLineaDeVuelo(aeropuertos, item);
            item.lineFeature = feature;
            auxLineFeatures.push(feature);
        });


        let auxPointFeatures: any[] = [];
        vuelos.forEach((item) => {
            const feature = crearPuntoDeVuelo(aeropuertos, item, simulationTime);
            item.pointFeature = feature;
            auxPointFeatures.push(feature);
        });

        const aeropuertoFeatures = Array.from(aeropuertos.values()).map(
            (aeropuerto) => {
                const point = new Point(
                    fromLonLat([aeropuerto.longitud, aeropuerto.latitud])
                );
                const feature = new Feature({
                    geometry: point,
                });
                feature.setStyle(airportStyle);
                return feature;
            }
        );
        console.log("Adding # features: ", auxLineFeatures.length, auxPointFeatures.length, aeropuertoFeatures.length);
        vectorSourceRef.current.clear(); // Clear the existing features
        vectorSourceRef.current.addFeatures([
            ...auxLineFeatures,
            ...auxPointFeatures,
            ...aeropuertoFeatures,
        ]);

        const vectorLayer = new VectorLayer({
            source: vectorSourceRef.current,
        });

        mapRef.current.addLayer(vectorLayer);
    }, [mapRef]);


    useEffect(() => {
        const updateTime = () => {
            setCurrentTime(new Date());
        };

        const intervalId = setInterval(updateTime, 1000); // Actualiza cada segundo

        return () => clearInterval(intervalId); // Limpiar el intervalo cuando el componente se desmonte
    }, []);

    useEffect(() => {
        const intervalId = setInterval(() => {
            setSimulationTime(
                (prevSimulationTime) =>new Date(prevSimulationTime.getTime() +simulationInterval * 60 * 1000)
            );
            // console.log("Simulation time: ", simulationTime);
            if(sendMessage) {
                // console.log("Enviando tiempo: ", simulationTime.toISOString());
                sendMessage("tiempo: " + simulationTime.toISOString(), true);
            }
        }, 1000);

        // console.log("Updating coordinates con tiempo: ", simulationTime);

        if (vectorSourceRef.current.getFeatures().length > 0){
            const aBorrar = updateCoordinates(
                aeropuertos,
                vuelos,
                simulationTime
            );
            console.log("aBorrar: ", aBorrar);
            for (let i = 0; i < aBorrar.length; i++) {
                const idVuelo = aBorrar[i];
                const item = vuelos.get(idVuelo);
                if (item) {
                    vectorSourceRef.current.removeFeature(item.pointFeature);
                    vectorSourceRef.current.removeFeature(item.lineFeature);
                    item.pointFeature = null;
                    item.lineFeature = null;
                    item.routeFeature = null;
                    vuelos.delete(idVuelo);
                }
            }
        }

        // Clean up interval on unmount
        return () => clearInterval(intervalId);
    }, [simulationTime, simulationInterval]); 

    useEffect(() => {
        if(nuevosVuelos.length > 0 && semaforo > 0) {
            console.log("Nuevos vuelos: ", nuevosVuelos);
            for (let i = 0; i < nuevosVuelos.length; i++) {
                const idVuelo = nuevosVuelos[i];
                const item = vuelos.get(idVuelo);
                if (item) {
                    item.lineFeature = crearLineaDeVuelo(aeropuertos, item);
                    item.pointFeature = crearPuntoDeVuelo(aeropuertos, item, simulationTime);
                    vectorSourceRef.current.addFeature(item.pointFeature);
                    vectorSourceRef.current.addFeature(item.lineFeature);
                }
            }
            setSemaforo(semaforo - 1);
        }
    }), [nuevosVuelos, semaforo];

    const enviosEnElAire = 1420;

    return <div id="map" style={{ width: "100%", height: "900px" }}>  <div>
    <Leyenda
    vuelosEnTransito= {vuelos.size}
    enviosEnElAire={enviosEnElAire} 
    fechaHoraActual={currentTime.toLocaleString()} 
    fechaHoraSimulada={simulationTime.toLocaleString()}
    />
    </div>  </div>;
};

export default Mapa;
