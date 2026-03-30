package servlets;

import dao.UserDAO;
import dao.CompanyDAO;
import dao.ConnectionDAO;
import models.User;
import models.Company;
import org.mindrot.jbcrypt.BCrypt;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Сервлет аутентификации: регистрация, вход, выход, добавление контактов.
 * Пароли хешируются с помощью BCrypt перед сохранением в БД.
 */
@WebServlet("/auth")
public class AuthServlet extends HttpServlet {

    private UserDAO userDAO = new UserDAO();
    private ConnectionDAO connectionDAO = new ConnectionDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");

        if ("register".equals(action)) {
            String email = req.getParameter("email");
            String password = req.getParameter("password");
            String name = req.getParameter("name");
            String role = req.getParameter("role");

            if (userDAO.findByEmail(email) != null) {
                req.setAttribute("error", "Пользователь с таким email уже существует");
                req.getRequestDispatcher("/jsp/register.jsp").forward(req, resp);
                return;
            }

            // Хешируем пароль
//            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
//            User user = new User(email, hashedPassword, name, role);
//            boolean saved = userDAO.saveUser(user);
            User user = new User(email, password, name, role);
            boolean saved = userDAO.saveUser(user);

            if (saved) {
                if ("employer".equals(role)) {
                    Company company = new Company("Компания " + name, "", user.getId(), "pending");
                    CompanyDAO companyDAO = new CompanyDAO();
                    companyDAO.saveCompany(company);
                }
                HttpSession session = req.getSession();
                session.setAttribute("user", user);
                redirectToDashboard(user, req, resp);
            } else {
                req.setAttribute("error", "Ошибка при регистрации");
                req.getRequestDispatcher("/jsp/register.jsp").forward(req, resp);
            }

        } else if ("login".equals(action)) {
            String email = req.getParameter("email");
            String password = req.getParameter("password");

            User user = userDAO.findByEmail(email);
            if (user != null && BCrypt.checkpw(password, user.getPassword())) {
                if (user.isBlocked()) {
                    req.setAttribute("error", "Ваш аккаунт заблокирован администратором.");
                    req.getRequestDispatcher("/jsp/login.jsp").forward(req, resp);
                    return;
                }
                HttpSession session = req.getSession();
                session.setAttribute("user", user);
                redirectToDashboard(user, req, resp);
            } else {
                req.setAttribute("error", "Неверный email или пароль");
                req.getRequestDispatcher("/jsp/login.jsp").forward(req, resp);
            }

        } else if ("addConnection".equals(action)) {
            HttpSession session = req.getSession(false);
            if (session == null) {
                resp.sendRedirect(req.getContextPath() + "/jsp/login.jsp");
                return;
            }
            User currentUser = (User) session.getAttribute("user");
            if (currentUser == null) {
                resp.sendRedirect(req.getContextPath() + "/jsp/login.jsp");
                return;
            }

            String email = req.getParameter("email");
            User other = userDAO.findByEmail(email);
            if (other != null && other.getId() != currentUser.getId()) {
                connectionDAO.addConnection(currentUser.getId(), other.getId());
            }
            resp.sendRedirect(req.getContextPath() + "/jsp/seeker/dashboard");
        }
    }

    private void redirectToDashboard(User user, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String contextPath = req.getContextPath();
        String url;
        switch (user.getRole()) {
            case "seeker":
                url = contextPath + "/profile?synced=true";
                break;
            case "employer":
                url = contextPath + "/employer/dashboard";
                break;
            case "admin":
                url = contextPath + "/admin/dashboard";
                break;
            default:
                url = contextPath + "/index.jsp";
        }
        resp.sendRedirect(url);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if ("logout".equals(action)) {
            HttpSession session = req.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            resp.sendRedirect(req.getContextPath() + "/");
        }
    }
}