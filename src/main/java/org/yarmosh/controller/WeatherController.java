package org.yarmosh.controller;

import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.servlet.IServletWebExchange;
import org.yarmosh.service.WeatherService;
import org.yarmosh.service.WeatherServiceException;

import java.io.Writer;

public class WeatherController implements IController {

    @Override
    public void process(IServletWebExchange webExchange,
                        ITemplateEngine templateEngine,
                        Writer writer) throws Exception {

        var request = webExchange.getRequest();

        WeatherService weatherService = new WeatherService();

        String regionName = request.getParameterValue("region");
        String action = request.getParameterValue("action");

        System.out.println("action=" + action + ", region=" + regionName);

        Object data = null;

        if ("getForRegion".equalsIgnoreCase(action)) {
            try {
                data = weatherService.getWeatherForRegion(regionName);
            }
            catch (WeatherServiceException e){
                data = e.getMessage();
            }
        }
        else if ("getSnowyDates".equalsIgnoreCase(action)) {
            int temp = Integer.parseInt(request.getParameterValue("temperature"));
            try {
                data = weatherService.getRegionSnowyDates(regionName, temp);
            }
            catch (WeatherServiceException e){
                data = e.getMessage();
            }
        }
        else if ("getForLanguage".equalsIgnoreCase(action)) {
            String lang = request.getParameterValue("lang");
            try {
                data = weatherService.getWeatherByLanguage(lang);
            }
            catch (WeatherServiceException e){
                data = e.getMessage();
            }
        }
        else if ("updateForRegion".equalsIgnoreCase(action)) {
            String date = request.getParameterValue("date");
            int temp = Integer.parseInt(request.getParameterValue("temperature"));
            String prec = request.getParameterValue("precipitation");
            try {
                weatherService.updateWeatherForRegion(regionName, date, temp, prec);
                data = "Погода добавлена!";
            }
            catch (WeatherServiceException e){
                data = e.getMessage();
            }
        }
        else if ("addRegion".equalsIgnoreCase(action)) {
            int square = Integer.parseInt(request.getParameterValue("square"));
            String citizenType = request.getParameterValue("citizen-type");
            try {
                weatherService.createRegion(regionName, square, citizenType);
                data = "Регион добавлен!";
            }
            catch (WeatherServiceException e){
                data = e.getMessage();
            }
        }
        else {
            data = "Неверный параметр action";
        }

        WebContext context = new WebContext(webExchange, webExchange.getLocale());
        context.setVariable("data", data);

        templateEngine.process("weather", context, writer);
    }
}
