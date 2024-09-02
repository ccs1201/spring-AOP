package com.ccs.springaop.usuario;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)

public class Usuario {

    @EqualsAndHashCode.Include
    private int id;
    private String nome;
    private String email;
    private String senha;
}
