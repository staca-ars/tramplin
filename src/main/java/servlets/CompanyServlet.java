package servlets;

import dao.CompanyDAO;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.*;
import models.Company;
import models.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * Сервлет для управления компанией работодателя.
 * GET – отображает форму редактирования компании.
 * POST – обрабатывает обновление информации о компании и загрузку логотипа.
 */
@WebServlet("/company")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 1024 * 1024 * 5,
        maxRequestSize = 1024 * 1024 * 10
)
public class CompanyServlet extends HttpServlet {

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

        Company company = companyDAO.findByOwnerId(user.getId());
        if (company == null) {
            company = new Company("Моя компания", "", user.getId(), "pending");
            companyDAO.saveCompany(company);
        }
        req.setAttribute("company", company);
        req.getRequestDispatcher("/jsp/employer/company_edit.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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

        String action = req.getParameter("action");
        // Повторная отправка на модерацию после отклонения
        if ("resubmit".equals(action)) {
            int companyId = Integer.parseInt(req.getParameter("companyId"));
            Company company = companyDAO.findById(companyId);
            if (company != null && company.getOwnerId() == user.getId()) {
                company.setModerationStatus("pending");
                company.setRejectionReason(null);
                companyDAO.updateCompany(company);
            }
            resp.sendRedirect(req.getContextPath() + "/employer/dashboard");
            return;
        }

        // Обновление основной информации о компании
        int companyId = Integer.parseInt(req.getParameter("companyId"));
        Company company = companyDAO.findById(companyId);
        if (company == null || company.getOwnerId() != user.getId()) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String oldLogoPath = company.getLogoUrl();

        company.setName(req.getParameter("name"));
        company.setDescription(req.getParameter("description"));
        company.setEmail(req.getParameter("email"));
        company.setPhone(req.getParameter("phone"));
        company.setWebsite(req.getParameter("website"));

        // Обработка загрузки нового логотипа
        Part filePart = req.getPart("logoFile");
        String newLogoPath = null;
        boolean isFileUploaded = false;
        File targetFile = null;

        if (filePart != null && filePart.getSize() > 0) {
            String contentType = filePart.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                session.setAttribute("error", "Можно загружать только изображения (jpg, png, gif и т.д.)");
                resp.sendRedirect(req.getContextPath() + "/company");
                return;
            }

            String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
            String extension = "";
            int dotIdx = fileName.lastIndexOf('.');
            if (dotIdx > 0) {
                extension = fileName.substring(dotIdx);
            }

            String timestamp = String.valueOf(System.currentTimeMillis());
            String newFileName = "logo_" + company.getId() + "_" + timestamp + extension;

            String uploadPath = getServletContext().getRealPath("/uploads/companies");
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            targetFile = new File(uploadPath, newFileName);
            try {
                filePart.write(targetFile.getAbsolutePath());
                newLogoPath = "/uploads/companies/" + newFileName;
                isFileUploaded = true;
            } catch (IOException e) {
                e.printStackTrace();
                session.setAttribute("error", "Ошибка при сохранении файла");
                resp.sendRedirect(req.getContextPath() + "/company");
                return;
            }
        }

        if (isFileUploaded) {
            company.setLogoUrl(newLogoPath);
        }

        companyDAO.updateCompany(company);

        // Удаляем старый файл логотипа, если загружен новый
        if (oldLogoPath != null && !oldLogoPath.isEmpty() && isFileUploaded && targetFile != null) {
            String oldFilePath = getServletContext().getRealPath(oldLogoPath);
            if (oldFilePath != null) {
                File oldFile = new File(oldFilePath);
                if (oldFile.exists() && !oldFile.getAbsolutePath().equals(targetFile.getAbsolutePath())) {
                    oldFile.delete();
                }
            }
        }

        resp.sendRedirect(req.getContextPath() + "/employer/dashboard");
    }
}