import { Aeropuerto } from '@/types/Aeropuerto';
import { Vuelo } from '@/types/Vuelo';
import { Coordinate } from 'ol/coordinate';
import { Point, LineString } from 'ol/geom';
import { tiempoEntreAhoraYSalida } from './FuncionesTiempo';
import { fromLonLat } from 'ol/proj';
import { dinamicPlaneStyle, dinamicSelectedPlaneStle, invisibleStyle, planeStyle, selectedLineStyle, selectedPlaneStyle, selectedAirportStyle, airportStyle, calcularAngulo, greenPlaneStyle, yellowPlaneStyle, redPlaneStyle, mulitpleSelectedLineStyle } from '@/components/mapa/EstilosMapa';
import { Feature } from 'ol';
import { getVectorContext } from 'ol/render';
import Icon from 'ol/style/Icon';
import Style from 'ol/style/Style';
import { ProgramacionVuelo } from '@/types/ProgramacionVuelo';
import React from 'react';
import { Envio } from '@/types/Envio';
import { Paquete } from '@/types/Paquete';

export function updateCoordinates(vuelos: Map<number, { vuelo: Vuelo, pointFeature: any, lineFeature: any}> | null, simulationTime: Date):
    number[]{
    let aBorrar:number[] = [];
    //Iterar por cada vuelo
    let cuenta=0;
    let verbose = false;
    // let medirTiempo = new Date();
    vuelos?.forEach((item, i) => {
        
        const vuelo = item.vuelo;
        if (vuelo.id == 106) {
            // verbose = true;
        }

        // console.log("vuelo: ", vuelo);
        const pointFeature = item.pointFeature;
        const lineFeature = item.lineFeature;

        if (verbose) {
            console.log("pointFeature: ", pointFeature);
        }

        // console.log("vuelo: ", vuelo);
        try{
            const line = lineFeature.getGeometry() as LineString;
            const coordinates = line.getCoordinates();
            // console.log("coordinates: ", coordinates);
            const totalDistance = vuelo.distanciaVuelo; //En km
            const speed = totalDistance / (vuelo.duracionVuelo); //En km/min

            if (verbose) {
                console.log("speed: ", speed);
            }

            const point = pointFeature.getGeometry() as Point;
            const destinationCoordinates = coordinates[1] as Coordinate;
            const originCoordinates = coordinates[0] as Coordinate;
            // console.log("simulationTime: ", simulationTime);
            const tiempoPasadoMinutos = tiempoEntreAhoraYSalida(vuelo, simulationTime);
            // console.log("tiempoPasadoMinutos: ", tiempoPasadoMinutos);

            const distanciaRecorrida = speed * tiempoPasadoMinutos;
            const ratio = distanciaRecorrida / totalDistance;
            if (verbose) {
                console.log("distanciaRecorrida: ", distanciaRecorrida);
                console.log("totalDistance: ", totalDistance);
                console.log("ratio: ", ratio);
            }
            // console.log("distanciaRecorrida: ", distanciaRecorrida);
            // console.log("totalDistance: ", totalDistance);
            // console.log("ratio: ", ratio);
            const newCoordinates = [
                originCoordinates[0] + ratio * (destinationCoordinates[0] - originCoordinates[0]),
                originCoordinates[1] + ratio * (destinationCoordinates[1] - originCoordinates[1]),
            ] as Coordinate;

            if (verbose) {
                console.log("newCoordinates: ", newCoordinates);
            }

            if (distanciaRecorrida >= totalDistance) {
                point.setCoordinates(destinationCoordinates);
                //Hacer el avión invisible
                pointFeature.setStyle(invisibleStyle);
                line.setCoordinates([destinationCoordinates, destinationCoordinates]);
                aBorrar.push(i);
            } else {
                point.setCoordinates(newCoordinates);
            }
            cuenta++;
        }catch(error){
            console.error("Error al actualizar coordenadas: ", error);
        }
    });
    // let medirTiempo2 = new Date();
    // console.log("Tiempo de updateCoordinates: ", medirTiempo2.getTime()-medirTiempo.getTime());
    // console.log("aBorrar: ", aBorrar);
    // console.log("Cuenta: ", cuenta, "Vuelos: ", vuelos.size);
    return aBorrar;

}

