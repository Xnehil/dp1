export type Aeropuerto = BaseModel & {
    codigoOACI: string;
    ciudad: string;
    pais: string;
    paisCorto: string;
    continente: string;
    gmt: number;
    zoneId: string; //
    capacidadMaxima: number;
    zonaHoraria: string;
    latitud: number;
    longitud: number;
};