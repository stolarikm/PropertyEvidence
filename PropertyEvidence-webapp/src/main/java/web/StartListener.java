package web;

import backend.ClientManager;
import backend.ClientManagerImpl;
import backend.PropertyManagerImpl;
import backend.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.sql.DataSource;

@WebListener
public class StartListener implements ServletContextListener  {

    private final static Logger log = LoggerFactory.getLogger(StartListener.class);

    @Override
    public void contextInitialized(ServletContextEvent ev) {
        log.info("web application initialized");
        ServletContext servletContext = ev.getServletContext();
        DataSource dataSource = Main.createMemoryDatabase();
        servletContext.setAttribute("clientManager", new ClientManagerImpl(dataSource));
        servletContext.setAttribute("propertyManager", new PropertyManagerImpl(dataSource));
    }

    @Override
    public void contextDestroyed(ServletContextEvent ev) {
        log.info("web application terminated");
    }
}