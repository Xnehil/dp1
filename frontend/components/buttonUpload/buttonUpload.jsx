import React from 'react';

function FileUploadButton({ onFileChange }) {
  const buttonStyle = {
    padding: '10px 20px',
    backgroundColor: '#84A98C',
    color: 'white',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer',
    display: 'flex',
    justifyContent: 'center', 
    alignItems: 'center',
    width: '100%',
  };

  const hiddenInputStyle = {
    display: 'none',
  };

  return (
    <div>
      <input
        type="file"
        id="file-upload"
        style={hiddenInputStyle}
        onChange={onFileChange}
      />
      <label htmlFor="file-upload" style={buttonStyle}>
        Subir Archivo
      </label>
    </div>
  );
}

export default FileUploadButton;