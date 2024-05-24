import { Aeropuerto } from '@/types/Aeropuerto';
import { Vuelo } from '@/types/Vuelo';
import { Coordinate } from 'ol/coordinate';
import { Point, LineString } from 'ol/geom';
import { tiempoEntreAhoraYSalida } from './FuncionesTiempo';
import { fromLonLat } from 'ol/proj';
import { invisibleStyle } from '@/components/mapa/EstilosMapa';

export function updateCoordinates(aeropuertos: Map<String, Aeropuerto>, vuelos: Vuelo[], pointFeatures: any[], lineFeatures: any[], simulationTime: Date):
    number[]{
    let aBorrar:number[] = [];
    for (let i = 0; i < pointFeatures.length; i++) {
        const vuelo = vuelos[i];
        // console.log("vuelo: ", vuelo);
        const pointFeature = pointFeatures[i];
        const lineFeature = lineFeatures[i];


        const line = lineFeature.getGeometry() as LineString;
        const coordinates = line.getCoordinates();
        // console.log("coordinates: ", coordinates);
        const totalDistance = vuelo.distanciaVuelo; //En km
        const speed = totalDistance / (vuelo.duracionVuelo); //En km/min

        const point = pointFeature.getGeometry() as Point;
        const destinationCoordinates = coordinates[1] as Coordinate;
        const originCoordinates = coordinates[0] as Coordinate;
        // console.log("currentCoordinate: ", currentCoordinate);
        // console.log("simulationTime: ", simulationTime);
        const tiempoPasadoMinutos = tiempoEntreAhoraYSalida(vuelo, aeropuertos, simulationTime);

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
        // console.log("newCoordinates: ", newCoordinates);
    }
    // console.log("aBorrar: ", aBorrar);
    return aBorrar;

}

export function coordenadasIniciales(vuelo: Vuelo, aeropuertos: Map<String, Aeropuerto>, lineFeatures: any[], simulationTime: Date, index:number): Point {
    const lineFeature = lineFeatures[index];
    const line = lineFeature.getGeometry() as LineString;
    const coordinates = line.getCoordinates();

    // Calculate the elapsed time in minutes
    const tiempoPasadoMinutos = tiempoEntreAhoraYSalida(vuelo, aeropuertos, simulationTime);

    // Qué tan avanzado está el vuelo
    const ratio = tiempoPasadoMinutos / vuelo.duracionVuelo;

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
