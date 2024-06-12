import * as React from 'react';
import InputLabel from '@mui/material/InputLabel';
import MenuItem from '@mui/material/MenuItem';
import FormControl from '@mui/material/FormControl';
import Select from '@mui/material/Select';

export default function SelectVariantsCity() {
  const [age, setAge] = React.useState('');

  const handleChange = (event) => {
    setAge(event.target.value);
  };

  return (
    <div>
      <FormControl variant="filled" sx={{ width: '40%' }}>
        <InputLabel id="demo-simple-select-filled-label">Ciudad</InputLabel>
        <Select
          labelId="demo-simple-select-filled-label"
          id="demo-simple-select-filled"
          value={age}
          onChange={handleChange}
        >
        
          <MenuItem value={10}>Lima</MenuItem>
          <MenuItem value={20}>Sao Paulo</MenuItem>
          <MenuItem value={30}>Lisboa</MenuItem>
        </Select>
      </FormControl>
    </div>
  );
}
