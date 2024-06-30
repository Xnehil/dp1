"use client";
import Leyenda from "@/components/mapa/Leyenda";
import DatosVuelo from "@/components/mapa/DatosVuelo";
import FinSemanal from "@/components/mapa/FinSemanal";
import VuelosAlmacen from "@/components/mapa/VuelosAlmacen";
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
    desactivarEnvio,
} from "@/utils/FuncionesMapa";
import BarraMapa from "./BarraMapa";
import { ProgramacionVuelo } from "@/types/ProgramacionVuelo";
import { Envio } from "@/types/Envio";
import { agregarPaquetesAlmacen, capacidadAlmacenesUsada, contarVuelos, decidirEstiloAeropuerto, limpiarMapasDeDatos } from "@/utils/FuncionesDatos";

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
    aeropuertos: React.MutableRefObject<Map<string, { aeropuerto: Aeropuerto; pointFeature: any }>>;
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
    const [selectedEnvio, setSelectedEnvio] = useState<Envio | null>(null);
    const selectedFeature = useRef<Feature | null>(null);
    const vistaActual = useRef<View | null>(null);
    const fechaFinSemana = new Date(horaInicio.getTime() + 7 * 24 * 60 * 60 * 1000); //suma 7 dias
    const [vuelosABorrar, setVuelosABorrar] = useState<number[]>([]);
    const [mostrarFinSemanal, setMostrarFinSemanal] = useState(false);
    const aBorrarEnvios = useRef<string[]>([]);
    const vuelosEnElAire = useRef<number>(0);
    const [colapso, setColapso] = useState(false);

    useEffect(() => {
        if (!mapRef.current) {
            const initialCoordinates = fromLonLat([0, 0]);
            mapRef.current = new OLMap({
                target: "map",
                layers: [
                    new TileLayer({
                        source: new OSM(),
                    }),
                ],
                view: new View({
                    center: initialCoordinates,
                    zoom: 0,
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
        let cuenta=0;
        vuelos.current?.forEach((item) => {
            //const isSelected = selectedFeature != null && selectedFeature.get("vueloId") === item.vuelo.id;
            const objeto:{feature:any, tieneCarga:boolean} = crearPuntoDeVuelo(
                aeropuertos.current,
                item,
                simulationTime,
                programacionVuelos.current,
                setColapso
            );
            item.pointFeature = objeto.feature;
            auxPointFeatures.push(objeto.feature)
            if(objeto.tieneCarga) cuenta++;
        });
        vuelosEnElAire.current = cuenta;

        const aeropuertoFeatures = Array.from(aeropuertos.current.values()).map(
            (item) => {
                const point = new Point(
                    fromLonLat([item.aeropuerto.longitud, item.aeropuerto.latitud])
                );
                const feature = new Feature({
                    geometry: point,
                });
                feature.set('aeropuertoId', item.aeropuerto.codigoOACI);// era OACI y no id, 1h para darme cuenta
                aeropuertos.current.set(item.aeropuerto.codigoOACI, {...item, pointFeature: feature});
                decidirEstiloAeropuerto(aeropuertos.current.get(item.aeropuerto.codigoOACI));
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
                desactivarEnvio(aBorrarEnvios, aeropuertos.current, vuelos);
                if (feature) {
                    const vueloId = feature.get("vueloId");
                    const aeropuertoId = feature.get("aeropuertoId");
                    seleccionarElemento(
                        vueloId,
                        aeropuertoId,
                        setSelectedVuelo,
                        setSelectedAeropuerto,
                        setSelectedEnvio,
                        selectedFeature,
                        vuelos,
                        aeropuertos,
                        feature,
                    );
                }
                else {
                    setSelectedVuelo(null);
                    setSelectedAeropuerto(null);
                    setSelectedEnvio(null);
                    if (selectedFeature.current != null) {
                        if (selectedFeature.current.get("vueloId")) {
                            selectedFeature.current.setStyle(selectedFeature.current.get("estiloAnterior"));
                            vuelos.current?.get(selectedFeature.current.get("vueloId"))?.lineFeature.setStyle(invisibleStyle);
                        } else if (selectedFeature.current.get("aeropuertoId")) {
                            selectedFeature.current.setStyle(selectedFeature.current.get("estiloAnterior"));
        
                        }
                    }
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
                sendMessage("mensaje: tiempo: " + limaTime, true);
            }
        }, 1000);

        if(simulationTime.getTime() > fechaFinSemana.getTime() || colapso){
            clearInterval(intervalId);
            console.log("Fin de la simulación");
            setMostrarFinSemanal(true);
            //Aquí André activas tus componenetes
        }
        // console.log("Updating coordinates con tiempo: ", simulationTime);

        if (vectorSourceRef.current.getFeatures().length > 0) {
            const aBorrar: number[] = updateCoordinates(
                vuelos.current,
                simulationTime
            );
            // console.log("aBorrar: ", aBorrar);
            setVuelosABorrar(aBorrar);
        }

        // Clean up interval on unmount
        return () => clearInterval(intervalId);
    }, [simulationTime, simulationInterval]);

    // useEffect(() => {
    //     const timeoutId = setInterval(() => limpiarMapasDeDatos(programacionVuelos, envios, new Date(simulationTime.getTime())), 360 * 1000); // 360 seconds = 6 minutes
    //     return () => clearInterval(timeoutId); 
    // }, []);

    useEffect(() => {
        function processItem(item: any, idVuelo: number) {
            if (item) {
                vectorSourceRef.current.removeFeature(item.pointFeature);
                vectorSourceRef.current.removeFeature(item.lineFeature);
                item.pointFeature = null;
                item.lineFeature = null;
                item.routeFeature = null;
                let result=false;
                try {
                    result = agregarPaquetesAlmacen(idVuelo, programacionVuelos, aeropuertos, simulationTime, envios, vuelos, setColapso) ?? false;
                } catch (error) {
                    console.error('Promesa rechazada: ', error);
                }
                vuelos.current?.delete(idVuelo);
                return result;
            }
            return false;
        }

        function processItems(aBorrar: number[]) {
            let cuenta=0;
            for (let i = 0; i < aBorrar.length; i++) {
                const idVuelo = aBorrar[i];
                const item = vuelos.current?.get(idVuelo);
                const result= processItem(item, idVuelo);
                if(result) cuenta++;
            }
            // console.log("Restando vuelos en el aire: %d de %d", cuenta, aBorrar.length);
            vuelosEnElAire.current = vuelosEnElAire.current - cuenta;
        }

        if(vuelosABorrar.length > 0){
             processItems(vuelosABorrar);
             for (let key in aeropuertos.current.keys()) {
                decidirEstiloAeropuerto(aeropuertos.current.get(key));
            } 
        }
    } ,[vuelosABorrar]);

    useEffect(() => {
        if (nuevosVuelos.length > 0 && semaforo > 0) {
            // console.log("Nuevos vuelos: ", nuevosVuelos);
            let cuenta=0;
            for (let i = 0; i < nuevosVuelos.length; i++) {
                const idVuelo = nuevosVuelos[i];
                const item = vuelos.current?.get(idVuelo);
                if (item) {
                    item.lineFeature = crearLineaDeVuelo(aeropuertos.current, item);
                    
                    let objeto:{feature:any, tieneCarga:boolean}= crearPuntoDeVuelo(
                        aeropuertos.current,
                        item,
                        simulationTime,
                        programacionVuelos.current,
                        setColapso
                    );
                    item.pointFeature = objeto.feature;
                    if(objeto.tieneCarga) cuenta++;
                    vectorSourceRef.current.addFeature(item.pointFeature);
                    vectorSourceRef.current.addFeature(item.lineFeature);
                }
            }
            // console.log("Sumando vuelos en el aire: %d de %d", cuenta, nuevosVuelos.length);
            vuelosEnElAire.current = vuelosEnElAire.current + cuenta;
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
                    setSelectedEnvio={setSelectedEnvio}
                    mapRef={mapRef}
                    selectedFeature={selectedFeature}
                    vuelos={vuelos}
                    aeropuertos={aeropuertos.current}
                    programacionVuelos={programacionVuelos.current}
                    envios={envios.current}
                    simulatedTime={simulationTime}
                    aBorrarEnvios={aBorrarEnvios}
                />
                <Leyenda
                    vuelosEnTransito={contarVuelos(vuelos)}
                    capacidadAlmacenes={capacidadAlmacenesUsada(aeropuertos)}
                    fechaHoraActual={currentTime.toLocaleString()}
                    fechaHoraSimulada={simulationTime}
                    fechaHoraInicio={horaInicio}
                    simulacion={simulationInterval!==1/60}
                />
                <DatosVuelo vuelo={selectedVuelo} aeropuerto={selectedAeropuerto} programacionVuelos={programacionVuelos} simulationTime={simulationTime}
                    envios={envios} aeropuertos={aeropuertos} envio = {selectedEnvio} vuelos = {vuelos}
                />
                {mostrarFinSemanal && <FinSemanal programacionVuelos={programacionVuelos} vuelos={vuelos}/>}
                <VuelosAlmacen selectedAeropuerto={selectedAeropuerto} vuelos={vuelos} simulationTime={simulationTime} programacionVuelos={programacionVuelos} />
            </div>{" "}
        </div>
    );
};

export default Mapa;
