package servlets;

import dao.*;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.*;
import models.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

/**
 * Сервлет для личного кабинета соискателя (редактирование профиля).
 * GET – отображает форму редактирования профиля, избранное, отклики, контакты.
 * POST – обрабатывает обновление профиля, загрузку фото, настройки приватности,
 * управление избранным и контактами.
 */
@WebServlet("/jsp/seeker/dashboard")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 1024 * 1024 * 5,
        maxRequestSize = 1024 * 1024 * 10
)
public class SeekerDashboardServlet extends HttpServlet {

    private SeekerProfileDAO profileDAO = new SeekerProfileDAO();
    private FavoriteDAO favoriteDAO = new FavoriteDAO();
    private ApplicationDAO applicationDAO = new ApplicationDAO();
    private ConnectionDAO connectionDAO = new ConnectionDAO();
    private UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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

        SeekerProfile profile = profileDAO.findByUserId(user.getId());
        if (profile == null) {
            profile = new SeekerProfile();
            profile.setUserId(user.getId());
            profileDAO.saveProfile(profile);
        }
        req.setAttribute("profile", profile);

        List<Opportunity> favorites = favoriteDAO.findFavoriteOpportunities(user.getId());
        req.setAttribute("favorites", favorites);

        List<Application> applications = applicationDAO.findByUserId(user.getId());
        OpportunityDAO oppDAO = new OpportunityDAO();
        for (Application app : applications) {
            Opportunity opp = oppDAO.findById(app.getOpportunityId());
            app.setOpportunity(opp);
        }
        req.setAttribute("applications", applications);

        List<User> connections = connectionDAO.findConnections(user.getId());
        req.setAttribute("connections", connections);

        req.getRequestDispatcher("/jsp/seeker/dashboard.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/jsp/login.jsp");
            return;
        }
        User user = (User) session.getAttribute("user");
        String action = req.getParameter("action");

        if ("updateProfile".equals(action)) {
            SeekerProfile oldProfile = profileDAO.findByUserId(user.getId());
            String oldPhotoUrl = (oldProfile != null) ? oldProfile.getPhotoUrl() : null;

            SeekerProfile profile = new SeekerProfile();
            profile.setUserId(user.getId());
            profile.setFullName(req.getParameter("fullName"));
            profile.setUniversity(req.getParameter("university"));

            String courseStr = req.getParameter("course");
            int course = (courseStr != null && !courseStr.isEmpty()) ? Integer.parseInt(courseStr) : 0;
            profile.setCourse(course);

            String gradYearStr = req.getParameter("graduationYear");
            int graduationYear = (gradYearStr != null && !gradYearStr.isEmpty()) ? Integer.parseInt(gradYearStr) : 0;
            profile.setGraduationYear(graduationYear);

            profile.setSkills(req.getParameter("skills"));
            profile.setProjects(req.getParameter("projects"));
            profile.setGithub(req.getParameter("github"));
            profile.setPortfolio(req.getParameter("portfolio"));

            Part filePart = req.getPart("photo");
            String newPhotoUrl = oldPhotoUrl;
            File targetFile = null;
            boolean isFileUploaded = false;

            if (filePart != null && filePart.getSize() > 0) {
                String contentType = filePart.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    session.setAttribute("error", "Можно загружать только изображения");
                    resp.sendRedirect(req.getContextPath() + "/jsp/seeker/dashboard");
                    return;
                }

                String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                String extension = "";
                int dotIdx = fileName.lastIndexOf('.');
                if (dotIdx > 0) {
                    extension = fileName.substring(dotIdx);
                }

                String timestamp = String.valueOf(System.currentTimeMillis());
                String newFileName = "avatar_" + user.getId() + "_" + timestamp + extension;

                String uploadPath = getServletContext().getRealPath("/uploads/users");
                File uploadDir = new File(uploadPath);
                if (!uploadDir.exists()) uploadDir.mkdirs();

                targetFile = new File(uploadPath, newFileName);
                try {
                    filePart.write(targetFile.getAbsolutePath());
                    newPhotoUrl = "/uploads/users/" + newFileName;
                    isFileUploaded = true;
                } catch (IOException e) {
                    e.printStackTrace();
                    session.setAttribute("error", "Ошибка при сохранении фото");
                    resp.sendRedirect(req.getContextPath() + "/jsp/seeker/dashboard");
                    return;
                }
            }

            profile.setPhotoUrl(newPhotoUrl);
            profileDAO.saveProfile(profile);

            if (oldPhotoUrl != null && !oldPhotoUrl.isEmpty() && isFileUploaded && targetFile != null) {
                String oldFilePath = getServletContext().getRealPath(oldPhotoUrl);
                if (oldFilePath != null) {
                    File oldFile = new File(oldFilePath);
                    if (oldFile.exists() && !oldFile.getAbsolutePath().equals(targetFile.getAbsolutePath())) {
                        oldFile.delete();
                    }
                }
            }

            resp.sendRedirect(req.getContextPath() + "/jsp/seeker/dashboard");
            return;

        } else if ("toggleFavorite".equals(action)) {
            int oppId = Integer.parseInt(req.getParameter("opportunityId"));
            boolean isFavorite = Boolean.parseBoolean(req.getParameter("isFavorite"));
            if (isFavorite) {
                favoriteDAO.addFavorite(user.getId(), oppId);
            } else {
                favoriteDAO.removeFavorite(user.getId(), oppId);
            }
            resp.sendRedirect(req.getContextPath() + "/jsp/seeker/dashboard");
            return;

        } else if ("addConnection".equals(action)) {
            String email = req.getParameter("email");
            User other = userDAO.findByEmail(email);
            if (other != null && other.getId() != user.getId()) {
                if (!connectionDAO.isConnected(user.getId(), other.getId())) {
                    connectionDAO.addConnection(user.getId(), other.getId());
                }
            } else {
                session.setAttribute("error", "Пользователь с таким email не найден");
            }
            resp.sendRedirect(req.getContextPath() + "/jsp/seeker/dashboard");
            return;

        } else if ("removeConnection".equals(action)) {
            int otherUserId = Integer.parseInt(req.getParameter("userId"));
            connectionDAO.removeConnection(user.getId(), otherUserId);
            resp.sendRedirect(req.getContextPath() + "/jsp/seeker/dashboard");
            return;

        } else if ("updateVisibility".equals(action)) {
            String visibility = req.getParameter("visibility");
            user.setVisibility(visibility);
            userDAO.updateVisibility(user.getId(), visibility);
            session.setAttribute("user", user);
            resp.sendRedirect(req.getContextPath() + "/jsp/seeker/dashboard");
            return;
        }

        resp.sendRedirect(req.getContextPath() + "/jsp/seeker/dashboard");
    }
}