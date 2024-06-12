import React from 'react';
import Button from '@mui/material/Button';
import FileUploadButton from "@/components/buttonUpload/buttonUpload.jsx"

function UploadFile() {
    const [file, setFile] = React.useState(null);

    const handleFileChange = (event) => {
        setFile(event.target.files[0]);
        console.log('Archivo seleccionado:', event.target.files[0]);
    };

    return (
        <div>
            <h2 className="text-2xl mb-2 text-[#52489C] text-left font-bold">
                Registro de envío por archivo
            </h2>
            <h2 className="text-3m mb-1 text-[#000000] text-left font">
              Sube el archivo deseado para registrar un conjunto de envíos
            </h2>
            <FileUploadButton onFileChange={handleFileChange} className="mt-4" />
            {file && <p>Archivo seleccionado: {file.name}</p>}
        </div>
    );
}

export default UploadFile;