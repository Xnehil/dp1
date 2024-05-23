"use client";

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

import { planeStyle, airportStyle, invisibleLineStyle } from "./EstilosMapa";
import { Vuelo } from "@/types/Vuelo";
import { Aeropuerto } from "@/types/Aeropuerto";
import { coordenadasIniciales, updateCoordinates } from "@/utils/FuncionesMapa";

type MapaProps = {
    vuelos: Vuelo[];
    aeropuertos: Map<string, Aeropuerto>;
    simulationInterval: number;
    setVuelos: React.Dispatch<React.SetStateAction<Vuelo[]>>;
    horaInicio: Date;
    websocket: WebSocket;
};

const Mapa = ({
    vuelos,
    aeropuertos,
    simulationInterval,
    setVuelos,
    horaInicio = new Date(),
    websocket,
}: MapaProps) => {
    const mapRef = useRef<OLMap | null>(null);
    const [simulationTime, setSimulationTime] = useState(new Date(horaInicio));
    const [pointFeatures, setPointFeatures] = useState<any[]>([]);
    const [lineFeatures, setLineFeatures] = useState<any[]>([]);

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

        //Limpiar el mapa
        if (mapRef.current.getLayers().getLength() > 1) {
            mapRef.current.getLayers().removeAt(1);
        }

        const auxLineFeatures = vuelos.map((vuelo) => {
            const aeropuertoOrigen = aeropuertos.get(vuelo.origen);
            const aeropuertoDestino = aeropuertos.get(vuelo.destino);
            const lonlatInicio = [
                aeropuertoOrigen?.longitud ?? 0,
                aeropuertoOrigen?.latitud ?? 0,
            ];
            const lonlatFin = [
                aeropuertoDestino?.longitud ?? 0,
                aeropuertoDestino?.latitud ?? 0,
            ];

            const line = new LineString([
                fromLonLat(lonlatInicio),
                fromLonLat(lonlatFin),
            ]);
            const feature = new Feature({
                geometry: line,
            });
            feature.setStyle(invisibleLineStyle);
            return feature;
        });

        setLineFeatures(auxLineFeatures);

        const auxPointFeatures = vuelos.map((vuelo, index) => {
            const point = coordenadasIniciales(
                vuelo,
                aeropuertos,
                auxLineFeatures,
                simulationTime,
                index
            );
            const feature = new Feature({
                geometry: point,
            });
            feature.setStyle(planeStyle);
            return feature;
        });

        setPointFeatures(auxPointFeatures);

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

        const vectorSource = new VectorSource({
            features: [
                ...auxLineFeatures,
                ...auxPointFeatures,
                ...aeropuertoFeatures,
            ],
        });

        const vectorLayer = new VectorLayer({
            source: vectorSource,
        });

        mapRef.current.addLayer(vectorLayer);
    }, [vuelos, aeropuertos, mapRef]);

    useEffect(() => {
        const intervalId = setInterval(() => {
            setSimulationTime(
                (prevSimulationTime) =>new Date(prevSimulationTime.getTime() +simulationInterval * 60 * 1000)
            );
            // console.log("Simulation time: ", simulationTime);
            if(websocket.readyState === 1)
                websocket.send("tiempo: " + simulationTime.toISOString());
        }, 700);

        console.log("Updating coordinates con tiempo: ", simulationTime);

        if (pointFeatures.length > 0 && lineFeatures.length > 0) {
            const aBorrar = updateCoordinates(
                aeropuertos,
                vuelos,
                pointFeatures,
                lineFeatures,
                simulationTime
            );
        }

        // Clean up interval on unmount
        return () => clearInterval(intervalId);
    }, [simulationTime, simulationInterval]); // A

    return <div id="map" style={{ width: "100%", height: "900px" }}></div>;
};

export default Mapa;
