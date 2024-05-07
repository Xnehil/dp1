"use client";

import React, { useEffect, useState } from 'react';
import IconoTarea from '@/components/IconoTarea'

import srcAjustes from '@/logos/ajustes.png'
import srcEstadisticas from '@/logos/estadisticas.png'
import srcPlaneamiento from '@/logos/planeamientoGestion.png'
import srcSimulacion from '@/logos/simulacionEnvios.png'
import srcVuelosEnVivo from '@/logos/vuelosEnVivo.png'


export default function Home() {
  return (
    <main>
      <div className='w-3/5 mx-auto flex items-center px-3 rounded-2xl'>
        <div className="text-5xl  text-black-500 px-4 text-center mt-6 mb-10">
          Bienvenid@ al sistema de gestión de envíos de RedEx
        </div>
      </div>
      <div className='flex justify-between'>
        <IconoTarea icon={srcVuelosEnVivo} description="Vuelos en vivo" />
        <IconoTarea icon={srcSimulacion} description="Simulación de envíos" />
        <IconoTarea icon={srcAjustes} description="Ajustes" />
      </div>
      <div className='flex justify-around mx-auto'>
        <IconoTarea icon={srcPlaneamiento} description="Planeamiento y gestión" />
        <IconoTarea icon={srcEstadisticas} description="Estadísticas" />
      </div>

    </main>
  );
}