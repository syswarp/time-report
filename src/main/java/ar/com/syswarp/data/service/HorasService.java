package ar.com.syswarp.data.service;

import ar.com.syswarp.data.entity.Horas;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class HorasService {

    private final HorasRepository repository;

    @Autowired
    public HorasService(HorasRepository repository) {
        this.repository = repository;
    }

    public Optional<Horas> get(UUID id) {
        return repository.findById(id);
    }

    public Horas update(Horas entity) {
        return repository.save(entity);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }

    public Page<Horas> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
