import { TextField, InputAdornment } from "@mui/material";
import SearchIcon from "@mui/icons-material/Search";
import React, { useState } from "react";
import { Feature, View } from "ol";
import { Aeropuerto } from "@/types/Aeropuerto";
import { Vuelo } from "@/types/Vuelo";
import { seleccionarVuelo } from "@/utils/FuncionesMapa";
import { Map as OLMap } from "ol";
import { fromLonLat } from "ol/proj";

type BarraMapaProps = {
    setSelectedVuelo: (value: Vuelo | null) => void,
    setSelectedAeropuerto: (value: Aeropuerto | null) => void,
    mapRef: React.RefObject<OLMap>,
    selectedFeature: React.RefObject<Feature>,
    vuelos: React.RefObject<
        Map<
            number,
            {
                vuelo: Vuelo;
                pointFeature: any;
                lineFeature: any;
                routeFeature: any;
            }
        >
    >;
    aeropuertos: Map<string, Aeropuerto>;
};
const BarraMapa = ({
    setSelectedVuelo,
    setSelectedAeropuerto,
    mapRef,
    selectedFeature,
    vuelos,
    aeropuertos
}: BarraMapaProps) => {
    const [aBuscar, setABuscar] = React.useState<string>(""); 
    const [error, setError] = useState(false);
    const [helperText, setHelperText] = useState('');

    const handleKeyPress = (event: React.KeyboardEvent) => {
        if (event.key === "Enter") {   
            buscarData();
        }
        else{
            setError(false);
            setHelperText('');
        }
    };

    const handleClick = () => {
        console.log("Search icon clicked, se va a buscar: ", aBuscar);
        buscarData();
    };

    function buscarData() {
        //Si se puede parsear a un número, se busca por código de vuelo
        if (!isNaN(Number(aBuscar))) {
            console.log("Buscando por código de vuelo: ", aBuscar);
            if(vuelos.current?.has(Number(aBuscar))){
                seleccionarVuelo(Number(aBuscar), setSelectedVuelo, selectedFeature, vuelos, vuelos.current?.get(Number(aBuscar))?.pointFeature);
                const item = vuelos.current?.get(Number(aBuscar));
                if (item && mapRef.current) {
                    const view = mapRef.current.getView();
                    if (view) {
                        view.animate({
                            center: item.pointFeature.getGeometry().getCoordinates(),
                            zoom: 4,
                            duration: 1000,
                        });
                    }
                }
            }
            else{
                setError(true);
                setHelperText("No se encontró el vuelo con código: " + aBuscar);
            }
        } else {
            console.log("Buscando por nombre de aeropuerto: ", aBuscar);
            let aeropuerto = aeropuertos.get(aBuscar.toUpperCase());
            if (aeropuerto) {
                setSelectedAeropuerto(aeropuerto);
                if (mapRef.current) {
                    const view = mapRef.current.getView();
                    if (view) {
                        view.animate({
                            center: fromLonLat  ([aeropuerto.longitud, aeropuerto.latitud]),
                            zoom: 6,
                            duration: 1000,
                        });
                    }
                }
            } else {
                setError(true);
                setHelperText("No se encontró el aeropuerto: " + aBuscar);
            }
        }
    }

    return (
        <div
            style={{
                position: "relative",
                top: "75px",
                left: "50%",
                transform: "translateX(-50%)",
                right: "10px",
                zIndex: 10,
                backgroundColor: "rgba(255, 255, 255,1)",
                color: "black",
                borderRadius: "20px",
                width: "calc(50%)",
                display: "flex",
                justifySelf: "center",
                alignSelf: "center",
            }}
        >
            <TextField
                fullWidth
                variant="outlined"
                placeholder="Ingresa el código de vuelo o aeropuerto"
                onKeyDown={handleKeyPress}
                onChange={(e) => setABuscar(e.target.value)}
                InputProps={{
                    startAdornment: (
                        <InputAdornment
                            position="start"
                            onClick={handleClick}
                            style={{ cursor: "pointer" }}
                        >
                            <SearchIcon />
                        </InputAdornment>
                    ),
                    style: {
                        borderRadius: "20px",
                        border: "none",
                    },
                }}
                sx={{
                    '& .MuiOutlinedInput-root':{
                        '&:hover fieldset': {
                            borderColor: 'transparent',
                        },
                        '&.Mui-focused fieldset': {
                            borderColor: 'transparent',
                        },
                    },
                }}
                error={error}
                helperText={helperText}
            />
        </div>
    );
};

export default BarraMapa;
