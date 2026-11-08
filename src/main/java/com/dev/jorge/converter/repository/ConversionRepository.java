package com.dev.jorge.converter.repository;

import com.dev.jorge.converter.entity.Conversion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ConversionRepository extends JpaRepository<Conversion, Long> {
    List<Conversion> findByUsernameOrderByCreatedAtDesc(String username);
}
