import React from 'react';

const App = () => {
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
    <div style={containerStyle}>
      <div style={imageContainerStyle}>
        <img src= "/logos/pantallaRastreo.jpg"  style={imgStyle} />
      </div>
      <div style={textContainerStyle}>
        <h1 style={headingStyle}>¡Rastrea tu paquete aquí!</h1>
        <p style={paragraphStyle}>Busca y localiza tu paquete en tiempo real mediante el código brindado</p>
        <button
          style={buttonStyle}
          //onMouseOver={(e) => e.currentTarget.style.backgroundColor = buttonHoverStyle.backgroundColor}
          //onMouseOut={(e) => e.currentTarget.style.backgroundColor = buttonStyle.backgroundColor}
        >
          Rastrea aquí
        </button>
      </div>
    </div>
  );
}

export default App;