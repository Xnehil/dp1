"use client"
import axios from "axios";
import TablaVuelosEnVivo from './TablaVuelosEnVivo.jsx'
import { useEffect, useState } from 'react';

const createData = (name, calories, fat, carbs, protein) => {
  return { name, calories, fat, carbs, protein };
};

export default function Page() {
  const apiURL = process.env.REACT_APP_API_URL_BASE;
  const [vuelos, setVuelos] = useState([]);
  const [loading, setLoading] = useState(true); // Variable de estado para controlar la carga

  useEffect(() => {
    const fetchActiveFlights = () => {
      axios.get(`${apiURL}/vuelo/enAire`)
        .then((response) => {
          setVuelos(response.data);
          setLoading(false); // Se establece loading en falso cuando se completa la petición
          console.log("Vuelos cargados: ", response.data);
        })
        .catch((error) => {
          console.error(error);
          setLoading(false); // Se establece loading en falso si ocurre un error
        });
    };

    // Fetch active flights immediately
    fetchActiveFlights();

    // Then fetch active flights every minute
    // const intervalId = setInterval(fetchActiveFlights, 60 * 1000);

    // // Clean up the interval on component unmount
    // return () => clearInterval(intervalId);
  }, []);

  return (
    <div className="w-4/5 m-auto text-center">
      <h2 className="text-3xl mb-5">Gestión de vuelos en vivo</h2>
      {loading ? ( // Renderizar el texto de carga mientras loading es verdadero
        <p>Cargando...</p>
      ) : (
        <TablaVuelosEnVivo vuelos={vuelos} />
      )}
    </div>
  );
}
