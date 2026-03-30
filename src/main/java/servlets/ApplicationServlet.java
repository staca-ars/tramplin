package servlets;

import dao.ApplicationDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import models.User;

import java.io.IOException;

/**
 * Сервлет для обработки действий с откликами.
 * Поддерживает: создание отклика (apply), изменение статуса (updateStatus),
 * отмену отклика (cancel).
 */
@WebServlet("/application")
public class ApplicationServlet extends HttpServlet {
    private ApplicationDAO appDAO = new ApplicationDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");

        // Отклик на вакансию (соискатель)
        if ("apply".equals(action)) {
            HttpSession session = req.getSession(false);
            if (session == null || session.getAttribute("user") == null) {
                resp.sendRedirect(req.getContextPath() + "/jsp/login.jsp");
                return;
            }
            User user = (User) session.getAttribute("user");
            int oppId = Integer.parseInt(req.getParameter("opportunityId"));
            boolean success = appDAO.apply(user.getId(), oppId);
            if (success) {
                resp.sendRedirect(req.getContextPath() + "/opportunity?id=" + oppId + "&applied=true");
            } else {
                resp.sendRedirect(req.getContextPath() + "/opportunity?id=" + oppId + "&error=true");
            }
        }
        // Изменение статуса отклика (работодатель)
        else if ("updateStatus".equals(action)) {
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

            int appId = Integer.parseInt(req.getParameter("applicationId"));
            String status = req.getParameter("status");
            appDAO.updateStatus(appId, status);
            resp.sendRedirect(req.getContextPath() + "/employer/dashboard");
        }
        // Отзыв отклика (соискатель)
        else if ("cancel".equals(action)) {
            HttpSession session = req.getSession(false);
            if (session == null || session.getAttribute("user") == null) {
                resp.sendRedirect(req.getContextPath() + "/jsp/login.jsp");
                return;
            }
            User user = (User) session.getAttribute("user");
            if (!"seeker".equals(user.getRole())) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
            int oppId = Integer.parseInt(req.getParameter("opportunityId"));
            boolean success = appDAO.cancelApplication(user.getId(), oppId);
            if (success) {
                resp.sendRedirect(req.getContextPath() + "/opportunity?id=" + oppId + "&cancelled=true");
            } else {
                resp.sendRedirect(req.getContextPath() + "/opportunity?id=" + oppId + "&error=true");
            }
        }
    }
}