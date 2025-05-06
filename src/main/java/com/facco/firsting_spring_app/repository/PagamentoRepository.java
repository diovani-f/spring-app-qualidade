package com.facco.firsting_spring_app.repository;

import com.facco.firsting_spring_app.model.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {
    // Métodos customizados podem ser adicionados aqui, como buscar pagamentos por locação
}
