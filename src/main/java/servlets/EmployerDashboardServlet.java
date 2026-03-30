package servlets;

import dao.*;
import models.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * Сервлет для дашборда работодателя.
 * Отображает информацию о компании и список вакансий с откликами.
 */
@WebServlet("/employer/dashboard")
public class EmployerDashboardServlet extends HttpServlet {

    private CompanyDAO companyDAO = new CompanyDAO();
    private OpportunityDAO oppDAO = new OpportunityDAO();
    private ApplicationDAO appDAO = new ApplicationDAO();

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

        Company company = companyDAO.findByOwnerId(user.getId());
        if (company == null) {
            company = new Company("Моя компания", "", user.getId(), "pending");
            companyDAO.saveCompany(company);
        }
        req.setAttribute("company", company);

        // Список вакансий компании
        List<Opportunity> opportunities = oppDAO.findByCompanyId(company.getId());
        // Загружаем отклики для каждой вакансии
        for (Opportunity opp : opportunities) {
            List<Application> apps = appDAO.findByOpportunityId(opp.getId());
            opp.setApplications(apps);
        }
        req.setAttribute("opportunities", opportunities);

        req.getRequestDispatcher("/jsp/employer/dashboard.jsp").forward(req, resp);
    }
}