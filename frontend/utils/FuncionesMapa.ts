import { Aeropuerto } from '@/types/Aeropuerto';
import { Vuelo } from '@/types/Vuelo';
import { Coordinate } from 'ol/coordinate';
import { Point, LineString } from 'ol/geom';
import { tiempoEntreAhoraYSalida } from './FuncionesTiempo';
import { fromLonLat } from 'ol/proj';
import { dinamicPlaneStyle, dinamicSelectedPlaneStle, invisibleStyle, planeStyle, selectedLineStyle, selectedPlaneStyle } from '@/components/mapa/EstilosMapa';
import { Feature } from 'ol';
import { getVectorContext } from 'ol/render';
import Icon from 'ol/style/Icon';
import Style from 'ol/style/Style';

export function updateCoordinates(aeropuertos: Map<String, Aeropuerto>, vuelos: Map<number, { vuelo: Vuelo, pointFeature: any, lineFeature: any}> | null, simulationTime: Date):
    number[]{
    let aBorrar:number[] = [];
    //Iterar por cada vuelo
    let cuenta=0;
    // let medirTiempo = new Date();
    vuelos?.forEach((item, i) => {
        const vuelo = item.vuelo;
        // console.log("vuelo: ", vuelo);
        const pointFeature = item.pointFeature;
        const lineFeature = item.lineFeature;

        // console.log("vuelo: ", vuelo);
        const line = lineFeature.getGeometry() as LineString;
        const coordinates = line.getCoordinates();
        // console.log("coordinates: ", coordinates);
        const totalDistance = vuelo.distanciaVuelo; //En km
        const speed = totalDistance / (vuelo.duracionVuelo); //En km/min

        const point = pointFeature.getGeometry() as Point;
        const destinationCoordinates = coordinates[1] as Coordinate;
        const originCoordinates = coordinates[0] as Coordinate;
        // console.log("simulationTime: ", simulationTime);
        const tiempoPasadoMinutos = tiempoEntreAhoraYSalida(vuelo, aeropuertos, simulationTime);
        // console.log("tiempoPasadoMinutos: ", tiempoPasadoMinutos);

        const distanciaRecorrida = speed * tiempoPasadoMinutos;
        const ratio = distanciaRecorrida / totalDistance;
        // console.log("distanciaRecorrida: ", distanciaRecorrida);
        // console.log("totalDistance: ", totalDistance);
        // console.log("ratio: ", ratio);
        const newCoordinates = [
            originCoordinates[0] + ratio * (destinationCoordinates[0] - originCoordinates[0]),
            originCoordinates[1] + ratio * (destinationCoordinates[1] - originCoordinates[1]),
        ] as Coordinate;

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
        // console.log("newCoordinates: ", newCoordinates);
    });
    // let medirTiempo2 = new Date();
    // console.log("Tiempo de updateCoordinates: ", medirTiempo2.getTime()-medirTiempo.getTime());
    // console.log("aBorrar: ", aBorrar);
    // console.log("Cuenta: ", cuenta, "Vuelos: ", vuelos.size);
    return aBorrar;

}

export function coordenadasIniciales(aeropuertos: Map<String, Aeropuerto>, item: any, simulationTime: Date): Point {
    const lineFeature = item.lineFeature;
    const line = lineFeature.getGeometry() as LineString;
    const coordinates = line.getCoordinates();

    // Calculate the elapsed time in minutes
    const tiempoPasadoMinutos = tiempoEntreAhoraYSalida(item.vuelo, aeropuertos, simulationTime);

    // Qué tan avanzado está el vuelo
    const ratio = tiempoPasadoMinutos / item.vuelo.duracionVuelo;

    const lonlatInicio = coordinates[0] as Coordinate;
    const lonlatFin = coordinates[1] as Coordinate;

    // Calculate the current position of the plane
    const newCoordinates = [
        lonlatInicio[0] + ratio * (lonlatFin[0] - lonlatInicio[0]),
        lonlatInicio[1] + ratio * (lonlatFin[1] - lonlatInicio[1]),
    ];
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

export function crearLineaDeVuelo(aeropuertos: Map<String, Aeropuerto>, item: any): any {
    const aeropuertoOrigen = aeropuertos.get(item.vuelo.origen);
    const aeropuertoDestino = aeropuertos.get(item.vuelo.destino);
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
    feature.setStyle(invisibleStyle);
    feature.set('vueloId', item.vuelo.id);
    return feature;
}

export function crearPuntoDeVuelo(aeropuertos: Map<String, Aeropuerto>, item: any, simulationTime: Date): any {
    const point = coordenadasIniciales(aeropuertos, item, simulationTime);
    const feature = new Feature({
        geometry: point,
    });
    feature.setStyle(dinamicPlaneStyle(item));
    feature.set('vueloId', item.vuelo.id); // Agregar el ID del vuelo
    return feature;
}

export function seleccionarVuelo(vueloId:number, setSelectedVuelo: any ,selectedFeature: any, vuelos: React.RefObject<Map<number, { vuelo: Vuelo, pointFeature: any, lineFeature: any}>>, feature: any){
    // console.log("vueloId: ", vueloId);
    // console.log("vuelos: ", vuelos.current);
    const vuelo = vuelos.current?.get(vueloId)?.vuelo;
    if (vuelo) {
        setSelectedVuelo(vuelo);
        console.log(
            `Vuelo seleccionado setteado: Vuelo ID${vuelo.id}`
        );
        if (selectedFeature.current != null) {
            selectedFeature.current.setStyle(dinamicPlaneStyle(vuelos.current?.get(selectedFeature.current.get("vueloId"))));
            vuelos.current?.get(selectedFeature.current.get("vueloId"))?.lineFeature.setStyle(invisibleStyle);
        }

        (feature as Feature).setStyle(
            dinamicSelectedPlaneStle(
                vuelos.current?.get(vueloId)
            )
        );
        selectedFeature.current = feature as Feature;

        vuelos.current?.get(vueloId)?.lineFeature.setStyle(selectedLineStyle);
    } else {
        console.error(
            `Vuelo no encontrado: Vuelo ID ${vueloId}`
        );
    }
}
