package org.yarmosh;

import org.yarmosh.service.WeatherService;
import org.yarmosh.service.WeatherServiceException;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        System.setErr(System.out);
        System.out.flush();

        WeatherService weatherService = new WeatherService();

        Scanner myObj = new Scanner(System.in);
        String choice;

        System.out.println("Lab3 Weather CLI");

        while (true) {
            System.out.println("\"1\" - Вывести сведения о погоде в заданном регионе.");
            System.out.println("\"2\" - Вывести даты, когда в заданном регионе шел снег и температура была ниже заданной отрицательной.");
            System.out.println("\"3\" - Вывести информацию о погоде за прошедшую неделю в регионах, жители которых общаются на заданном языке.");
            System.out.println("\"4\" - Обновить информацию о погоде для заданного региона.");
            System.out.println("\"5\" - Добавить новый регион.");
            System.out.println("\"0\" - Выйти из программы.");
            System.out.print("Введите команду: ");
            choice = myObj.nextLine();

            try {
                switch (choice) {
                    case "1": {
                        System.out.print("Введите название региона: ");
                        String regionName = myObj.nextLine();
                        weatherService.getWeatherForRegion(regionName).forEach(System.out::println);
                        break;
                    }
                    case "2": {
                        System.out.print("Введите название региона: ");
                        String regionName = myObj.nextLine();
                        int temperature = 1;
                        while (temperature >= 0) {
                            System.out.print("Введите температуру (<0): ");
                            temperature = Integer.parseInt(myObj.nextLine());
                        }
                        List<String> dates = weatherService.getRegionSnowyDates(regionName, temperature);
                        System.out.println("Даты, когда в заданном регионе был снег и тепература ниже " + temperature + "C: ");
                        dates.forEach(System.out::println);
                        break;
                    }
                    case "3": {
                        System.out.print("Введите язык: ");
                        String language = myObj.nextLine().toLowerCase();
                        weatherService.getWeatherByLanguage(language).forEach(System.out::println);
                        break;
                    }
                    case "4": {
                        System.out.print("Введите название региона: ");
                        String region = myObj.nextLine();
                        System.out.print("Введите дату в формате ГГ-ММ-ДД: ");
                        String date = myObj.nextLine();
                        System.out.print("Введите температуру (в °C): ");
                        int temperature = Integer.parseInt(myObj.nextLine());
                        System.out.print("Введите тип осадков: ");
                        String precipitation = myObj.nextLine();
                        weatherService.updateWeatherForRegion(region, date, temperature, precipitation);
                        System.out.println("Информация успешно обновлена.");
                        break;
                    }
                    case "5": {
                        System.out.print("Введите название региона: ");
                        String region = myObj.nextLine();
                        System.out.print("Введите площадь региона (в км²): ");
                        int square = Integer.parseInt(myObj.nextLine());
                        System.out.print("Введите тип жителей: ");
                        String citizenType = myObj.nextLine();
                        weatherService.createRegion(region, square, citizenType);
                        System.out.println("Регион успешно добавлен.");
                        break;
                    }
                    case "0": {
                        System.out.println("Завершение работы программы.");
                        return;
                    }
                    default: {
                        System.out.println("Неверная команда. Попробуйте еще раз.");
                    }
                }
            } catch (WeatherServiceException e) {
                System.out.println("Ошибка: " + e.getMessage());
            } catch (NumberFormatException e) {
                System.out.println("Ошибка ввода числа. Попробуйте снова.");
            }
        }

    }
}
