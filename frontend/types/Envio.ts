import { Paquete } from "./Paquete";

export type Envio = BaseModel & {
    codigoEnvio: string;
    origen: string;
    destino: string;
    fechaHoraSalida: number;
    fechaHoraLlegadaPrevista: number;
    cantidadPaquetes: number;
    paquetes: Array<Paquete>;
};