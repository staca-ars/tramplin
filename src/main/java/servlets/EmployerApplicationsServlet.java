package servlets;

import dao.ApplicationDAO;
import dao.OpportunityDAO;
import dao.CompanyDAO;
import models.ApplicationWithUser;
import models.Opportunity;
import models.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * Сервлет для отображения откликов на конкретную вакансию в панели работодателя.
 * Доступен по URL /employer/applications?opportunityId=...
 */
@WebServlet("/employer/applications")
public class EmployerApplicationsServlet extends HttpServlet {

    private ApplicationDAO appDAO = new ApplicationDAO();
    private OpportunityDAO oppDAO = new OpportunityDAO();
    private CompanyDAO companyDAO = new CompanyDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/jsp/login.jsp");
            return;
        }
        User user = (User) session.getAttribute("user");
        if (!"employer".equals(user.getRole())) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        int oppId = Integer.parseInt(req.getParameter("opportunityId"));
        Opportunity opp = oppDAO.findById(oppId);
        if (opp == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // Проверка, что вакансия принадлежит компании текущего пользователя
        if (companyDAO.findByOwnerId(user.getId()).getId() != opp.getCompanyId()) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        List<ApplicationWithUser> applications = appDAO.findApplicationsWithUserByOpportunityId(oppId);
        req.setAttribute("opportunity", opp);
        req.setAttribute("applications", applications);
        req.getRequestDispatcher("/jsp/employer/applications.jsp").forward(req, resp);
    }
}