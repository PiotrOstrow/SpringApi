package org.example.api.mappers;

import org.example.api.model.City;
import org.example.api.model.CityDto;
import org.mapstruct.Mapper;

import java.util.Optional;

@Mapper
public interface CityMapper {

	CityDto map(City city);

	City map(CityDto city);
}
