import React from 'react';
import logoRegEnv from '../logos/registroEnvio.png'; // Importa la ruta de la imagen
import Image from 'next/image';

const LogoRegEnv = () => (
  <Image src={logoRegEnv} alt="Logo de Reg Env" height={50}/>
);

export default LogoRegEnv;