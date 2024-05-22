import { Aeropuerto } from '@/types/Aeropuerto';
import { Vuelo } from '@/types/Vuelo';
import { Coordinate } from 'ol/coordinate';
import { Point, LineString } from 'ol/geom';
import { tiempoEntreAhoraYSalida } from './FuncionesTiempo';
import { fromLonLat } from 'ol/proj';

export function updateCoordinates(aeropuertos: Map<String, Aeropuerto>, vuelos: Vuelo[], pointFeatures: any[], lineFeatures: any[], velocidadParaSimulacion: number) {
    for (let i = 0; i < pointFeatures.length; i++) {
        const vuelo = vuelos[i];
        const pointFeature = pointFeatures[i];
        const lineFeature = lineFeatures[i];


        const line = lineFeature.getGeometry() as LineString;
        const coordinates = line.getCoordinates();
        const totalDistance = vuelo.distancia; //En km
        const speed = totalDistance / vuelo.duracion; //En km/min

        const point = pointFeature.getGeometry() as Point;
        const currentCoordinate = point.getCoordinates();
        const nextCoordinate = coordinates[1];

        const tiempoPasadoMinutos = tiempoEntreAhoraYSalida(vuelo, aeropuertos);

        const distanciaRecorrida = speed * tiempoPasadoMinutos;
        const ratio = distanciaRecorrida / totalDistance;

        const newCoordinates = [
            currentCoordinate[0] + ratio * (nextCoordinate[0] - currentCoordinate[0]),
            currentCoordinate[1] + ratio * (nextCoordinate[1] - currentCoordinate[1]),
        ] as Coordinate;
        point.setCoordinates(newCoordinates);
    }
}

export function coordenadasIniciales(vuelo: Vuelo, aeropuertos: Map<String, Aeropuerto>): Coordinate {
    const aeropuertoOrigen = aeropuertos.get(vuelo.origen);
    const aeropuertoDestino = aeropuertos.get(vuelo.destino);
    const lonlatInicio = [aeropuertoOrigen?.longitud ?? 0, aeropuertoOrigen?.latitud ?? 0];
    const lonlatFin = [aeropuertoDestino?.longitud ?? 0, aeropuertoDestino?.latitud ?? 0];

    const distanciaRecorrida = (vuelo.distancia  / vuelo.duracion) * tiempoEntreAhoraYSalida(vuelo, aeropuertos); //Velocidad * tiempo transacurrido
    const ratio = distanciaRecorrida / vuelo.distancia;

    console.log(distanciaRecorrida);
    console.log(vuelo);
    const newCoordinates = [
        lonlatInicio[0] + ratio * (lonlatFin[0] - lonlatInicio[0]),
        lonlatInicio[1] + ratio * (lonlatFin[1] - lonlatInicio[1]),
    ];
    console.log(newCoordinates);
    return newCoordinates;
}