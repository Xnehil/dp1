"use client";

import Icon from 'ol/style/Icon';
import Stroke from 'ol/style/Stroke';
import Style from 'ol/style/Style';

let airportStyle: Style;
let planeStyle: Style;
let selectedPlaneStyle: Style;

if(typeof window !== 'undefined') {
    airportStyle = new Style({
        image: new Icon({
            anchor: [0.5, 1],
            anchorXUnits: 'fraction',
            anchorYUnits: 'fraction',
            src: '/logos/oficinas.png',
            scale: 0.15,
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


export { airportStyle, planeStyle, selectedPlaneStyle};

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