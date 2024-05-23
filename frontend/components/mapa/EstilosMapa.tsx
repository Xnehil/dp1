"use client";

import Icon from 'ol/style/Icon';
import Stroke from 'ol/style/Stroke';
import Style from 'ol/style/Style';

export const airportStyle = new Style({
    image: new Icon({
        anchor: [0.5, 1],
        anchorXUnits: 'fraction',
        anchorYUnits: 'fraction',
        src: '/logos/oficinas.png',
        scale: 0.15,
    }),
});

export const planeStyle = new Style({
    image: new Icon({
        anchor: [0.5, 0.5],
        anchorXUnits: 'fraction',
        anchorYUnits: 'fraction',
        src: '/logos/vuelo.png',
        scale: 0.12,
        color: 'red',
    }),
});

// Invisible line style
export const invisibleLineStyle = new Style({
});

// Visible line style for selected line
export const selectedLineStyle = new Style({
    stroke: new Stroke({
      color: 'red', // Change color to make the selected line distinguishable
      lineDash: [4],
      width: 2, // Increase width for the selected line
    }),
});