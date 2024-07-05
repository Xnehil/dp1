
export type Vuelo = BaseModel & {
    origen: string;
    destino: string;
    fechaHoraSalida: string;
    fechaHoraLlegada: string;
    capacidad: number;
    cambioDeDia: number;
    cargaPorDia: Map<string, number>;
    duracionVuelo: number;
    distanciaVuelo: number;
    //Pintar auxiliar para la primera carga
    pintarAuxiliar: boolean;
};