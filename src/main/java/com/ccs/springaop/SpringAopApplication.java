package com.ccs.springaop;

import com.ccs.springaop.entitie.Usuario;
import com.ccs.springaop.service.UsuarioService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@Slf4j
public class SpringAopApplication {

    private final UsuarioService usuarioService;

    public SpringAopApplication(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringAopApplication.class, args);

    }

    @Bean
    CommandLineRunner commandLineRunner() {
        return args -> {

            for (int i = 1; i <= 5; i++) {
                Usuario usuario = criarUsuario(i);
                usuarioService.salvar(usuario);
            }

            log.info("Listando Usuarios: {}", usuarioService.listar());
        };
    }

    private static Usuario criarUsuario(int i) {

        return new Usuario(i, "Usuario " + i, "usuario" + i + "@email.com");
    }

}
