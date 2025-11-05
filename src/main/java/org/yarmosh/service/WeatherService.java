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

    private int getRegionIdByName(String name) throws WeatherServiceException {
        try {
            List<Region> regions = daoRegion.getAll();
            for (Region region : regions) {
                if (region.getName().equalsIgnoreCase(name)) {
                    return region.getId();
                }
            }
            throw new WeatherServiceException("Регион с названием '" + name + "' не найден.");
        } catch (Exception e) {
            throw new WeatherServiceException("Ошибка БД при поиске региона по названию.");
        }
    }

    public List<Weather> getWeatherForRegion(String regionName) throws WeatherServiceException {
        try {
            int regionId = getRegionIdByName(regionName);
            List<Weather> weathers = daoWeather.getAll();

            List<Weather> result = new ArrayList<>();
            for (Weather weather : weathers) {
                if (weather.getRegion() == regionId) {
                    result.add(weather);
                }
            }

            if (result.isEmpty()) {
                throw new WeatherServiceException("Нет данных о погоде для региона '" + regionName + "'.");
            }
            return result;
        } catch (WeatherServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new WeatherServiceException("Ошибка БД при получении данных о погоде.");
        }
    }

    public List<String> getRegionSnowyDates(String regionName, int temperature) throws WeatherServiceException {
        try {
            int regionId = getRegionIdByName(regionName);
            List<Weather> weathers = daoWeather.getAll();
            List<String> result = new ArrayList<>();

            for (Weather weather : weathers) {
                if (weather.getRegion() == regionId &&
                        weather.getTemperature() < temperature &&
                        "снег".equalsIgnoreCase(weather.getPrecipitation())) {
                    result.add(weather.getDate());
                }
            }

            if (result.isEmpty()) {
                throw new WeatherServiceException("В регионе " + regionName + " не было дней с заданной погодой.");
            }
            return result;
        } catch (WeatherServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new WeatherServiceException("Ошибка БД при получении погодных данных.");
        }
    }

    public List<Weather> getWeatherByLanguage(String language) throws WeatherServiceException {
        try {
            List<Region> regions = daoRegion.getAll();
            List<Weather> weathers = daoWeather.getAll();
            List<String> lastWeek = getCurrWeekDays();

            List<Integer> regionWithLanguageIds = new ArrayList<>();
            for (Region region : regions) {
                CitizenType citizenType = region.getCitizenType();
                if (citizenType.getLanguage().equalsIgnoreCase(language)) {
                    regionWithLanguageIds.add(region.getId());
                }
            }

            if (regionWithLanguageIds.isEmpty()) {
                throw new WeatherServiceException("Нет регионов с языком: " + language + ".");
            }

            List<Weather> result = new ArrayList<>();
            for (Weather weather : weathers) {
                if (lastWeek.contains(weather.getDate()) && regionWithLanguageIds.contains(weather.getRegion())) {
                    result.add(weather);
                }
            }

            if (result.isEmpty()) {
                throw new WeatherServiceException("Нет информации о погоде за последнюю неделю в регионах с языком: " + language + ".");
            }
            return result;
        } catch (WeatherServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new WeatherServiceException("Ошибка БД при получении данных о погоде по языку.");
        }
    }

    public static List<String> getCurrWeekDays() {
        List<String> days = new ArrayList<>();
        LocalDate today = LocalDate.now();
        LocalDate monday = today.with(DayOfWeek.MONDAY);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (int i = 0; i < 7; i++) {
            days.add(monday.plusDays(i).format(formatter));
        }
        return days;
    }

    public void updateWeatherForRegion(String regionName, String date,
                                       int temperature, String precipitation)
            throws WeatherServiceException {
        try {
            int regionId = getRegionIdByName(regionName);
            Region region = daoRegion.read(regionId);
            daoWeather.create(new Weather(1, region, date, temperature, precipitation));
        } catch (WeatherServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new WeatherServiceException("Ошибка БД при обновлении данных о погоде.");
        }
    }

    public void createRegion(String regionName, int regionSquare, String citizenType)
            throws WeatherServiceException {
        try {
            List<CitizenType> citizenTypes = daoCitizenType.getAll();
            CitizenType typeForRegion = null;

            for (CitizenType type : citizenTypes) {
                if (type.getName().equals(citizenType)) {
                    typeForRegion = daoCitizenType.read(type.getId());
                    break;
                }
            }

            if (typeForRegion == null) {
                throw new WeatherServiceException("Тип жителей \"" + citizenType + "\" не найден.");
            }

            daoRegion.create(new Region(1, regionName, regionSquare, typeForRegion));
            System.out.println("Регион добавлен успешно!");
        } catch (WeatherServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new WeatherServiceException("Ошибка БД при создании региона.");
        }
    }
}
