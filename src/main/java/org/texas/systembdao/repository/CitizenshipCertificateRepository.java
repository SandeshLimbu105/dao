package org.texas.systembdao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.texas.systembdao.entity.CitizenshipCertificate;

import java.util.List;
import java.util.Optional;

@Repository
public interface CitizenshipCertificateRepository extends JpaRepository<CitizenshipCertificate, Long> {

    List<CitizenshipCertificate> findByCitizenNidOrderByIssuedAtDesc(String citizenNid);

    Optional<CitizenshipCertificate> findByCertificateId(String certificateId);
}