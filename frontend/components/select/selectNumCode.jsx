import * as React from 'react';
import InputLabel from '@mui/material/InputLabel';
import MenuItem from '@mui/material/MenuItem';
import FormControl from '@mui/material/FormControl';
import Select from '@mui/material/Select';
import Typography from '@mui/material/Typography';

export default function SelectVariants({disabled=false, numCode, setnumCode}) {

  const handleChange = (event) => {
    setnumCode(event.target.value);
  };

  return (
    <div>
      <FormControl variant="filled" sx={{ width: '85%' }}>
        <InputLabel id="demo-simple-select-filled-label">Teléfono</InputLabel>
        <Select
          labelId="demo-simple-select-filled-label"
          id="demo-simple-select-filled"
          value={numCode}
          onChange={handleChange}
          inputProps={{ readOnly: disabled }}
        >
        
          <MenuItem value={10}>+51 (Perú) </MenuItem>
          <MenuItem value={20}>+52 (Mexico) </MenuItem>
          <MenuItem value={30}>+502 (Guatemala) </MenuItem>
          <MenuItem value={40}>+593 (Ecuador) </MenuItem>
          <MenuItem value={50}>+809 (República Dominicana) </MenuItem>
          <MenuItem value={60}>+53 (Cuba) </MenuItem>
          <MenuItem value={70}>+506 (Costa Rica) </MenuItem>
          <MenuItem value={80}>+56 (Chile) </MenuItem>
          <MenuItem value={90}>+54 (Argentina) </MenuItem>
          <MenuItem value={100}>+591 (Bolivia) </MenuItem>
          <MenuItem value={110}>+86 (China) </MenuItem>

        </Select>
      </FormControl>
    </div>
  );
}