package com.dp1.backend.services;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dp1.backend.models.Cliente;
import com.dp1.backend.repository.ClienteRepository;

@Service
public class ClienteService {
    @Autowired
    private ClienteRepository clienteRepository;

    private static final Logger logger = LogManager.getLogger(ClienteService.class);

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }
    
    public Cliente createCliente(Cliente cliente)
    {
        try {
            Cliente existingCliente = clienteRepository.findByEmail(cliente.getEmail());
            if (existingCliente != null) {
                // If a client with the same email already exists, return that client
                return existingCliente;
            }
            logger.info("Creando cliente con datos: " + cliente.getNombre() + " " + cliente.getApellido() + " " + cliente.getEmail() + " " );
            return clienteRepository.save(cliente);
        } catch (Exception e) {
            return null;
        }
    }

    public Cliente getCliente(int id)
    {
        try {
            return clienteRepository.findById(id).get();
        } catch (Exception e) {
            return null;
        }
    }

    public Cliente updateCliente(Cliente cliente){
        try {
            if (cliente == null)
            {
                return null;
            }
            return clienteRepository.save(cliente);
        } catch (Exception e) {
            return null;
        }
    }
    public String deleteCliente(int id){
        try {
            Cliente cliente = clienteRepository.findById(id).get();
            if (cliente != null) {
                clienteRepository.delete(cliente);
            }
            else {
                return "Cliente no encontrado";
            }
            return "Cliente eliminado";
        } catch (Exception e) {
            return e.getLocalizedMessage();
        }
    }

    public ArrayList<Cliente> getClientes()
    {
        try {
            return (ArrayList<Cliente>) clienteRepository.findAll();
        } catch (Exception e) {
            return null;
        }
    }
}

