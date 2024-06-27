import { TextField, InputAdornment } from "@mui/material";
import SearchIcon from "@mui/icons-material/Search";
import React, { useState } from "react";
import { Feature, View } from "ol";
import { Aeropuerto } from "@/types/Aeropuerto";
import { Vuelo } from "@/types/Vuelo";
import { seleccionarAeropuerto, seleccionarVuelo } from "@/utils/FuncionesMapa";
import { Map as OLMap } from "ol";
import { fromLonLat } from "ol/proj";
import { ProgramacionVuelo } from "@/types/ProgramacionVuelo";

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
    aeropuertos: Map<string, {aeropuerto: Aeropuerto; pointFeature: any}>;
    programacionVuelos: Map<string, ProgramacionVuelo>;
    simulatedTime: Date;
};
const BarraMapa = ({
    setSelectedVuelo,
    setSelectedAeropuerto,
    mapRef,
    selectedFeature,
    vuelos,
    aeropuertos,
    programacionVuelos,
    simulatedTime,
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

    function animateMapView(pointFeature:any) {
        if (mapRef.current) {
            const view = mapRef.current.getView();
            if (view) {
                view.animate({
                    center: pointFeature.getGeometry().getCoordinates(),
                    zoom: 5,
                    duration: 1000,
                });
            }
        }
    }
    
    function searchInFlights(aBuscar:number) {
        for (const [key, value] of vuelos.current?.entries() || []) {
            const idVuelo = value.vuelo.id;
            const fechaVueloFormatted = simulatedTime.toISOString().slice(0, 10);
            const claveProgramacion = idVuelo + "-" + fechaVueloFormatted;
            const programacion = programacionVuelos.get(claveProgramacion);
            if (programacion && programacion.paquetes.some(paquete => paquete.id === Number(aBuscar))) {
                seleccionarVuelo(key, setSelectedVuelo, setSelectedAeropuerto, selectedFeature, vuelos, value.pointFeature);
                animateMapView(value.pointFeature);
                return true;
            }
        }
        return false;
    }
    
    function searchInAirports(aBuscar:number) {
        for (const [key, value] of aeropuertos.entries()) {
            if (value.aeropuerto.paquetes.some(paquete => paquete.id === Number(aBuscar))) {
                seleccionarAeropuerto(key, setSelectedAeropuerto, setSelectedVuelo, selectedFeature, aeropuertos, value.pointFeature, vuelos);
                animateMapView(value.pointFeature);
                return true;
            }
        }
        return false;
    }

    function buscarData() {
        //Si se puede parsear a un número, se busca por código de vuelo
        if (!isNaN(Number(aBuscar))) {
            if(Number(aBuscar) < 3000){
                console.log("Buscando por código de vuelo: ", aBuscar);
                if(vuelos.current?.has(Number(aBuscar))){
                    seleccionarVuelo(Number(aBuscar), setSelectedVuelo, setSelectedAeropuerto, selectedFeature, vuelos, vuelos.current?.get(Number(aBuscar))?.pointFeature);
                    const item = vuelos.current?.get(Number(aBuscar));
                    if (item && mapRef.current) {
                        const view = mapRef.current.getView();
                        if (view) {
                            view.animate({
                                center: item.pointFeature.getGeometry().getCoordinates(),
                                zoom: 5,
                                duration: 1000,
                            });
                        }
                    }
                }
                else{
                    setError(true);
                    setHelperText("No se encontró el vuelo con código: " + aBuscar);
                }
            }
            else{
                //Primero buscar en vuelos
                let encontrado = false;
                console.log("Buscando por código de paquete: ", aBuscar);
                setHelperText("Buscando en vuelos...");
                if (searchInFlights(Number(aBuscar))) {
                    setHelperText("");
                } else {
                    setHelperText("Buscando en aeropuertos...");
                    if (searchInAirports(Number(aBuscar))) {
                        setHelperText("");
                    } else {
                        setError(true);
                        setHelperText("No se encontró el paquete con código: " + aBuscar);
                    }
                }
            }
        } else {
            console.log("Buscando por nombre de aeropuerto: ", aBuscar);
            let item = aeropuertos.get(aBuscar.toUpperCase());
            if (item) {
                seleccionarAeropuerto(aBuscar.toUpperCase(), setSelectedAeropuerto, setSelectedVuelo , selectedFeature, aeropuertos, item.pointFeature, vuelos);
                if (mapRef.current) {
                    const view = mapRef.current.getView();
                    if (view) {
                        view.animate({
                            center: fromLonLat  ([item.aeropuerto.longitud, item.aeropuerto.latitud]),
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
                placeholder="Ingresa el código de vuelo, aeropuerto o paquete"
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
