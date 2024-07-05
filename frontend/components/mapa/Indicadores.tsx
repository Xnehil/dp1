"use client";
import React, { useState } from "react";
import "@/styles/ComponentesLeyenda.css"
import { tiempoEntre, tiempoNumeroADiasHorasMinutos } from "@/utils/FuncionesTiempo";

interface InfoVuelosProps {
    vuelosEnTransito: {cuenta:number; porcentaje:number};
    capacidadAlmacenes: number;
    fechaHoraActual: string;
    fechaHoraSimulada: Date;
    fechaHoraInicio: Date;
    simulacion?: boolean;
  }
  
  const InfoVuelos: React.FC<InfoVuelosProps> = ({ 
    vuelosEnTransito, 
    capacidadAlmacenes, 
    fechaHoraActual, 
    fechaHoraSimulada ,
    fechaHoraInicio ,
    simulacion = false
  }) => {
    const [visible, setVisible] = useState<boolean>(false);
  
    const toggleVisibility = () => {
      setVisible(!visible);
    };
  
    return (
      <div className="info-vuelos-wrapper">
        <button className="toggle-button" onClick={toggleVisibility}>
          {visible ? '◀' : '▶'}
        </button>
        <div className={`info-vuelos-contenedor ${visible ? 'visible' : 'hidden'}`}>
          <div className="resumen-vuelos">
            <div className="resumen-item">
              <span className="resumen-valor">{formatearCantidad(vuelosEnTransito.cuenta)}</span>
              <span className="resumen-etiqueta">vuelos en tránsito</span>
            </div>
            <div className="column">
              <div className="resumen-item">
                <span className="resumen-valor">{`${(capacidadAlmacenes * 100).toFixed(2)}%`}</span>
                <span className="resumen-etiqueta">capacidad de almacenes usada</span>
              </div>
              <div className="resumen-item">
                <span className="resumen-valor">{`${(vuelosEnTransito.porcentaje * 100).toFixed(2)}%`}</span>
                <span className="resumen-etiqueta">capacidad de vuelos usada</span>
              </div>
            </div>
          </div>
          <hr />
          <div className="info-fecha">
            <div className="fecha-item">
              <span className="fecha-etiqueta">Fecha y hora actual</span>
              <span className="fecha-valor">{fechaHoraActual}</span>
            </div>
            {simulacion && (
              <>
                <div className="fecha-item">
                  <span className="fecha-etiqueta">Fecha y hora simulada</span>
                  <span className="fecha-valor">{fechaHoraSimulada.toLocaleString()}</span>
                </div>
                <div className="fecha-item">
                  <span className="fecha-etiqueta">Tiempo transcurrido</span>
                  <span className="fecha-valor">{tiempoNumeroADiasHorasMinutos(tiempoEntre(fechaHoraInicio, fechaHoraSimulada))}</span>
                </div>
              </>
            )}
          </div>
        </div>
      </div>
    );
  };

  function formatearCantidad(vuelosEnElAire:number){
    if (vuelosEnElAire < 0){
      return "0";
    }
    else {
      return vuelosEnElAire;
    }
  }

  
  export default InfoVuelos;
