"use client";
import React, { useState, useEffect } from "react";
import { Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper, styled } from "@mui/material";
import { Aeropuerto } from "@/types/Aeropuerto";
import { Vuelo } from "@/types/Vuelo";
import "@/styles/VuelosAlmacen.css";

const StyledTableCell = styled(TableCell)({
  backgroundColor: '#52489C',
  color: 'white',
});

type VuelosAlmacenProps = {
  selectedAeropuerto: Aeropuerto | null;
  vuelos: React.RefObject<Map<number, { vuelo: Vuelo }>>;
};

const VuelosAlmacen: React.FC<VuelosAlmacenProps> = ({ selectedAeropuerto, vuelos }) => {
  const [vuelosAlmacen, setVuelosAlmacen] = useState<Vuelo[]>([]);
  const [visible, setVisible] = useState<boolean>(false);

  useEffect(() => {
    if (selectedAeropuerto) {
      const vuelosLlegando = Array.from(vuelos.current?.values() || [])
        .map((item) => item.vuelo)
        .filter((vuelo) => vuelo.destino === selectedAeropuerto.codigoOACI);
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
                <StyledTableCell>Hora Llegada</StyledTableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {vuelosAlmacen.map((vuelo) => (
                <TableRow key={vuelo.id}>
                  <TableCell>{vuelo.id}</TableCell>
                  <TableCell>{vuelo.origen}</TableCell>
                  <TableCell>{formatDate(vuelo.fechaHoraLlegada)}</TableCell>
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