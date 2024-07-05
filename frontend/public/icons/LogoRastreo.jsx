import React from 'react';
import logoRastreo from '../logos/rastreo.png'; // Importa la ruta de la imagen
import Image from 'next/image';

const LogoRastreo = () => (
  <Image src={logoRastreo} alt="Logo de Rastreo" height={50}/>
);

export default LogoRastreo;