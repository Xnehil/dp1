import * as React from 'react';
import InputLabel from '@mui/material/InputLabel';
import MenuItem from '@mui/material/MenuItem';
import FormControl from '@mui/material/FormControl';
import Select from '@mui/material/Select';

export default function SelectVariants() {
  const [age, setAge] = React.useState('');

  const handleChange = (event) => {
    setAge(event.target.value);
  };

  return (
    <div>
      <FormControl variant="filled" sx={{ width: '40%' }}>
        <InputLabel id="demo-simple-select-filled-label">Tipo de documento</InputLabel>
        <Select
          labelId="demo-simple-select-filled-label"
          id="demo-simple-select-filled"
          value={age}
          onChange={handleChange}
        >
        
          <MenuItem value={10}>DNI</MenuItem>
          <MenuItem value={20}>Pasaporte</MenuItem>
          <MenuItem value={30}>Carnet de Extranjería</MenuItem>
        </Select>
      </FormControl>
    </div>
  );
}

