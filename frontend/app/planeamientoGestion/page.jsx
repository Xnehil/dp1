import React from "react";
import Link from "next/link";
import Image from "next/image";

import IconoTarea from "@/components/IconoTarea";
import srcVuelo from '@/public/logos/vuelo.png'
import srcCliente from '@/public/logos/cliente.png'
import srcCalendario from '@/public/logos/calendario.png'
import srcOficinas from '@/public/logos/oficinas.png'
import srcPaquete from '@/public/logos/paquete.png'

export default function PlaneamientoYGestion() {

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
          <IconoTarea icon={srcPaquete} description="Paquetes" />
          <IconoTarea icon={srcCliente} description="Clientes" />
        </div>
      </div>
      <div className="w-[1.5px] h-[450px] bg-gray-800 mt-16"></div>


      <div className='w-1/2 flex flex-col'>
        <h2 className="text-5xl  text-black-500 mt-0 mb-5 mx-auto">
          Planificar
        </h2>
        <div className='flex flex-row justify-around p-5 mt-14'>
          {/* Botón gigante "Correr algoritmos de planificación"*/}
          <div className="flex flex-col justify-center items-center">
            <div className={`bg-[#55BBBB] flex flex-col justify-center items-center rounded-3xl w-52 h-52`}>
              <Image src={srcCalendario} className="m-8" alt="Icon" width={"135"} height={"135"} />
            </div>
            <p className="text-center text-2xl text-gray-700 mt-4 w-52">Correr algoritmos de planificación</p>
          </div>
          {/* */}
        </div>
      </div>
    </div>
  );
}