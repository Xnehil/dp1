import * as React from 'react';
import Box from '@mui/material/Box';
import Stepper from '@mui/material/Stepper';
import Step from '@mui/material/Step';
import StepLabel from '@mui/material/StepLabel';
import Button from '@mui/material/Button';
import Typography from '@mui/material/Typography';
import TextField from '@mui/material/TextField'; 
import BasicSelect from "@/components/select/select.jsx";
import SelectVariants from "@/components/select/selectNumCode.jsx";
import SelectVariantsCity from "@/components/select/selectCity.jsx"

const steps = ['Datos del cliente', 'Destino y paquetes', 'Datos del receptor', 'Confirmar envío'];

export default function HorizontalLinearStepper() {
  const [activeStep, setActiveStep] = React.useState(0);
  const [skipped, setSkipped] = React.useState(new Set());

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
                      <TextField id="nombre" label="Ej. miguel.david@gmail.com" variant="filled" fullWidth />
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
            <SelectVariantsCity/>
            <h2 className="flex flex-col gap-3 text-3m mb-2 text-[#000000] text-left font-bold">
                    Ciudad de destino
                    <SelectVariantsCity/>
            </h2>

              <h2 className="text-2xl mb-2 text-[#52489C] text-left font-bold">
                        Paquetes
              </h2>  
              <Box display="flex" flexDirection="column" alignItems="left" width="30%">
                          <h2 className="flex flex-col gap-3 text-3m mb-2 text-[#000000] text-left font-bold" >
                              Número de paquetes
                          </h2>
                          <TextField id="apellido" label="Ej. 3" variant="filled" fullWidth />
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
                  <TextField id="nombre" label="Ej. miguel.david@gmail.com" variant="filled" fullWidth />
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
            <Typography>Confirmar envío</Typography>
            {/* Aquí puedes agregar los campos del formulario correspondientes al Paso 4 */}
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
      <div>
        {activeStep === steps.length ? (
          <React.Fragment>
            <Typography sx={{ mt: 2, mb: 1 }}>
              Todos los pasos completados - has terminado
            </Typography>
            <Box sx={{ display: 'flex', flexDirection: 'row', pt: 2 }}>
              <Box sx={{ flex: '1 1 auto' }} />
              <Button onClick={handleReset}>Resetear</Button>
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
                sx={{ mr: 1 , color: '#84A98C'}}
              >
                Atrás
              </Button>
              <Box sx={{ flex: '1 1 auto' }} />
              <Button 
                sx={{ color: '#52489C', backgroundColor: "#FFFFFF" }} 
                variant="contained"
                onClick={handleNext}>
                {activeStep === steps.length - 1 ? 'Finalizar' : 'Siguiente'}
              </Button>
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
