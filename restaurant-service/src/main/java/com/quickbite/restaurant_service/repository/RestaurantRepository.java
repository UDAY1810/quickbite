package com.quickbite.restaurant_service.repository;

import com.quickbite.restaurant_service.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    @Query("""
       SELECT r FROM Restaurant r
       WHERE LOWER(r.name) LIKE LOWER(CONCAT('%', :search, '%'))
          OR LOWER(r.cuisine) LIKE LOWER(CONCAT('%', :search, '%'))
       """)
    List<Restaurant> search(@Param("search") String search);
}