"use client";

import Link from "next/link";
import React, { useState } from "react";
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";
import { FaCalendarAlt } from "react-icons/fa";

function ConfiguracionSimulacion({ buttonText, activeTab }) {
    const [startDate, setStartDate] = useState(new Date("2024-01-12T05:36:52"));
    return (
        <div className="flex flex-col items-center mt-10 w-full">
            <div className="w-full max-w-4xl px-10">
                <h2 className="text-3xl mb-2 text-[#161616] text-left">
                    Configuración
                </h2>
                <p className="mb-4 text-[#525252] text-left">
                    Elige los parámetros para la simulación
                </p>
                <div className="border rounded-md p-4 mb-4 bg-gray-100">
                    <label className="block mb-2 text-left">
                        Fecha de inicio (dd/mm/yyyy - hh:mm:ss)
                    </label>
                    <div className="flex items-center border rounded-md bg-white">
                        <DatePicker
                            selected={startDate}
                            onChange={(date) => setStartDate(date)}
                            showTimeSelect
                            dateFormat="dd/MM/yyyy - HH:mm:ss"
                            className="flex-grow p-2 text-left outline-none bg-transparent text-lg"
                        />
                        <div
                            className="p-2 cursor-pointer flex-shrink-0"
                            onClick={() =>
                                document
                                    .querySelector(
                                        ".react-datepicker-wrapper input"
                                    )
                                    .focus()
                            }
                        >
                            <FaCalendarAlt className="text-lg" />
                        </div>
                    </div>
                </div>
                <Link href={`/simulacion/${activeTab}?startDate=${startDate}`} className="w-full">
                    <button className="bg-[#52489c] text-white py-2 w-full rounded-md text-lg">
                        {buttonText}
                    </button>
                </Link>
            </div>
        </div>
    );
}

export default function SimulacionPage() {
    const [activeTab, setActiveTab] = useState("semanal");

    return (
        <div className="flex flex-col items-center mt-10 w-full">
            <div className="w-full max-w-4xl">
                <div className="flex justify-around mb-4 border-b-2 border-gray-200">
                    <button
                        className={`py-2 px-4 ${
                            activeTab === "semanal"
                                ? "border-b-2 border-[#52489c] font-bold"
                                : "font-normal"
                        }`}
                        onClick={() => setActiveTab("semanal")}
                    >
                        Simulación semanal
                    </button>
                    <button
                        className={`py-2 px-4 ${
                            activeTab === "colapso"
                                ? "border-b-2 border-[#52489c] font-bold"
                                : "font-normal"
                        }`}
                        onClick={() => setActiveTab("colapso")}
                    >
                        Simulación hasta el colapso
                    </button>
                </div>
                {activeTab === "semanal" ? (
                    <ConfiguracionSimulacion
                        buttonText="Simulación semanal"
                        activeTab={activeTab}
                    />
                ) : (
                    <ConfiguracionSimulacion
                        buttonText="Simulación hasta el colapso"
                        activeTab={activeTab}
                    />
                )}
            </div>
        </div>
    );
}
