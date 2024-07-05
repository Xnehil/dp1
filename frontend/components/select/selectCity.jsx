import * as React from 'react';
import InputLabel from '@mui/material/InputLabel';
import MenuItem from '@mui/material/MenuItem';
import FormControl from '@mui/material/FormControl';
import Select from '@mui/material/Select';

export default function SelectVariantsCity({disabled=false, city, setCity}) {

  const handleChange = (event) => {
    setCity(event.target.value);
  };

  const cities = [
    { value: "SKBO", name: "Bogota" },
    { value: "SEQM", name: "Quito" },
    { value: "SVMI", name: "Caracas" },
    { value: "SBBR", name: "Brasilia" },
    { value: "SPIM", name: "Lima" },
    { value: "SLLP", name: "La Paz" },
    { value: "SCEL", name: "Santiago de Chile" },
    { value: "SABE", name: "Buenos Aires" },
    { value: "SGAS", name: "Asunción" },
    { value: "SUAA", name: "Montevideo" },
    { value: "LATI", name: "Tirana" },
    { value: "EDDI", name: "Berlin" },
    { value: "LOWW", name: "Viena" },
    { value: "EBCI", name: "Bruselas" },
    { value: "UMMS", name: "Minsk" },
    { value: "LBSF", name: "Sofia" },
    { value: "LKPR", name: "Praga" },
    { value: "LDZA", name: "Zagreb" },
    { value: "EKCH", name: "Copenhague" },
    { value: "EHAM", name: "Amsterdam" },
    { value: "VIDP", name: "Delhi" },
    { value: "OSDI", name: "Damasco" },
    { value: "OERK", name: "Riad" },
    { value: "OMDB", name: "Dubai" },
    { value: "OAKB", name: "Kabul" },
    { value: "OOMS", name: "Mascate" },
    { value: "OYSN", name: "Sana" },
    { value: "OPKC", name: "Karachi" },
    { value: "UBBB", name: "Baku" },
    { value: "OJAI", name: "Aman" }
];
  
  cities.sort((a, b) => a.name.localeCompare(b.name));
  
  const menuItems = cities.map(city => (
    <MenuItem key={city.value} value={city.value}>{`${city.name} (${city.value})`}</MenuItem>
  ));

  return (
    <div>
      <FormControl variant="filled" sx={{ width: '40%' }}>
        <InputLabel id="demo-simple-select-filled-label">Ciudad</InputLabel>
        <Select
          labelId="demo-simple-select-filled-label"
          id="demo-simple-select-filled"
          value={city}
          onChange={handleChange}
          inputProps={{ readOnly: disabled }}
        >

          {menuItems}

        </Select>
      </FormControl>
    </div>
  );
}
