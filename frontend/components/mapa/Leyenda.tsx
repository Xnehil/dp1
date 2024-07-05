"use client";
import React, { useState } from "react";
import "@/styles/ComponentesLeyenda.css"
import { tiempoEntre, tiempoNumeroADiasHorasMinutos } from "@/utils/FuncionesTiempo";


const Leyenda: React.FC = () => {
  const [visible, setVisible] = useState<boolean>(false);

  const toggleVisibility = () => {
    setVisible(!visible);
  };

  return (
    <div className="leyenda-wrapper">
      <button className="toggle-button" onClick={toggleVisibility}>
        {visible ? 'L' : 'L'}
      </button>
      <div className={`leyenda-contenedor ${visible ? 'visible' : 'hidden'}`}>
        <hr />
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
          <hr />
          <div className="leyenda-titulo">Carga</div>
          <div className="carga-titulos">
            <span className="carga-titulo">Vuelo</span>
            <span className="carga-titulo">Almacén</span>
            <span className="carga-titulo">Capacidad</span>
          </div>
          <div className="leyenda-item">
            <span className="leyenda-etiqueta">
              <img src="/logos/avionVerde.png" alt="0-33%" className="icono-leyenda" />
            </span>
            <span className="leyenda-etiqueta">
              <img src="/logos/edificioVerde.png" alt="Oficina 0-33%" className="icono-leyenda"/>
            </span>
            <span className="leyenda-etiqueta">0-33%</span>
          </div>
          <div className="leyenda-item">
            <span className="leyenda-etiqueta">
              <img src="/logos/avionAmarillo.png" alt="33-66%" className="icono-leyenda" />
            </span>
            <span className="leyenda-etiqueta">
              <img src="/logos/edificioAmarillo.png" alt="Oficina 33-66%" className="icono-leyenda" />
            </span>
            <span className="leyenda-etiqueta">33-66%</span>
          </div>
          <div className="leyenda-item">
            <span className="leyenda-etiqueta">
              <img src="/logos/avionRojo.png" alt="66-100%" className="icono-leyenda" />
            </span>
            <span className="leyenda-etiqueta">
              <img src="/logos/edificioRojo.png" alt="Oficina 66-100%" className="icono-leyenda" />
            </span>
            <span className="leyenda-etiqueta">66-100%</span>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Leyenda;
