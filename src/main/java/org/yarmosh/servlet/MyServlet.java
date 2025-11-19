package org.yarmosh.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.web.IWebApplication;
import org.thymeleaf.web.servlet.IServletWebExchange;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;
import org.yarmosh.controller.HomeController;
import org.yarmosh.controller.IController;
import org.yarmosh.controller.WeatherController;
import org.yarmosh.service.WeatherService;
import org.thymeleaf.templateresolver.WebApplicationTemplateResolver;


import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name="MyServletname", urlPatterns = "/MyServlettest/*")
public class MyServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(MyServlet.class.getName());
    private static final long serialVersionUID = 1L;
    private JakartaServletWebApplication application;
    private ITemplateEngine templateEngine;

    @Override
    public void init(){
        this.application = JakartaServletWebApplication.buildApplication(getServletContext());

        this.templateEngine = buildTemplateEngine(this.application);

        WeatherService weatherService = new WeatherService();

    }

    private ITemplateEngine buildTemplateEngine(final IWebApplication application) {
        final WebApplicationTemplateResolver templateResolver = new WebApplicationTemplateResolver(application);

        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setPrefix("/WEB-INF/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setCacheTTLMs(Long.valueOf(3600000L));

        templateResolver.setCacheable(true);

        final TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

        return templateEngine;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("text/html;charset=UTF-8");

        try (Writer writer = response.getWriter()) {

            IController controller;

            String uri = request.getRequestURI();
            String path = uri.replaceFirst(".*/MyServlettest/", "");

            if (path.equals("") || path.equals("/")) {
                controller = new HomeController();
            } else if (path.startsWith("weather")) {
                controller = new WeatherController(path);
            } else {
                controller = new HomeController();
            }

            IServletWebExchange exchange = application.buildExchange(request, response);
            controller.process(exchange, templateEngine, writer);

        } catch (Exception e) {

            logger.log(Level.SEVERE, "Критическая ошибка сервлета", e);

            showErrorPage(request, response, "Критическая ошибка: " + e.getMessage());
        }
    }

    private void showErrorPage(HttpServletRequest req,
                               HttpServletResponse resp,
                               String message) throws IOException {

        WebContext ctx = new WebContext(
                application.buildExchange(req, resp),
                req.getLocale()
        );
        ctx.setVariable("message", message);

        resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

        templateEngine.process("error", ctx, resp.getWriter());
    }


    private void handleCookies(HttpServletRequest request, HttpServletResponse response) {

        String lastVisitRaw = null;
        int visits = 0;

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if ("lastVisit".equals(c.getName())) {
                    lastVisitRaw = c.getValue();
                } else if ("visits".equals(c.getName())) {
                    try {
                        visits = Integer.parseInt(c.getValue());
                    } catch (NumberFormatException ignored) {}
                }
            }
        }

        visits++;

        Cookie visitCookie = new Cookie("visits", String.valueOf(visits));
        Cookie dateCookie = new Cookie("lastVisit", String.valueOf(System.currentTimeMillis()));

        visitCookie.setPath("/");
        dateCookie.setPath("/");

        int month = 60 * 60 * 24 * 30;
        visitCookie.setMaxAge(month);
        dateCookie.setMaxAge(month);

        response.addCookie(visitCookie);
        response.addCookie(dateCookie);

        String lastVisitStr = null;

        if (lastVisitRaw != null) {
            try {
                long ts = Long.parseLong(lastVisitRaw);
                Date date = new Date(ts);

                SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
                lastVisitStr = fmt.format(date);
            } catch (Exception ignored) {}
        }

        request.setAttribute("lastVisit", lastVisitStr);
        request.setAttribute("visits", visits);
    }

}