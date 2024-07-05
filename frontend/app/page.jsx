"use client";

import React, { useEffect, useState } from "react";
import Link from "next/link";

import IconoTarea from "@/components/IconoTarea";

import srcAjustes from "@/public/logos/ajustes.png";
import srcEstadisticas from "@/public/logos/estadisticas.png";
import srcRegistroEnvio from "@/public/logos/registroEnvio.png";
import srcRastreo from "@/public/logos/rastreo.png";
import srcPlaneamiento from "@/public/logos/planeamientoGestion.png";
import srcSimulacion from "@/public/logos/simulacionEnvios.png";
import srcVuelosEnVivo from "@/public/logos/vuelosEnVivo.png";
import TextField from '@mui/material/TextField';


export default function Home() {
    return (
        <main className=" mb-8">
            <div className="w-1/2 mx-auto flex items-center px-3 rounded-2xl">
                <h1 className="text-6xl  text-black-500 px-4 text-center mt-0 mb-10 font-title">
                    Bienvenid@ al sistema de gestión de envíos de RedEx
                </h1>
            </div>
            <section className="w-4/5 mx-auto">
                <div className="flex flex-row justify-between p-5">
                    <Link href={"/vuelosEnVivo"} className="h-full">
                        <IconoTarea
                            icon={srcVuelosEnVivo}
                            description="Vuelos en vivo"
                            size="220"
                        />
                    </Link>
                    <Link href={"/simulacion"} className="h-full">
                        <IconoTarea
                            icon={srcSimulacion}
                            description="Simulación de envíos"
                            size="220"
                        />
                    </Link>
                    <Link href={"/rastreo"} className="h-full">
                        <IconoTarea
                            icon={srcRastreo}
                            description="Rastreo" />
                    </Link>

                </div>

                <div className="flex flex-row justify-around p-5">
                    <Link href={"/planeamientoGestion"}>
                        <IconoTarea
                            icon={srcPlaneamiento}
                            description="Planeamiento y gestión"
                        />
                    </Link>
                    <Link href={"/registroEnvios"} className="h-full">
                        <IconoTarea
                            icon={srcRegistroEnvio}
                            description="Registrar envío"
                            size="220"
                        />
                    </Link>

                </div>
            </section>
        </main>
    );
}
