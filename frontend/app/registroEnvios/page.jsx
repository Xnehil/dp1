"use client";

import Link from "next/link";
import React, { useState } from "react";
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";
import { FaCalendarAlt } from "react-icons/fa";

function ConfiguracionRegistro({ buttonText, activeTab }) {
    const [startDate, setStartDate] = useState(new Date("2024-01-12T05:36:52"));
    
    return (
        <div className="flex flex-col items-center mt-10 w-full">
            <div className="w-full max-w-4xl px-10">
                <h2 className="text-3xl mb-2 text-[#161616] text-left">
                    Registro de envío
                </h2>
                <p className="mb-4 text-[#525252] text-left">
                    Elige los parámetros para la simulación
                </p>
                <div className="border rounded-md p-4 mb-4 bg-gray-100">
                    <label className="block mb-2 text-left">
                        Fecha de inicio (dd/mm/yyyy - hh:mm:ss)
                    </label>
                    <div className="flex items-center border rounded-md bg-white">
                        
                        <div
                            className="p-2 cursor-pointer flex-shrink-0"
                            onClick={() =>
                                document
                                    .querySelector(
                                        ".react-datepicker-wrapper input"
                                    )
                                    .focus()
                            }
                        >
                            <FaCalendarAlt className="text-lg" />
                        </div>
                    </div>
                </div>
                <Link href={`/simulacion/${activeTab}?startDate=${startDate}`} className="w-full">
                    <button className="bg-[#52489c] text-white py-2 w-full rounded-md text-lg">
                        {buttonText}
                    </button>
                </Link>
            </div>
        </div>
    );
}

export default function SimulacionPage() {
    const [activeTab, setActiveTab] = useState("individual");

    return (
        <div className="flex flex-col items-center mt-10 w-full">
            <div className="w-full max-w-4xl">
                <div className="flex justify-around mb-4 border-b-2 border-gray-200">
                    <button
                        className={`py-2 px-4 ${
                            activeTab === "individual"
                                ? "border-b-2 border-[#52489c] font-bold"
                                : "font-normal"
                        }`}
                        onClick={() => setActiveTab("individual")}
                    >
                        Registro individual
                    </button>
                    <button
                        className={`py-2 px-4 ${
                            activeTab === "archivo"
                                ? "border-b-2 border-[#52489c] font-bold"
                                : "font-normal"
                        }`}
                        onClick={() => setActiveTab("archivo")}
                    >
                        Registro por archivo
                    </button>
                </div>
                {activeTab === "individual" ? (
                    <ConfiguracionRegistro
                        buttonText="Registro individual"
                        activeTab={activeTab}
                    />
                ) : (
                    <ConfiguracionRegistro
                        buttonText="Registro por archivo"
                        activeTab={activeTab}
                    />
                )}
            </div>
        </div>
    );
}

/*


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
*/