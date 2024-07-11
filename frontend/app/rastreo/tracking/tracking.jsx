"use client"; // Añade esta línea al principio del archivo

import React, { useEffect, useState } from 'react';
import { useRouter } from 'next/router';

const TrackingPage = () => {
  const router = useRouter();
  const { code } = router.query; // Obtener el código del paquete de la URL
  const [data, setData] = useState(null);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (code) {
      fetch(`https://your-api-url.com/paquete/${code}`) // Reemplaza 'your-api-url.com' con la URL de tu API
        .then(response => response.json())
        .then(data => setData(data))
        .catch(error => setError(error));
    }
  }, [code]);

  if (error) return <div>Error al obtener la información del paquete</div>;
  if (!data) return <div>Cargando...</div>;

  return (
    <div>
      <h1>Información del Paquete</h1>
      <pre>{JSON.stringify(data, null, 2)}</pre>
    </div>
  );
};

export default TrackingPage;