package org.texas.systembdao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.texas.systembdao.entity.DaoCitizenPartial;

import java.util.Optional;

@Repository
public interface DaoCitizenRepository extends JpaRepository<DaoCitizenPartial, Long> {

    Optional<DaoCitizenPartial> findByNid(String nid);

    boolean existsByNid(String nid);
}