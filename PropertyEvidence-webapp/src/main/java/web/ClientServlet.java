package web;

import backend.Client;
import backend.ClientManager;
import backend.ClientManagerImpl;
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

@WebServlet(urlPatterns = {"/client/*", "*.client"})
public class ClientServlet extends HttpServlet {

    private final static Logger log = LoggerFactory.getLogger(ClientServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        log.debug("GET: retrieve clients");
        listClients(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        request.setCharacterEncoding("utf-8");
        String action = request.getPathInfo();
        log.debug("POST: " + action);
        switch (action) {
            case "/create":
                postCreate(request, response);
                return;
            case "/delete":
                postDelete(request, response);
                return;
            case "/update":
                postUpdate(request, response);
                return;
            case "/updateConfirm":
                postConfirmUpdate(request, response);
                return;
            default:
                request.setAttribute("error", "Unknown action");
                log.error("unknown action");
                listClients(request, response);
        }
    }

    /**
     * Executes update with values filled in form, used in post method
     *
     * @param request http request
     * @param response http response
     * @throws ServletException in case of servlet fault
     * @throws IOException in case of IO error
     */
    private void postConfirmUpdate(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String updateName = request.getParameter("name");
        String updatePhone = request.getParameter("phone");
        if (updateName == null || updateName.length() == 0 || updatePhone == null || updatePhone.length() == 0) {
            request.setAttribute("error", "Values must not be empty");
            listClients(request, response);
            return;
        }

        try {
            Client updatedClient = new Client();
            updatedClient.setId(Long.valueOf(request.getParameter("id")));
            updatedClient.setFullName(updateName);
            updatedClient.setPhoneNumber(updatePhone);

            getClientManager().updateClient(updatedClient);
        } catch (IllegalEntityException | DatabaseFaultException ex) {
            request.setAttribute("error", ex.getMessage());
            log.error(ex.getMessage());
            listClients(request, response);
            return;
        }
        response.sendRedirect(request.getContextPath() + "/client");
    }

    /**
     * Pre-fills the form with client's values, used in post method
     *
     * @param request http request
     * @param response http response
     * @throws ServletException in case of servlet fault
     * @throws IOException in case of IO error
     */
    private void postUpdate(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Long updateId = Long.valueOf(request.getParameter("id"));
        Client clientToUpdate = getClientManager().getClientById(updateId);

        request.setAttribute("clientToUpdate", clientToUpdate);
        listClients(request, response);
    }

    /**
     * Deletes client, used in post method
     *
     * @param request http request
     * @param response http response
     * @throws ServletException in case of servlet fault
     * @throws IOException in case of IO error
     */
    private void postDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Long id = Long.valueOf(request.getParameter("id"));

        try {
            Client clientToDelete = getClientManager().getClientById(id);
            getClientManager().deleteClient(clientToDelete);
        } catch (IllegalEntityException | DatabaseFaultException ex) {
            request.setAttribute("error", ex.getMessage());
            log.error(ex.getMessage());
            listClients(request, response);
            return;
        }
        response.sendRedirect(request.getContextPath() + "/client");
    }

    /**
     * Creates client, used in post method
     *
     * @param request http request
     * @param response http response
     * @throws ServletException in case of servlet fault
     * @throws IOException in case of IO error
     */
    private void postCreate(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name");
        String phone = request.getParameter("phone");
        if (name == null || name.length() == 0 || phone == null || phone.length() == 0) {
            log.error("invalid form data");
            request.setAttribute("error", "Values must not be empty");
            listClients(request, response);
            return;
        }

        try {
            Client client = new Client();
            client.setFullName(name);
            client.setPhoneNumber(phone);

            getClientManager().createClient(client);
        } catch (IllegalEntityException | DatabaseFaultException ex) {
            request.setAttribute("error", ex.getMessage());
            log.error(ex.getMessage());
            listClients(request, response);
            return;
        }
        response.sendRedirect(request.getContextPath() + "/client");
    }

    /**
     * Lists all clients, used in post method
     *
     * @param request http request
     * @param response http response
     * @throws ServletException in case of servlet fault
     * @throws IOException in case of IO error
     */
    private void listClients(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ClientManager manager = getClientManager();

        request.setAttribute("clients", manager.getClients());
        request.getRequestDispatcher("/client.jsp").forward(request, response);
    }

    /**
     * Returns an instance of ClientManagerImpl
     *
     * @return ClientManagerImpl instance
     */
    private ClientManager getClientManager() {
        return (ClientManagerImpl) getServletContext().getAttribute("clientManager");
    }
}