"use client";

import { Vuelo } from '@/types/Vuelo';
import Icon from 'ol/style/Icon';
import Stroke from 'ol/style/Stroke';
import Style from 'ol/style/Style';

let airportStyle: Style;
let selectedAirportStyle: Style;
let planeStyle: Style;
let selectedPlaneStyle: Style;
let greenAirportStyle: Style;
let yellowAirportStyle: Style;
let redAirportStyle: Style;

if(typeof window !== 'undefined') {
    airportStyle = new Style({
        image: new Icon({
            anchor: [0.5, 1],
            anchorXUnits: 'fraction',
            anchorYUnits: 'fraction',
            src: '/logos/oficinasEnhanced.png',
            scale: 0.15,
        }),
    });

    greenAirportStyle = new Style({
        image: new Icon({
            anchor: [0.5, 1],
            anchorXUnits: 'fraction',
            anchorYUnits: 'fraction',
            src: '/logos/edificioVerde.png',
            scale: 0.15,
        }),
    });

    yellowAirportStyle = new Style({
        image: new Icon({
            anchor: [0.5, 1],
            anchorXUnits: 'fraction',
            anchorYUnits: 'fraction',
            src: '/logos/edificioAmarillo.png',
            scale: 0.15,
        }),
    });

    redAirportStyle = new Style({
        image: new Icon({
            anchor: [0.5, 1],
            anchorXUnits: 'fraction',
            anchorYUnits: 'fraction',
            src: '/logos/edificioRojo.png',
            scale: 0.15,
        }),
    });

    selectedAirportStyle = new Style({
        image: new Icon({
            anchor: [0.5, 1],
            anchorXUnits: 'fraction',
            anchorYUnits: 'fraction',
            src: '/logos/oficinasEnhancedBlue.png',
            scale: 0.20,
        }),
    });

    planeStyle = new Style({
        image: new Icon({
            anchor: [0.5, 0.5],
            anchorXUnits: 'fraction',
            anchorYUnits: 'fraction',
            src: '/logos/vueloEnhanced.png', //mas grande para aumentar el area de click
            scale: 0.20,
            //color: 'red', //por alguna razon, si asignas un color cualquiera, mostrara solo en negro
        }),
    });

    selectedPlaneStyle = new Style({
        image: new Icon({
            anchor: [0.5, 0.5],
            anchorXUnits: 'fraction',
            anchorYUnits: 'fraction',
            src: '/logos/vueloEnhancedBlue.png',
            scale: 0.30,
        }),
    });
}

export function dinamicPlaneStyle(item: { vuelo: Vuelo, pointFeature: any, lineFeature: any} | undefined, angulo: number | null){
    if(item==undefined) return planeStyle;
    let auxAngulo=0;
    if (angulo==null) auxAngulo=calcularAngulo(item);
    else auxAngulo=angulo;
    return new Style({
        image: new Icon({
            anchor: [0.5, 0.5],
            anchorXUnits: 'fraction',
            anchorYUnits: 'fraction',
            src: '/logos/vueloEnhanced.png', //mas grande para aumentar el area de click
            scale: 0.17,
            color: 'red', //por alguna razon, si asignas un color cualquiera, mostrara solo en negro
            rotation: auxAngulo
        }),
    });
}

export function dinamicSelectedPlaneStle(item: {vuelo:Vuelo, pointFeature:any, lineFeature:any}| undefined){
    if(item==undefined) return selectedPlaneStyle;
    const angulo: number=item.pointFeature.get("angulo");
    return new Style({
        image: new Icon({
            anchor: [0.5, 0.5],
            anchorXUnits: 'fraction',
            anchorYUnits: 'fraction',
            src: '/logos/vueloEnhancedBlue.png',
            scale: 0.30,
            rotation: angulo
        }),
    });

}

export function greenPlaneStyle(item: {vuelo:Vuelo, pointFeature:any, lineFeature:any}| undefined, angulo:number | null){
    if(item==undefined) return selectedPlaneStyle;
    let auxAngulo=0;
    if (angulo==null) auxAngulo=item.pointFeature.get("angulo");
    else auxAngulo=angulo;
    return new Style({
        image: new Icon({
            anchor: [0.5, 0.5],
            anchorXUnits: 'fraction',
            anchorYUnits: 'fraction',
            src: '/logos/avionVerde.png',
            scale: 0.17,
            rotation: auxAngulo,
        }),
    });
}

export function yellowPlaneStyle(item: {vuelo:Vuelo, pointFeature:any, lineFeature:any}| undefined, angulo: number | null){
    if(item==undefined) return selectedPlaneStyle;
    let auxAngulo=0;
    if (angulo==null) auxAngulo=item.pointFeature.get("angulo");
    else auxAngulo=angulo;
    return new Style({
        image: new Icon({
            anchor: [0.5, 0.5],
            anchorXUnits: 'fraction',
            anchorYUnits: 'fraction',
            src: '/logos/avionAmarillo.png',
            scale: 0.17,
            rotation: auxAngulo,
        }),
    });
}

export function redPlaneStyle(item: {vuelo:Vuelo, pointFeature:any, lineFeature:any}| undefined, angulo: number | null){
    if(item==undefined) return selectedPlaneStyle;
    let auxAngulo=0;
    if (angulo==null) auxAngulo=item.pointFeature.get("angulo");
    else auxAngulo=angulo;
    return new Style({
        image: new Icon({
            anchor: [0.5, 0.5],
            anchorXUnits: 'fraction',
            anchorYUnits: 'fraction',
            src: '/logos/avionRojo.png',
            scale: 0.17,
            rotation: auxAngulo,
        }),
    });
}


export function calcularAngulo(item: {vuelo:Vuelo, pointFeature:any, lineFeature:any} , rotacion:number=-Math.PI/4){
    const coordinates = item.lineFeature.getGeometry().getCoordinates();
    const start = coordinates[0];
    const end = coordinates[1];
    const dx = end[0] - start[0];
    const dy = end[1] - start[1];
    return Math.atan2(-dy, dx) -   rotacion;
}


export { airportStyle, selectedAirportStyle, planeStyle, selectedPlaneStyle, greenAirportStyle, yellowAirportStyle, redAirportStyle };

// Invisible line style
export const invisibleStyle = new Style({
});

// Visible line style for selected line
export const selectedLineStyle = new Style({
    stroke: new Stroke({
      color: 'red', // Change color to make the selected line distinguishable
      lineDash: [4],
      width: 2, // Increase width for the selected line
    }),
});