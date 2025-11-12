package org.yarmosh.controller;

import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.servlet.IServletWebExchange;
import org.yarmosh.service.WeatherService;

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

        Object data = null;

        if ("getForRegion".equalsIgnoreCase(action)) {
            data = weatherService.getWeatherForRegion(regionName);
        }
        else if ("getSnowyDates".equalsIgnoreCase(action)) {
            int temp = Integer.parseInt(request.getParameterValue("temperature"));
            data = weatherService.getRegionSnowyDates(regionName, temp);
        }
        else if ("getForLanguage".equalsIgnoreCase(action)) {
            String lang = request.getParameterValue("lang");
            data = weatherService.getWeatherByLanguage(lang);
        }
        else if ("updateForRegion".equalsIgnoreCase(action)) {
            String date = request.getParameterValue("date");
            int temp = Integer.parseInt(request.getParameterValue("temperature"));
            String prec = request.getParameterValue("precipitation");
            weatherService.updateWeatherForRegion(regionName, date, temp, prec);
            data = "Weather added!";
        }
        else if ("addRegion".equalsIgnoreCase(action)) {
            int square = Integer.parseInt(request.getParameterValue("square"));
            String citizenType = request.getParameterValue("citizen-type");
            weatherService.createRegion(regionName, square, citizenType);
            data = "Region added!";
        }
        else {
            data = "Неверный параметр action";
        }

        WebContext context = new WebContext(webExchange, webExchange.getLocale());
        context.setVariable("data", data);

        templateEngine.process("weather", context, writer);
    }
}
