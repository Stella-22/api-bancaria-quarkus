package br.com.ada.estela.service;

import br.com.ada.estela.enums.PerfilUsuario;
import br.com.ada.estela.mappers.ClienteMapper;
import br.com.ada.estela.model.Cliente;
import br.com.ada.estela.resource.cliente.ClienteDTO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;

import java.util.List;

@ApplicationScoped
public class ClienteService {

    @Inject
    private AuthService authService;

    public ClienteDTO cadastrar(ClienteDTO clienteDTO) {

        Cliente cliente = new Cliente();
        cliente.setNome(clienteDTO.getNome().trim());
        cliente.setCpf(clienteDTO.getCpf());
        cliente.setEmail(clienteDTO.getEmail());
        cliente.setSenha(authService.hashPassword(clienteDTO.getSenha()));
        cliente.setRole(PerfilUsuario.CLIENTE);
        cliente.persist();
        return ClienteMapper.toDTO(cliente);
    }

    public List<ClienteDTO> buscarTodos() {
        return Cliente.findAll().stream()
                .map(c -> ClienteMapper.toDTO((Cliente) c))
                .toList();
    }

    public ClienteDTO buscarPorId(Long id) {
        return ClienteMapper.toDTO(Cliente.findById(id));
    }

    public ClienteDTO atualizar(Long id, ClienteDTO clienteDTO) {
        Cliente cliente = Cliente.findById(id);
        if (cliente == null) {
            throw new NotFoundException("Cliente com id " + id + " nao encontrado");
        }
        if (clienteDTO.getCpf() != null &&
                !clienteDTO.getCpf().isBlank() &&
                !cliente.getCpf().equals(clienteDTO.getCpf())) {
            throw new BadRequestException("CPF nao pode ser alterado");
        }

        if (clienteDTO.getNome() != null && !clienteDTO.getNome().isBlank()) {
            cliente.setNome(clienteDTO.getNome().trim());
        }
        if (clienteDTO.getEmail() != null && !clienteDTO.getEmail().isBlank() && clienteDTO.getEmail().contains("@")) {
            cliente.setEmail(clienteDTO.getEmail());
        }
        if (clienteDTO.getSenha() != null && !clienteDTO.getSenha().isBlank()) {
            cliente.setSenha(authService.hashPassword(clienteDTO.getSenha()));
        }
        cliente.persist();
        return ClienteMapper.toDTO(cliente);

    }
}
