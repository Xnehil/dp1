"use client";  // Añade esta línea al principio del archivo
import React, { useState } from 'react';

const Modal = ({ isOpen, onClose, onTrack }) => {
  if (!isOpen) return null;

  const handleSubmit = (event) => {
    event.preventDefault();
    const code = event.target.elements.code.value;
    onTrack(code);
  };

  return (
    <div style={styles.overlay} onClick={onClose}>
      <div style={styles.modal} onClick={(e) => e.stopPropagation()}>
        <button style={styles.closeButton} onClick={onClose}>
          X
        </button>
        <form onSubmit={handleSubmit}>
          <h2>Inserta el código de tu paquete o envío</h2>
          <input type="text" name="code" placeholder="Código" required style={styles.input} />
          <p style={styles.reminderText}>
            ¿No lo recuerdas? <a href="#" style={styles.link}>Haz clic aquí</a>
          </p>
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

const App = () => {
  const [isModalOpen, setIsModalOpen] = useState(false);

  const openModal = () => {
    setIsModalOpen(true);
  };

  const closeModal = () => {
    setIsModalOpen(false);
  };

  const handleTrack = (code) => {
    console.log('Tracking code:', code);
    // Aquí puedes agregar la lógica para rastrear el código
    closeModal();
  };

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
    backgroundColor: '#4a90e2',
    color: '#ffffff',
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
            // onMouseOver={(e) => e.currentTarget.style.backgroundColor = buttonHoverStyle.backgroundColor}
            // onMouseOut={(e) => e.currentTarget.style.backgroundColor = buttonStyle.backgroundColor}
            onClick={openModal}
          >
            Rastrea aquí
          </button>
        </div>
      </div>
      <Modal isOpen={isModalOpen} onClose={closeModal} onTrack={handleTrack} />
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
    width: '300px',
    position: 'relative',
    textAlign: 'center',
  },
  closeButton: {
    position: 'absolute',
    top: '10px',
    right: '10px',
    backgroundColor: 'none',
    border: 'none',
    fontSize: '20px',
    cursor: 'pointer',
  },
  input: {
    width: '100%',
    padding: '10px',
    margin: '10px 0',
    border: '1px solid #ccc',
    borderRadius: '4px',
  },
  reminderText: {
    margin: '10px 0',
    fontSize: '0.9em',
  },
  link: {
    color: '#00f',
    textDecoration: 'underline',
    cursor: 'pointer',
  },
  buttonContainer: {
    display: 'flex',
    justifyContent: 'space-between',
    marginTop: '20px',
  },
  cancelButton: {
    backgroundColor: '#a3d9a5',
    border: 'none',
    padding: '10px 20px',
    borderRadius: '4px',
    cursor: 'pointer',
    fontSize: '1em',
  },
  trackButton: {
    backgroundColor: '#6e4cc2',
    color: 'white',
    border: 'none',
    padding: '10px 20px',
    borderRadius: '4px',
    cursor: 'pointer',
    fontSize: '1em',
  },
};

export default App;