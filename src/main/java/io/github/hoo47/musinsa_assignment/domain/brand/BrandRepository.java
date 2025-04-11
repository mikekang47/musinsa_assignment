package io.github.hoo47.musinsa_assignment.domain.brand;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BrandRepository extends JpaRepository<Brand, Long> {

    /**
     * Retrieves a brand by its ID with a pessimistic read lock.
     * Prevents concurrent modification of the brand by other transactions.
     *
     * @param id the brand ID
     * @return the brand entity (Optional)
     */
    @Lock(LockModeType.PESSIMISTIC_READ)
    @Query("SELECT b FROM Brand b WHERE b.id = :id")
    Optional<Brand> findByIdWithReadLock(@Param("id") Long id);
} 
