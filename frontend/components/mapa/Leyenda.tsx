"use client";
import React, { useState } from "react";
import "@/styles/ComponentesLeyenda.css"
import { tiempoEntre, tiempoNumeroADiasHorasMinutos } from "@/utils/FuncionesTiempo";

interface InfoVuelosProps {
    vuelosEnTransito: {cuenta:number; porcentaje:number};
    capacidadAlmacenes: number;
    fechaHoraActual: string;
    fechaHoraSimulada: string;
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
            <div className="resumen-item">
              <span className="resumen-valor">{`${(capacidadAlmacenes * 100).toFixed(2)}%`}</span>
              <span className="resumen-etiqueta">capacidad de almacenes usada</span>
            </div>
            <div className="resumen-item">
              <span className="resumen-valor">{`${(vuelosEnTransito.porcentaje * 100).toFixed(2)}%`}</span>
              <span className="resumen-etiqueta">capacidad de vuelos usada</span>
            </div>
          </div>
          <div className="info-fecha">
            <div className="fecha-item">
              <span className="fecha-etiqueta">Fecha y hora actual</span>
              <span className="fecha-valor">{fechaHoraActual}</span>
            </div>
            {simulacion && (
              <>
                <div className="fecha-item">
                  <span className="fecha-etiqueta">Fecha y hora simulada</span>
                  <span className="fecha-valor">{fechaHoraSimulada}</span>
                </div>
                <div className="fecha-item">
                  <span className="fecha-etiqueta">Tiempo transcurrido</span>
                  <span className="fecha-valor">{tiempoNumeroADiasHorasMinutos(tiempoEntre(fechaHoraInicio, new Date(fechaHoraSimulada)))}</span>
                </div>
              </>
            )}
          </div>
          <div className="leyenda">
            <div className="leyenda-titulo">Leyenda</div>
            <div className="leyenda-item">
              <img src="/logos/vueloEnhancedBlue.png" alt="Vuelo seleccionado" className="icono-leyenda" />
              <span className="leyenda-etiqueta">Vuelo seleccionado</span>
            </div>
            <div className="leyenda-item">
              <span className="ruta-vuelo-seleccionado"></span>
              <span className="leyenda-etiqueta">Ruta de vuelo seleccionado</span>
            </div>
            <div className="leyenda-titulo">Carga</div>
            <div className="carga-titulos">
              <span className="carga-titulo">Vuelo</span>
              <span className="carga-titulo">Almacén</span>
              <span className="carga-titulo">Capacidad</span>
            </div>
            <div className="leyenda-item">
              <span className="leyenda-etiqueta">
              <img src="/logos/avionVerde.png" alt="0-30%" className="icono-leyenda" />
              </span>
              <span className="leyenda-etiqueta">
              <img src="/logos/edificioVerde.png" alt="Oficina 0-30%" className="icono-leyenda"/>
              </span>
              <span className="leyenda-etiqueta">0-33%</span>
            </div>
            <div className="leyenda-item">
            <span className="leyenda-etiqueta">
              <img src="/logos/avionAmarillo.png" alt="30-70%" className="icono-leyenda" />
              </span>
              <span className="leyenda-etiqueta">
              <img src="/logos/edificioAmarillo.png" alt="Oficina 30-70%" className="icono-leyenda" />
              </span>
              <span className="leyenda-etiqueta">33-66%</span>
            </div>
            <div className="leyenda-item">
              <span className="leyenda-etiqueta">
              <img src="/logos/avionRojo.png" alt="70-100%" className="icono-leyenda" />
              </span>
              <span className="leyenda-etiqueta">
              <img src="/logos/edificioRojo.png" alt="Oficina 70-100%" className="icono-leyenda" />
              </span>
              <span className="leyenda-etiqueta">66-100%</span>
            </div>
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
