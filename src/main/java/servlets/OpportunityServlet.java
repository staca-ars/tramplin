package servlets;

import dao.ApplicationDAO;
import dao.OpportunityDAO;
import dao.CompanyDAO;
import dao.TagDAO;
import models.Opportunity;
import models.Company;
import models.Tag;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import models.User;

import java.io.IOException;
import java.util.List;

/**
 * Сервлет для отображения страницы конкретной вакансии.
 * Проверяет права доступа: одобренную вакансию могут смотреть все,
 * неодобренную – только администратор и владелец компании.
 */
@WebServlet("/opportunity")
public class OpportunityServlet extends HttpServlet {

    private OpportunityDAO oppDAO = new OpportunityDAO();
    private CompanyDAO companyDAO = new CompanyDAO();
    private TagDAO tagDAO = new TagDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idParam = req.getParameter("id");
        if (idParam == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID вакансии не указан");
            return;
        }
        int oppId = Integer.parseInt(idParam);
        Opportunity opp = oppDAO.findById(oppId);
        if (opp == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Вакансия не найдена");
            return;
        }

        // Проверка, откликался ли текущий пользователь (только для соискателей)
        boolean hasApplied = false;
        User currentUser = (User) req.getSession().getAttribute("user");
        if (currentUser != null && "seeker".equals(currentUser.getRole())) {
            ApplicationDAO appDAO = new ApplicationDAO();
            hasApplied = appDAO.hasApplied(currentUser.getId(), oppId);
        }
        req.setAttribute("hasApplied", hasApplied);

        // Загружаем компанию
        Company company = companyDAO.findById(opp.getCompanyId());

        // Проверка доступа к неодобренной вакансии
        if (!"approved".equals(opp.getModerationStatus())) {
            if (currentUser == null || (!"admin".equals(currentUser.getRole()) && currentUser.getId() != company.getOwnerId())) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Доступ запрещён");
                return;
            }
        }

        List<Tag> tags = tagDAO.findTagsByOpportunityId(oppId);
        req.setAttribute("opportunity", opp);
        req.setAttribute("company", company);
        req.setAttribute("tags", tags);
        req.getRequestDispatcher("/jsp/opportunity.jsp").forward(req, resp);
    }
}