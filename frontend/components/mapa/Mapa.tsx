"use client";

import React, { useEffect, useRef, useState } from "react";
import "ol/ol.css";
import {Map as OLMap} from "ol";
import View from "ol/View";
import TileLayer from "ol/layer/Tile";
import OSM from "ol/source/OSM";
import { Feature } from "ol";
import { LineString, Point } from "ol/geom";
import VectorSource from "ol/source/Vector";
import VectorLayer from "ol/layer/Vector";
import { Coordinate } from "ol/coordinate";
import { fromLonLat, toLonLat } from "ol/proj";

import { planeStyle, airportStyle, lineStyle } from "./EstilosMapa";
import { Vuelo } from "@/types/Vuelo";
import { Aeropuerto } from "@/types/Aeropuerto";
import { coordenadasIniciales, updateCoordinates } from "@/utils/FuncionesMapa";

type MapaProps = {
    vuelos: Vuelo[];
    aeropuertos: Map<string, Aeropuerto>;
    simulationInterval: number;
};

const Mapa = ({vuelos, aeropuertos, simulationInterval}: MapaProps)  => {
    const mapRef = useRef<OLMap | null>(null);
    const [simulationTime, setSimulationTime] = useState(new Date());

    useEffect(() => {
        const intervalId = setInterval(() => {
            // Advance simulation time by simulationInterval minutes every real-time second
            setSimulationTime(new Date(simulationTime.getTime() + simulationInterval * 60 * 1000));
        }, 1000);

        // Clean up interval on unmount
        return () => clearInterval(intervalId);
    }, [simulationTime, simulationInterval]); // A

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
        if (typeof window === "undefined" || !mapRef.current){
            return;
        }

        //Limpiar el mapa
        if (mapRef.current.getLayers().getLength() > 1) {
            mapRef.current.getLayers().removeAt(1);
        }
        
        const lineFeatures = vuelos.map((vuelo) => {
            const aeropuertoOrigen = aeropuertos.get(vuelo.origen);
            const aeropuertoDestino = aeropuertos.get(vuelo.destino);
            const lonlatInicio= [aeropuertoOrigen?.longitud ?? 0, aeropuertoOrigen?.latitud ?? 0];
            const lonlatFin = [aeropuertoDestino?.longitud ?? 0, aeropuertoDestino?.latitud ?? 0] ;

            const line = new LineString([
                fromLonLat(lonlatInicio),
                fromLonLat(lonlatFin),
            ]);
            const feature = new Feature({
                geometry: line,
            });
            feature.setStyle(lineStyle);
            return feature;
        });

        const pointFeatures = vuelos.map((vuelo) => {
            const point = coordenadasIniciales(vuelo, aeropuertos, simulationTime);
            const feature = new Feature({
                geometry: point,
            });
            feature.setStyle(planeStyle);
            return feature;
        });

        const aeropuertoFeatures = Array.from(aeropuertos.values()).map((aeropuerto) => {
            const point = new Point(fromLonLat([aeropuerto.longitud, aeropuerto.latitud]));
            const feature = new Feature({
                geometry: point,
            });
            feature.setStyle(airportStyle);
            return feature;
        });

        const vectorSource = new VectorSource({
            features: [...lineFeatures, ...pointFeatures, ...aeropuertoFeatures],
        });

        const vectorLayer = new VectorLayer({
            source: vectorSource,
        });

        mapRef.current.addLayer(vectorLayer);

        const intervalId: NodeJS.Timeout = setInterval(() => {
            updateCoordinates(aeropuertos, vuelos, pointFeatures, lineFeatures,simulationTime);
        }, 3000);

        return () => {
            clearInterval(intervalId);
        };
    }, [vuelos, aeropuertos, mapRef]);

    return <div id="map" style={{ width: "100%", height: "900px" }}></div>;
};

export default Mapa;

