package cz.muni.fi.pv168.web;

import cz.muni.fi.pv168.app.mission.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * Servlet for managing missions.
 *
 * @author Adam Baňanka
 */
@WebServlet(MissionServlet.URL_MAPPING + "/*")
public class MissionServlet extends HttpServlet{
    private static final String LIST_JSP = "/list.jsp";
    public static final String URL_MAPPING = "/missions";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        showMissionsList(request, response);
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
                    showMissionsList(request, response);
                    return;
                }
                try {
                    Mission mission = new Mission();
                    mission.setName(name);
                    mission.setStatus(MissionStatus.NOT_ASSIGNED);
                    mission.setRequiredRank(Integer.parseInt(rank));
                    getMissionManager().createMission(mission);
                    response.sendRedirect(request.getContextPath()+URL_MAPPING);
                    return;
                } catch (Exception e) {
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                    return;
                }
            case "/delete":
                try {
                    Long id = Long.valueOf(request.getParameter("id"));
                    getMissionManager().deleteMission(getMissionManager().findMission(id));
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

    private MissionManager getMissionManager() {
        return (MissionManager) getServletContext().getAttribute("missionManager");
    }

    private void showMissionsList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            request.setAttribute("missions", getMissionManager().findAllMissions());
            request.getRequestDispatcher(LIST_JSP).forward(request, response);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
