package ar.com.syswarp.data.service;

import ar.com.syswarp.data.entity.Horas;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HorasRepository extends JpaRepository<Horas, UUID> {

}