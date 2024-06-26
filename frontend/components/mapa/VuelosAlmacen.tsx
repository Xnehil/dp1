"use client";
import React, { useState, useEffect } from "react";
import { Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper, styled } from "@mui/material";
import { Aeropuerto } from "@/types/Aeropuerto";
import { Vuelo } from "@/types/Vuelo";
import "@/styles/VuelosAlmacen.css";
import { mostrarTiempoEnZonaHoraria } from "@/utils/FuncionesTiempo";
import { ProgramacionVuelo } from "@/types/ProgramacionVuelo";

const StyledTableCell = styled(TableCell)({
  backgroundColor: '#52489C',
  color: 'white',
});

type VuelosAlmacenProps = {
  selectedAeropuerto: Aeropuerto | null;
  vuelos: React.RefObject<Map<number, {vuelo: Vuelo;pointFeature: any;lineFeature: any;routeFeature: any;}>>;
  simulationTime: Date | null;
  programacionVuelos: React.MutableRefObject<Map<string, ProgramacionVuelo>>;
};

const VuelosAlmacen: React.FC<VuelosAlmacenProps> = ({ selectedAeropuerto, vuelos, simulationTime, programacionVuelos }) => {
  const [vuelosAlmacen, setVuelosAlmacen] = useState<Vuelo[]>([]);
  const [visible, setVisible] = useState<boolean>(false);

  useEffect(() => {
    if (!simulationTime) return;
    if (selectedAeropuerto) {
      const ventana = 2.5 * 60 * 60 * 1000; // 3 hours in milliseconds
      const horaLimite = new Date(simulationTime.getTime() + ventana);
      const fechaVueloFormatted = simulationTime
                        .toISOString()
                        .slice(0, 10);
                    
      
      const vuelosLlegando = Array.from(vuelos.current?.values() || [])
        .map((item) => item.vuelo)
        .filter((vuelo) => {
          if (vuelo.destino !== selectedAeropuerto.codigoOACI) return false;
          const horaLlegada = new Date(vuelo.fechaHoraLlegada);
          //Poner horaLlegada en el día de simulationTime
          horaLlegada.setFullYear(simulationTime.getFullYear());
          horaLlegada.setMonth(simulationTime.getMonth());
          horaLlegada.setDate(simulationTime.getDate());
          //Solo vuelos que tengan una programación vuelo
          const claveProgramacion =vuelo.id + "-" + fechaVueloFormatted;
          if (!programacionVuelos.current.has(claveProgramacion)) return false;
          return horaLlegada >= simulationTime && horaLlegada <= horaLimite;
        });
      setVuelosAlmacen(vuelosLlegando);
      setVisible(true);
    } else {
      setVuelosAlmacen([]);
      setVisible(false);
    }
  }, [selectedAeropuerto, vuelos]);

  const formatDate = (dateString: string) => {
    const date = new Date(dateString);
    return date.toLocaleTimeString();
  };

  const toggleVisibility = () => {
    setVisible(!visible);
  };

  return (
    <div className="vuelos-almacen-wrapper">
      <div className={`vuelos-almacen-contenedor ${visible ? 'visible' : 'hidden'}`}>
        <h3>Vuelos llegando a {selectedAeropuerto?.ciudad}</h3>
        <TableContainer component={Paper} className="vuelos-almacen-tabla">
          <Table stickyHeader>
            <TableHead>
              <TableRow>
                <StyledTableCell>Vuelo</StyledTableCell>
                <StyledTableCell>Origen</StyledTableCell>
                <StyledTableCell>Hora Llegada (local)</StyledTableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {vuelosAlmacen.map((vuelo) => (
                <TableRow key={vuelo.id}>
                  <TableCell>{vuelo.id}</TableCell>
                  <TableCell>{vuelo.origen}</TableCell>
                  {/* <TableCell>{formatDate(vuelo.fechaHoraLlegada)}</TableCell> */}
                  <TableCell>{mostrarTiempoEnZonaHoraria(new Date(vuelo.fechaHoraLlegada), selectedAeropuerto?.gmt ?? 0)}</TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      </div>
      <button className="toggle-button" onClick={toggleVisibility}>
        {visible ? '◀' : '▶'}
      </button>
    </div>
  );
};

export default VuelosAlmacen;