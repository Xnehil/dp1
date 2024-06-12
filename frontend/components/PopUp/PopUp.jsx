import React, { useState } from 'react';
import Button from '@mui/material/Button';
import Modal from '@mui/material/Modal';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';

const ModalButton = () => {
  const [open, setOpen] = useState(false);

  const handleOpen = () => {
    setOpen(true);
  };

  const handleClose = () => {
    setOpen(false);
  };

  return (
    <div>
      <h1>Ejemplo de Modal</h1>
      <Button variant="contained" onClick={handleOpen}>
        Abrir Modal
      </Button>
      <Modal
        open={open}
        onClose={handleClose}
        aria-labelledby="modal-title"
        aria-describedby="modal-description"
      >
        <Box sx={{
          position: 'absolute',
          top: '50%',
          left: '50%',
          transform: 'translate(-50%, -50%)',
          width: 400,
          bgcolor: 'background.paper',
          border: '2px solid #000',
          boxShadow: 24,
          p: 4,
        }}>
          <Typography id="modal-title" variant="h6" component="h2">
            Título del Modal
          </Typography>
          <Typography id="modal-description" sx={{ mt: 2 }}>
            Contenido del modal. Puedes poner aquí cualquier contenido que desees mostrar dentro del modal.
          </Typography>
          <Button onClick={handleClose} sx={{ mt: 2 }}>Cerrar</Button>
        </Box>
      </Modal>
    </div>
  );
};

export default ModalButton;