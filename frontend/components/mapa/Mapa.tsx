"use client";

import React, { useEffect, useRef } from "react";
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

    const planes = [
        { start: [-77.0428, -12.0464], end: [-73.5673, -33.4691] },
        { start: [-77.0428, -12.0464], end: [-74.0721, 4.711] },
    ];

    useEffect(() => {
        if (typeof window === "undefined") {
            return;
        }
        const initialCoordinates = fromLonLat([-77.0428, -12.0464]);

        const lineFeatures = planes.map((plane) => {
            const line = new LineString([
                fromLonLat(plane.start),
                fromLonLat(plane.end),
            ]);
            const feature = new Feature({
                geometry: line,
            });
            feature.setStyle(lineStyle);
            return feature;
        });

        const pointFeatures = planes.map((plane) => {
            const point = new Point(fromLonLat(plane.start));
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

        map = new OLMap({
            target: "map",
            layers: [
                new TileLayer({
                    source: new OSM(),
                }),
                vectorLayer,
            ],
            view: new View({
                center: initialCoordinates,
                zoom: 4,
            }),
        });

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
        }, 100);

        return () => {
            clearInterval(intervalId);
            map.setTarget(undefined);
        };
    }, []);

    return <div id="map" style={{ width: "100%", height: "900px" }}></div>;
};

export default Mapa;
