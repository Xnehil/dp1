import React from "react";
import Link from "next/link";

import IconoTarea from "@/components/IconoTarea";
import srcVuelo from '@/logos/vuelo.png'
import srcCliente from '@/logos/cliente.png'
import srcCalendario from '@/logos/calendario.png'
import srcOficinas from '@/logos/oficinas.png'
import srcPaquete from '@/logos/paquete.png'

export default function PlaneamientoYGestion() {

  return (
    <div className="flex flex-row mt-10">
      <div className='w-1/2 flex flex-col border-solid'>
        <h2 className="text-6xl  text-black-500 mt-6 mb-10 mx-auto">
          Gestionar
        </h2>
        <div className='flex flex-row justify-around p-5 mb-10'>
          <IconoTarea icon={srcOficinas} description="Oficinas" />
          <Link href={"planeamientoGestion/gestionVuelos"}>
            <IconoTarea icon={srcVuelo} description="Vuelos" />
          </Link>
        </div>
        <div className='flex flex-row justify-around p-5'>
          <IconoTarea icon={srcPaquete} description="Paquetes" />
          <IconoTarea icon={srcCliente} description="Clientes" />
        </div>
      </div>
      <div className="w-[1.5px] h-[500px] bg-gray-800 mt-32"></div>


      <div className='w-1/2 flex flex-col'>
        <h2 className="text-6xl  text-black-500 mt-6 mb-10 mx-auto">
          Planificar
        </h2>
        <div className='flex flex-row justify-around p-5 mt-10'>
          <IconoTarea icon={srcCalendario} description="Correr algoritmos de planificación" size="80" styleBg="bg-[#59C3C3]" />
        </div>
      </div>
    </div>
  );
}