export type Cliente = BaseModel & {
    username: string;
    password: string;
    email: string;
    numeroDocumento: string;
    tipoDocumento: number;
    nombre: string;
    apellido: string;
    segundoNombre: string;
    codigoPais: number;
    telefono: string;
};