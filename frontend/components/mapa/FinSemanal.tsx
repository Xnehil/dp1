"use client";
import React, { useMemo, useState } from 'react';
import { Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper, Button, Box, Dialog, DialogActions, DialogContent, DialogTitle, Typography } from '@mui/material';
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
  colapso: boolean;
};

const formatTime = (timeString: string) => {
  const date = new Date(timeString);
  return date.toLocaleTimeString('en-GB', { hour: '2-digit', minute: '2-digit' });
};

const FinSemanal: React.FC<FinSemanalProps> = ({ programacionVuelos, vuelos, colapso =false }) => {
  const [open, setOpen] = useState(true);

  const handleClose = () => {
    setOpen(false);
  };

  const rows = useMemo(() => {
    const allRows = Array.from(programacionVuelos.current.values())
      .map((programacion) => {
        if (!vuelos.current) return null;
        const vueloInfo = vuelos.current.get(programacion.idVuelo);
        if (!vueloInfo) return null;
        
        return {
          code: programacion.idVuelo,
          departure: formatTime(vueloInfo.vuelo.fechaHoraSalida),
          arrival: formatTime(vueloInfo.vuelo.fechaHoraLlegada),
          origin: vueloInfo.vuelo.origen,
          destination: vueloInfo.vuelo.destino,
          packages: programacion.cantPaquetes,
        };
      })
      .filter(row => row !== null);

    // Return only the last 300 items
    return allRows.slice(-300);
  }, [programacionVuelos, vuelos]);

  return (
    <Dialog open={open} onClose={handleClose} maxWidth="md" fullWidth>
      <DialogTitle>Últimos vuelos de la planificación semanal:</DialogTitle>
      <DialogContent>
      {colapso && (
          <Typography color="error" style={{ marginBottom: 16 }}>
            Se ha detectado un colapso en la planificación.
          </Typography>
        )}
        <TableContainer component={Paper}>
          <Table>
            <TableHead>
              <TableRow>
                <StyledTableCell>Código vuelo</StyledTableCell>
                <StyledTableCell>Hora salida</StyledTableCell>
                <StyledTableCell>Hora llegada</StyledTableCell>
                <StyledTableCell>Ciudad origen</StyledTableCell>
                <StyledTableCell>Ciudad destino</StyledTableCell>
                <StyledTableCell>Paquetes asignados</StyledTableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {rows.map((row, index) => (
                row && (
                  <TableRow key={index}>
                    <TableCell>{row.code}</TableCell>
                    <TableCell>{row.departure}</TableCell>
                    <TableCell>{row.arrival}</TableCell>
                    <TableCell>{row.origin}</TableCell>
                    <TableCell>{row.destination}</TableCell>
                    <TableCell>{row.packages}</TableCell>
                  </TableRow>
                )
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      </DialogContent>
      <DialogActions>
        {/* <Button variant="contained" color="success" sx={{ backgroundColor: '#28a745' }}>
          DESCARGAR
        </Button> */}
        <Button variant="contained" color="primary" sx={{ backgroundColor: '#007bff' }} onClick={handleClose}>
          Continuar
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default FinSemanal;