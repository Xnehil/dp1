
export type Vuelo = BaseModel & {
    origen: string;
    destino: string;
    fechaHoraSalida: Date;
    fechaHoraLlegada: Date;
    capacidad: number;
    cambioDeDia: boolean;
    cargaPorDia: Map<string, number>;
    duracion: number;
    distancia: number;
};