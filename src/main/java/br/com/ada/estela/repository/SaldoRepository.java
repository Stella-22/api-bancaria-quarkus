package br.com.ada.estela.repository;

import br.com.ada.estela.model.Saldo;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SaldoRepository implements PanacheRepository<Saldo> {

}

