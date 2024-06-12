"use client";
import Leyenda from "@/components/mapa/Leyenda";
import DatosVuelo from "@/components/mapa/DatosVuelo";
import FinSemanal from "@/components/mapa/FinSemanal";
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
    seleccionarElemento,
} from "@/utils/FuncionesMapa";
import BarraMapa from "./BarraMapa";
import { ProgramacionVuelo } from "@/types/ProgramacionVuelo";
import { Envio } from "@/types/Envio";
import { agregarPaquetesAlmacen, limpiarMapasDeDatos } from "@/utils/FuncionesDatos";

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
    aeropuertos: React.MutableRefObject<Map<string, Aeropuerto>>;
    programacionVuelos: React.MutableRefObject<Map<string, ProgramacionVuelo>>;
    envios: React.MutableRefObject<Map<string, Envio>>;
    simulationInterval: number;
    horaInicio: Date;
    nuevosVuelos: number[];
    semaforo: number;
    setSemaforo: React.Dispatch<React.SetStateAction<number>>;
    sendMessage: (message: string, keep: boolean) => void;
    onSimulationTimeChange: any;
};

const Mapa = ({
    vuelos,
    aeropuertos,
    programacionVuelos,
    envios,
    simulationInterval,
    horaInicio = new Date(),
    nuevosVuelos,
    semaforo,
    setSemaforo,
    sendMessage,
    onSimulationTimeChange,
}: MapaProps) => {
    const mapRef = useRef<OLMap | null>(null);
    const vectorSourceRef = useRef(new VectorSource());
    const [simulationTime, setSimulationTime] = useState(new Date(horaInicio));
    const [currentTime, setCurrentTime] = useState(new Date());
    const [selectedVuelo, setSelectedVuelo] = useState<Vuelo | null>(null);
    const [selectedAeropuerto, setSelectedAeropuerto] = useState<Aeropuerto | null>(null);
    const selectedFeature = useRef<Feature | null>(null);
    const vistaActual = useRef<View | null>(null);
    const fechaFinSemana = new Date(horaInicio.getTime() + 7 * 24 * 60  60 * 1000); //suma 7 dias

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
            const feature = crearLineaDeVuelo(aeropuertos.current, item);
            item.lineFeature = feature;
            auxLineFeatures.push(feature);
        });

        let auxPointFeatures: any[] = [];
        vuelos.current?.forEach((item) => {
            //const isSelected = selectedFeature != null && selectedFeature.get("vueloId") === item.vuelo.id;
            const feature = crearPuntoDeVuelo(
                aeropuertos.current,
                item,
                simulationTime,
                programacionVuelos.current
            );
            item.pointFeature = feature;
            auxPointFeatures.push(feature);
        });

        const aeropuertoFeatures = Array.from(aeropuertos.current.values()).map(
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
            const clickHandler = (event: any) => {
                const feature = mapRef.current?.getFeaturesAtPixel(event.pixel)[0];
                // console.log("Feature clicked: ", feature);
                if (feature) {
                    const vueloId = feature.get("vueloId");
                    const aeropuertoId = feature.get("aeropuertoId");
                    seleccionarElemento(
                        vueloId,
                        aeropuertoId,
                        setSelectedVuelo,
                        setSelectedAeropuerto,
                        selectedFeature,
                        vuelos,
                        aeropuertos,
                        feature
                    );
                }
            };
    
            mapRef.current.on("click", clickHandler);
    
            // Cleanup function to remove the event listener when the component is unmounted
            return () => {
                if (mapRef.current) {
                    mapRef.current.un("click", clickHandler);
                }
            };
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
            onSimulationTimeChange(simulationTime);
            if (sendMessage) {
                const limaTime = simulationTime.toLocaleString("en-US", {
                    timeZone: "America/Lima",
                });
                sendMessage("tiempo: " + limaTime, true);
            }
            if(simulationTime.getTime() === fechaFinSemana.getTime()){
                simulationInterval = 0;
            }
        }, 1000);

        // console.log("Updating coordinates con tiempo: ", simulationTime);

        if (vectorSourceRef.current.getFeatures().length > 0) {
            const aBorrar = updateCoordinates(
                aeropuertos.current,
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
                    agregarPaquetesAlmacen(idVuelo, programacionVuelos, aeropuertos, simulationTime, envios);
                }
            }
        }

        // Clean up interval on unmount
        return () => clearInterval(intervalId);
    }, [simulationTime, simulationInterval]);

    useEffect(() => {
        const timeoutId = setInterval(() => limpiarMapasDeDatos(programacionVuelos, envios, new Date(simulationTime.getTime())), 360 * 1000); // 360 seconds = 6 minutes
        return () => clearInterval(timeoutId); // Clear the interval if the component is unmounted
    }, []);

    useEffect(() => {
        if (nuevosVuelos.length > 0 && semaforo > 0) {
            // console.log("Nuevos vuelos: ", nuevosVuelos);
            for (let i = 0; i < nuevosVuelos.length; i++) {
                const idVuelo = nuevosVuelos[i];
                const item = vuelos.current?.get(idVuelo);
                if (item) {
                    item.lineFeature = crearLineaDeVuelo(aeropuertos.current, item);
                    item.pointFeature = crearPuntoDeVuelo(
                        aeropuertos.current,
                        item,
                        simulationTime,
                        programacionVuelos.current
                    );
                    vectorSourceRef.current.addFeature(item.pointFeature);
                    vectorSourceRef.current.addFeature(item.lineFeature);
                }
            }
            setSemaforo(semaforo - 1);
        }
    }),[nuevosVuelos, semaforo];

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
                    aeropuertos={aeropuertos.current}
                />
                <Leyenda
                    vuelosEnTransito={vuelos.current?.size ?? 0}
                    enviosEnElAire={enviosEnElAire}
                    fechaHoraActual={currentTime.toLocaleString()}
                    fechaHoraSimulada={simulationTime.toLocaleString()}
                />
                <DatosVuelo vuelo={selectedVuelo} aeropuerto={selectedAeropuerto} programacionVuelos={programacionVuelos} simulationTime={simulationTime}
                    envios={envios} aeropuertos={aeropuertos}
                />
            </div>{" "}
        </div>
    );
};

export default Mapa;
