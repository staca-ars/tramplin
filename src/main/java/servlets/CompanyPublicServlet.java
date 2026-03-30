package servlets;

import dao.CompanyDAO;
import dao.OpportunityDAO;
import models.Company;
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
 * Сервлет для публичной страницы компании.
 * Доступен по URL вида /company/{id}. Показывает информацию о компании
 * и список её активных (одобренных) вакансий. Администратор видит все вакансии.
 */
@WebServlet("/company/*")
public class CompanyPublicServlet extends HttpServlet {

    private CompanyDAO companyDAO = new CompanyDAO();
    private OpportunityDAO oppDAO = new OpportunityDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Извлекаем ID компании из URL: /company/123
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID компании не указан");
            return;
        }
        String idStr = pathInfo.substring(1);
        int companyId;
        try {
            companyId = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Некорректный ID компании");
            return;
        }

        Company company = companyDAO.findById(companyId);
        if (company == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Компания не найдена");
            return;
        }

        // Проверка прав: администратор может видеть все вакансии (в том числе неодобренные)
        HttpSession session = req.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("user") : null;
        boolean isAdmin = (currentUser != null && "admin".equals(currentUser.getRole()));

        List<Opportunity> opportunities = oppDAO.findByCompanyId(companyId);
        if (!isAdmin) {
            // Обычные пользователи видят только одобренные вакансии
            opportunities.removeIf(opp -> !"approved".equals(opp.getModerationStatus()));
        }

        req.setAttribute("company", company);
        req.setAttribute("opportunities", opportunities);
        req.getRequestDispatcher("/jsp/company.jsp").forward(req, resp);
    }
}