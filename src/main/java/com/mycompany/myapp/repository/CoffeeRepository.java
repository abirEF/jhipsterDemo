package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Coffee;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Coffee entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CoffeeRepository extends JpaRepository<Coffee, Long> {}
