package org.yarmosh.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

@WebServlet(name="MyServletname", urlPatterns = "/MyServlettest/*")
public class MyServlet extends HttpServlet {
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

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        try (Writer writer = response.getWriter()) {

            IController controller;

            String requestURI = request.getRequestURI();
            String query = request.getQueryString();

            if ("/MyServlettest".equals(requestURI) && query == null) {
                controller = new HomeController();
            }
            else {
                controller = new WeatherController();
            }

            final IServletWebExchange webExchange = this.application.buildExchange(request, response);

            controller.process(webExchange, templateEngine, writer);

        }  catch (Exception e) {
            e.printStackTrace();
            if (!response.isCommitted()) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            }
        }
    }
}