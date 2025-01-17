"use client";  // Añade esta línea al principio del archivo

import { useRouter } from 'next/navigation';
import axios from 'axios';
import React, { useEffect, useRef, useState } from 'react';
import { convertirHoraVuelo } from '@/utils/FuncionesTiempo';

const Modal = ({ isOpen, onClose, onTrack, mensajeError }) => {
  if (!isOpen) return null;

  const handleSubmit = (event) => {
    event.preventDefault();
    const code = event.target.elements.code.value;
    onTrack(code);
  };

  return (
    <div style={styles.overlay} onClick={onClose}>
      <div style={styles.modal} onClick={(e) => e.stopPropagation()}>
        <form onSubmit={handleSubmit}>
          <h2 className="text-2l mb-2 text-[#52489C] text-left font-bold">Inserta el código de tu paquete</h2>
          <input type="text" name="code" placeholder="Código" required style={styles.input} />
          {/* <p style={styles.reminderText}>
            ¿No lo recuerdas? <a href="#" style={styles.link}>Haz clic aquí</a>
          </p> */}
          <p style={{ color: 'red' }}>{mensajeError}</p>
          <div style={styles.buttonContainer}>
            <button type="button" style={styles.cancelButton} onClick={onClose}>
              Cancelar
            </button>
            <button type="submit" style={styles.trackButton}>
              Rastrear
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

const NewModal = ({ isOpen, onClose, paquete, envio, vuelos, aeropuertos }) => {
  if (!isOpen) return null;
  const renderDetails = () => {
    const originAirportCode = vuelos.length > 0 ? vuelos[0].origen : '';
    const destinationAirportCode = vuelos.length > 0 ? vuelos[vuelos.length - 1].destino : '';
    //const originCity = aeropuertos.get(originAirportCode)?.ciudad || 'N/A';
    //const destinationCity = aeropuertos.get(destinationAirportCode)?.ciudad || 'N/A';
    const destinollegado = paquete.llegodestino == "true" ? paquete.llegodestino = "En proceso" : "Terminado"
    const arrivalDate = new Date(envio.fechaHoraSalida);
    const formattedDate = new Date(arrivalDate.getTime() + ((5+(aeropuertos.get(envio.origen)?.gmt ?? 0)) * 3600 * 1000)).toLocaleString();
    const fechallegada = envio.fechaHoraLlegadaPrevista;
    // const formattedDateArrival = fechallegada.split('.')[0].replace('T', '  ');

    const ultimoVuelo = vuelos[vuelos.length - 1];
    const horaLlegada = new Date(ultimoVuelo.fechaHoraLlegada);
    //Eso solo tiene información de la hora de llegada, no de la fecha
    const fechaHoraLlegada = new Date(arrivalDate.getTime() + paquete.rutaPosible.flights[paquete.rutaPosible.flights.length - 1].diaRelativo * 24 * 3600 * 1000)
    fechaHoraLlegada.setHours(horaLlegada.getHours(), horaLlegada.getMinutes(), horaLlegada.getSeconds());
    // console.log('Fecha de llegada:', fechaHoraLlegada);
    const llego = fechaHoraLlegada < new Date();

    return (
      <div className="flex flex-row">
        <div className="w-2/5 pr-8">
          <h2 className="text-2xl mb-10 text-[#84A98C] text-left font-bold">
            Paquete {paquete.id}: {envio.origen} → {envio.destino}
          </h2>
          <h3 className="text-lg mb-5 text-[#52489C] text-left font-medium">
            <p className="mb-5 pl-10"><strong>Código:</strong> {paquete.id}</p>
            <p className="mb-5 pl-10"><strong>Ciudad de Origen:</strong> {aeropuertos.get(envio.origen)?.ciudad ?? ''} ({envio.origen})</p>
            <p className="mb-5 pl-10"><strong>Ciudad de Destino:</strong> {aeropuertos.get(envio.destino)?.ciudad ?? ''} ({envio.destino})</p>
            <p className="mb-5 pl-10"><strong>Estado:</strong> {!llego ? "En proceso" : "Terminado"}</p>
            <p className="mb-5 pl-10"><strong>Fecha de envío:</strong> {formattedDate}</p>
            {/* <p className="mb-5 pl-10"><strong>Fecha de entrega:</strong> {formattedDateArrival}</p> */}
            <p className="mb-5 pl-10"><strong>Emisor:</strong> {envio.emisor?.nombre ?? ''} {envio.emisor?.apellido ?? ''}</p>
            <p className="mb-5 pl-10"><strong>Receptor:</strong> {envio.receptor?.nombre ?? ''} {envio.receptor?.apellido ?? ''}</p>
            <p className="mb-5 pl-10"><strong>Número documento receptor:</strong> {envio.receptor?.numeroDocumento ?? ''}</p>
          </h3>
        </div>
        <div className="w-3/5 border-l border-gray-300 pl-8">
          <div className="overflow-y-auto" style={{ maxHeight: '450px' }}>
            <UbicacionDiagrama 
              origen={envio.origen}
              destino={envio.destino}
              escalas={vuelos.map(vuelo => vuelo.destino).slice(0, -1)}
              vuelos={vuelos}
              aeropuertos={aeropuertos}
            />
          </div>
        </div>
      </div>
    );
  };


  return (
    <div style={styles.overlay} onClick={onClose}>
      <div style={styles.modalLarge} onClick={(e) => e.stopPropagation()}>
        <button style={styles.closeButton} onClick={onClose}>
          Rastrear otro código
        </button>
        {renderDetails()}
      </div>
    </div>
  );



};

const UbicacionDiagrama = ({ origen, destino, escalas, vuelos, aeropuertos }) => {
  return (
    <div className="flex flex-col items-center justify-center h-full">
      <div className="flex flex-col items-center">
        <ItemUbicacionDiagrama ciudad={origen} aeropuertos={aeropuertos} />
        <ItemLineaDiagrama vuelo={vuelos[0]} aeropuertos={aeropuertos} />
        {escalas && escalas.map((escala, index) => (
          <React.Fragment key={index}>
            <ItemUbicacionDiagrama ciudad={escala} aeropuertos={aeropuertos} />
            <ItemLineaDiagrama vuelo={vuelos[index + 1]} aeropuertos={aeropuertos} />
          </React.Fragment>
        ))}
        <ItemUbicacionDiagrama ciudad={destino} aeropuertos={aeropuertos} />
      </div>
    </div>
  );
};

const ItemUbicacionDiagrama = ({ ciudad, aeropuertos }) => {
  const origenInfo = aeropuertos.get(ciudad);
return (
  <div className="mb-4 mt-4 text-center p-4 bg-gray-100 rounded-lg shadow-md">
    {origenInfo ? (
      <div>
        <h2 className="text-xl font-bold text-[#52489C]">{origenInfo.ciudad}, {origenInfo.pais}</h2>
        <p className="text-m text-gray-600">({ciudad})</p>
      </div>
    ) : (
      <h2 className="text-xl font-bold text-[#52489C]">{ciudad}</h2>
    )}
  </div>);
}

const ItemLineaDiagrama = ({ vuelo , aeropuertos}) => {
  if (!vuelo) return (
    <div className="h-16 w-0.5 bg-gray-300"></div>
  )
  return (
    <div className="flex flex-col items-center">
      <div className="mb-2 text-sm text-gray-600">{convertirHoraVuelo(vuelo.fechaHoraSalida, aeropuertos.get(vuelo.origen ?? "SKBO")?.gmt ?? 0)}</div>
      <div className="flex flex-col items-center">
        <div className="h-16 w-0.5 bg-gray-300"></div>
        <div className="text-m font-bold text-[#52489C] my-2">Vuelo número {vuelo.id}</div>
        <div className="h-16 w-0.5 bg-gray-300"></div>
      </div>
      <div className="mt-2 text-sm text-gray-600">{convertirHoraVuelo(vuelo.fechaHoraLlegada, aeropuertos.get(vuelo.destino ?? "SKBO")?.gmt ?? 0)}</div>
    </div>
  );
};


const App = () => {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isNewModalOpen, setNewModalOpen] = useState(false);
  const [paquete, setPaquete] = useState({});
  const [envio, setEnvio] = useState({});
  const [vuelos, setVuelos] = useState([]);
  const aeropuertos = useRef(new Map());
  const [cargasTerminadas, setCargasTerminadas] = useState(0);
  const [mensajeError, setMensajeError] = useState('');

  const apiURL = process.env.REACT_APP_API_URL_BASE;

  const openModal = () => {
    setIsModalOpen(true);
  };

  const closeModal = () => {
    setIsModalOpen(false);
  };

  const closeNewModal = () => {
    setNewModalOpen(false);
  };

  const handleTrack = async (code) => {
    console.log('Tracking code:', code);
    try {
      const response = await axios.get(`${apiURL}/paquete/${code}`);
      if (response.data =="") {
        setMensajeError('No se encontró un paquete con ese código');
        return
      }
      setPaquete(response.data);
      setCargasTerminadas((prev) => prev + 1);
      console.log(response.data); 
      closeModal();
    } catch (error) {
      console.error('Error rastreando el paquete:', error);
      setMensajeError('Ingrese un código correcto');
    }
  };

  useEffect(() => {
    if (paquete && paquete.rutaPosible && paquete.rutaPosible.flights) {
      const fetchFlights = async () => {
        try {
          const flightPromises = paquete.rutaPosible.flights.map(flight =>
            axios.get(`${apiURL}/vuelo/${flight.idVuelo}`)
          );
          const flightResponses = await Promise.all(flightPromises);
          const flights = flightResponses.map(response => response.data);
          console.log('Vuelos:', flights);
          setVuelos(flights);
          setCargasTerminadas((prev) => prev + 1);
        } catch (error) {
          console.error('Error trayendo los vuelos:', error);
        }
      };

      const fetchEnvio = async () => {
        try {
          const response = await axios.get(`${apiURL}/envio/codigo/${paquete.codigoEnvio}`);
          setEnvio(response.data);
          setCargasTerminadas((prev) => prev + 1);
          console.log('Envio:', response.data);
        } catch (error) {
          console.error('Error trayendo el envio:', error);
        }
      };
      fetchFlights();
      fetchEnvio();
    }
  }, [paquete]);

  useEffect(() => {
    if (vuelos && vuelos.length > 0) {
      const fetchAirports = async () => {
        try {
          const airportPromises = []
          for (let i = 0; i < vuelos.length; i++) {
            airportPromises.push(axios.get(`${apiURL}/aeropuerto/codigo/${vuelos[i].origen}`));
            if (i === vuelos.length - 1) {
              airportPromises.push(axios.get(`${apiURL}/aeropuerto/codigo/${vuelos[i].destino}`));
            }
          }
          const airportResponses = await Promise.all(airportPromises);
          const airports = airportResponses.map(response => response.data);
          aeropuertos.current = new Map(airports.map(airport => [airport.codigoOACI, airport]));
          setCargasTerminadas((prev) => prev + 1);
          console.log('Aeropuertos:', aeropuertos.current);
        } catch (error) {
          console.error('Error trayendo los aeropuertos:', error);

        }
      };
      fetchAirports();
    }
  }, [vuelos]);

  useEffect(() => {
    if (cargasTerminadas === 4) {
      setNewModalOpen(true);
      setCargasTerminadas(0);
    }
  }, [cargasTerminadas]);

  const containerStyle = {
    display: 'flex',
    backgroundColor: '#ffffff',
    boxShadow: '0 0 10px rgba(0, 0, 0, 0.1)',
    borderRadius: '10px',
    overflow: 'hidden',
    width: '80%',
    margin: 'auto',
    height: '80vh'
  };

  const imageContainerStyle = {
    flex: 1,
    backgroundColor: '#dfefff',
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center'
  };

  const imgStyle = {
    width: '100%',
    height: '100%',
    objectFit: 'cover'
  };

  const textContainerStyle = {
    flex: 1,
    padding: '20px',
    display: 'flex',
    flexDirection: 'column',
    justifyContent: 'center',
    alignItems: 'center',
    textAlign: 'center'
  };

  const headingStyle = {
    color: '#4a4a4a',
    marginBottom: '10px'
  };

  const paragraphStyle = {
    color: '#7a7a7a',
    marginBottom: '20px'
  };

  const buttonStyle = {
    backgroundColor: '#ebebeb',
    color: '#52489c',
    border: 'none',
    padding: '10px 20px',
    borderRadius: '5px',
    cursor: 'pointer',
    transition: 'background-color 0.3s'
  };

  const buttonHoverStyle = {
    backgroundColor: '#357ab8'
  };

  return (
    <div>
      <div style={containerStyle}>
        <div style={imageContainerStyle}>
          <img src="/logos/pantallaRastreo.jpg" style={imgStyle} />
        </div>
        <div style={textContainerStyle}>
          <h1 className="text-3m mb-2 text-[#52489C] text-left font-extrabold">¡Rastrea tu paquete aquí!</h1>
          <p style={paragraphStyle}>Busca y localiza tu paquete en tiempo real mediante el código brindado</p>
          <button
            style={buttonStyle}
            onClick={openModal}
          >
            Rastrea aquí
          </button>
        </div>
      </div>
      <Modal isOpen={isModalOpen} onClose={closeModal} onTrack={handleTrack}  mensajeError={mensajeError} />
      <NewModal isOpen={isNewModalOpen} onClose={closeNewModal} paquete={paquete} envio={envio} vuelos={vuelos} aeropuertos={aeropuertos.current} />
    </div>
  );
}

const styles = {
  overlay: {
    position: 'fixed',
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    backgroundColor: 'rgba(0, 0, 0, 0.7)',
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
  },
  modal: {
    backgroundColor: 'white',
    padding: '20px',
    borderRadius: '8px',
    width: '400px',
    position: 'relative',
    textAlign: 'center',
  },
  modalLarge: {
    backgroundColor: 'white', // Fondo blanco del modal grande
    padding: '20px',
    borderRadius: '8px',
    width: '1300px', // Ancho del modal grande
    height: '500px', // Alto del modal grande
    position: 'relative',
    textAlign: 'center',
    overflowY: 'auto', // Permite hacer scroll si el contenido es más largo que el modal
  },
  closeButton: {
    position: 'absolute',
    bottom: '10px', // Cambiado de 'top' a 'bottom'
    right: '10px',
    backgroundColor: 'transparent',
    border: 'none',
    fontSize: '16px',
    cursor: 'pointer',
    color: '#84a98c', 
    textDecoration: 'underline',
  },  
  input: {
    width: '100%',
    padding: '10px',
    margin: '10px 0',
    borderRadius: '4px',
    border: '1px solid #ddd',
  },
  reminderText: {
    fontSize: '14px',
    color: '#777',
  },
  link: {
    color: '#52489c',
    textDecoration: 'none',
  },
  buttonContainer: {
    display: 'flex',
    justifyContent: 'space-between',
    marginTop: '20px',
  },
  cancelButton: {
    backgroundColor: '#84a98c',
    color: '#fff',
    border: 'none',
    padding: '10px 20px',
    borderRadius: '4px',
    cursor: 'pointer',
  },
  trackButton: {
    backgroundColor: '#52489c',
    color: '#fff',
    border: 'none',
    padding: '10px 20px',
    borderRadius: '4px',
    cursor: 'pointer',
  },
};

export default App;