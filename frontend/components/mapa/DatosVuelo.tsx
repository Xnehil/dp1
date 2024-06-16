"use client";
import React, { useState, useRef, useEffect } from "react";
import "@/styles/ComponentesDatosVuelo.css";
import { Vuelo } from "@/types/Vuelo";
import { Aeropuerto } from "@/types/Aeropuerto";
import { ProgramacionVuelo } from "@/types/ProgramacionVuelo";
import { Envio } from "@/types/Envio";
import { aHoraMinutos, mostrarTiempoEnZonaHoraria, tiempoFaltante, utcStringToZonedDate } from "@/utils/FuncionesTiempo";

type DatosVueloProps = {
  vuelo: Vuelo | null;
  aeropuerto: Aeropuerto | null;
  programacionVuelos: React.MutableRefObject<Map<string, ProgramacionVuelo>>;
  simulationTime: Date;
  envios: React.MutableRefObject<Map<string, Envio>>;
  aeropuertos: React.MutableRefObject<Map<string, {aeropuerto: Aeropuerto; pointFeature: any}>>;
};

const DatosVuelo: React.FC<DatosVueloProps> = ({ vuelo, aeropuerto, programacionVuelos, simulationTime, envios, aeropuertos }) => {
  const [visible, setVisible] = useState<boolean>(false);
  const [opcion, setOpcion] = useState<number>(0);
  const [programacionVuelo, setProgramacionVuelo] = useState<ProgramacionVuelo | null>(null);
  const [cargado, setCargado] = useState<boolean>(false);
  const [busqueda, setBusqueda] = useState<string>("");
  const [filtros, setFiltros] = useState<{idPaquete: number, ciudad: string, idEnvio: string}>({idPaquete: 0, ciudad: "", idEnvio: ""});

  const toggleVisibility = () => {
    setVisible(!visible);
  };

  useEffect(() => {
    if(vuelo == null) return;
    console.log("Vuelo: ", vuelo);
    //En base al día de la simulación, se obtiene la programación de vuelo correspondiente
    const claveProgramacion = vuelo.id + "-" + simulationTime.toISOString().slice(0,10);
    // console.log("Clave programación: ", claveProgramacion);
    // console.log("Programaciones de vuelo: ", programacionVuelos.current);
    const auxProgramacion = programacionVuelos.current.get(claveProgramacion);
    // console.log("Programación de vuelo: ", auxProgramacion);
    setProgramacionVuelo(auxProgramacion ?? null);
    setVisible(true);
    setOpcion(1);
  }, [vuelo]);

  useEffect(() => {
    if(aeropuerto == null) return;
    setOpcion(2);
    setVisible(true);
  }, [aeropuerto]);

  function procesarBusqueda() {
    console.log("Buscando: ", busqueda);
    //Si es un nu´mero, se busca por ID de paquete
    if(!isNaN(Number(busqueda))) {
      setFiltros({idPaquete: Number(busqueda), ciudad: "", idEnvio: ""});
    } else {
      //Si no es un número, pero es menor a 4 caracteres, se busca por ciudad
      if(busqueda.length <= 4) {
        setFiltros({idPaquete: 0, ciudad: busqueda, idEnvio: ""});
      } else {
        //Si no es un número y tiene más 4 caracteres, se busca por ID de envío
        setFiltros({idPaquete: 0, ciudad: "", idEnvio: busqueda});
      }
    }
  }

  useEffect(() => {
    console.log("Filtros: ", filtros);
  } , [filtros]);


  return (
    <div className="datos-vuelo-wrapper">
      <button className="toggle-button-datos-vuelo" onClick={toggleVisibility}>
        {visible ? "▼" : "▲"}
      </button>
      
      <div
        className={`datos-vuelo-contenedor ${visible ? "visible" : "hidden"}`}
      >
        {(vuelo)? (
          <>
            <div className="datos-vuelo-header">
              <img src="/logos/vueloEnhancedBlue.png" alt="Avión" className="icono-vuelo" />
              <div className="datos-vuelo-info">
                <h2 className="vuelo-codigo">Vuelo {vuelo.id}</h2>
                <p className="vuelo-horario">
                  Salida: {vuelo.origen} -{" "}
                  {utcStringToZonedDate(vuelo.fechaHoraSalida, aeropuertos.current.get(vuelo.origen)?.aeropuerto.gmt ?? 0)}
                </p>
                <p className="vuelo-horario">
                  Llegada: {vuelo.destino} -{" "}
                  {utcStringToZonedDate(vuelo.fechaHoraLlegada, aeropuertos.current.get(vuelo.destino)?.aeropuerto.gmt ?? 0)}
                </p>
              </div>
              <div className="datos-vuelo-capacidad">
                <h2>Carga: {programacionVuelo?.cantPaquetes ?? 0} Paquetes</h2>
                <p>
                  {(((programacionVuelo?.cantPaquetes ?? 0) / vuelo.capacidad) * 100).toFixed(2)}
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
                  onChange={(e) => setBusqueda(e.target.value)}
                  onKeyDown={(e) => {
                    if (e.key === "Enter") {
                      procesarBusqueda();
                    }
                    }
                  }
                />
                <button className="boton-busqueda" onClick={procesarBusqueda}>
                  Buscar
                </button>
              </div>
              <div className="datos-vuelo-tabla">
                <table>
                  <thead>
                    <tr>
                      <th>Código</th>
                      <th>Origen</th>
                      <th>Destino</th>
                      <th>ID envío</th>
                    </tr>
                  </thead>
                  <tbody>
                    {programacionVuelo?.paquetes
                      .filter(paquete => {
                        const envio = envios.current.get(paquete.codigoEnvio);
                          return (filtros.idPaquete === 0 && filtros.ciudad === "" && filtros.idEnvio === "") || 
                                (filtros.idPaquete !== 0 && paquete.id === filtros.idPaquete) ||
                                (filtros.ciudad !== "" &&  (envio?.origen === filtros.ciudad || envio?.destino === filtros.ciudad)) ||
                                (filtros.idEnvio !== "" && paquete.codigoEnvio === filtros.idEnvio);
                      })
                      .map((paquete, index) => {
                          const envio = envios.current.get(paquete.codigoEnvio);
                          return (
                              <tr key={index}>
                                  <td>{paquete.id}</td>
                                  <td>{envio?.origen ?? "NULL"}</td>
                                  <td>{envio?.destino ?? "NULL"}</td>
                                  <td>{paquete.codigoEnvio}</td>
                              </tr>
                          );
                      })}
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
                <h2 className="vuelo-codigo">Almacén {aeropuerto.pais}</h2>
                <h2 className="vuelo-codigo"> {aeropuerto.codigoOACI}</h2>
                <p className="vuelo-horario">
                  Hora local: {" "}
                  {mostrarTiempoEnZonaHoraria(simulationTime, aeropuerto.gmt)}
                </p>
              </div>
              <div className="datos-vuelo-capacidad">
                <h2>Capacidad máxima:</h2>
                <h2>
                   {aeropuerto.capacidadMaxima} Paquetes
                </h2>
                <p>
                  {(aeropuerto.cantidadActual  / aeropuerto.capacidadMaxima * 100).toFixed(2)}
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
                  placeholder="Ingrese código paquete o ID de envío"
                  className="input-busqueda"
                  onChange={(e) => setBusqueda(e.target.value)}
                  onKeyDown={(e) => {
                    if (e.key === "Enter") {
                      procesarBusqueda();
                    }
                    }
                  }
                />
                <button className="boton-busqueda" onClick={procesarBusqueda}>
                  Buscar
                </button>
              </div>
              <div className="datos-vuelo-tabla">
                <table>
                  <thead>
                    <tr>
                      <th>Código</th>
                      <th>Origen</th>
                      <th>Destino</th>
                      <th>ID envío</th>
                    </tr>
                  </thead>
                  <tbody>
                      {aeropuerto.paquetes
                          .filter(paquete => {
                              const envio = envios.current.get(paquete.codigoEnvio);
                              return (filtros.idPaquete === 0 && filtros.ciudad === "" && filtros.idEnvio === "") || 
                                     (filtros.idPaquete !== 0 && paquete.id === filtros.idPaquete) ||
                                     (filtros.ciudad !== "" && (envio?.origen.startsWith(filtros.ciudad) || envio?.destino.startsWith(filtros.ciudad))) ||
                                     (filtros.idEnvio !== "" && paquete.codigoEnvio.startsWith(filtros.idEnvio));
                          })
                          .map((paquete, index) => {
                              const envio = envios.current.get(paquete.codigoEnvio);
                              return (
                                  <tr key={index}>
                                      <td>{paquete.id}</td>
                                      <td>{envio?.origen ?? "NULL"}</td>
                                      <td>{envio?.destino ?? "NULL"}</td>
                                      <td>{paquete.codigoEnvio}</td>
                                  </tr>
                              );
                          })}
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