export function coordenadasIniciales(aeropuertos: Map<String, {aeropuerto: Aeropuerto; pointFeature:any}>, item: any, simulationTime: Date): Point {
    const lineFeature = item.lineFeature;
    const line = lineFeature.getGeometry() as LineString;
    const coordinates = line.getCoordinates();

    let verbose = false;
    if(item.vuelo.id == 106){
        verbose = true;
    }

    if (verbose) {
        console.log("vuelo: ", item.vuelo);
        console.log("line: ", line);
        console.log("coordinates: ", coordinates);
    }

    // Calculate the elapsed time in minutes
    const tiempoPasadoMinutos = tiempoEntreAhoraYSalida(item.vuelo,  simulationTime, verbose);

    if (verbose) {
        console.log("tiempoPasadoMinutos: ", tiempoPasadoMinutos);
    }


    // Qué tan avanzado está el vuelo
    const ratio = tiempoPasadoMinutos / item.vuelo.duracionVuelo;

    if (verbose) {
        console.log("ratio: ", ratio);
    }

    const lonlatInicio = coordinates[0] as Coordinate;
    const lonlatFin = coordinates[1] as Coordinate;

    // Calculate the current position of the plane
    const newCoordinates = [
        lonlatInicio[0] + ratio * (lonlatFin[0] - lonlatInicio[0]),
        lonlatInicio[1] + ratio * (lonlatFin[1] - lonlatInicio[1]),
    ];

    if (verbose) {
        console.log("newCoordinates: ", newCoordinates);
    }
    // console.log("Vuelo: ", vuelo);
    // console.log("lonlatInicio: ", lonlatInicio);
    // console.log("lonlatFin: ", lonlatFin);
    // console.log("tiempoPasadoMinutos: ", tiempoPasadoMinutos);
    // console.log("ratio: ", ratio);
    // console.log("newCoordinates: ", newCoordinates);
    const punto = new Point(fromLonLat(newCoordinates));
    // console.log("punto: ", punto);
    return punto;
}

export function crearLineaDeVuelo(aeropuertos: Map<String, {aeropuerto: Aeropuerto; pointFeature: any}>, item: any): any {
    const aeropuertoOrigen = aeropuertos.get(item.vuelo.origen);
    const aeropuertoDestino = aeropuertos.get(item.vuelo.destino);
    const lonlatInicio = [
        aeropuertoOrigen?.aeropuerto.longitud ?? 0,
        aeropuertoOrigen?.aeropuerto.latitud ?? 0,
    ];
    const lonlatFin = [
        aeropuertoDestino?.aeropuerto.longitud ?? 0,
        aeropuertoDestino?.aeropuerto.latitud ?? 0,
    ];

    const line = new LineString([
        fromLonLat(lonlatInicio),
        fromLonLat(lonlatFin),
    ]);
    const feature = new Feature({
        geometry: line,
    });
    feature.setStyle(invisibleStyle);
    feature.set('vueloId', item.vuelo.id);
    return feature;
}

