package org.yarmosh.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.IWebRequest;
import org.thymeleaf.web.servlet.IServletWebExchange;
import org.yarmosh.service.WeatherService;
import org.yarmosh.service.WeatherServiceException;

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
            sendError(templateEngine, writer, webExchange, "Некорректный путь");
            return;
        }

        String action = p[1];

        try {
            switch (action) {

                case "getForRegion":
                    handleGetForRegion(request, ctx, service);
                    templateEngine.process("weather-region", ctx, writer);
                    return;

                case "getSnowyDates":
                    handleSnowyDates(request, ctx, service);
                    templateEngine.process("snowy-dates", ctx, writer);
                    return;

                case "getForLanguage":
                    handleGetForLanguage(request, ctx, service);
                    templateEngine.process("weather-langs", ctx, writer);
                    return;

                case "updateForRegion":
                    handleUpdateForRegion(request, ctx, service);
                    templateEngine.process("update-weather", ctx, writer);
                    return;

                case "addRegion":
                    handleAddRegion(request, ctx, service);
                    templateEngine.process("add-region", ctx, writer);
                    return;

                default:
                    ctx.setVariable("message", "Неизвестное действие");
                    templateEngine.process("error", ctx, writer);
            }

        } catch (Exception e) {
            logger.error("Critical error in WeatherController", e);
            sendError(templateEngine, writer, webExchange, "Критическая ошибка сервера");
        }
    }

    // ---------------------- ACTION HANDLERS -----------------------

    private void handleGetForRegion(IWebRequest request,
                                    WebContext ctx,
                                    WeatherService service)
            throws WeatherServiceException {

        String region = request.getParameterValue("region");
        if (region == null) return;

        ctx.setVariable("data", service.getWeatherForRegion(region));
    }

    private void handleSnowyDates(IWebRequest request,
                                  WebContext ctx,
                                  WeatherService service)
            throws WeatherServiceException {

        String region = request.getParameterValue("region");
        String tempStr = request.getParameterValue("temperature");

        if (region == null || tempStr == null) return;

        try {
            int temp = Integer.parseInt(tempStr);
            ctx.setVariable("data", service.getRegionSnowyDates(region, temp));
        } catch (NumberFormatException e) {
            ctx.setVariable("data", "Температура должна быть числом");
        }
    }

    private void handleGetForLanguage(IWebRequest request,
                                      WebContext ctx,
                                      WeatherService service)
            throws WeatherServiceException {

        String lang = request.getParameterValue("lang");
        if (lang == null) return;

        ctx.setVariable("data", service.getWeatherByLanguage(lang));
    }

    private void handleUpdateForRegion(IWebRequest request,
                                       WebContext ctx,
                                       WeatherService service)
            throws WeatherServiceException {

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
            ctx.setVariable("data", "Температура должна быть числом");
        }
    }

    private void handleAddRegion(IWebRequest request,
                                 WebContext ctx,
                                 WeatherService service)
            throws WeatherServiceException {

        String region = request.getParameterValue("region");
        String s = request.getParameterValue("square");
        String ct = request.getParameterValue("citizen-type");

        if (region == null || s == null || ct == null) return;

        try {
            int square = Integer.parseInt(s);
            service.createRegion(region, square, ct);
            ctx.setVariable("data", "Регион добавлен!");
        } catch (NumberFormatException e) {
            ctx.setVariable("data", "Площадь должна быть числом");
        }
    }

    // ---------------------- ERROR RENDER -----------------------

    private void sendError(ITemplateEngine engine,
                           Writer writer,
                           IServletWebExchange exchange,
                           String message) {

        try {
            WebContext ctx = new WebContext(exchange, exchange.getLocale());
            ctx.setVariable("message", message);
            engine.process("error", ctx, writer);
        } catch (Exception ignored) {}
    }
}
