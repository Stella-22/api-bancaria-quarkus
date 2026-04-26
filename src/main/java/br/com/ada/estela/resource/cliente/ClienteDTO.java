package br.com.ada.estela.resource.cliente;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClienteDTO {

    private Long id;

    @NotBlank(message = "Nome do cliente é obrigatório")
    @Size(min = 3, message = "O nome do cliente deve conter pelo menos 3 caracteres")
    private String nome;

    @NotBlank(message = "O CPF do cliente é obrigatório")
    @Size(min = 14, message = "O CPF do cliente deve conter 14 caracteres (XXX.XXX.XXX-XX) ")
    private String cpf;

    @NotBlank(message = "Email do cliente é obrigatório")
    @Email(message = "O email do cliente deve ser válido")
    private String email;

    @NotBlank(message = "Senha do cliente é obrigatória")
    @Size(min = 8, message = "A senha do cliente deve conter pelo menos 8 caracteres")
    private String senha;

    public ClienteDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
