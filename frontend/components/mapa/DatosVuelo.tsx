"use client";
import React, { useState, useRef, useEffect } from "react";
import "@/styles/ComponentesDatosVuelo.css";
import { Vuelo } from "@/types/Vuelo";
import { Aeropuerto } from "@/types/Aeropuerto";
import { ProgramacionVuelo } from "@/types/ProgramacionVuelo";
import { Envio } from "@/types/Envio";
import { aHoraMinutos, mostrarTiempoEnZonaHoraria, tiempoFaltante, utcStringToZonedDate } from "@/utils/FuncionesTiempo";
import { Paquete } from "@/types/Paquete";

type DatosVueloProps = {
  vuelo: Vuelo | null;
  aeropuerto: Aeropuerto | null;
  programacionVuelos: React.RefObject<Map<string, ProgramacionVuelo>>;
  simulationTime: Date;
  envios: React.RefObject<Map<string, Envio>>;
  aeropuertos: React.RefObject<Map<string, {aeropuerto: Aeropuerto; pointFeature: any}>>;
  envio : Envio | null;
  vuelos : React.RefObject<Map<number, {vuelo: Vuelo, pointFeature: any, lineFeature: any, routeFeature: any}>>;
};

const DatosVuelo: React.FC<DatosVueloProps> = ({ vuelo, aeropuerto, programacionVuelos, simulationTime, envios, aeropuertos , envio, vuelos}) => {
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
    // console.log("Vuelo: ", vuelo);
    const claveProgramacion = `${vuelo.id}-${simulationTime.toISOString().slice(0,10)}`;
    // console.log("Programación de vuelo: ", programacionVuelos.current.get(claveProgramacion) ?? null);
    const fechaAyer = new Date(simulationTime);
    fechaAyer.setDate(fechaAyer.getDate() - 1);
    const claveProgramacionManana = `${vuelo.id}-${fechaAyer.toISOString().slice(0,10)}`;
    setProgramacionVuelo(programacionVuelos.current?.get(claveProgramacion) ?? programacionVuelos.current?.get(claveProgramacionManana) ?? null);
    setVisible(true);
    setOpcion(1);
  }, [vuelo]);

  useEffect(() => {
    if(aeropuerto == null) return;
    setOpcion(2);
    setVisible(true);
  }, [aeropuerto]);

  useEffect(() => {
    if(envio == null) return;
    setOpcion(3);
    setVisible(true);
  }, [envio]);

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

  function construirRuta(paquete: Paquete, nombres: boolean = false) {
    const envio = envios.current?.get(paquete.codigoEnvio);
    let ruta = "";
    if(nombres){
      for (let i = 0; i < paquete.ruta.length; i++) {
        if(i==0){
          ruta = aeropuertos.current?.get(envio?.origen ?? "SKBO")?.aeropuerto.ciudad ?? "NULL";
          ruta += " -> ";
        }
  
        if(i==paquete.ruta.length-1){
          ruta +=  aeropuertos.current?.get(envio?.destino ?? "SKBO")?.aeropuerto.ciudad ?? "NULL";
          break;
        }
        let vuelo = vuelos.current?.get(paquete.ruta[i]);
        let destino = vuelo?.vuelo.destino;
  
        ruta += aeropuertos.current?.get(destino ?? "SKBO")?.aeropuerto.ciudad ?? "NULL";
  
        
      }
    }
    else{
      for (let i = 0; i < paquete.ruta.length; i++) {
        if (i === paquete.ruta.length - 1) {
          ruta += paquete.ruta[i];
          break;
        }
        ruta += paquete.ruta[i] + " -> ";
      }
    }
    
    return ruta;
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
        className={`datos-vuelo-contenedor ${visible ? "visible" : "hidden"} ${
          opcion == 3 ? "envio" : ""
        }`}
      >
        {vuelo && opcion == 1 ? (
          <>
            <div className="datos-vuelo-header">
              <img
                src="/logos/vueloEnhancedBlue.png"
                alt="Avión"
                className="icono-vuelo"
              />
              <div className="datos-vuelo-info">
                <h2 className="vuelo-codigo">Vuelo {vuelo.id}</h2>
                <p className="vuelo-horario">
                  Salida: {vuelo.origen} -{" "}
                  {utcStringToZonedDate(
                    vuelo.fechaHoraSalida,
                    aeropuertos.current?.get(vuelo.origen)?.aeropuerto.gmt ?? 0
                  )}
                </p>
                <p className="vuelo-horario">
                  Llegada: {vuelo.destino} -{" "}
                  {utcStringToZonedDate(
                    vuelo.fechaHoraLlegada,
                    aeropuertos.current?.get(vuelo.destino)?.aeropuerto.gmt ?? 0
                  )}
                </p>
              </div>
              <div className="datos-vuelo-capacidad">
                <h2>
                  Carga: <br /> {programacionVuelo?.cantPaquetes ?? 0} /{" "}
                  {vuelo.capacidad} paquetes
                </h2>
                <p>
                  {(
                    ((programacionVuelo?.cantPaquetes ?? 0) / vuelo.capacidad) *
                    100
                  ).toFixed(2)}
                  % lleno
                </p>
                {renderImage(
                  ((programacionVuelo?.cantPaquetes ?? 0) / vuelo.capacidad) *
                    100,
                  "avion"
                )}
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
                  }}
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
                      .filter((paquete) => {
                        const envio = envios.current?.get(paquete.codigoEnvio);
                        return (
                          (filtros.idPaquete === 0 &&
                            filtros.ciudad === "" &&
                            filtros.idEnvio === "") ||
                          (filtros.idPaquete !== 0 &&
                            paquete.id === filtros.idPaquete) ||
                          (filtros.ciudad !== "" &&
                            (envio?.origen === filtros.ciudad ||
                              envio?.destino === filtros.ciudad)) ||
                          (filtros.idEnvio !== "" &&
                            paquete.codigoEnvio === filtros.idEnvio)
                        );
                      })
                      .map((paquete, index) => {
                        const envio = envios.current?.get(paquete.codigoEnvio);
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
        ) : aeropuerto && opcion == 2 ? (
          <>
            <div className="datos-vuelo-header">
              <img
                src="/logos/oficinasEnhancedBlue.png"
                alt="Oficina"
                className="icono-vuelo"
              />
              <div className="datos-vuelo-info">
                <h2 className="vuelo-codigo">Almacén {aeropuerto.pais}</h2>
                <h2 className="vuelo-codigo"> {aeropuerto.codigoOACI}</h2>
                <p className="vuelo-horario">
                  Hora local:{" "}
                  {mostrarTiempoEnZonaHoraria(simulationTime, aeropuerto.gmt)}
                </p>
              </div>
              <div className="datos-vuelo-capacidad">
                <h2>Capacidad:</h2>
                <h2>
                  {aeropuerto.cantidadActual}/{aeropuerto.capacidadMaxima}{" "}
                  paquetes
                </h2>
                <p>
                  {(
                    (aeropuerto.cantidadActual / aeropuerto.capacidadMaxima) *
                    100
                  ).toFixed(2)}
                  % lleno
                </p>
                {renderImage(
                  (aeropuerto.cantidadActual / aeropuerto.capacidadMaxima) *
                    100,
                  "edificio"
                )}
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
                  }}
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
                      .filter((paquete) => {
                        const envio = envios.current?.get(paquete.codigoEnvio);
                        return (
                          (filtros.idPaquete === 0 &&
                            filtros.ciudad === "" &&
                            filtros.idEnvio === "") ||
                          (filtros.idPaquete !== 0 &&
                            paquete.id === filtros.idPaquete) ||
                          (filtros.ciudad !== "" &&
                            (envio?.origen.startsWith(filtros.ciudad) ||
                              envio?.destino.startsWith(filtros.ciudad))) ||
                          (filtros.idEnvio !== "" &&
                            paquete.codigoEnvio.startsWith(filtros.idEnvio))
                        );
                      })
                      .map((paquete, index) => {
                        const envio = envios.current?.get(paquete.codigoEnvio);
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
        ) : envio && opcion == 3 ? (
          <>
            <div className="datos-vuelo-header">
              <img
                src="/logos/paqueteCeleste.png"
                alt="Paquete"
                className="icono-vuelo"
              />
              <div className="datos-vuelo-info">
                <h2 className="vuelo-codigo">Envío {envio.id}</h2>
                <p className="vuelo-horario">
                  Origen: {envio.origen}{" "}
                  {"(" +
                    aeropuertos.current?.get(envio.origen)?.aeropuerto.ciudad +
                    ")"}
                </p>
                <p className="vuelo-horario">
                  Destino: {envio.destino}{" "}
                  {"(" +
                    aeropuertos.current?.get(envio.destino)?.aeropuerto.ciudad +
                    ")"}
                </p>
              </div>
            </div>
            <div className="datos-vuelo-content">
              <div className="datos-vuelo-busqueda">
                Detalle de los {envio.paquetes.length} paquetes
              </div>
              <div className="datos-vuelo-tabla">
                <table>
                  <thead>
                    <tr>
                      <th>Cód. paquete</th>
                      <th>Ruta (códigos de vuelo)</th>
                    </tr>
                  </thead>
                  <tbody>
                    {envio.paquetes.map((paquete, index) => {
                      return (
                        <tr key={index}>
                          <td>{paquete.id}</td>
                          <td>{construirRuta(paquete, false)}</td>
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
            <div
              className="datos-vuelo-header"
              style={{
                display: "flex",
                justifyContent: "center",
                alignItems: "center",
                textAlign: "center",
              }}
            >
              <img
                src="/logos/oficinasEnhancedBlue.png"
                alt="Oficinas"
                className="icono-vuelo"
                style={{ marginRight: "10px" }}
              />
              <div className="datos-vuelo-info">
                <h2
                  className="vuelo-codigo"
                  style={{ fontWeight: "bold", margin: 0 }}
                  
                >
                  Seleccione un vuelo o almacén
                </h2>
              </div>
              <img
                src="/logos/vueloEnhancedBlue.png"
                alt="Avión"
                className="icono-vuelo"
                style={{ marginLeft: "10px" }}
              />
            </div>
          </>
        )}
      </div>
    </div>
  );
};

export default DatosVuelo;
