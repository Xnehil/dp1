export type Paquete = BaseModel & {
    codigoEnvio: string;
    fechasRuta: Array<number>;
    ruta: Array<number>;
    llegoDestino: boolean;
    tiempoRestante: number;
};