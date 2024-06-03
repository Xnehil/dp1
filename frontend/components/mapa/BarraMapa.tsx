import { TextField, InputAdornment } from "@mui/material";
import SearchIcon from "@mui/icons-material/Search";
import React from "react";

const BarraMapa = () => {
    const [aBuscar, setABuscar] = React.useState<string>(""); 
    const handleKeyPress = (event: React.KeyboardEvent) => {
        if (event.key === "Enter") {
            console.log("Enter key pressed, se va a buscar: ", aBuscar);
            buscarData();
        }
    };

    const handleClick = () => {
        console.log("Search icon clicked, se va a buscar: ", aBuscar);
        buscarData();
    };

    function buscarData() {}

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
            />
        </div>
    );
};

export default BarraMapa;
