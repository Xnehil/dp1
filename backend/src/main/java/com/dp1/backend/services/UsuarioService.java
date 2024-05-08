package com.dp1.backend.services;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dp1.backend.models.Usuario;
import com.dp1.backend.repository.UsuarioRepository;

@Service
public class UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }
    
    public Usuario createUsuario(Usuario usuario)
    {
        try {
            return usuarioRepository.save(usuario);
        } catch (Exception e) {
            return null;
        }
    }

    public Usuario getUsuario(int id)
    {
        try {
            return usuarioRepository.findById(id).get();
        } catch (Exception e) {
            return null;
        }
    }

    public Usuario updateUsuario(Usuario usuario){
        try {
            if (usuario == null)
            {
                return null;
            }
            return usuarioRepository.save(usuario);
        } catch (Exception e) {
            return null;
        }
    }
    public String deleteUsuario(int id){
        try {
            Usuario usuario = usuarioRepository.findById(id).get();
            if (usuario != null) {
                usuarioRepository.delete(usuario);
            }
            else {
                return "Usuario no encontrado";
            }
            return "Usuario eliminado";
        } catch (Exception e) {
            return e.getLocalizedMessage();
        }
    }

    public ArrayList<Usuario> getUsuarios()
    {
        try {
            return (ArrayList<Usuario>) usuarioRepository.findAll();
        } catch (Exception e) {
            return null;
        }
    }
}