export function crearPuntoDeVuelo(aeropuertos: Map<String, {aeropuerto:Aeropuerto; pointFeature: any}>, item: any, simulationTime: Date,
    programacionVuelos: Map<string, ProgramacionVuelo>, setColapso: any): {feature: any, tieneCarga: boolean} {
    const point = coordenadasIniciales(aeropuertos, item, simulationTime);
    const feature = new Feature({
        geometry: point,
    });
    const llaveBusqueda = item.vuelo.id + "-" + simulationTime.toISOString().slice(0, 10);
    const programacion = programacionVuelos.get(llaveBusqueda);
    const paquetes = programacion?.cantPaquetes ?? 0;
    const angulo = calcularAngulo(item);
    let tieneCarga = true;
    if (paquetes > 0) {
        let razon = paquetes / item.vuelo.capacidad;
        feature.set('pintarAuxiliar', true); 
        feature.set('cantPaquetes', paquetes);
        if (razon < 0.33){
            feature.setStyle(greenPlaneStyle(item, angulo));
        }
        else if (razon < 0.66){
            feature.setStyle(yellowPlaneStyle(item, angulo));
        }
        else if (razon <= 1){
            feature.setStyle(redPlaneStyle(item, angulo));
        } else {
            console.error("Error en la cantidad de paquetes");
            setColapso(true);
        }
    } else {
        tieneCarga = false;
        feature.setStyle(invisibleStyle);
    }
    feature.set('vueloId', item.vuelo.id); // Agregar el ID del vuelo
    feature.set('angulo', angulo);
    return {feature, tieneCarga};
}

export function seleccionarVuelo(vueloId:number, setSelectedVuelo: any , setSelectedAeropuerto: any,
    selectedFeature: any, vuelos: React.RefObject<Map<number, { vuelo: Vuelo, pointFeature: any, lineFeature: any}>>, feature: any){
    // console.log("vueloId: ", vueloId);
    // console.log("vuelos: ", vuelos.current);
    const vuelo = vuelos.current?.get(vueloId)?.vuelo;
    if (vuelo) {
        setSelectedVuelo(vuelo);
        setSelectedAeropuerto(null);
        console.log(
            `Vuelo seleccionado setteado: Vuelo ID${vuelo.id}`
        );
        if (selectedFeature.current != null) {
            if (selectedFeature.current.get("vueloId")) {
                selectedFeature.current.setStyle(selectedFeature.current.get("estiloAnterior"));
                vuelos.current?.get(selectedFeature.current.get("vueloId"))?.lineFeature.setStyle(invisibleStyle);
            } else if (selectedFeature.current.get("aeropuertoId")) {
                selectedFeature.current.setStyle(selectedFeature.current.get("estiloAnterior"));
            }
        }
        (feature as Feature).set("estiloAnterior", (feature as Feature).getStyle());
        (feature as Feature).setStyle(dinamicSelectedPlaneStle(vuelos.current?.get(vueloId)));
        selectedFeature.current = feature as Feature;

        vuelos.current?.get(vueloId)?.lineFeature.setStyle(selectedLineStyle);
    } else {
        console.error(
            `Vuelo no encontrado: Vuelo ID ${vueloId}`
        );
    }
}

export function seleccionarAeropuerto(aeropuertoId: string, setSelectedAeropuerto: any, setSelectedVuelo: any,
    selectedFeature: any, aeropuertos: Map<string, {aeropuerto: Aeropuerto; pointFeature:any}>, feature: any, vuelos: React.RefObject<Map<number, { vuelo: Vuelo, pointFeature: any, lineFeature: any }>>) {
    const aeropuerto = aeropuertos.get(aeropuertoId)?.aeropuerto
    if (aeropuerto) {
        setSelectedAeropuerto(aeropuerto);
        setSelectedVuelo(null);
        console.log(
            `Aeropuerto seleccionado setteado: Aeropuerto ID ${aeropuerto.codigoOACI}`
        );
        if (selectedFeature.current != null) {
            if (selectedFeature.current.get("vueloId")) {
                selectedFeature.current.setStyle(selectedFeature.current.get("estiloAnterior"));
                vuelos.current?.get(selectedFeature.current.get("vueloId"))?.lineFeature.setStyle(invisibleStyle);
            } else if (selectedFeature.current.get("aeropuertoId")) {
                selectedFeature.current.setStyle(selectedFeature.current.get("estiloAnterior"));

            }
        }
        (feature as Feature).set("estiloAnterior", (feature as Feature).getStyle());
        (feature as Feature).setStyle(selectedAirportStyle);
        selectedFeature.current = feature as Feature;
    } else {
        console.error(
            `Aeropuerto no encontrado: Aeropuerto ID ${aeropuertoId}`
        );
    }
}

