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
        showBooksList(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //support non-ASCII characters in form
        request.setCharacterEncoding("utf-8");
        //action specified by pathInfo
        String action = request.getPathInfo();
        switch (action) {
            case "/add":
                //getting POST parameters from form
                String name = request.getParameter("name");
                //String status = request.getParameter("status");
                String rank = request.getParameter("rank");
                //form data validity check
                if (name == null || name.length() == 0 /*|| status == null || status.length() == 0*/
                        || rank == null || rank.length() == 0) {
                    request.setAttribute("Error", "All fields must be filled!");
                    showBooksList(request, response);
                    return;
                }
                //form data processing - storing to database
                try {
                    Mission mission = new Mission();
                    mission.setName(name);
                    mission.setStatus(MissionStatus.NOT_ASSIGNED);
                    mission.setRequiredRank(Integer.parseInt(rank));
                    getMissionManager().createMission(mission);
                    //redirect-after-POST protects from multiple submission
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

    private void showBooksList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            request.setAttribute("missions", getMissionManager().findAllMissions());
            request.getRequestDispatcher(LIST_JSP).forward(request, response);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
