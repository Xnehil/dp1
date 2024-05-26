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

import { planeStyle, airportStyle, invisibleStyle } from "./EstilosMapa";
import { Vuelo } from "@/types/Vuelo";
import { Aeropuerto } from "@/types/Aeropuerto";
import { coordenadasIniciales, crearLineaDeVuelo, crearPuntoDeVuelo, updateCoordinates } from "@/utils/FuncionesMapa";

type MapaProps = {
    vuelos: Map<number, { vuelo: Vuelo, pointFeature: any, lineFeature: any}>;
    aeropuertos: Map<string, Aeropuerto>;
    simulationInterval: number;
    setVuelos: React.Dispatch<React.SetStateAction<Map<number, { vuelo: Vuelo, pointFeature: any, lineFeature: any}>>>;
    horaInicio: Date;
    websocket: WebSocket | null;
    nuevosVuelos: number[];
};

const Mapa = ({
    vuelos,
    aeropuertos,
    simulationInterval,
    setVuelos,
    horaInicio = new Date(),
    websocket,
    nuevosVuelos,
}: MapaProps) => {
    const mapRef = useRef<OLMap | null>(null);
    const vectorSourceRef = useRef(new VectorSource());
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

        let auxLineFeatures: any[] = [];
        vuelos.forEach((item) => {
            const feature = crearLineaDeVuelo(aeropuertos, item);
            item.lineFeature = feature;
            auxLineFeatures.push(feature);
        });

        setLineFeatures(auxLineFeatures);

        let auxPointFeatures: any[] = [];
        vuelos.forEach((item) => {
            const feature = crearPuntoDeVuelo(aeropuertos, item, simulationTime);
            item.pointFeature = feature;
            auxPointFeatures.push(feature);
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
    }, [vuelos, aeropuertos, mapRef]);

    useEffect(() => {
        const intervalId = setInterval(() => {
            setSimulationTime(
                (prevSimulationTime) =>new Date(prevSimulationTime.getTime() +simulationInterval * 60 * 1000)
            );
            // console.log("Simulation time: ", simulationTime);
            if(websocket?.readyState === 1)
                websocket.send("tiempo: " + simulationTime.toISOString());
        }, 1000);

        // console.log("Updating coordinates con tiempo: ", simulationTime);

        if (pointFeatures.length > 0 && lineFeatures.length > 0) {
            const aBorrar = updateCoordinates(
                aeropuertos,
                vuelos,
                simulationTime
            );
            for (let i = 0; i < aBorrar.length; i++) {
                const idVuelo = aBorrar[i];
                const item = vuelos.get(idVuelo);
                if (item) {
                    vectorSourceRef.current.removeFeature(item.pointFeature);
                    vectorSourceRef.current.removeFeature(item.lineFeature);
                    item.pointFeature = null;
                    item.lineFeature = null;
                    vuelos.delete(idVuelo);
                }
            }
        }

        // Clean up interval on unmount
        return () => clearInterval(intervalId);
    }, [simulationTime, simulationInterval]); 

    useEffect(() => {
        if(nuevosVuelos.length > 0) {
            for (let i = 0; i < nuevosVuelos.length; i++) {
                const idVuelo = nuevosVuelos[i];
                const item = vuelos.get(idVuelo);
                if (item) {
                    item.pointFeature = crearPuntoDeVuelo(aeropuertos, item, simulationTime);
                    item.lineFeature = crearLineaDeVuelo(aeropuertos, item);
                    vectorSourceRef.current.addFeature(item.pointFeature);
                    vectorSourceRef.current.addFeature(item.lineFeature);
                }
            }
        }
    }), [nuevosVuelos];

    return <div id="map" style={{ width: "100%", height: "900px" }}></div>;
};

export default Mapa;
