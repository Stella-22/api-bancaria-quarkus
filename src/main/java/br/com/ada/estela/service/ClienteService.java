package br.com.ada.estela.service;

import br.com.ada.estela.enums.PerfilUsuario;
import br.com.ada.estela.model.Cliente;
import br.com.ada.estela.model.Usuario;
import br.com.ada.estela.resource.cliente.ClienteDTO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;

import java.util.List;

@ApplicationScoped
public class ClienteService {

    @Inject
    private AuthService authService;

    public Cliente cadastrar(ClienteDTO clienteDTO) {

        Usuario usuario = new Usuario();
        usuario.setNome(clienteDTO.getNome().trim());
        usuario.setEmail(clienteDTO.getEmail());
        usuario.setSenha(authService.hashPassword(clienteDTO.getSenha()));
        usuario.setRole(PerfilUsuario.CLIENTE);

        Cliente cliente = new Cliente();
        cliente.setNome(clienteDTO.getNome().trim());
        cliente.setCpf(clienteDTO.getCpf());
        cliente.setUsuario(usuario);
        cliente.persist();
        return cliente;
    }

    public List<Cliente> buscarTodos() {
        return Cliente.listAll();
    }

    public Cliente buscarPorId(Long id) {
        return Cliente.findById(id);
    }

    public Cliente atualizar(Long id, @Valid ClienteDTO clienteDTO) {
        Cliente cliente = buscarPorId(id);
        if (cliente == null) {
            throw new NotFoundException("Cliente com id " + id + " nao encontrado");
        }
        if (!cliente.getCpf().equals(clienteDTO.getCpf())) {
            throw new BadRequestException("CPF nao pode ser alterado");
        }

        cliente.setNome(clienteDTO.getNome().trim());
        cliente.getUsuario().setEmail(clienteDTO.getEmail());
        cliente.getUsuario().setSenha(authService.hashPassword(clienteDTO.getSenha()));
        cliente.persist();
        return cliente;

    }
}
