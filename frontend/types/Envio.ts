import { Paquete } from "./Paquete";

export type Envio = BaseModel & {
    codigoEnvio: string;
    origen: string;
    destino: string;
    fechaHoraSalida: Date;
    fechaHoraLlegadaPrevista: Date;
    cantidadPaquetes: number;
    paquetes: Array<Paquete>;
};