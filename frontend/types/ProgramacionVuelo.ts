import { Paquete } from "./Paquete";

export type ProgramacionVuelo =  {
    fechaSalida: Date;
    idVuelo: number;
    cantPaquetes: number;
    paquetes: Array<Paquete>;
};