"use client";
import React, { useState } from "react";
import "@/styles/ComponentesDatosVuelo.css";

const DatosVuelo: React.FC = () => {
  const [visible, setVisible] = useState<boolean>(false);

  const toggleVisibility = () => {
    setVisible(!visible);
  };

  return (
    <div className="datos-vuelo-wrapper">
      <button className="toggle-button-datos-vuelo" onClick={toggleVisibility}>
        {visible ? '▼' : '▲'}
      </button>
      <div className={`datos-vuelo-contenedor ${visible ? 'visible' : 'hidden'}`}>
        <div className="datos-vuelo-header">
          <img src="/logos/vuelo.png" alt="Avión" className="icono-vuelo" />
          <div className="datos-vuelo-info">
            <h2 className="vuelo-codigo">Vuelo SAN4312</h2>
            <p className="vuelo-horario">Salida: San Juan - 18:00</p>
            <p className="vuelo-horario">Llegada: Madrid - 21:00</p>
          </div>
          <div className="datos-vuelo-capacidad">
            <h2>450 Paquetes</h2>
            <p>70% lleno</p>
            <img src="/logos/vuelo.png" alt="Paquete" className="icono-paquete" />
          </div>
        </div>
        <div className="datos-vuelo-content">
          <div className="datos-vuelo-busqueda">
            <input type="text" placeholder="Ingrese código paquete, ciudad o ID de envío" className="input-busqueda" />
            <button className="boton-busqueda">Buscar</button>
          </div>
          <div className="datos-vuelo-tabla">
            <table>
              <thead>
                <tr>
                  <th>Código</th>
                  <th>Tiempo restante</th>
                  <th>Origen</th>
                  <th>Destino</th>
                  <th>ID envío</th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td>120356</td>
                  <td>18:00</td>
                  <td>Lima</td>
                  <td>Santiago</td>
                  <td>450</td>
                </tr>
                <tr>
                  <td>121356</td>
                  <td>12:00</td>
                  <td>Lima</td>
                  <td>Santiago</td>
                  <td>450</td>
                </tr>
                <tr>
                  <td>220252</td>
                  <td>11:00</td>
                  <td>Lima</td>
                  <td>Santiago</td>
                  <td>250</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  );
};

export default DatosVuelo;