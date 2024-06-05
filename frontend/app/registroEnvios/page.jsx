"use client";

import Link from "next/link";
import React, { useState } from "react";
import "react-datepicker/dist/react-datepicker.css";

// pages/index.js

const RegistroEnvio = () => {
  const [formData, setFormData] = useState({
    documentoTipo: '',
    documentoNumero: '',
    apellido: '',
    nombre: '',
    segundoNombre: '',
    telefono: '',
    correo: ''
  });

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    console.log('Formulario enviado', formData);
    // Aquí puedes manejar el envío del formulario
  };

  const styles = {
    container: {
      maxWidth: '600px',
      margin: '0 auto',
      padding: '20px',
      border: '1px solid #ccc',
      borderRadius: '8px',
      backgroundColor: '#f9f9f9'
    },
    header: {
      textAlign: 'center'
    },
    form: {
      display: 'flex',
      flexDirection: 'column'
    },
    formGroup: {
      marginBottom: '15px'
    },
    label: {
      display: 'block',
      marginBottom: '5px'
    },
    input: {
      width: '100%',
      padding: '8px',
      boxSizing: 'border-box'
    },
    button: {
      display: 'block',
      width: '100%',
      padding: '10px',
      backgroundColor: '#007bff',
      color: '#fff',
      border: 'none',
      borderRadius: '4px',
      cursor: 'pointer'
    },
    buttonHover: {
      backgroundColor: '#0056b3'
    }
  };

  return (
    <div style={styles.container}>
      <h2 style={styles.header}>Registro de envío</h2>
      <form onSubmit={handleSubmit} style={styles.form}>
        <div style={styles.formGroup}>
          <label style={styles.label}>Tipo de documento</label>
          <select name="documentoTipo" value={formData.documentoTipo} onChange={handleChange} style={styles.input}>
            <option value="">Seleccione...</option>
            <option value="DNI">DNI</option>
            <option value="Pasaporte">Pasaporte</option>
          </select>
        </div>
        <div style={styles.formGroup}>
          <label style={styles.label}>Número de documento</label>
          <input type="text" name="documentoNumero" value={formData.documentoNumero} onChange={handleChange} style={styles.input} />
        </div>
        <div style={styles.formGroup}>
          <label style={styles.label}>Apellido</label>
          <input type="text" name="apellido" value={formData.apellido} onChange={handleChange} style={styles.input} />
        </div>
        <div style={styles.formGroup}>
          <label style={styles.label}>Nombre</label>
          <input type="text" name="nombre" value={formData.nombre} onChange={handleChange} style={styles.input} />
        </div>
        <div style={styles.formGroup}>
          <label style={styles.label}>Segundo Nombre</label>
          <input type="text" name="segundoNombre" value={formData.segundoNombre} onChange={handleChange} style={styles.input} />
        </div>
        <div style={styles.formGroup}>
          <label style={styles.label}>Teléfono</label>
          <input type="text" name="telefono" value={formData.telefono} onChange={handleChange} style={styles.input} />
        </div>
        <div style={styles.formGroup}>
          <label style={styles.label}>Correo electrónico</label>
          <input type="email" name="correo" value={formData.correo} onChange={handleChange} style={styles.input} />
        </div>
        <button type="submit" style={styles.button}>Siguiente</button>
      </form>
    </div>
  );
};

export default RegistroEnvio;
