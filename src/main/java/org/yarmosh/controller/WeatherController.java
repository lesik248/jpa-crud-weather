package org.yarmosh.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.IWebRequest;
import org.thymeleaf.web.servlet.IServletWebExchange;
import org.yarmosh.service.*;

import java.io.Writer;

public class WeatherController implements IController {

    private static final Logger logger = LoggerFactory.getLogger(WeatherController.class);

    private final String path;

    public WeatherController(String path) {
        this.path = path;
    }

    @Override
    public void process(IServletWebExchange webExchange,
                        ITemplateEngine templateEngine,
                        Writer writer) throws Exception {

        WebContext ctx = new WebContext(webExchange, webExchange.getLocale());
        IWebRequest request = webExchange.getRequest();
        WeatherService service = new WeatherService();

        String[] p = path.split("/");
        if (p.length < 2) {
            throw new Exception("Некорректный путь");
        }

        String action = p[1];
        String viewName = null; // шаблон, который надо отрендерить

        try {

            switch (action) {
                case "getForRegion":
                    handleGetForRegion(request, ctx, service);
                    viewName = "weather-region";
                    break;

                case "getSnowyDates":
                    handleSnowyDates(request, ctx, service);
                    viewName = "snowy-dates";
                    break;

                case "getForLanguage":
                    handleGetForLanguage(request, ctx, service);
                    viewName = "weather-langs";
                    break;

                case "updateForRegion":
                    handleUpdateForRegion(request, ctx, service);
                    viewName = "update-weather";
                    break;

                case "addRegion":
                    handleAddRegion(request, ctx, service);
                    viewName = "add-region";
                    break;

                default:
                    throw new Exception("Неизвестное действие");
            }

        } catch (WeatherNotFoundException nf) {

            logger.warn("WeatherNotFoundException: {}", nf.getMessage());
            ctx.setVariable("error", nf.getMessage());
            templateEngine.process(viewName, ctx, writer);
            return;

        } catch (WeatherDatabaseException dbEx) {

            logger.error("WeatherDatabaseException: {}", dbEx.getMessage(), dbEx);
            throw dbEx;

        } catch (Exception e) {

            logger.error("Critical error in WeatherController", e);
            throw e;
        }

        templateEngine.process(viewName, ctx, writer);
    }



    private void handleGetForRegion(IWebRequest request,
                                    WebContext ctx,
                                    WeatherService service)
            throws WeatherNotFoundException, WeatherDatabaseException {

        String region = request.getParameterValue("region");
        if (region == null) return;

        ctx.setVariable("data", service.getWeatherForRegion(region));
    }

    private void handleSnowyDates(IWebRequest request,
                                  WebContext ctx,
                                  WeatherService service)
            throws WeatherNotFoundException, WeatherDatabaseException {

        String region = request.getParameterValue("region");
        String tempStr = request.getParameterValue("temperature");

        if (region == null || tempStr == null) return;

        try {
            int temp = Integer.parseInt(tempStr);
            ctx.setVariable("data", service.getRegionSnowyDates(region, temp));
        } catch (NumberFormatException e) {
            logger.warn("NumberFormatException: {}", e.getMessage());
            ctx.setVariable("error", "Температура должна быть числом");
        }
    }

    private void handleGetForLanguage(IWebRequest request,
                                      WebContext ctx,
                                      WeatherService service)
            throws WeatherNotFoundException, WeatherDatabaseException {

        String lang = request.getParameterValue("lang");
        if (lang == null) return;

        ctx.setVariable("data", service.getWeatherByLanguage(lang));
    }

    private void handleUpdateForRegion(IWebRequest request,
                                       WebContext ctx,
                                       WeatherService service)
            throws WeatherNotFoundException, WeatherDatabaseException {

        String region = request.getParameterValue("region");
        String date = request.getParameterValue("date");
        String t = request.getParameterValue("temperature");
        String p = request.getParameterValue("precipitation");

        if (region == null || date == null || t == null || p == null) return;

        try {
            int temp = Integer.parseInt(t);
            service.updateWeatherForRegion(region, date, temp, p);
            ctx.setVariable("data", "Погода обновлена!");
        } catch (NumberFormatException e) {
            logger.warn("NumberFormatException: {}", e.getMessage());
            ctx.setVariable("error", "Температура должна быть числом");
        }
    }

    private void handleAddRegion(IWebRequest request,
                                 WebContext ctx,
                                 WeatherService service)
            throws WeatherNotFoundException, WeatherDatabaseException {

        String region = request.getParameterValue("region");
        String s = request.getParameterValue("square");
        String ct = request.getParameterValue("citizen-type");

        if (region == null || s == null || ct == null) return;

        try {
            int square = Integer.parseInt(s);
            service.createRegion(region, square, ct);
            ctx.setVariable("data", "Регион добавлен!");
        } catch (NumberFormatException e) {
            logger.warn("NumberFormatException: {}", e.getMessage());
            ctx.setVariable("error", "Площадь должна быть числом");
        }
    }

}
