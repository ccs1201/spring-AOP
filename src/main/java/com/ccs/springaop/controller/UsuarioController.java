package com.ccs.springaop.controller;

import com.ccs.springaop.entitie.Usuario;
import com.ccs.springaop.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    public List<Usuario> getUsuarios() {
        return usuarioService.listar();
    }

    @PostMapping
    public boolean salvar(@RequestBody Usuario usuario) {
        return usuarioService.salvar(usuario);
    }
}
