import React, { useState } from 'react';
import { Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper , TablePagination } from '@mui/material';

const formatDateTime = (isoDate) => {
  // Crear un objeto Date a partir de la cadena ISO 8601
  const date = new Date(isoDate);
  
  // Obtener día, mes y año
  const day = date.getDate();
  const month = date.getMonth() + 1; // Los meses van de 0 a 11, por eso se suma 1
  const year = date.getFullYear();

  // Obtener hora y minutos
  const hours = date.getHours();
  const minutes = date.getMinutes();

  // Formatear la fecha como "dd/mm/yyyy hh:mm"
  const formattedDate = `${day.toString().padStart(2, '0')}/${month.toString().padStart(2, '0')}/${year} ${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}`;

  return formattedDate;
};

const TablaVuelosEnVivo = ({ vuelos }) => {
  const [page, setPage] = useState(0);
  const rowsPerPage = 12;

  const handleChangePage = (event, newPage) => {
    setPage(newPage);
  };

  const paginatedVuelos = vuelos.slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage);

  return (
    <div>
      <TableContainer component={Paper}>
        <Table sx={{ minWidth: 650 }} aria-label="simple table">
          <TableHead sx={{ backgroundColor: "#50599C" }}>
            <TableRow>
              <TableCell sx={{ color: 'white', fontSize: 'xl' }}>Código de vuelo</TableCell>
              <TableCell align="right" sx={{ color: 'white', fontSize: 'xl' }}>Origen</TableCell>
              <TableCell align="right" sx={{ color: 'white', fontSize: 'xl' }}>Destino</TableCell>
              <TableCell align="right" sx={{ color: 'white', fontSize: 'xl' }}>Capacidad</TableCell>
              <TableCell align="right" sx={{ color: 'white', fontSize: 'xl' }}>Fecha de salida</TableCell>
              <TableCell align="right" sx={{ color: 'white', fontSize: 'xl' }}>Fecha de llegada</TableCell>
              <TableCell align="right" sx={{ color: 'white', fontSize: 'xl' }}>Tiempo de vuelo (min)</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {paginatedVuelos.map((row) => (
              <TableRow
                key={row.idVuelo}
                sx={{ '&:last-child td, &:last-child th': { border: 0 } }}
              >
                <TableCell component="th" scope="row" sx={{ fontSize: 'xl' }}>
                  {row.idVuelo}
                </TableCell>
                <TableCell align="right" sx={{ fontSize: 'xl' }}>{row.origen}</TableCell>
                <TableCell align="right" sx={{ fontSize: 'xl' }}>{row.destino}</TableCell>
                <TableCell align="right" sx={{ fontSize: 'xl' }}>{row.capacidad}</TableCell>
                <TableCell align="right" sx={{ fontSize: 'xl' }}>{formatDateTime(row.fechaHoraSalida)}</TableCell>
                <TableCell align="right" sx={{ fontSize: 'xl' }}>{formatDateTime(row.fechaHoraLlegada)}</TableCell>
                <TableCell align="right" sx={{ fontSize: 'xl' }}>{row.duracionVuelo}</TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
      {/* Paginación */}
      <TablePagination
        rowsPerPageOptions={[]}
        component="div"
        count={vuelos.length}
        rowsPerPage={rowsPerPage}
        page={page}
        onPageChange={handleChangePage}
      />
    </div>
  );
};

export default TablaVuelosEnVivo;
