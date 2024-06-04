"use client";
import React, { useState, useRef, useEffect } from "react";
import "@/styles/ComponentesDatosVuelo.css";
import { Vuelo } from "@/types/Vuelo";
import { Aeropuerto } from "@/types/Aeropuerto";

type DatosVueloProps = {
  vuelo: Vuelo | null;
  aeropuerto: Aeropuerto | null;
};

const DatosVuelo: React.FC<DatosVueloProps> = ({ vuelo, aeropuerto }) => {
  const [visible, setVisible] = useState<boolean>(false);
  const [opcion, setOpcion] = useState<number>(0);

  const toggleVisibility = () => {
    setVisible(!visible);
  };

  useEffect(() => {
    if(vuelo == null) return;
    setOpcion(1);
  }, [vuelo]);

  useEffect(() => {
    if(aeropuerto == null) return;
    setOpcion(2);
  }, [aeropuerto]);

  return (
    <div className="datos-vuelo-wrapper">
      <button className="toggle-button-datos-vuelo" onClick={toggleVisibility}>
        {visible ? "▼" : "▲"}
      </button>
      <div
        className={`datos-vuelo-contenedor ${visible ? "visible" : "hidden"}`}
      >
        {vuelo ? (
          <>
            <div className="datos-vuelo-header">
              <img src="/logos/vueloEnhancedBlue.png" alt="Avión" className="icono-vuelo" />
              <div className="datos-vuelo-info">
                <h2 className="vuelo-codigo">Vuelo {vuelo.id}</h2>
                <p className="vuelo-horario">
                  Salida: {vuelo.origen} -{" "}
                  {new Date(vuelo.fechaHoraSalida).toLocaleTimeString()}
                </p>
                <p className="vuelo-horario">
                  Llegada: {vuelo.destino} -{" "}
                  {new Date(vuelo.fechaHoraLlegada).toLocaleTimeString()}
                </p>
              </div>
              <div className="datos-vuelo-capacidad">
                <h2>Cap: {vuelo.capacidad} Paquetes</h2>
                <p>
                  {Math.round((vuelo.cargaPorDia.size / vuelo.capacidad) * 100)}
                  % lleno
                </p>
                <img
                  src="/logos/vuelo.png"
                  alt="Paquete"
                  className="icono-paquete"
                />
              </div>
            </div>
            <div className="datos-vuelo-content">
              <div className="datos-vuelo-busqueda">
                <input
                  type="text"
                  placeholder="Ingrese código paquete, ciudad o ID de envío"
                  className="input-busqueda"
                />
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
          </>
        ) : aeropuerto ? (
          <>
            <div className="datos-vuelo-header">
              <img src="/logos/oficinasEnhancedBlue.png" alt="Oficina" className="icono-vuelo" />
              <div className="datos-vuelo-info">
                <h2 className="vuelo-codigo">Almacén {aeropuerto.pais} - {aeropuerto.codigoOACI}</h2>
                <p className="vuelo-horario">
                  Hora local: {" "}
                  {new Date(aeropuerto.updateDate).toLocaleTimeString()}
                </p>
              </div>
              <div className="datos-vuelo-capacidad">
                <h2>Cap Max: {aeropuerto.capacidadMaxima} Paquetes</h2>
                <p>
                  {Math.round((1) * 100)}
                  % lleno
                </p>
                <img
                  src="/logos/oficinas.png"
                  alt="Paquete"
                  className="icono-paquete"
                />
              </div>
            </div>
            <div className="datos-vuelo-content">
              <div className="datos-vuelo-busqueda">
                <input
                  type="text"
                  placeholder="Ingrese código paquete, ciudad o ID de envío"
                  className="input-busqueda"
                />
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
          </>
        ) : (
          <>
            <div className="datos-vuelo-header">
              <img src="/logos/vuelo.png" alt="Avión" className="icono-vuelo" />
              <div className="datos-vuelo-info">
                <h2 className="vuelo-codigo">Seleccione un vuelo</h2>
                <p className="vuelo-horario">Salida: XXX - HH:mm</p>
                <p className="vuelo-horario">Llegada: XXX - HH:mm</p>
              </div>
            </div>
          </>
        )}
      </div>
    </div>
  );
};

export default DatosVuelo;
