import * as React from 'react';
import InputLabel from '@mui/material/InputLabel';
import MenuItem from '@mui/material/MenuItem';
import FormControl from '@mui/material/FormControl';
import Select from '@mui/material/Select';

export default function SelectVariantsCity({disabled=false, city, setCity}) {

  const handleChange = (event) => {
    setCity(event.target.value);
  };

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
        
          <MenuItem value={"SPIM"}>Lima</MenuItem>
          <MenuItem value={"LATI"}>Albania</MenuItem>
          <MenuItem value={"VIDP"}>Delhi</MenuItem>
        </Select>
      </FormControl>
    </div>
  );
}
