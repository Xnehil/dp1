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

type MapaProps = {
    vuelos: Vuelo[];
    aeropuertos: Map<string, Aeropuerto>;
};

const Mapa = ({vuelos, aeropuertos}: MapaProps)  => {
    let map: OLMap;
    const mapRef = useRef<OLMap | null>(null);

    useEffect(() => {
        const initialCoordinates = fromLonLat([-77.0428, -12.0464]);
        map = new OLMap({
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
        mapRef.current = map;
    }, []);

    useEffect(() => {
        if (typeof window === "undefined" || !mapRef.current){
            return;
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

        const pointFeatures = vuelos.map((vuelos) => {
            const aeropuertoOrigen = aeropuertos.get(vuelos.origen);
            const point = new Point(fromLonLat([aeropuertoOrigen?.longitud ?? 0, aeropuertoOrigen?.latitud ?? 0]));
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
            for (let i = 0; i < pointFeatures.length; i++) {
                const pointFeature = pointFeatures[i];
                const lineFeature = lineFeatures[i];
                
                const line = lineFeature.getGeometry() as LineString;
                const coordinates = line.getCoordinates();
                const point = pointFeature.getGeometry() as Point;
                const currentCoordinate = point.getCoordinates();
                const nextCoordinate = coordinates[1];
                const newCoordinates = [
                    currentCoordinate[0] + (nextCoordinate[0] - currentCoordinate[0]) / 500,
                    currentCoordinate[1] + (nextCoordinate[1] - currentCoordinate[1]) / 500,
                ] as Coordinate;
                point.setCoordinates(newCoordinates);
            }
        }, 1000);

        return () => {
            clearInterval(intervalId);
        };
    }, [vuelos, aeropuertos, mapRef]);

    return <div id="map" style={{ width: "100%", height: "900px" }}></div>;
};

export default Mapa;
