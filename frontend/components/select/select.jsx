import * as React from 'react';
import InputLabel from '@mui/material/InputLabel';
import MenuItem from '@mui/material/MenuItem';
import FormControl from '@mui/material/FormControl';
import Select from '@mui/material/Select';

export default function SelectVariants({disabled=false, value, setValue}) {

  const handleChange = (event) => {
    setValue(event.target.value);
  };

  return (
    <div>
      <FormControl variant="filled" sx={{ width: '40%' }}>
        <InputLabel id="demo-simple-select-filled-label">Tipo de documento</InputLabel>
        <Select
          labelId="demo-simple-select-filled-label"
          id="demo-simple-select-filled"
          value={value}
          onChange={handleChange}
          inputProps={{ readOnly: disabled }}

        >
        
          <MenuItem value={10}>DNI</MenuItem>
          <MenuItem value={20}>Pasaporte</MenuItem>
          <MenuItem value={30}>Carnet de Extranjería</MenuItem>
        </Select>
      </FormControl>
    </div>
  );
}

