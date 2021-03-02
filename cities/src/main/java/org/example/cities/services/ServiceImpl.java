package org.example.cities.services;

import org.example.cities.mappers.CityMapper;
import org.example.cities.model.City;
import org.example.cities.model.CityDto;
import org.example.cities.repositories.CityRepository;
import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Service
public class ServiceImpl implements org.example.cities.services.Service {

	private final CityRepository cityRepository;
	private final CityMapper mapper = Mappers.getMapper(CityMapper.class);

	public ServiceImpl(CityRepository cityRepository) {
		this.cityRepository = cityRepository;
	}

	@Override
	public List<CityDto> findAll() {
		return cityRepository.findAll().stream().map(mapper::map).collect(Collectors.toList());
	}

	@Override
	public Optional<CityDto> findOne(long id) {
		return cityRepository.findById(id).map(mapper::map);
	}

	@Override
	public List<CityDto> search(String term) {
		return cityRepository.search(term).stream().map(mapper::map).collect(Collectors.toList());
	}

	@Override
	public CityDto create(CityDto cityDto) {
		if(cityDto == null)
			throw new RuntimeException("Invalid input");
		if(cityDto.getCity().isBlank())
			throw new RuntimeException("Invalid city name");
		if(cityDto.getCountry().isBlank())
			throw new RuntimeException("Invalid country name");

		// have to find manually because sql server implementation throws plain SqlServerException
		// on UNIQUE KEY constraint violation instead of ConstraintViolationException
		if(cityRepository.existsCityByCityAndCountry(cityDto.getCity(), cityDto.getCountry()))
			throw new ResponseStatusException(HttpStatus.CONFLICT, cityDto.getCity() + ", " + cityDto.getCountry() + " already exists");

		return mapper.map(cityRepository.save(mapper.map(cityDto)));
	}

	@Override
	public CityDto replace(CityDto cityDto, long id) {
		if(isBlank(cityDto.getCity()) || isBlank(cityDto.getCountry()))
			throw new ResponseStatusException(HttpStatus.NO_CONTENT);

		Optional<City> result = cityRepository.findById(id);
		if(result.isEmpty())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Id " + id + " not found");

		City city = result.get();
		city.setCity(cityDto.getCity());
		city.setCountry(cityDto.getCountry());

		return mapper.map(cityRepository.save(city));
	}

	@Override
	public CityDto update(CityDto cityDto, long id) {
		if(isBlank(cityDto.getCity()) && isBlank(cityDto.getCountry()))
			throw new ResponseStatusException(HttpStatus.NO_CONTENT);

		Optional<City> result = cityRepository.findById(id);
		if(result.isEmpty())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Id " + id + " not found");

		City city = result.get();
		if(!isBlank(cityDto.getCity()))
			city.setCity(cityDto.getCity());
		if(!isBlank(cityDto.getCountry()))
			city.setCountry(cityDto.getCountry());

		return mapper.map(cityRepository.save(city));
	}

	@Override
	public void delete(long id) {
		//cityRepository.deleteById();
	}
}
