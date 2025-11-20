package org.yarmosh.service;

import org.yarmosh.dao.DaoCitizenType;
import org.yarmosh.dao.DaoRegion;
import org.yarmosh.dao.DaoWeather;
import org.yarmosh.model.CitizenType;
import org.yarmosh.model.Region;
import org.yarmosh.model.Weather;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class WeatherService {

    private final DaoWeather daoWeather;
    private final DaoRegion daoRegion;
    private final DaoCitizenType daoCitizenType;

    public WeatherService() {
        daoWeather = new DaoWeather();
        daoRegion = new DaoRegion();
        daoCitizenType = new DaoCitizenType();
    }

    private int getRegionIdByName(String name)
            throws WeatherNotFoundException, WeatherDatabaseException {
        try {
            List<Region> regions = daoRegion.getAll();

            for (Region region : regions) {
                if (region.getName().equalsIgnoreCase(name)) {
                    return region.getId();
                }
            }

            throw new WeatherNotFoundException("Регион '" + name + "' не найден.");

        } catch (WeatherNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new WeatherDatabaseException(
                    "Ошибка БД при поиске региона по названию.", e);
        }
    }

    public List<Weather> getWeatherForRegion(String regionName)
            throws WeatherNotFoundException, WeatherDatabaseException {
        try {
            int regionId = getRegionIdByName(regionName);
            List<Weather> allWeather = daoWeather.getAll();

            List<Weather> result = new ArrayList<>();
            for (Weather w : allWeather) {
                if (w.getRegion() == regionId) {
                    result.add(w);
                }
            }

            if (result.isEmpty()) {
                throw new WeatherNotFoundException(
                        "Нет данных о погоде для региона '" + regionName + "'."
                );
            }

            return result;

        } catch (WeatherNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new WeatherDatabaseException(
                    "Ошибка БД при получении данных о погоде.", e);
        }
    }

    public List<String> getRegionSnowyDates(String regionName, int temperature)
            throws WeatherNotFoundException, WeatherDatabaseException {
        try {
            int regionId = getRegionIdByName(regionName);
            List<Weather> allWeather = daoWeather.getAll();

            List<String> result = new ArrayList<>();
            for (Weather weather : allWeather) {
                if (weather.getRegion() == regionId &&
                        weather.getTemperature() < temperature &&
                        "снег".equalsIgnoreCase(weather.getPrecipitation())) {

                    result.add(weather.getDate());
                }
            }

            if (result.isEmpty()) {
                throw new WeatherNotFoundException(
                        "В регионе '" + regionName + "' не найдено снежных дней."
                );
            }

            return result;

        } catch (WeatherNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new WeatherDatabaseException(
                    "Ошибка БД при получении погодных данных.", e);
        }
    }

    public List<Weather> getWeatherByLanguage(String language)
            throws WeatherNotFoundException, WeatherDatabaseException {
        try {
            List<Region> regions = daoRegion.getAll();
            List<Weather> weatherList = daoWeather.getAll();
            List<String> lastWeek = getCurrWeekDays();

            List<Integer> regionIds = new ArrayList<>();
            for (Region region : regions) {
                if (region.getCitizenType().getLanguage().equalsIgnoreCase(language)) {
                    regionIds.add(region.getId());
                }
            }

            if (regionIds.isEmpty()) {
                throw new WeatherNotFoundException(
                        "Нет регионов с языком '" + language + "'."
                );
            }

            List<Weather> result = new ArrayList<>();
            for (Weather w : weatherList) {
                if (lastWeek.contains(w.getDate()) && regionIds.contains(w.getRegion())) {
                    result.add(w);
                }
            }

            if (result.isEmpty()) {
                throw new WeatherNotFoundException(
                        "Нет погодных данных за последнюю неделю для регионов с языком '" + language + "'."
                );
            }

            return result;

        } catch (WeatherNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new WeatherDatabaseException(
                    "Ошибка БД при получении данных о погоде по языку.", e);
        }
    }

    public static List<String> getCurrWeekDays() {
        List<String> days = new ArrayList<>();
        LocalDate today = LocalDate.now();
        LocalDate monday = today.with(DayOfWeek.MONDAY);

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (int i = 0; i < 7; i++) {
            days.add(monday.plusDays(i).format(fmt));
        }

        return days;
    }

    public void updateWeatherForRegion(String regionName, String date,
                                       int temperature, String precipitation)
            throws WeatherNotFoundException, WeatherDatabaseException {

        try {
            int regionId = getRegionIdByName(regionName);
            Region region = daoRegion.read(regionId);

            daoWeather.create(new Weather(0, region, date, temperature, precipitation));

        } catch (WeatherNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new WeatherDatabaseException(
                    "Ошибка БД при обновлении погодных данных.", e);
        }
    }

    public void createRegion(String regionName, int regionSquare, String citizenType)
            throws WeatherNotFoundException, WeatherDatabaseException {

        try {
            List<CitizenType> types = daoCitizenType.getAll();
            CitizenType found = null;

            for (CitizenType type : types) {
                if (type.getName().equalsIgnoreCase(citizenType)) {
                    found = daoCitizenType.read(type.getId());
                    break;
                }
            }

            if (found == null) {
                throw new WeatherNotFoundException(
                        "Тип жителей '" + citizenType + "' не найден."
                );
            }

            daoRegion.create(new Region(0, regionName, regionSquare, found));

        } catch (WeatherNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new WeatherDatabaseException(
                    "Ошибка БД при создании региона.", e);
        }
    }
}