export function seleccionarElemento(
    vueloId: number | null,
    aeropuertoId: string | null,
    setSelectedVuelo: any,
    setSelectedAeropuerto: any,
    setSelectedEnvio: any,
    selectedFeature: any,
    vuelos: React.RefObject<Map<number, { vuelo: Vuelo, pointFeature: any, lineFeature: any }>>,
    aeropuertos: React.RefObject<Map<string, { aeropuerto: Aeropuerto; pointFeature: any }>>,
    feature: any,
) {
    if (vueloId) {
        const vuelo = vuelos.current?.get(vueloId)?.vuelo;
        if (vuelo) {
            setSelectedVuelo(vuelo);
            setSelectedAeropuerto(null);
            setSelectedEnvio(null);
            // console.log(`Vuelo seleccionado setteado: Vuelo ID${vuelo.id}`);
            console.log("Item del vuelo: ", vuelos.current?.get(vueloId));
            if (selectedFeature.current != null) {
                if (selectedFeature.current.get("vueloId")) {
                    selectedFeature.current.setStyle(selectedFeature.current.get("estiloAnterior"));
                    vuelos.current?.get(selectedFeature.current.get("vueloId"))?.lineFeature.setStyle(invisibleStyle);
                } else if (selectedFeature.current.get("aeropuertoId")) {
                    selectedFeature.current.setStyle(selectedFeature.current.get("estiloAnterior"));
                }
            }
            (feature as Feature).set("estiloAnterior", (feature as Feature).getStyle());
            (feature as Feature).setStyle(dinamicSelectedPlaneStle(vuelos.current?.get(vueloId)));
            
            selectedFeature.current = feature as Feature;
            vuelos.current?.get(vueloId)?.lineFeature.setStyle(selectedLineStyle);
        } else {
            console.error(`Vuelo no encontrado: Vuelo ID ${vueloId}`);
        }
    } else if (aeropuertoId) {
        const aeropuerto = aeropuertos.current?.get(aeropuertoId);
        if (aeropuerto) {
            setSelectedAeropuerto(aeropuerto.aeropuerto);
            setSelectedVuelo(null);
            setSelectedEnvio(null);
            // console.log(`Aeropuerto seleccionado setteado: Aeropuerto ID ${aeropuerto.id}`);
            console.log("Aero: ", aeropuerto);
            // console.log("Feature: ", feature);
            // console.log("SelectedFeature: ", selectedFeature.current);
            if (selectedFeature.current != null) {
                if (selectedFeature.current.get("vueloId")) {
                    selectedFeature.current.setStyle(selectedFeature.current.get("estiloAnterior"));
                    vuelos.current?.get(selectedFeature.current.get("vueloId"))?.lineFeature.setStyle(invisibleStyle);
                } else if (selectedFeature.current.get("aeropuertoId")) {
                    selectedFeature.current.setStyle(selectedFeature.current.get("estiloAnterior"));

                }
            }
            (feature as Feature).set("estiloAnterior", (feature as Feature).getStyle());
            (feature as Feature).setStyle(selectedAirportStyle);
            selectedFeature.current = feature as Feature;
        } else {
            console.error(`Aeropuerto no encontrado: Aeropuerto ID ${aeropuertoId}`);
        }
    }
    else{
        console.log("No se seleccionó nada");
    }
}

