"use client";
import React, { useState } from 'react';
import { 
  Box, 
  Button, 
  Stepper, 
  Step, 
  StepLabel, 
  TextField, 
  Select, 
  MenuItem, 
  FormControl, 
  InputLabel 
} from '@mui/material';

const steps = ['Datos del cliente', 'Destino y paquetes', 'Datos del receptor', 'Confirmar envío'];

const RegistrarEnvio: React.FC = () => {
  const [activeStep, setActiveStep] = useState(0);

  const handleNext = () => {
    setActiveStep((prevActiveStep) => prevActiveStep + 1);
  };

  const handleBack = () => {
    setActiveStep((prevActiveStep) => prevActiveStep - 1);
  };

  const renderStepContent = (step: number) => {
    switch (step) {
      case 0:
        return (
          <Box component="form" noValidate autoComplete="off">
            <FormControl fullWidth margin="normal">
              <InputLabel id="document-type-label">Tipo de documento</InputLabel>
              <Select labelId="document-type-label" label="Tipo de documento">
                <MenuItem value="DNI">DNI</MenuItem>
                <MenuItem value="Pasaporte">Pasaporte</MenuItem>
              </Select>
            </FormControl>
            <TextField fullWidth margin="normal" label="Número de documento" />
            <TextField fullWidth margin="normal" label="Apellido" />
            <TextField fullWidth margin="normal" label="Nombre" />
            <TextField fullWidth margin="normal" label="Segundo nombre" />
            <TextField fullWidth margin="normal" label="Teléfono" />
            <TextField fullWidth margin="normal" label="Correo electrónico" />
          </Box>
        );
      case 1:
        return (
          <Box component="form" noValidate autoComplete="off">
            <FormControl fullWidth margin="normal">
              <InputLabel id="origin-city-label">Ciudad origen</InputLabel>
              <Select labelId="origin-city-label" label="Ciudad origen">
                <MenuItem value="Lima">Lima</MenuItem>
              </Select>
            </FormControl>
            <FormControl fullWidth margin="normal">
              <InputLabel id="destination-city-label">Ciudad destino</InputLabel>
              <Select labelId="destination-city-label" label="Ciudad destino">
                <MenuItem value="Santiago">Santiago</MenuItem>
              </Select>
            </FormControl>
            <TextField fullWidth margin="normal" label="Número de paquetes" />
          </Box>
        );
      case 2:
        return (
          <Box component="form" noValidate autoComplete="off">
            <FormControl fullWidth margin="normal">
              <InputLabel id="receiver-document-type-label">Tipo de documento</InputLabel>
              <Select labelId="receiver-document-type-label" label="Tipo de documento">
                <MenuItem value="DNI">DNI</MenuItem>
                <MenuItem value="Pasaporte">Pasaporte</MenuItem>
              </Select>
            </FormControl>
            <TextField fullWidth margin="normal" label="Número de documento" />
            <TextField fullWidth margin="normal" label="Primer apellido" />
            <TextField fullWidth margin="normal" label="Segundo apellido" />
            <TextField fullWidth margin="normal" label="Nombres" />
            <TextField fullWidth margin="normal" label="Teléfono" />
          </Box>
        );
      case 3:
        return (
          <Box component="form" noValidate autoComplete="off">
            <TextField fullWidth margin="normal" label="Ciudad destino" defaultValue="Santiago" disabled />
            <TextField fullWidth margin="normal" label="Número de paquetes" defaultValue="3" disabled />
            <TextField fullWidth margin="normal" label="Nombre remitente" defaultValue="Valentino Stone" disabled />
            <TextField fullWidth margin="normal" label="Apellido remitente" defaultValue="Benzema" disabled />
            <TextField fullWidth margin="normal" label="Nombre destinatario" defaultValue="Valentino Stone" disabled />
            <TextField fullWidth margin="normal" label="Apellido destinatario" defaultValue="Benzema" disabled />
          </Box>
        );
      default:
        return 'Unknown step';
    }
  };

  return (
    <Box sx={{ width: '100%', padding: 4 }}>
      <Stepper activeStep={activeStep}>
        {steps.map((label, index) => (
          <Step key={label}>
            <StepLabel>{label}</StepLabel>
          </Step>
        ))}
      </Stepper>
      <Box sx={{ mt: 2 }}>
        {renderStepContent(activeStep)}
      </Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', mt: 2 }}>
        <Button
          disabled={activeStep === 0}
          onClick={handleBack}
          variant="contained"
          color="inherit"
        >
          Anterior
        </Button>
        <Button
          variant="contained"
          color="primary"
          onClick={handleNext}
          disabled={activeStep === steps.length - 1}
        >
          {activeStep === steps.length - 1 ? 'Terminar' : 'Siguiente'}
        </Button>
      </Box>
    </Box>
  );
};

export default RegistrarEnvio;
