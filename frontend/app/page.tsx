"use client";

import React, { useEffect, useState } from 'react';
import axios from 'axios';
import Image from "next/image";

import { Usuario } from "../types/Usuario"

export default function Home() {
  const [usuario, setUsuario] = useState<Usuario|null>(null);
  const api_url = 'http://localhost:8080/usuario';

  useEffect(() => {
    axios.get(api_url)
      .then(response => {
        setUsuario(response.data);
      })
      .catch(error => {
        console.error('Error fetching data: ', error);
      })
  }, []);

  if (!usuario) {
    return <div>Loading...</div>;
  }

  return (
    <main className="flex min-h-screen flex-col items-center justify-between p-24">
      <div className="z-10 max-w-5xl w-full items-center justify-between font-mono text-sm lg:flex">
        <h1>{usuario.username}</h1>
        <p>{usuario.email}</p>
      </div>
    </main>
  );
}