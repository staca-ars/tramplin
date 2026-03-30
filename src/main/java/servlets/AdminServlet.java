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
 * Сервлет для панели администратора.
 * Обрабатывает запросы на модерацию компаний и вакансий,
 * управление пользователями (блокировка, разблокировка, удаление).
 * Доступен только для пользователей с ролью admin.
 */
@WebServlet("/admin/dashboard")
public class AdminServlet extends HttpServlet {

    private CompanyDAO companyDAO = new CompanyDAO();
    private OpportunityDAO oppDAO = new OpportunityDAO();
    private UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/jsp/login.jsp");
            return;
        }
        User user = (User) session.getAttribute("user");
        if (!"admin".equals(user.getRole())) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        // Загружаем данные для отображения в админ-панели
        List<Company> pendingCompanies = companyDAO.getPendingCompanies();
        List<Opportunity> pendingOpportunities = oppDAO.getPendingOpportunities();
        List<User> allUsers = userDAO.findAll();
        List<Opportunity> allOpportunities = oppDAO.findAllForAdmin();

        req.setAttribute("pendingCompanies", pendingCompanies);
        req.setAttribute("pendingOpportunities", pendingOpportunities);
        req.setAttribute("allUsers", allUsers);
        req.setAttribute("allOpportunities", allOpportunities);

        req.getRequestDispatcher("/jsp/admin/dashboard.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/jsp/login.jsp");
            return;
        }
        User user = (User) session.getAttribute("user");
        if (!"admin".equals(user.getRole())) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String action = req.getParameter("action");
        String entity = req.getParameter("entity");

        try {
            if ("approve".equals(action)) {
                if ("company".equals(entity)) {
                    int companyId = Integer.parseInt(req.getParameter("id"));
                    companyDAO.approveCompany(companyId);
                } else if ("opportunity".equals(entity)) {
                    int oppId = Integer.parseInt(req.getParameter("id"));
                    oppDAO.approveOpportunity(oppId);
                }
            } else if ("reject".equals(action)) {
                if ("company".equals(entity)) {
                    int companyId = Integer.parseInt(req.getParameter("id"));
                    String reason = req.getParameter("reason");
                    companyDAO.rejectCompany(companyId, reason);
                } else if ("opportunity".equals(entity)) {
                    int oppId = Integer.parseInt(req.getParameter("id"));
                    String reason = req.getParameter("reason");
                    oppDAO.rejectOpportunity(oppId, reason);
                }
            } else if ("block".equals(action) && "user".equals(entity)) {
                int userId = Integer.parseInt(req.getParameter("id"));
                userDAO.blockUser(userId);
            } else if ("unblock".equals(action) && "user".equals(entity)) {
                int userId = Integer.parseInt(req.getParameter("id"));
                userDAO.unblockUser(userId);
            } else if ("delete".equals(action)) {
                if ("company".equals(entity)) {
                    int companyId = Integer.parseInt(req.getParameter("id"));
                    companyDAO.deleteCompany(companyId);
                } else if ("opportunity".equals(entity)) {
                    int oppId = Integer.parseInt(req.getParameter("id"));
                    oppDAO.deleteOpportunity(oppId);
                } else if ("user".equals(entity)) {
                    int userId = Integer.parseInt(req.getParameter("id"));
                    userDAO.deleteUser(userId);
                }
            }
        } catch (NumberFormatException e) {
            // В случае некорректного ID, просто перенаправляем обратно (или можно установить сообщение об ошибке)
            resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
            return;
        }

        resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
    }
}