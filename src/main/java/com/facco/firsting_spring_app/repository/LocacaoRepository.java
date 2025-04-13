package com.facco.firsting_spring_app.repository;

import com.facco.firsting_spring_app.model.Locacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LocacaoRepository extends JpaRepository<Locacao, Long> {
    boolean existsByVeiculoId(Long veiculoId);

    @Query("SELECT COUNT(l) > 0 FROM Locacao l WHERE l.cliente.id = :clienteId AND l.dataRealDevolucao IS NULL")
    boolean clientePossuiLocacaoAtiva(@Param("clienteId") Long clienteId);

    @Query("SELECT COUNT(l) FROM Locacao l WHERE l.cliente.id = :clienteId AND l.dataRealDevolucao IS NOT NULL AND l.dataRealDevolucao > l.dataPrevistaDevolucao")
    long contarDevolucoesAtrasadas(@Param("clienteId") Long clienteId);

}
