import React from 'react';
import Button from '@mui/material/Button';
import FileUploadButton from "@/components/buttonUpload/buttonUpload.jsx"
import axios from 'axios';
import { Dialog, DialogActions, DialogContent, DialogTitle } from '@mui/material';

function UploadFile() {
    const [file, setFile] = React.useState(null);
    const [estado, setEstado] = React.useState(null); //1: exito, 2: error
    const [isDialogOpen, setIsDialogOpen] = React.useState(false);
    const [codigos, setCodigos] = React.useState([]);

    const baseUrl = process.env.REACT_APP_API_URL_BASE;

    React.useEffect(() => {
        if (estado === 1 || estado === 2) {
          setIsDialogOpen(true);
        }
      }, [estado]);

    const handleFileChange = (event) => {
        const file = event.target.files[0];
        console.log('Archivo seleccionado:', file);
        //Cambiar cursor a cargando
        document.body.style.cursor = 'wait';
        const formData = new FormData();
        formData.append('file', file);
    
        axios.post(`${baseUrl}/archivo/upload`, formData, {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        })
        .then((response) => {
            document.body.style.cursor = 'default';
            if (response.status === 200) {
                console.log('Archivo subido correctamente:', response.data);
                setEstado(1);
                //Los códigos llegan como 412 4124 123. Es decir, números separados por espacios
                //Si es que llega solo uno
                if (response.data.indexOf(' ') === -1) {
                    setCodigos([response.data]);
                }
                else{
                    setCodigos(response.data.split(' '));
                }
            }
            else if (response.status === 500) {
                console.log('Error al subir el archivo:', response.data);
                setEstado(2);
            }
        })
        .catch((error) => {
            console.error('Error al subir el archivo:', error);
            setEstado(2);
        }).finally(() => {
            //Cambiar cursor a normal
            document.body.style.cursor = 'default';
        });
    };

    const handleClose = () => {
        setIsDialogOpen(false);
        setTimeout(() => setEstado(null), 150); 
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

            <Dialog open={isDialogOpen} onClose={handleClose} fullWidth>
                <DialogContent>
                    <div className={`p-4 ${estado === 1 ? "bg-green-100 text-green-900" : "bg-red-100 text-red-900"}`}>
                        <p className='text-center font-bold pb-2'>
                            {estado === 1 ? "Archivo subido correctamente" : estado === 2 ? "Error al subir el archivo" : ""}
                        </p>
                        <div>
                            {estado === 1 ? (
                                <div className='max-h-96 overflow-y-auto'>
                                    <p className='w-full'>Códigos de {codigos.length} paquetes generados:</p>
                                    <table>
                                        <tbody>
                                            {codigos.reduce((acc, codigo, index) => {
                                            if (index % 8 === 0) acc.push([]);
                                            acc[acc.length - 1].push(codigo);
                                            return acc;
                                            }, []).map((row, rowIndex) => (
                                            <tr key={rowIndex}>
                                                {row.map((codigo, colIndex) => (
                                                <td key={colIndex} style={{ paddingRight: '20px' }}>{codigo}</td>
                                                ))}
                                            </tr>
                                            ))}
                                        </tbody>
                                        </table>
                                </div>
                            ) : estado === 2 ? (
                                <div>
                                    <p>El archivo no tiene el formato adecuado</p>
                                </div>
                            ) : null}
                        </div>
                         
                    </div>
                </DialogContent>
                <DialogActions>
                     <Button onClick={handleClose}>Aceptar</Button>
                </DialogActions>
            </Dialog>
        </div>
    );
}

export default UploadFile;