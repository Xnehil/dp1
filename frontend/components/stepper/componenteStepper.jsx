import * as React from 'react';
import Box from '@mui/material/Box';
import Stepper from '@mui/material/Stepper';
import Step from '@mui/material/Step';
import StepLabel from '@mui/material/StepLabel';
import Button from '@mui/material/Button';
import Typography from '@mui/material/Typography';
import TextField from '@mui/material/TextField';
import Modal from '@mui/material/Modal';
import BasicSelect from "@/components/select/select.jsx";
import SelectVariants from "@/components/select/selectNumCode.jsx";
import SelectVariantsCity from "@/components/select/selectCity.jsx"

const steps = ['Datos del cliente', 'Destino y paquetes', 'Datos del receptor', 'Confirmar envío'];

export default function HorizontalLinearStepper() {
  const [value, setValue] = React.useState('');
  const [city, setCity] = React.useState('');
  const [numCode, setnumCode] = React.useState('');
  const [activeStep, setActiveStep] = React.useState(0);
  const [skipped, setSkipped] = React.useState(new Set());
  const [openModal, setOpenModal] = React.useState(false);

  const [numDocREM, setnumDocREM] = React.useState('');
  const [tipoDocREM, settipoDocREM] = React.useState('');
  const [apellidoREM, setapellidoREM] = React.useState('');
  const [nombreREM, setnombreREM] = React.useState('');
  const [segundonombreREM, setsegundonombreREM] = React.useState('');
  const [telefonoREM, settelefonoREM] = React.useState('');
  const [numeroREM, setnumeroREM] = React.useState('');
  const [emailREM, setemailREM] = React.useState('');

  const [ciudadOrigen, setciudadOrigen] = React.useState('');
  const [ciudadDestino, setciudadDestino] = React.useState('');
  const [numPaquetes, setnumPaquetes] = React.useState('');

  const [numDocDES, setnumDocDES] = React.useState('');
  const [tipoDocDES, settipoDocDES] = React.useState('');
  const [apellidoDES, setapellidoDES] = React.useState('');
  const [nombreDES, setnombreDES] = React.useState('');
  const [segundonombreDES, setsegundonombreDES] = React.useState('');
  const [telefonoDES, settelefonoDES] = React.useState('');
  const [numeroDES, setnumeroDES] = React.useState('');
  const [emailDES, setemailDES] = React.useState('');

  // Funciones handleChange
  const handleChangeNumDocREM = (e) => setnumDocREM(e.target.value);
  const handleChangeTipoDocREM = (e) => settipoDocREM(e.target.value);
  const handleChangeApellidoREM = (e) => setapellidoREM(e.target.value);
  const handleChangeNombreREM = (e) => setnombreREM(e.target.value);
  const handleChangeSegundonombreREM = (e) => setsegundonombreREM(e.target.value);
  const handleChangeTelefonoREM = (e) => settelefonoREM(e.target.value);
  const handleChangeNumeroREM = (e) => setnumeroREM(e.target.value);
  const handleChangeEmailREM = (e) => setemailREM(e.target.value);

  const handleChangeCiudadOrigen = (e) => setciudadOrigen(e.target.value);
  const handleChangeCiudadDestino = (e) => setciudadDestino(e.target.value);
  const handleChangeNumPaquetes = (e) => setnumPaquetes(e.target.value);

  const handleChangeNumDocDES = (e) => setnumDocDES(e.target.value);
  const handleChangeTipoDocDES = (e) => settipoDocDES(e.target.value);
  const handleChangeApellidoDES = (e) => setapellidoDES(e.target.value);
  const handleChangeNombreDES = (e) => setnombreDES(e.target.value);
  const handleChangeSegundonombreDES = (e) => setsegundonombreDES(e.target.value);
  const handleChangeTelefonoDES = (e) => settelefonoDES(e.target.value);
  const handleChangeNumeroDES = (e) => setnumeroDES(e.target.value);
  const handleChangeEmailDES = (e) => setemailDES(e.target.value);

  const isStepOptional = (step) => {
    return step === 1;
  };

  const isStepSkipped = (step) => {
    return skipped.has(step);
  };

  const handleNext = () => {
    let newSkipped = skipped;
    if (isStepSkipped(activeStep)) {
      newSkipped = new Set(newSkipped.values());
      newSkipped.delete(activeStep);
    }

    setActiveStep((prevActiveStep) => prevActiveStep + 1);
    setSkipped(newSkipped);
  };


  const handleOpenModal = () => {
    setOpenModal(true);
  };

  const handleCloseModal = () => {
    setOpenModal(false);
    handleNext();
  };

  const handleFinish = () => {
    handleOpenModal(); // Abre el modal al hacer clic en 'Finalizar'
    // Puedes agregar más lógica aquí si es necesario
  };


  const handleBack = () => {
    setActiveStep((prevActiveStep) => prevActiveStep - 1);
  };

  const handleSkip = () => {
    if (!isStepOptional(activeStep)) {
      // You probably want to guard against something like this,
      // it should never occur unless someone's actively trying to break something.
      throw new Error("You can't skip a step that isn't optional.");
    }

    setActiveStep((prevActiveStep) => prevActiveStep + 1);
    setSkipped((prevSkipped) => {
      const newSkipped = new Set(prevSkipped.values());
      newSkipped.add(activeStep);
      return newSkipped;
    });
  };

  const handleReset = () => {
    setActiveStep(0);
  };

  const getStepContent = (step) => {
    switch (step) {
      case 0:
        return (
          <React.Fragment>
            <Typography sx={{ mt: 2, mb: 1 }}>Paso {activeStep + 1}</Typography>
            <h2 className="text-2xl mb-2 text-[#52489C] text-left font-bold">
              Datos personales del cliente
            </h2>
            <h2 className="text-3m mb-2 text-[#000000] text-left font-bold">
              Tipo de documento
            </h2>
            <div className="flex flex-col gap-4">
              <BasicSelect required value={tipoDocREM} setValue={settipoDocREM} />
              <h2 className="flex flex-col gap-3 text-3m mb-2 text-[#000000] text-left font-bold">
                Número de documento
                <TextField required id="filled-basic" label="Ej. 742056989" variant="filled" sx={{ width: '40%' }}
                  value={numDocREM} onChange={handleChangeNumDocREM} />
              </h2>
              <h2 className="flex flex-row gap-2 text-3m mb-2 text-[#000000] text-left font-bold">
                <Box
                  display="flex"
                  justifyContent="space-between"
                  width="100%"
                  mt={1}
                  gap={2}
                >
                  <Box display="flex" flexDirection="column" alignItems="left" width="30%">
                    <h2 className="flex flex-col gap-3 text-3m mb-2 text-[#000000] text-left font-bold">
                      Apellido
                    </h2>
                    <TextField required id="apellido" label="Ej. Cruzalegui" variant="filled" fullWidth
                      value={apellidoREM} onChange={handleChangeApellidoREM} />
                  </Box>
                  <Box display="flex" flexDirection="column" alignItems="left" width="30%">
                    <h2 className="flex flex-col gap-3 text-3m mb-2 text-[#000000] text-left font-bold">
                      Nombre
                    </h2>
                    <TextField required id="nombre" label="Ej. Miguel" variant="filled" fullWidth
                      value={nombreREM} onChange={handleChangeNombreREM} />
                  </Box>
                  <Box display="flex" flexDirection="column" alignItems="left" width="30%">
                    <h2 className="flex flex-col gap-3 text-3m mb-2 text-[#000000] text-left font-bold">
                      Segundo nombre
                    </h2>
                    <TextField required id="segundo-nombre" label="Ej. David" variant="filled" fullWidth
                      value={segundonombreREM} onChange={handleChangeSegundonombreREM} />
                  </Box>

                </Box>
              </h2>
              <h2 className="flex flex-row gap-2 text-3m mb-2 text-[#000000] text-left font-bold">
                <Box
                  display="flex"
                  justifyContent="flex-start"
                  width="100%"
                  gap={1}
                >
                  <Box display="flex" flexDirection="column" alignItems="left" width="35%">
                    <h2 className="flex flex-col gap-3 text-3m mb-2 text-[#000000] text-left font-bold">
                      Teléfono
                    </h2>
                    <SelectVariants required numCode={telefonoREM} setnumCode={settelefonoREM} />
                  </Box>
                  <Box display="flex" flexDirection="column" alignItems="left" width="30%">
                    <h2 className="flex flex-col text-3m mb-2 text-[#000000] text-left font-bold">
                      Número
                    </h2>
                    <TextField required id="nombre" label="Ej. 985632599" variant="filled" fullWidth
                      value={numeroREM} onChange={handleChangeNumeroREM} />
                  </Box>

                </Box>
              </h2>

              <h2 className="flex flex-row gap-2 text-3m mb-2 text-[#000000] text-left font-bold">
                <Box
                  display="flex"
                  justifyContent="flex-start"
                  width="100%"
                  gap={1}
                >
                  <Box display="flex" flexDirection="column" alignItems="left" width="50%">
                    <h2 className="flex flex-col text-3m mb-2 text-[#000000] text-left font-bold">
                      Correo electrónico
                    </h2>
                    <TextField required id="nombre" label="Ej. miguel.david@gmail.com" variant="filled" fullWidth
                      value={emailREM} onChange={handleChangeEmailREM} />
                  </Box>

                </Box>
              </h2>

            </div>
            <Box sx={{ display: 'flex', flexDirection: 'row', pt: 2 }}>

            </Box>
          </React.Fragment>
        )
      case 1:
        return (
          <React.Fragment>
            <Typography sx={{ mt: 2, mb: 1 }}>Paso {activeStep + 1}</Typography>
            <h2 className="text-2xl mb-2 text-[#52489C] text-left font-bold">
              Destino
            </h2>

            <h2 className="text-3m mb-2 text-[#000000] text-left font-bold">
              Ciudad de origen
            </h2>

            <div className="flex flex-col gap-4">
              <SelectVariantsCity required city={ciudadOrigen} setCity={setciudadOrigen} />
              <h2 className="flex flex-col gap-3 text-3m mb-2 text-[#000000] text-left font-bold">
                Ciudad de destino
                <SelectVariantsCity required city={ciudadDestino} setCity={setciudadDestino} />
              </h2>

              <h2 className="text-2xl mb-2 text-[#52489C] text-left font-bold">
                Paquetes
              </h2>
              <Box display="flex" flexDirection="column" alignItems="left" width="30%">
                <h2 className="flex flex-col gap-3 text-3m mb-2 text-[#000000] text-left font-bold" >
                  Número de paquetes
                </h2>
                <TextField required id="apellido" label="Ej. 3" variant="filled" fullWidth
                  value={numPaquetes} onChange={handleChangeNumPaquetes} />
              </Box>
              <h2 className="flex flex-row gap-2 text-3m mb-2 text-[#000000] text-left font-bold">

              </h2>

            </div>
            <Box sx={{ display: 'flex', flexDirection: 'row', pt: 2 }}>

            </Box>
          </React.Fragment>
        );
      case 2:
        return (
          <React.Fragment>
            <Typography sx={{ mt: 2, mb: 1 }}>Paso {activeStep + 1}</Typography>
            <h2 className="text-2xl mb-2 text-[#52489C] text-left font-bold">
              Datos del receptor
            </h2>
            <h2 className="text-3m mb-2 text-[#000000] text-left font-bold">
              Tipo de documento
            </h2>
            <div className="flex flex-col gap-4">
              <BasicSelect required value={tipoDocDES} setValue={settipoDocDES} />
              <h2 className="flex flex-col gap-3 text-3m mb-2 text-[#000000] text-left font-bold">
                Número de documento
                <TextField required id="filled-basic" label="Ej. 742056989" variant="filled" sx={{ width: '40%' }}
                  value={numDocDES} onChange={(handleChangeNumDocDES)} />
              </h2>
              <h2 className="flex flex-row gap-2 text-3m mb-2 text-[#000000] text-left font-bold">
                <Box
                  display="flex"
                  justifyContent="space-between"
                  width="100%"
                  mt={1}
                  gap={2}
                >
                  <Box display="flex" flexDirection="column" alignItems="left" width="30%">
                    <h2 className="flex flex-col gap-3 text-3m mb-2 text-[#000000] text-left font-bold">
                      Apellido
                    </h2>
                    <TextField required id="apellido" label="Ej. Cruzalegui" variant="filled" fullWidth
                      value={apellidoDES} onChange={handleChangeApellidoDES} />
                  </Box>
                  <Box display="flex" flexDirection="column" alignItems="left" width="30%">
                    <h2 className="flex flex-col gap-3 text-3m mb-2 text-[#000000] text-left font-bold">
                      Nombre
                    </h2>
                    <TextField required id="nombre" label="Ej. Miguel" variant="filled" fullWidth
                      value={nombreDES} onChange={handleChangeNombreDES} />
                  </Box>
                  <Box display="flex" flexDirection="column" alignItems="left" width="30%">
                    <h2 className="flex flex-col gap-3 text-3m mb-2 text-[#000000] text-left font-bold">
                      Segundo nombre
                    </h2>
                    <TextField required id="segundo-nombre" label="Ej. David" variant="filled" fullWidth
                      value={segundonombreDES} onChange={handleChangeSegundonombreDES} />
                  </Box>

                </Box>
              </h2>
              <h2 className="flex flex-row gap-2 text-3m mb-2 text-[#000000] text-left font-bold">
                <Box
                  display="flex"
                  justifyContent="flex-start"
                  width="100%"
                  gap={1}
                >
                  <Box display="flex" flexDirection="column" alignItems="left" width="35%">
                    <h2 className="flex flex-col gap-3 text-3m mb-2 text-[#000000] text-left font-bold">
                      Teléfono
                    </h2>
                    <SelectVariants required numCode={telefonoDES} setnumCode={settelefonoDES} />
                  </Box>
                  <Box display="flex" flexDirection="column" alignItems="left" width="30%">
                    <h2 className="flex flex-col text-3m mb-2 text-[#000000] text-left font-bold">
                      Número
                    </h2>
                    <TextField required id="nombre" label="Ej. 985632599" variant="filled" fullWidth
                      value={numeroDES} onChange={handleChangeNumeroDES} />
                  </Box>

                </Box>
              </h2>

              <h2 className="flex flex-row gap-2 text-3m mb-2 text-[#000000] text-left font-bold">
                <Box
                  display="flex"
                  justifyContent="flex-start"
                  width="100%"
                  gap={1}
                >
                  <Box display="flex" flexDirection="column" alignItems="left" width="50%">
                    <h2 className="flex flex-col text-3m mb-2 text-[#000000] text-left font-bold">
                      Correo electrónico
                    </h2>
                    <TextField required id="nombre" label="Ej. miguel.david@gmail.com" variant="filled" fullWidth
                      value={emailDES} onChange={handleChangeEmailDES} />
                  </Box>

                </Box>
              </h2>

            </div>
            <Box sx={{ display: 'flex', flexDirection: 'row', pt: 2 }}>

            </Box>
          </React.Fragment>
        );
      case 3:
        return (
          <div>
            <React.Fragment>
              <Typography sx={{ mt: 2, mb: 1 }}>Paso {activeStep + 1}</Typography>
              <h2 className="text-2xl mb-2 text-[#52489C] text-left font-bold">
                Confirmar envío
              </h2>
              <h2 className="text-3m mb-2 text-[#000000] text-left font-bold">
                Tipo de documento
              </h2>
              <div className="flex flex-col gap-4">
                <BasicSelect disabled={true} value={tipoDocREM} setValue={settipoDocREM} />
                <h2 className="flex flex-col gap-3 text-3m mb-2 text-[#000000] text-left font-bold">
                  Número de documento
                  <TextField disabled id="filled-basic" label="Número de documento" variant="filled" sx={{ width: '40%' }}
                    value={numDocREM} />
                </h2>
                <h2 className="flex flex-row gap-2 text-3m mb-2 text-[#000000] text-left font-bold">
                  <Box
                    display="flex"
                    justifyContent="space-between"
                    width="100%"
                    mt={1}
                    gap={2}
                  >
                    <Box display="flex" flexDirection="column" alignItems="left" width="30%">
                      <h2 className="flex flex-col gap-3 text-3m mb-2 text-[#000000] text-left font-bold">
                        Apellido
                      </h2>
                      <TextField disabled id="apellido" label="Apellido" variant="filled" fullWidth
                        value={apellidoREM} />
                    </Box>
                    <Box display="flex" flexDirection="column" alignItems="left" width="30%">
                      <h2 className="flex flex-col gap-3 text-3m mb-2 text-[#000000] text-left font-bold">
                        Nombre
                      </h2>
                      <TextField disabled id="nombre" label="Nombre" variant="filled" fullWidth
                        value={nombreREM} onChange={setnombreREM} />
                    </Box>
                    <Box display="flex" flexDirection="column" alignItems="left" width="30%">
                      <h2 className="flex flex-col gap-3 text-3m mb-2 text-[#000000] text-left font-bold">
                        Segundo nombre
                      </h2>
                      <TextField disabled id="segundo-nombre" label="Segundo nombre" variant="filled" fullWidth
                        value={segundonombreREM} onChange={setsegundonombreREM} />
                    </Box>

                  </Box>
                </h2>
                <h2 className="flex flex-row gap-2 text-3m mb-2 text-[#000000] text-left font-bold">
                  <Box
                    display="flex"
                    justifyContent="flex-start"
                    width="100%"
                    gap={1}
                  >
                    <Box display="flex" flexDirection="column" alignItems="left" width="35%">
                      <h2 className="flex flex-col gap-3 text-3m mb-2 text-[#000000] text-left font-bold">
                        Teléfono
                      </h2>
                      <SelectVariants disabled={true} numCode={telefonoREM} setnumCode={settelefonoREM} />
                    </Box>
                    <Box display="flex" flexDirection="column" alignItems="left" width="30%">
                      <h2 className="flex flex-col text-3m mb-2 text-[#000000] text-left font-bold">
                        Número
                      </h2>
                      <TextField disabled id="nombre" label="Número" variant="filled" fullWidth
                        value={numeroREM} />
                    </Box>

                  </Box>
                </h2>

                <h2 className="flex flex-row gap-2 text-3m mb-2 text-[#000000] text-left font-bold">
                  <Box
                    display="flex"
                    justifyContent="flex-start"
                    width="100%"
                    gap={1}
                  >
                    <Box display="flex" flexDirection="column" alignItems="left" width="50%">
                      <h2 className="flex flex-col text-3m mb-2 text-[#000000] text-left font-bold">
                        Correo electrónico
                      </h2>
                      <TextField disabled id="nombre" label="Email" variant="filled" fullWidth
                        value={emailREM} />
                    </Box>

                  </Box>
                </h2>

              </div>
              <Box sx={{ display: 'flex', flexDirection: 'row', pt: 2 }}>

              </Box>
            </React.Fragment>

            <React.Fragment>
              <h2 className="text-2xl mb-2 text-[#52489C] text-left font-bold">
                Destino
              </h2>

              <h2 className="text-3m mb-2 text-[#000000] text-left font-bold">
                Ciudad de origen
              </h2>

              <div className="flex flex-col gap-4">
                <SelectVariantsCity disabled={true} city={ciudadOrigen} setCity={setciudadOrigen} />
                <h2 className="flex flex-col gap-3 text-3m mb-2 text-[#000000] text-left font-bold">
                  Ciudad de destino
                  <SelectVariantsCity disabled={true} city={ciudadDestino} setCity={setciudadDestino} />
                </h2>

                <h2 className="text-2xl mb-2 text-[#52489C] text-left font-bold">
                  Paquetes
                </h2>
                <Box display="flex" flexDirection="column" alignItems="left" width="30%">
                  <h2 className="flex flex-col gap-3 text-3m mb-2 text-[#000000] text-left font-bold" >
                    Número de paquetes
                  </h2>
                  <TextField disabled id="apellido" label="Paquetes" variant="filled" fullWidth
                    value={numPaquetes} />
                </Box>
                <h2 className="flex flex-row gap-2 text-3m mb-2 text-[#000000] text-left font-bold">

                </h2>

              </div>
              <Box sx={{ display: 'flex', flexDirection: 'row', pt: 2 }}>

              </Box>
            </React.Fragment>

            <React.Fragment>
              <h2 className="text-2xl mb-2 text-[#52489C] text-left font-bold">
                Datos del receptor
              </h2>
              <h2 className="text-3m mb-2 text-[#000000] text-left font-bold">
                Tipo de documento
              </h2>
              <div className="flex flex-col gap-4">
                <BasicSelect disabled={true} value={tipoDocDES} setValue={settipoDocDES} />
                <h2 className="flex flex-col gap-3 text-3m mb-2 text-[#000000] text-left font-bold">
                  Número de documento
                  <TextField disabled id="filled-basic" label="Número de documento" variant="filled" sx={{ width: '40%' }}
                    value={numDocDES} />
                </h2>
                <h2 className="flex flex-row gap-2 text-3m mb-2 text-[#000000] text-left font-bold">
                  <Box
                    display="flex"
                    justifyContent="space-between"
                    width="100%"
                    mt={1}
                    gap={2}
                  >
                    <Box display="flex" flexDirection="column" alignItems="left" width="30%">
                      <h2 className="flex flex-col gap-3 text-3m mb-2 text-[#000000] text-left font-bold">
                        Apellido
                      </h2>
                      <TextField disabled id="apellido" label="Apellido" variant="filled" fullWidth
                        value={apellidoDES} />
                    </Box>
                    <Box display="flex" flexDirection="column" alignItems="left" width="30%">
                      <h2 className="flex flex-col gap-3 text-3m mb-2 text-[#000000] text-left font-bold">
                        Nombre
                      </h2>
                      <TextField disabled id="nombre" label="Nombre" variant="filled" fullWidth
                        value={nombreDES} />
                    </Box>
                    <Box display="flex" flexDirection="column" alignItems="left" width="30%">
                      <h2 className="flex flex-col gap-3 text-3m mb-2 text-[#000000] text-left font-bold">
                        Segundo nombre
                      </h2>
                      <TextField disabled id="segundo-nombre" label="Segundo nombre" variant="filled" fullWidth
                        value={segundonombreDES} />
                    </Box>

                  </Box>
                </h2>
                <h2 className="flex flex-row gap-2 text-3m mb-2 text-[#000000] text-left font-bold">
                  <Box
                    display="flex"
                    justifyContent="flex-start"
                    width="100%"
                    gap={1}
                  >
                    <Box display="flex" flexDirection="column" alignItems="left" width="35%">
                      <h2 className="flex flex-col gap-3 text-3m mb-2 text-[#000000] text-left font-bold">
                        Teléfono
                      </h2>
                      <SelectVariants disabled={true} numCode={telefonoDES} setnumCode={settelefonoDES} />
                    </Box>
                    <Box display="flex" flexDirection="column" alignItems="left" width="30%">
                      <h2 className="flex flex-col text-3m mb-2 text-[#000000] text-left font-bold">
                        Número
                      </h2>
                      <TextField disabled id="nombre" label="Número" variant="filled" fullWidth
                        value={numeroDES} />
                    </Box>

                  </Box>
                </h2>

                <h2 className="flex flex-row gap-2 text-3m mb-2 text-[#000000] text-left font-bold">
                  <Box
                    display="flex"
                    justifyContent="flex-start"
                    width="100%"
                    gap={1}
                  >
                    <Box display="flex" flexDirection="column" alignItems="left" width="50%">
                      <h2 className="flex flex-col text-3m mb-2 text-[#000000] text-left font-bold">
                        Correo electrónico
                      </h2>
                      <TextField disabled id="nombre" label="Email" variant="filled" fullWidth
                        value={emailDES} />
                    </Box>

                  </Box>
                </h2>

              </div>
              <Box sx={{ display: 'flex', flexDirection: 'row', pt: 2 }}>

              </Box>
            </React.Fragment>

          </div>
        );
      default:
        return 'Paso desconocido';
    }
  };

  return (
    <Box sx={{ width: '100%' }}>
      <Stepper activeStep={activeStep}>
        {steps.map((label, index) => {
          const stepProps = {};
          const labelProps = {};
          if (isStepOptional(index)) {
            labelProps.optional = (
              <Typography variant="caption"></Typography>
            );
          }
          if (isStepSkipped(index)) {
            stepProps.completed = false;
          }
          return (
            <Step key={label} {...stepProps}>
              <StepLabel {...labelProps}>{label}</StepLabel>
            </Step>
          );
        })}
      </Stepper>
      <div>
        {activeStep === steps.length ? (
          <React.Fragment>
            <Typography className="flex flex-col text-3l mb-2 text-[#000000] text-center font-bold" sx={{ mt: 2, mb: 1 }}>
              Tu envío fue registrado con éxito
            </Typography>
            <Box sx={{ display: 'flex', flexDirection: 'row', pt: 2 }}>
              <Box sx={{ flex: '1 1 auto' }} />
              <Button sx={{ color: '#52489C', backgroundColor: "#FFFFFF" }} onClick={handleReset}>Registrar otro envío</Button>
            </Box>
          </React.Fragment>
        ) : (
          <React.Fragment>
            <Typography sx={{ mt: 2, mb: 1 }}>{getStepContent(activeStep)}</Typography>
            <Box sx={{ display: 'flex', flexDirection: 'row', pt: 2 }}>
              <Button
                color="inherit"
                disabled={activeStep === 0}
                onClick={handleBack}
                sx={{ mr: 1, color: '#84A98C' }}
              >
                Atrás
              </Button>
              <Box sx={{ flex: '1 1 auto' }} />
              <Button
                sx={{ color: '#52489C', backgroundColor: "#FFFFFF" }}
                variant="contained"
                onClick={activeStep === steps.length - 1 ? handleFinish : handleNext}>
                {activeStep === steps.length - 1 ? 'Finalizar' : 'Siguiente'}
              </Button>

              <Modal
                open={openModal}
                onClose={handleCloseModal}
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
                  <h2 className="text-2xl mb-2 text-[#52489C] text-left font-bold">
                  ¡Códigos de rastreo generados!
                  </h2>
                  <Typography id="modal-description" sx={{ mt: 2 }}>
                    Contenido del modal. Puedes poner aquí cualquier contenido que desees mostrar dentro del modal.
                  </Typography>
                  <Button onClick={handleCloseModal} sx={{ mt: 2, color: '#52489C', backgroundColor: "#FFFFFF"  }}>Terminar</Button>
                </Box>
              </Modal>

            </Box>
          </React.Fragment>
        )}
      </div>
    </Box>
  );

  /* 
  return (
    <Box sx={{ width: '100%' }}>
      <Stepper activeStep={activeStep}>
        {steps.map((label, index) => {
          const stepProps = {};
          const labelProps = {};
          if (isStepOptional(index)) {
            labelProps.optional = (
              <Typography variant="caption">Siguiente paso</Typography>
            );
          }
          if (isStepSkipped(index)) {
            stepProps.completed = false;
          }
          return (
            <Step key={label} {...stepProps}>
              <StepLabel {...labelProps}>{label}</StepLabel>
            </Step>
          );
        })}
      </Stepper>
      {activeStep === steps.length ? (
        <React.Fragment>
          <Typography sx={{ mt: 2, mb: 1 }}>
            All steps completed - you&apos;re finished
          </Typography>
          <Box sx={{ display: 'flex', flexDirection: 'row', pt: 2 }}>
            <Box sx={{ flex: '1 1 auto' }} />
            <Button onClick={handleReset}>Reset</Button>
          </Box>
        </React.Fragment>
      ) : (
        <React.Fragment>
          <Typography sx={{ mt: 2, mb: 1 }}>Paso {activeStep + 1}</Typography>
          <h2 className="text-3m mb-2 text-[#000000] text-left font-bold">
                    Tipo de documento
          </h2>
          <div className="flex flex-col gap-4">
            <BasicSelect/>
            <h2 className="flex flex-col gap-3 text-3m mb-2 text-[#000000] text-left font-bold">
                    Número de documento
                    <TextField id="filled-basic" label="Ej. 742056989" variant="filled" sx={{ width: '40%' }}/>
            </h2>
            <h2 className="flex flex-row gap-2 text-3m mb-2 text-[#000000] text-left font-bold">
              <Box
                display="flex"
                justifyContent="space-between"
                width="100%"
                mt={1}
                gap={2}
              >
                <Box display="flex" flexDirection="column" alignItems="left" width="30%">
                  <h2 className="flex flex-col gap-3 text-3m mb-2 text-[#000000] text-left font-bold">
                      Apellido
                  </h2>
                  <TextField id="apellido" label="Ej. Cruzalegui" variant="filled" fullWidth />
                </Box>
                <Box display="flex" flexDirection="column" alignItems="left" width="30%">
                  <h2 className="flex flex-col gap-3 text-3m mb-2 text-[#000000] text-left font-bold">
                        Nombre
                  </h2>
                  <TextField id="nombre" label="Ej. Miguel" variant="filled" fullWidth />
                </Box>
                <Box display="flex" flexDirection="column" alignItems="left" width="30%">
                  <h2 className="flex flex-col gap-3 text-3m mb-2 text-[#000000] text-left font-bold">
                        Segundo nombre
                  </h2>
                  <TextField id="segundo-nombre" label="Ej. David" variant="filled" fullWidth />
                </Box>

              </Box>  
            </h2>
            <h2 className="flex flex-row gap-2 text-3m mb-2 text-[#000000] text-left font-bold">
              <Box
                display="flex"
                justifyContent="flex-start"
                width="100%"
                gap={1}
              >
                <Box display="flex" flexDirection="column" alignItems="left" width="35%">
                  <h2 className="flex flex-col gap-3 text-3m mb-2 text-[#000000] text-left font-bold">
                      Teléfono
                  </h2>
                  <SelectVariants/>
                </Box>
                <Box display="flex" flexDirection="column" alignItems="left" width="30%">
                  <h2 className="flex flex-col text-3m mb-2 text-[#000000] text-left font-bold">
                        Número
                  </h2>
                  <TextField id="nombre" label="Ej. 985632599" variant="filled" fullWidth />
                </Box>
              </Box>  
            </h2>
          </div>

          
          <Box sx={{ display: 'flex', flexDirection: 'row', pt: 2 }}>
          
            <Button
              color="inherit"
              disabled={activeStep === 0}
              onClick={handleBack}
              sx={{ mr: 1 , color: '#84A98C'}}
            >
              Atrás
            </Button>
            <Box sx={{ flex: '1 1 auto' }} />
            {isStepOptional(activeStep) && (
              <Button color="inherit" onClick={handleSkip} sx={{ mr:1, color: '#84A98C' }}>
                Skip
              </Button>
            )}

            <Button 
              onClick={handleNext} 
              sx={{ color: '#52489C' }} 
              variant="contained"
            >
              
              {activeStep === steps.length - 1 ? 'Finish' : 'Siguiente'}
            </Button>
          </Box>
        </React.Fragment>
      )}
    </Box>
  );
  */
}
