package web;

import backend.Property;
import backend.PropertyManager;
import backend.PropertyType;
import common.DatabaseFaultException;
import common.IllegalEntityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;

/**
 * Servlet for managing properties
 * @author Martin Balucha
 */

@WebServlet(urlPatterns = {"/property/*", "*.property"})
public class PropertyServlet extends HttpServlet{

    private final static Logger log = LoggerFactory.getLogger(PropertyServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.debug("GET: retrieve properties");
        listAllProperties(request, response);
    }


    /**
     * Returns an instance of property manager
     * @return  a property manager
     */
    private PropertyManager getPropertyManager() {
        return (PropertyManager) getServletContext().getAttribute("propertyManager");
    }


    /**
     * Lists all properties which are currently in the database
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    private void listAllProperties(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        PropertyManager manager = getPropertyManager();
        request.setAttribute("properties", manager.getAllProperties());
        request.getRequestDispatcher("/property.jsp").forward(request, response);
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");

        String action = request.getPathInfo();
        log.debug("POST: " + action);
        switch (action) {
            case "/create":
                doPostCreate(request, response);
                return;

            case "/delete":
                doPostDelete(request, response);
                return;

            case "/update":
                doPostUpdate(request, response);
                return;

            case "/updateConfirm":
                doPostUpdateConfirm(request, response);
                return;

            default:
                request.setAttribute("error", "Action not recognised");
                log.error("unknown action");
                listAllProperties(request, response);
        }
    }


    private void doPostUpdate(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        Long id = Long.valueOf(request.getParameter("id"));
        Property propertyToBeUpdated = getPropertyManager().getPropertyById(id);

        request.setAttribute("propertyToBeUpdated", propertyToBeUpdated);
        listAllProperties(request, response);
    }


    private void doPostUpdateConfirm(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String address = request.getParameter("address");
        BigDecimal area = new BigDecimal(request.getParameter("area"));
        BigDecimal price = new BigDecimal(request.getParameter("price"));
        PropertyType type;
        try {
            type = PropertyType.valueOf(request.getParameter("type").toUpperCase());
        } catch (IllegalArgumentException ex) {
            request.setAttribute("error", "Invalid type of the property");
            log.error(ex.getMessage());
            listAllProperties(request, response);
            return;
        }

        if (address == null || address.isEmpty() || area == null || price == null) {
            request.setAttribute("error", "Parameters must not be null or have zero length");
            return;
        }

        try {
            Property updatedProperty = new Property();
            updatedProperty.setAddress(address);
            updatedProperty.setType(type);
            updatedProperty.setArea(area);
            updatedProperty.setPrice(price);
            updatedProperty.setId(Long.valueOf(request.getParameter("id")));
            getPropertyManager().updateProperty(updatedProperty);
        } catch (IllegalEntityException | DatabaseFaultException ex) {
            request.setAttribute("error", ex.getMessage());
            log.error(ex.getMessage());
            listAllProperties(request, response);
            return;
        }
        response.sendRedirect(request.getContextPath() + "/property");
    }


    private void doPostDelete(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        Long id = Long.valueOf(request.getParameter("id"));
        try {
            PropertyManager manager = getPropertyManager();
            Property wantedProperty = manager.getPropertyById(id);
            manager.deleteProperty(wantedProperty);
        } catch (IllegalEntityException | DatabaseFaultException ex) {
            request.setAttribute("error", ex.getMessage());
            log.error(ex.getMessage());
            listAllProperties(request, response);
            return;
        }
        response.sendRedirect(request.getContextPath() + "/property");
    }


    private void doPostCreate(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
        String address = request.getParameter("address");
        BigDecimal area = new BigDecimal(request.getParameter("area"));
        BigDecimal price = new BigDecimal(request.getParameter("price"));
        PropertyType type;
        try {
            type = PropertyType.valueOf(request.getParameter("type").toUpperCase());
        } catch (IllegalArgumentException ex) {
            request.setAttribute("error", "Invalid type of the property");
            log.error(ex.getMessage());
            listAllProperties(request, response);
            return;
        }

        if (address == null || address.isEmpty() || area == null || price == null) {
            request.setAttribute("error", "Parameters must not be null or have zero length");
            log.error("invalid form data");
            return;
        }

        try {
            Property property = new Property();
            property.setPrice(price);
            property.setArea(area);
            property.setType(type);
            property.setAddress(address);

            getPropertyManager().createProperty(property);
        } catch (IllegalEntityException | DatabaseFaultException ex) {
            request.setAttribute("error", ex.getMessage());
            log.error(ex.getMessage());
            listAllProperties(request, response);
        }
        response.sendRedirect(request.getContextPath() + "/property");
    }

}
