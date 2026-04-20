package br.com.ada.estela.service;

import br.com.ada.estela.enums.PerfilUsuario;
import br.com.ada.estela.model.Usuario;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.util.Optional;

@ApplicationScoped
public class UsuarioService {

    private static final Logger LOG = Logger.getLogger(UsuarioService.class);
    private static final String NOME_GERENTE = "gerente";

    @Inject
    private AuthService authService;

    @Transactional
    public void criarGerente() {

        Optional<Usuario> optGerente = Usuario.find("nome", NOME_GERENTE).firstResultOptional();
        if (optGerente.isEmpty()) {
            Usuario gerente = new Usuario();
            gerente.setNome(NOME_GERENTE);
            gerente.setSenha(authService.hashPassword(NOME_GERENTE));
            gerente.setEmail("gerente@email.com");
            gerente.setRole(PerfilUsuario.GERENTE);
            gerente.persist();
            LOG.info("Usuário GERENTE criado com sucesso!");
        } else {
            LOG.info("Usuário GERENTE já existe.");
        }
    }

}
