package com.ccs.springaop.service;

import com.ccs.springaop.entitie.Usuario;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UsuarioService {

    final List<Usuario> usuarios = new ArrayList<>();

    public boolean salvar(Usuario usuario) {
        if (usuario.getId() == 0) {
            usuario.setId(usuarios.getLast().getId() + 1);
        }
        return usuarios.add(usuario);
    }

    public List<Usuario> listar() {
        return usuarios;
    }

    public void remover(Usuario usuario) {
        usuarios.remove(usuario);
    }

}
