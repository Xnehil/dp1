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
  InputLabel, 
  Typography,
  Grid,
  Divider
} from '@mui/material';

const steps = ['Datos del cliente', 'Destino y paquetes', 'Datos del receptor', 'Confirmar envío'];

const RegisterShipmentPage: React.FC = () => {
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
            <Typography variant="h6">Datos personales del cliente</Typography>
            <Typography variant="subtitle2" color="textSecondary">¿Quién está enviando los paquetes?</Typography>
            <Divider sx={{ my: 2 }} />
            <Grid container spacing={2}>
              <Grid item xs={12}>
                <FormControl fullWidth>
                  <InputLabel id="document-type-label">Tipo de documento</InputLabel>
                  <Select labelId="document-type-label" label="Tipo de documento">
                    <MenuItem value="DNI">DNI</MenuItem>
                    <MenuItem value="Pasaporte">Pasaporte</MenuItem>
                  </Select>
                </FormControl>
              </Grid>
              <Grid item xs={12}>
                <TextField fullWidth label="Número de documento" />
              </Grid>
              <Grid item xs={4}>
                <TextField fullWidth label="Apellido" />
              </Grid>
              <Grid item xs={4}>
                <TextField fullWidth label="Nombre" />
              </Grid>
              <Grid item xs={4}>
                <TextField fullWidth label="Segundo nombre" />
              </Grid>
              <Grid item xs={2}>
                <TextField fullWidth label="Código de ciudad" defaultValue="+51" />
              </Grid>
              <Grid item xs={10}>
                <TextField fullWidth label="Teléfono" />
              </Grid>
              <Grid item xs={12}>
                <TextField fullWidth label="Correo electrónico" />
              </Grid>
            </Grid>
          </Box>
        );
      case 1:
        return (
          <Box component="form" noValidate autoComplete="off">
            <Typography variant="h6">Destino y paquetes</Typography>
            <Typography variant="subtitle2" color="textSecondary">¿A dónde envías?</Typography>
            <Divider sx={{ my: 2 }} />
            <Grid container spacing={2}>
              <Grid item xs={12}>
                <FormControl fullWidth>
                  <InputLabel id="origin-city-label">Ciudad origen</InputLabel>
                  <Select labelId="origin-city-label" label="Ciudad origen">
                    <MenuItem value="Lima">Lima</MenuItem>
                  </Select>
                </FormControl>
              </Grid>
              <Grid item xs={12}>
                <FormControl fullWidth>
                  <InputLabel id="destination-city-label">Ciudad destino</InputLabel>
                  <Select labelId="destination-city-label" label="Ciudad destino">
                    <MenuItem value="Santiago">Santiago</MenuItem>
                  </Select>
                </FormControl>
              </Grid>
            </Grid>
            <Typography variant="subtitle2" color="white">.</Typography>
            <Typography variant="h6">Paquetes</Typography>
            <Typography variant="subtitle2" color="textSecondary">¿Cuántos paquetes?</Typography>
            <Divider sx={{ my: 2 }} />
            <Grid container spacing={2}>
              <Grid item xs={12}>
                <TextField fullWidth label="Número de paquetes" />
              </Grid>
            </Grid>
          </Box>
        );
      case 2:
        return (
          <Box component="form" noValidate autoComplete="off">
            <Typography variant="h6">Datos del receptor</Typography>
            <Typography variant="subtitle2" color="textSecondary">¿Quién recibe el paquete?</Typography>
            <Divider sx={{ my: 2 }} />
            <Grid container spacing={2}>
              <Grid item xs={12}>
                <FormControl fullWidth>
                  <InputLabel id="receiver-document-type-label">Tipo de documento</InputLabel>
                  <Select labelId="receiver-document-type-label" label="Tipo de documento">
                    <MenuItem value="DNI">DNI</MenuItem>
                    <MenuItem value="Pasaporte">Pasaporte</MenuItem>
                  </Select>
                </FormControl>
              </Grid>
              <Grid item xs={12}>
                <TextField fullWidth label="Número de documento" />
              </Grid>
              <Grid item xs={4}>
                <TextField fullWidth label="Primer apellido" />
              </Grid>
              <Grid item xs={4}>
                <TextField fullWidth label="Segundo apellido" />
              </Grid>
              <Grid item xs={4}>
                <TextField fullWidth label="Nombres" />
              </Grid>
              <Grid item xs={2}>
                <TextField fullWidth label="Código de ciudad" defaultValue="+51" />
              </Grid>
              <Grid item xs={10}>
                <TextField fullWidth label="Teléfono" />
              </Grid>
            </Grid>
          </Box>
        );
      case 3:
        return (
          <Box component="form" noValidate autoComplete="off">
            <Typography variant="h6">Confirmar envío</Typography>
            <Divider sx={{ my: 2 }} />
            <Grid container spacing={2}>
              <Grid item xs={12}>
                <TextField fullWidth label="Ciudad destino" defaultValue="Santiago" disabled />
              </Grid>
              <Grid item xs={12}>
                <TextField fullWidth label="Número de paquetes" defaultValue="3" disabled />
              </Grid>
              <Grid item xs={12}>
                <TextField fullWidth label="Nombre remitente" defaultValue="Valentino Stone" disabled />
              </Grid>
              <Grid item xs={12}>
                <TextField fullWidth label="Apellido remitente" defaultValue="Benzema" disabled />
              </Grid>
              <Grid item xs={12}>
                <TextField fullWidth label="Nombre destinatario" defaultValue="Valentino Stone" disabled />
              </Grid>
              <Grid item xs={12}>
                <TextField fullWidth label="Apellido destinatario" defaultValue="Benzema" disabled />
              </Grid>
            </Grid>
          </Box>
        );
      default:
        return 'Unknown step';
    }
  };

  return (
    <Box sx={{ width: '50%', margin: 'auto', padding: 4 }}>
      <Stepper activeStep={activeStep} sx={{ mb: 4 }}>
        {steps.map((label) => (
          <Step key={label}>
            <StepLabel>{label}</StepLabel>
          </Step>
        ))}
      </Stepper>
      <Box sx={{ mb: 2 }}>
        {renderStepContent(activeStep)}
      </Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
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
        >
          {activeStep === steps.length - 1 ? 'Terminar' : 'Siguiente'}
        </Button>
      </Box>
    </Box>
  );
};

export default RegisterShipmentPage;
