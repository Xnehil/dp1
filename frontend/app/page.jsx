"use client";

import React, { useEffect, useState } from 'react';
import IconoTarea from '@/components/IconoTarea'

import srcAjustes from '@/logos/ajustes.png'
import srcEstadisticas from '@/logos/estadisticas.png'
import srcPlaneamiento from '@/logos/planeamientoGestion.png'
import srcSimulacion from '@/logos/simulacionEnvios.png'
import srcVuelosEnVivo from '@/logos/vuelosEnVivo.png'
import Link from 'next/link';

export default function Home() {
  return (
    <main>
      <div className='w-3/5 mx-auto flex items-center px-3 rounded-2xl'>
        <h1 className="text-4xl  text-black-500 px-4 text-center mt-0 mb-10">
          Bienvenid@ al sistema de gestión de envíos de RedEx
        </h1>
      </div>
      <section className='w-4/5 mx-auto'>
        <div className='flex flex-row justify-between p-5'>
          <IconoTarea icon={srcVuelosEnVivo} description="Vuelos en vivo" />
          <IconoTarea icon={srcSimulacion} description="Simulación de envíos" />
          <IconoTarea icon={srcAjustes} description="Ajustes" />
        </div>
        <div className='flex flex-row justify-around p-5'>
          <Link href={"/planeamientoGestion"}>
            <IconoTarea icon={srcPlaneamiento} description="Planeamiento y gestión" />
          </Link>
          <IconoTarea icon={srcEstadisticas} description="Estadísticas" />
        </div>
      </section>

    </main>
  );
}