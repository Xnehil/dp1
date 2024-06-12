import React from 'react';
import { Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper, Button, Box } from '@mui/material';

const FinSemanal: React.FC = () => {
  const rows = [
    { code: 'LIM1456', departure: '18:00', arrival: '21:00', origin: 'Lima', destination: 'Santiago', packages: 450 },
    { code: 'SAN4312', departure: '18:00', arrival: '21:00', origin: 'Lima', destination: 'Santiago', packages: 450 },
    { code: 'BOG4901', departure: '18:00', arrival: '21:00', origin: 'Lima', destination: 'Santiago', packages: 250 },
    { code: 'MAD9093', departure: '18:00', arrival: '21:00', origin: 'Lima', destination: 'Santiago', packages: 180 },
    { code: 'PAR1823', departure: '18:00', arrival: '21:00', origin: 'Lima', destination: 'Santiago', packages: 150 },
    { code: 'MOL2900', departure: '18:00', arrival: '21:00', origin: 'Lima', destination: 'Santiago', packages: 450 },
  ];

  return (
    <Box sx={{ padding: 4 }}>
      <Paper sx={{ marginBottom: 2, padding: 2 }}>
        <h2>La planificación es la siguiente:</h2>
        <TableContainer>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>Código</TableCell>
                <TableCell>Hora salida</TableCell>
                <TableCell>Llegada estimada</TableCell>
                <TableCell>Ciudad origen</TableCell>
                <TableCell>Ciudad destino</TableCell>
                <TableCell>Paquetes</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {rows.map((row) => (
                <TableRow key={row.code}>
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
      </Paper>
      <Box display="flex" justifyContent="space-between">
        <Button variant="contained" color="success">Cancelar</Button>
        <Button variant="contained" color="primary">Continuar</Button>
      </Box>
    </Box>
  );
};

export default FinSemanal;
