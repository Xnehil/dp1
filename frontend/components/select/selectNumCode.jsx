import * as React from 'react';
import InputLabel from '@mui/material/InputLabel';
import MenuItem from '@mui/material/MenuItem';
import FormControl from '@mui/material/FormControl';
import Select from '@mui/material/Select';
import Typography from '@mui/material/Typography';

const countryCodes = [
  { code: 51, label: '+51 (Perú)' },
  { code: 52, label: '+52 (Mexico)' },
  { code: 502, label: '+502 (Guatemala)' },
  { code: 593, label: '+593 (Ecuador)' },
  { code: 809, label: '+809 (República Dominicana)' },
  { code: 53, label: '+53 (Cuba)' },
  { code: 506, label: '+506 (Costa Rica)' },
  { code: 56, label: '+56 (Chile)' },
  { code: 54, label: '+54 (Argentina)' },
  { code: 591, label: '+591 (Bolivia)' },
  { code: 86, label: '+86 (China)' },
];

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
        
          {countryCodes.map((country) => (
            <MenuItem key={country.code} value={country.code}>{country.label}</MenuItem>
          ))}

        </Select>
      </FormControl>
    </div>
  );
}