export function procesarSeleccionEnvio(
    envio: Envio ,
    setSelectedVuelo: any,
    setSelectedAeropuerto: any,
    setSelectedEnvio: any,
    selectedFeature: any,
    vuelos: React.RefObject<Map<number, { vuelo: Vuelo, pointFeature: any, lineFeature: any }>>,
    aeropuertos: Map<string, { aeropuerto: Aeropuerto; pointFeature: any }>,
) {
    let aDesactivar: string[] = [];

    setSelectedAeropuerto(null);
    setSelectedVuelo(null);
    setSelectedEnvio(envio);
    if (selectedFeature.current != null) {
        if (selectedFeature.current.get("vueloId")) {
            selectedFeature.current.setStyle(selectedFeature.current.get("estiloAnterior"));
            vuelos.current?.get(selectedFeature.current.get("vueloId"))?.lineFeature.setStyle(invisibleStyle);
        } else if (selectedFeature.current.get("aeropuertoId")) {
            selectedFeature.current.setStyle(selectedFeature.current.get("estiloAnterior"));
        }
    }

    activarUnAeropuerto(aeropuertos, envio.origen);
    aDesactivar.push(envio.origen);
    activarUnAeropuerto(aeropuertos, envio.destino);
    aDesactivar.push(envio.destino);

    for(let paquete of envio.paquetes){
        for (let idVuelo of paquete.ruta){
            const vuelo = vuelos.current?.get(idVuelo);
            if (vuelo && vuelo.pointFeature.get('pintarAuxiliar')) {
                activarUnVuelo(vuelo);
                aDesactivar.push(idVuelo.toString());
                break;
            }
        }
    }

    return aDesactivar;
}

export function desactivarEnvio(aDesactivar : React.RefObject<string[]>,
    aeropuertos : Map<string, { aeropuerto: Aeropuerto; pointFeature: any }>, 
    vuelos: React.RefObject<Map<number, { vuelo: Vuelo, pointFeature: any, lineFeature: any }>>) 
{
    if (aDesactivar.current == null) {
        return;
    }
    for (let id of aDesactivar.current) {
        //Si es numérico, es un vuelo
        if (!isNaN(Number(id))) {
            const vuelo = vuelos.current?.get(Number(id));
            if (vuelo) {
                desactivarUnVuelo(vuelo);
            }
        } else {
            //Si no, es un aeropuerto
            const aeropuerto = aeropuertos.get(id);
            if (aeropuerto) {
                desactivarUnAeropuerto(aeropuerto);
            }
        }
    }
}

function desactivarUnAeropuerto(aeropuerto : { aeropuerto: Aeropuerto; pointFeature: any }) {
    aeropuerto.pointFeature.setStyle(aeropuerto.pointFeature.get('estiloAnterior'));
    aeropuerto.pointFeature.set('seleccionado', false);
}

function desactivarUnVuelo(vuelo: { vuelo: Vuelo, pointFeature: any, lineFeature: any }) {
    vuelo.pointFeature.setStyle(vuelo.pointFeature.get('estiloAnterior'));
    vuelo.pointFeature.set('seleccionado', false);
    vuelo.lineFeature.setStyle(invisibleStyle);
}

function activarUnAeropuerto(aeropuertos: Map<string, { aeropuerto: Aeropuerto; pointFeature: any }>, aeropuertoId: string) {
    const aeropuerto = aeropuertos.get(aeropuertoId);
    if (aeropuerto) {
        aeropuerto.pointFeature.set('estiloAnterior', aeropuerto.pointFeature.getStyle());
        aeropuerto.pointFeature.set('seleccionado', true);
        aeropuerto.pointFeature.setStyle(selectedAirportStyle);
    }
}

function activarUnVuelo(vuelo: { vuelo: Vuelo, pointFeature: any, lineFeature: any }) {
    if(!vuelo.pointFeature.get('seleccionado')){
        vuelo.pointFeature.set('estiloAnterior', vuelo.pointFeature.getStyle());
        vuelo.pointFeature.setStyle(dinamicSelectedPlaneStle(vuelo));
        vuelo.lineFeature.setStyle(mulitpleSelectedLineStyle);
        vuelo.pointFeature.set('seleccionado', true);
    }
}

