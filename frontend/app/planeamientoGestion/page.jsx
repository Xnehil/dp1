"use client";

import React, { useEffect, useState } from "react";
import Link from "next/link";
import Image from "next/image";

import IconoTarea from "@/components/IconoTarea";
import srcVuelo from '@/public/logos/vuelo.png'
import srcCliente from '@/public/logos/cliente.png'
import srcCalendario from '@/public/logos/calendario.png'
import srcOficinas from '@/public/logos/oficinas.png'
import srcPaquete from '@/public/logos/paquete.png'
import axios from "axios";
import { Button, Dialog, DialogActions, DialogContent } from "@mui/material";

export default function PlaneamientoYGestion() {
  const api_base_url = process.env.REACT_APP_API_URL_BASE;
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const [estado, setEstado] = useState(null); //1: exito, 2: error
  const [mensaje, setMensaje] = useState("");
  const [isRequesting, setIsRequesting] = useState(false);

  const handleClick = async () => {
    if (isRequesting) return;
    setIsRequesting(true);
    try {
      //Poner cursor de carga
      window.document.body.style.cursor = 'wait';

      const response = await axios.get(`${api_base_url}/aco/ejecutar/todaCiudad`);
      console.log(response.data);
      setMensaje(response.data);
      setEstado(1);
    } catch (error) {
      console.error(error);
      setEstado(2);
    } finally {
      setIsRequesting(false);
      //Quitar cursor de carga
      window.document.body.style.cursor = 'auto';
    }
  };

  const handleClose = () => {
    setIsDialogOpen(false);
    setTimeout(() => setEstado(null), 150); 
  };

  useEffect(() => {
    if (estado === 1 || estado === 2) {
      setIsDialogOpen(true);
    }
  }, [estado]);



  return (
    <div className="flex flex-row mt-2 h-screen">
      <div className='w-1/2 flex flex-col border-solid'>
        <h2 className="text-5xl  text-black-500 mt-0 mb-12 mx-auto">
          Gestionar
        </h2>
        <div className='flex flex-row justify-around p-0 mb-5'>
          <IconoTarea icon={srcOficinas} description="Oficinas" />
          <Link href={"planeamientoGestion/gestionVuelos"}>
            <IconoTarea icon={srcVuelo} description="Vuelos" />
          </Link>
        </div>
        <div className='flex flex-row justify-around p-0'>
          <Link href={"planeamientoGestion/registrarEnvio"}>
            <IconoTarea icon={srcPaquete} description="Envíos" />
          </Link>
          <IconoTarea icon={srcCliente} description="Clientes" />
        </div>
      </div>
      <div className="w-[1.5px] h-[450px] bg-gray-800 mt-16"></div>


      <div className='w-1/2 flex flex-col'>
        <h2 className="text-5xl  text-black-500 mt-0 mb-5 mx-auto">
          Planificar
        </h2>
        <div className='flex flex-row justify-around p-5 mt-14 h-1/2'>
          {/* Botón gigante "Correr algoritmos de planificación"*/}
          <div className="flex flex-col justify-center items-center">
            <div className={`bg-[#55BBBB] flex flex-col justify-center items-center rounded-3xl w-52 h-52 ${isRequesting ? 'opacity-50 cursor-not-allowed' : 'cursor-pointer'}`}
                 onClick={!isRequesting ? handleClick : undefined} // Prevent handleClick if isRequesting is true
                 style={{pointerEvents: isRequesting ? 'none' : 'auto'}}>
              <Image src={srcCalendario} className="m-8" alt="Icon" width={"135"} height={"135"} />
            </div>
            <p className="mt-2 text-4xl text-gray-700 text-center font-title max-w-[300px]">
              Correr algoritmos de planificación
            </p>
          </div>
          {/* */}
        </div>
      </div>

      <Dialog open={isDialogOpen} onClose={handleClose} fullWidth>
                <DialogContent>
                    <div className={`p-4 ${estado === 1 ? "bg-green-100 text-green-900" : "bg-red-100 text-red-900"}`}>
                        <p className='text-center font-bold pb-2'>
                            {estado === 1 ? "Planificación ejecutada correctamente" : estado === 2 ? "Error en la planificación" : ""}
                        </p>
                        <div>
                            {estado === 1 ? (
                                <div className='max-h-96 overflow-y-auto'>
                                    <p>{mensaje}</p>
                                </div>
                            ) : estado === 2 ? (
                                <div>
                                    <p>Hubo un error al ejecutar la planificación</p>
                                </div>
                            ) : null}
                        </div>
                         
                    </div>
                </DialogContent>
                <DialogActions>
                     <Button onClick={handleClose}>Aceptar</Button>
                </DialogActions>
            </Dialog>
    </div>
  );
}