package org.example.api.repositories;


import org.example.api.model.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CityRepository extends JpaRepository<City, Long> {

	@Query("SELECT c from City c WHERE c.city LIKE %?1% OR c.country LIKE %?1%")
	List<City> search(String term);

	boolean existsCityByCityAndCountry(String city, String country);
}
