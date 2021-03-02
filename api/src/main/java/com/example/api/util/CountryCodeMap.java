package com.example.api.util;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service
public class CountryCodeMap {

	private final Map<String, String> countries = new HashMap<>();

	public CountryCodeMap() {
		for (String iso : Locale.getISOCountries())
			countries.put(new Locale("", iso).getDisplayCountry().toLowerCase(), iso);
	}

	public String getCountryCode(String country) {
		return countries.get(country.toLowerCase());
	}
}
