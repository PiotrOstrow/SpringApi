package org.example.api.services;

import org.example.api.model.CityDto;

import java.util.List;
import java.util.Optional;

public interface Service {

	public List<CityDto> findAll();
	Optional<CityDto> findOne(long id);

	List<CityDto> search(String term);

	CityDto create(CityDto cityDto);

	CityDto replace(CityDto cityDto, long id);

	CityDto update(CityDto cityDto, long id);

	void delete(long id);
}
