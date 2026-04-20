package br.com.ada.estela.config;

import br.com.ada.estela.service.UsuarioService;
import io.quarkus.runtime.Startup;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@Startup
@ApplicationScoped
public class InicializacaoDados {

    @Inject
    private UsuarioService usuarioService;

    @PostConstruct
    public void init() {
        usuarioService.criarGerente();
    }
}



