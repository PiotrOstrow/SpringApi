package org.example.cities.mappers;

import org.example.cities.model.City;
import org.example.cities.model.CityDto;
import org.mapstruct.Mapper;

import java.util.Optional;

@Mapper
public interface CityMapper {

	CityDto map(City city);

	City map(CityDto city);
}
