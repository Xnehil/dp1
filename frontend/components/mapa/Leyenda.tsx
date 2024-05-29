"use client";
import React, { useState } from "react";
import "@/styles/ComponentesLeyenda.css"

interface InfoVuelosProps {
    vuelosEnTransito: number;
    enviosEnElAire: number;
    fechaHoraActual: string;
    fechaHoraSimulada: string;
  }
  
  const InfoVuelos: React.FC<InfoVuelosProps> = ({ 
    vuelosEnTransito, 
    enviosEnElAire, 
    fechaHoraActual, 
    fechaHoraSimulada 
  }) => {
    const [visible, setVisible] = useState<boolean>(false);
  
    const toggleVisibility = () => {
      setVisible(!visible);
    };
  
    return (
      <div className="info-vuelos-wrapper">
        <div className={`info-vuelos-contenedor ${visible ? 'visible' : 'hidden'}`}>
          <div className="resumen-vuelos">
            <div className="resumen-item">
              <span className="resumen-valor">{vuelosEnTransito}</span>
              <span className="resumen-etiqueta">vuelos en tránsito</span>
            </div>
            <div className="resumen-item">
              <span className="resumen-valor">{enviosEnElAire}</span>
              <span className="resumen-etiqueta">envíos en el aire</span>
            </div>
          </div>
          <div className="info-fecha">
            <div className="fecha-item">
              <span className="fecha-etiqueta">Fecha y hora actual</span>
              <span className="fecha-valor">{fechaHoraActual}</span>
            </div>
            <div className="fecha-item">
              <span className="fecha-etiqueta">Fecha y hora simulada</span>
              <span className="fecha-valor">{fechaHoraSimulada}</span>
            </div>
          </div>
          <div className="leyenda">
            <span className="leyenda-titulo">Leyenda</span>
            <div className="leyenda-item">
              <img src="/logos/vuelo.png" alt="Vuelo seleccionado" className="icono-leyenda vuelo-seleccionado" />
              <span className="leyenda-etiqueta">Vuelo seleccionado</span>
            </div>
            <div className="leyenda-item">
              <span className="ruta-vuelo-seleccionado"></span>
              <span className="leyenda-etiqueta">Ruta de vuelo seleccionado</span>
            </div>
            <div className="leyenda-titulo">Carga</div>
            <div className="carga-titulos">
              <span className="carga-titulo">Vuelos</span>
              <span className="carga-titulo">Oficinas</span>
              <span className="carga-titulo">Capacidad</span>
            </div>
            <div className="leyenda-item">
              <img src="/logos/vuelo.png" alt="0-30%" className="icono-leyenda" style={{ backgroundColor: '#24A148' }} />
              <img src="/logos/oficinas.png" alt="Oficina 0-30%" className="icono-leyenda" style={{ backgroundColor: '#166A08' }} />
              <span className="leyenda-etiqueta">0-30%</span>
            </div>
            <div className="leyenda-item">
              <img src="/logos/vuelo.png" alt="30-70%" className="icono-leyenda" style={{ backgroundColor: '#DBB42B' }} />
              <img src="/logos/oficinas.png" alt="Oficina 30-70%" className="icono-leyenda" style={{ backgroundColor: '#B0A114' }} />
              <span className="leyenda-etiqueta">30-70%</span>
            </div>
            <div className="leyenda-item">
              <img src="/logos/vuelo.png" alt="70-100%" className="icono-leyenda" style={{ backgroundColor: '#B70B14' }} />
              <img src="/logos/oficinas.png" alt="Oficina 70-100%" className="icono-leyenda" style={{ backgroundColor: '#B4425D' }} />
              <span className="leyenda-etiqueta">70-100%</span>
            </div>
          </div>
        </div>
        <button className="toggle-button" onClick={toggleVisibility}>
          {visible ? '◀' : '▶'}
        </button>
      </div>
    );
  };
  
  export default InfoVuelos;
