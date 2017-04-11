package cz.muni.fi.pv168.web;

import cz.muni.fi.pv168.app.agent.Agent;
import cz.muni.fi.pv168.app.agent.AgentManager;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * Servlet for managing agents.
 *
 * @author Daniel Homola
 */
@WebServlet(AgentServlet.URL_MAPPING + "/*")
public class AgentServlet extends HttpServlet{
    private static final String LIST_JSP = "/listAgent.jsp";
    public static final String URL_MAPPING = "/agents";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        showAgentsList(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        String action = request.getPathInfo();
        switch (action) {
            case "/add":
                String name = request.getParameter("name");
                String rank = request.getParameter("rank");
                if (name == null || name.length() == 0 || rank == null || rank.length() == 0) {
                    request.setAttribute("Error", "All fields must be filled!");
                    showAgentsList(request, response);
                    return;
                }
                try {
                    Agent agent = new Agent();
                    agent.setName(name);
                    agent.setRank(Integer.parseInt(rank));
                    agent.setAlive(true);
                    getAgentManager().createAgent(agent);
                    response.sendRedirect(request.getContextPath()+URL_MAPPING);
                    return;
                } catch (Exception e) {
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                    return;
                }
            case "/delete":
                try {
                    Long id = Long.valueOf(request.getParameter("id"));
                    getAgentManager().deleteAgent(getAgentManager().findAgent(id));
                    response.sendRedirect(request.getContextPath()+URL_MAPPING);
                    return;
                } catch (Exception e) {
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                    return;
                }
            case "/update":
                //TODO
                return;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Unknown action " + action);
        }
    }

    private AgentManager getAgentManager() {
        return (AgentManager) getServletContext().getAttribute("agentManager");
    }

    private void showAgentsList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            request.setAttribute("agents", getAgentManager().findAllAgents());
            request.getRequestDispatcher(LIST_JSP).forward(request, response);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
