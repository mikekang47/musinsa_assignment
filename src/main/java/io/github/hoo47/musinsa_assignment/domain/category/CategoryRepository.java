package io.github.hoo47.musinsa_assignment.domain.category;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Retrieves a category by its ID with a pessimistic read lock.
     * Prevents concurrent modification of the category by other transactions.
     *
     * @param id the category ID
     * @return the category entity (Optional)
     */
    @Lock(LockModeType.PESSIMISTIC_READ)
    @Query("SELECT c FROM Category c WHERE c.id = :id")
    Optional<Category> findByIdWithReadLock(@Param("id") Long id);
}
