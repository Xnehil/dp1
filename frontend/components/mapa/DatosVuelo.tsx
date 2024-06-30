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
    if (!vuelo) return;
    console.log("Vuelo: ", vuelo);
    const claveProgramacion = `${vuelo.id}-${simulationTime.toISOString().slice(0,10)}`;
    console.log("Programación de vuelo: ", programacionVuelos.current.get(claveProgramacion) ?? null);
    const fechaAyer = new Date(simulationTime);
    fechaAyer.setDate(fechaAyer.getDate() - 1);
    const claveProgramacionManana = `${vuelo.id}-${fechaAyer.toISOString().slice(0,10)}`;
    setProgramacionVuelo(programacionVuelos.current.get(claveProgramacion) ?? programacionVuelos.current.get(claveProgramacionManana) ?? null);
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

  function renderImage(percentage: number, tipo: string) {
    if (percentage < 33) {
      return <img src={`/logos/${tipo}Verde.png`} alt="Paquete" className="icono-paquete" />
    } else if (percentage < 66) {
      return <img src={`/logos/${tipo}Amarillo.png`} alt="Paquete" className="icono-paquete" />
    }
    else {
      return <img src={`/logos/${tipo}Rojo.png`} alt="Paquete" className="icono-paquete" />
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
        {(vuelo && opcion==1)? (
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
                <h2>Carga: <br /> {programacionVuelo?.cantPaquetes ?? 0} / {vuelo.capacidad} paquetes</h2>
                <p>
                  {(((programacionVuelo?.cantPaquetes ?? 0) / vuelo.capacidad) * 100).toFixed(2)}
                  % lleno
                </p>
                {renderImage(((programacionVuelo?.cantPaquetes ?? 0) / vuelo.capacidad) * 100, "avion")}
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
                      <th>Cód. paquete</th>
                      <th>Origen</th>
                      <th>Destino</th>
                      <th>Cód. envío</th>
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
        ) : (aeropuerto && opcion==2) ? (
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
                <h2>Capacidad:</h2>
                <h2>
                   {aeropuerto.cantidadActual}/{aeropuerto.capacidadMaxima} paquetes
                </h2>
                <p>
                  {(aeropuerto.cantidadActual  / aeropuerto.capacidadMaxima * 100).toFixed(2)}
                  % lleno
                </p>
                {renderImage((aeropuerto.cantidadActual  / aeropuerto.capacidadMaxima) * 100, "edificio")}
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
                      <th>Cód. paquete</th>
                      <th>Origen</th>
                      <th>Destino</th>
                      <th>Cód. envío</th>
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
