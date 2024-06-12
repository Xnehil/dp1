"use client";
import React, { useState } from 'react';
import { Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper, Button, Box, Dialog, DialogActions, DialogContent, DialogTitle } from '@mui/material';
import { styled } from '@mui/system';
import { Vuelo } from "@/types/Vuelo";
import { ProgramacionVuelo } from "@/types/ProgramacionVuelo";

const StyledTableCell = styled(TableCell)({
  backgroundColor: '#52489C',
  color: 'white',
});

type FinSemanalProps = {
  programacionVuelos: React.MutableRefObject<Map<string, ProgramacionVuelo>>;
  vuelos: React.RefObject<Map<number, { vuelo: Vuelo }>>;
};

const formatTime = (timeString: string) => {
  const date = new Date(timeString);
  return date.toLocaleTimeString('en-GB', { hour: '2-digit', minute: '2-digit' });
};

const FinSemanal: React.FC<FinSemanalProps> = ({ programacionVuelos, vuelos }) => {
  const [open, setOpen] = useState(true);

  const handleClose = () => {
    setOpen(false);
  };

  const rows = Array.from(programacionVuelos.current.values()).map((programacion) => {
    if (!vuelos.current) return null;
    const vueloInfo = vuelos.current.get(programacion.idVuelo);
    if (!vueloInfo) {
      return null;
    }
    return {
      code: programacion.idVuelo,
      departure: formatTime(vueloInfo.vuelo.fechaHoraSalida),
      arrival: formatTime(vueloInfo.vuelo.fechaHoraLlegada),
      origin: vueloInfo.vuelo.origen,
      destination: vueloInfo.vuelo.destino,
      packages: programacion.cantPaquetes,
    };
  }).filter(row => row !== null);

  return (
    <Dialog open={open} onClose={handleClose} maxWidth="md" fullWidth>
      <DialogTitle>La planificación es la siguiente:</DialogTitle>
      <DialogContent>
        <TableContainer component={Paper}>
          <Table>
            <TableHead>
              <TableRow>
                <StyledTableCell>Código</StyledTableCell>
                <StyledTableCell>Hora salida</StyledTableCell>
                <StyledTableCell>Llegada estimada</StyledTableCell>
                <StyledTableCell>Ciudad origen</StyledTableCell>
                <StyledTableCell>Ciudad destino</StyledTableCell>
                <StyledTableCell>Paquetes</StyledTableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {rows.map((row, index) => (
                <TableRow key={index}>
                  <TableCell>{row.code}</TableCell>
                  <TableCell>{row.departure}</TableCell>
                  <TableCell>{row.arrival}</TableCell>
                  <TableCell>{row.origin}</TableCell>
                  <TableCell>{row.destination}</TableCell>
                  <TableCell>{row.packages}</TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      </DialogContent>
      <DialogActions>
        <Button variant="contained" color="success" sx={{ backgroundColor: '#28a745' }}>
          DESCARGAR
        </Button>
        <Button variant="contained" color="primary" sx={{ backgroundColor: '#007bff' }} onClick={handleClose}>
          CONTINUAR
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default FinSemanal;