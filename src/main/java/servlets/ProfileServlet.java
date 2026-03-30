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
 * Сервлет для отображения и управления профилем соискателя.
 * GET – показывает публичный или личный профиль (с дополнительными блоками для владельца).
 * POST – обрабатывает действия: добавление/удаление из избранного, управление контактами,
 * удаление отклика.
 */
@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {

    private SeekerProfileDAO profileDAO = new SeekerProfileDAO();
    private UserDAO userDAO = new UserDAO();
    private FavoriteDAO favoriteDAO = new FavoriteDAO();
    private ApplicationDAO applicationDAO = new ApplicationDAO();
    private ConnectionDAO connectionDAO = new ConnectionDAO();
    private OpportunityDAO oppDAO = new OpportunityDAO();
    private CompanyDAO companyDAO = new CompanyDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String userIdParam = req.getParameter("id");
        int userId;

        HttpSession session = req.getSession(false);
        User currentUser = null;
        if (session != null) {
            currentUser = (User) session.getAttribute("user");
        }

        // Если ID не передан – показываем профиль текущего пользователя
        if (userIdParam == null || userIdParam.isEmpty()) {
            if (currentUser == null) {
                resp.sendRedirect(req.getContextPath() + "/jsp/login.jsp");
                return;
            }
            userId = currentUser.getId();
        } else {
            userId = Integer.parseInt(userIdParam);
        }

        SeekerProfile profile = profileDAO.findByUserId(userId);
        if (profile == null) {
            profile = new SeekerProfile();
            profile.setUserId(userId);
            profileDAO.saveProfile(profile);
        }

        User profileUser = userDAO.findById(userId);
        if (profileUser == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // Проверка приватности
        boolean canView = true;
        if ("private".equals(profileUser.getVisibility())) {
            if (currentUser == null) {
                canView = false;
            } else if (currentUser.getId() != userId) {
                // Для упрощения – только владелец. Можно добавить проверку на контакты.
                canView = false;
            }
        }
        if (!canView) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Этот профиль закрыт");
            return;
        }

        boolean isOwner = (currentUser != null && currentUser.getId() == userId);
        req.setAttribute("isOwner", isOwner);

        // Загружаем дополнительные данные только для владельца
        if (isOwner) {
            List<Opportunity> favorites = favoriteDAO.findFavoriteOpportunities(userId);
            req.setAttribute("favorites", favorites);

            List<Application> applications = applicationDAO.findByUserId(userId);
            for (Application app : applications) {
                Opportunity opp = oppDAO.findById(app.getOpportunityId());
                if (opp != null) {
                    Company company = companyDAO.findById(opp.getCompanyId());
                    if (company != null) {
                        opp.setCompanyName(company.getName());
                    }
                }
                app.setOpportunity(opp);
            }
            req.setAttribute("applications", applications);
        }

        req.setAttribute("profile", profile);
        req.setAttribute("profileUser", profileUser);
        req.getRequestDispatcher("/jsp/profile.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/jsp/login.jsp");
            return;
        }
        User currentUser = (User) session.getAttribute("user");
        String action = req.getParameter("action");

        if ("toggleFavorite".equals(action)) {
            int oppId = Integer.parseInt(req.getParameter("opportunityId"));
            boolean isFavorite = Boolean.parseBoolean(req.getParameter("isFavorite"));
            if (isFavorite) {
                favoriteDAO.addFavorite(currentUser.getId(), oppId);
            } else {
                favoriteDAO.removeFavorite(currentUser.getId(), oppId);
            }
            resp.sendRedirect(req.getContextPath() + "/profile");
        } else if ("addConnection".equals(action)) {
            String email = req.getParameter("email");
            User other = userDAO.findByEmail(email);
            if (other != null && other.getId() != currentUser.getId()) {
                if (!connectionDAO.isConnected(currentUser.getId(), other.getId())) {
                    connectionDAO.addConnection(currentUser.getId(), other.getId());
                }
            } else {
                session.setAttribute("error", "Пользователь с таким email не найден");
            }
            resp.sendRedirect(req.getContextPath() + "/profile");
        } else if ("removeConnection".equals(action)) {
            int otherUserId = Integer.parseInt(req.getParameter("userId"));
            connectionDAO.removeConnection(currentUser.getId(), otherUserId);
            resp.sendRedirect(req.getContextPath() + "/profile");
        } else if ("deleteResponse".equals(action)) {
            int oppId = Integer.parseInt(req.getParameter("opportunityId"));
            boolean success = applicationDAO.cancelApplication(currentUser.getId(), oppId);
            if (success) {
                session.setAttribute("success", "Отклик удалён");
            } else {
                session.setAttribute("error", "Не удалось удалить отклик");
            }
            resp.sendRedirect(req.getContextPath() + "/profile");
        } else {
            resp.sendRedirect(req.getContextPath() + "/profile");
        }
    }
}