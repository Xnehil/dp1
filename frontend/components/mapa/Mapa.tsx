"use client";
import Leyenda from "@/components/mapa/Leyenda";
import DatosVuelo from "@/components/mapa/DatosVuelo";
import React, { useEffect, useRef, useState } from "react";
import "ol/ol.css";
import { Map as OLMap } from "ol";
import View from "ol/View";
import TileLayer from "ol/layer/Tile";
import OSM from "ol/source/OSM";
import { Feature } from "ol";
import { Geometry } from "ol/geom";
import { LineString, Point } from "ol/geom";
import VectorSource from "ol/source/Vector";
import VectorLayer from "ol/layer/Vector";
import { Coordinate } from "ol/coordinate";
import { fromLonLat, toLonLat } from "ol/proj";

import {
    planeStyle,
    airportStyle,
    selectedPlaneStyle,
    invisibleStyle,
    selectedLineStyle,
    dinamicPlaneStyle,
    dinamicSelectedPlaneStle,
} from "./EstilosMapa";
import { Vuelo } from "@/types/Vuelo";
import { Aeropuerto } from "@/types/Aeropuerto";
import {
    coordenadasIniciales,
    crearLineaDeVuelo,
    crearPuntoDeVuelo,
    seleccionarVuelo,
    seleccionarAeropuerto,
    updateCoordinates,
} from "@/utils/FuncionesMapa";
import BarraMapa from "./BarraMapa";

type MapaProps = {
    vuelos: React.RefObject<
        Map<
            number,
            {
                vuelo: Vuelo;
                pointFeature: any;
                lineFeature: any;
                routeFeature: any;
            }
        >
    >;
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
    const [selectedVuelo, setSelectedVuelo] = useState<Vuelo | null>(null);
    const [selectedAeropuerto, setSelectedAeropuerto] = useState<Aeropuerto | null>(null);
    const selectedFeature = useRef<Feature | null>(null);
    const vistaActual = useRef<View | null>(null);

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
            vistaActual.current = mapRef.current.getView();
        }
    }, []);

    useEffect(() => {
        if (typeof window === "undefined" || !mapRef.current) {
            return;
        }

        let auxLineFeatures: any[] = [];
        vuelos.current?.forEach((item) => {
            const feature = crearLineaDeVuelo(aeropuertos, item);
            item.lineFeature = feature;
            auxLineFeatures.push(feature);
        });

        let auxPointFeatures: any[] = [];
        vuelos.current?.forEach((item) => {
            //const isSelected = selectedFeature != null && selectedFeature.get("vueloId") === item.vuelo.id;
            const feature = crearPuntoDeVuelo(
                aeropuertos,
                item,
                simulationTime
            );
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
                feature.set('aeropuertoId', aeropuerto.codigoOACI);// era OACI y no id, 1h para darme cuenta
                return feature;
            }
        );

        console.log(
            "Adding # features: ",
            auxLineFeatures.length,
            auxPointFeatures.length,
            aeropuertoFeatures.length
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

        if (mapRef.current) {
            mapRef.current.on("click", (event) => {
                mapRef.current?.forEachFeatureAtPixel(
                    event.pixel,
                    (feature) => {
                        const vueloId = feature.get("vueloId");
                        if (vueloId) {
                            seleccionarVuelo(
                                vueloId,
                                setSelectedVuelo,
                                selectedFeature,
                                vuelos,
                                feature
                            );
                        }
                        const aeropuertoId = feature.get("aeropuertoId");
                        if (aeropuertoId) {
                            seleccionarAeropuerto(
                                aeropuertoId,
                                setSelectedAeropuerto,
                                selectedFeature,
                                aeropuertos,
                                feature
                            );
                        }
                    }
                );
            });
        }
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
                (prevSimulationTime) =>
                    new Date(
                        prevSimulationTime.getTime() +
                            simulationInterval * 60 * 1000
                    )
            );
            // console.log("Simulation time: ", simulationTime);
            if (sendMessage) {
                // console.log("Enviando tiempo: ", simulationTime.toDateString());
                const limaTime = simulationTime.toLocaleString("en-US", {
                    timeZone: "America/Lima",
                });
                sendMessage("tiempo: " + limaTime, true);
            }
        }, 1000);

        // console.log("Updating coordinates con tiempo: ", simulationTime);

        if (vectorSourceRef.current.getFeatures().length > 0) {
            const aBorrar = updateCoordinates(
                aeropuertos,
                vuelos.current,
                simulationTime
            );
            // console.log("aBorrar: ", aBorrar);
            for (let i = 0; i < aBorrar.length; i++) {
                const idVuelo = aBorrar[i];
                const item = vuelos.current?.get(idVuelo);
                if (item) {
                    vectorSourceRef.current.removeFeature(item.pointFeature);
                    vectorSourceRef.current.removeFeature(item.lineFeature);
                    item.pointFeature = null;
                    item.lineFeature = null;
                    item.routeFeature = null;
                    vuelos.current?.delete(idVuelo);
                }
            }
        }

        // Clean up interval on unmount
        return () => clearInterval(intervalId);
    }, [simulationTime, simulationInterval]);

    useEffect(() => {
        if (nuevosVuelos.length > 0 && semaforo > 0) {
            // console.log("Nuevos vuelos: ", nuevosVuelos);
            for (let i = 0; i < nuevosVuelos.length; i++) {
                const idVuelo = nuevosVuelos[i];
                const item = vuelos.current?.get(idVuelo);
                if (item) {
                    item.lineFeature = crearLineaDeVuelo(aeropuertos, item);
                    item.pointFeature = crearPuntoDeVuelo(
                        aeropuertos,
                        item,
                        simulationTime
                    );
                    vectorSourceRef.current.addFeature(item.pointFeature);
                    vectorSourceRef.current.addFeature(item.lineFeature);
                }
            }
            setSemaforo(semaforo - 1);
        }
    }),
        [nuevosVuelos, semaforo];

    const enviosEnElAire = 1420;

    return (
        <div id="map" style={{ width: "100%", height: "900px" }}>
            {" "}
            <div>
                <BarraMapa
                    setSelectedVuelo={setSelectedVuelo}
                    setSelectedAeropuerto={setSelectedAeropuerto}
                    mapRef={mapRef}
                    selectedFeature={selectedFeature}
                    vuelos={vuelos}
                    aeropuertos={aeropuertos}
                />
                <Leyenda
                    vuelosEnTransito={vuelos.current?.size ?? 0}
                    enviosEnElAire={enviosEnElAire}
                    fechaHoraActual={currentTime.toLocaleString()}
                    fechaHoraSimulada={simulationTime.toLocaleString()}
                />
                <DatosVuelo vuelo={selectedVuelo} />
            </div>{" "}
        </div>
    );
};

export default Mapa;
