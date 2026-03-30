//package servlets;
//
//import dao.CompanyDAO;
//import dao.OpportunityDAO;
//import dao.SeekerProfileDAO;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.annotation.MultipartConfig;
//import jakarta.servlet.annotation.WebServlet;
//import jakarta.servlet.http.HttpServlet;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import jakarta.servlet.http.HttpSession;
//import jakarta.servlet.http.Part;
//import models.Company;
//import models.Opportunity;
//import models.SeekerProfile;
//import models.User;
//
//import java.io.File;
//import java.io.IOException;
//import java.nio.file.Paths;
//
///**
// * Сервлет для загрузки обложек для пользователей, компаний и вакансий.
// * Принимает multipart/form-data, проверяет права доступа, сохраняет файл
// * и обновляет соответствующую запись в БД.
// * отключен из за ряда проблем
// */
//@WebServlet("/cover")
//@MultipartConfig(
//        fileSizeThreshold = 1024 * 1024,
//        maxFileSize = 1024 * 1024 * 5,
//        maxRequestSize = 1024 * 1024 * 10
//)
//public class CoverUploadServlet extends HttpServlet {
//
//    @Override
//    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        HttpSession session = req.getSession(false);
//        if (session == null || session.getAttribute("user") == null) {
//            resp.sendRedirect(req.getContextPath() + "/jsp/login.jsp");
//            return;
//        }
//        User currentUser = (User) session.getAttribute("user");
//
//        String entityType = req.getParameter("entityType");
//        int entityId = Integer.parseInt(req.getParameter("entityId"));
//        Part filePart = req.getPart("coverFile");
//
//        if (filePart == null || filePart.getSize() == 0) {
//            session.setAttribute("error", "Файл не выбран");
//            resp.sendRedirect(getReferer(req));
//            return;
//        }
//
//        // Проверка типа файла
//        String contentType = filePart.getContentType();
//        if (contentType == null || !contentType.startsWith("image/")) {
//            session.setAttribute("error", "Можно загружать только изображения");
//            resp.sendRedirect(getReferer(req));
//            return;
//        }
//
//        // Проверка прав доступа
//        boolean authorized = false;
//        switch (entityType) {
//            case "user":
//                authorized = (currentUser.getId() == entityId);
//                break;
//            case "company":
//                CompanyDAO companyDAO = new CompanyDAO();
//                Company company = companyDAO.findById(entityId);
//                authorized = (company != null && company.getOwnerId() == currentUser.getId());
//                break;
//            case "opportunity":
//                OpportunityDAO oppDAO = new OpportunityDAO();
//                Opportunity opp = oppDAO.findById(entityId);
//                if (opp != null) {
//                    CompanyDAO cDAO = new CompanyDAO();
//                    Company c = cDAO.findById(opp.getCompanyId());
//                    authorized = (c != null && c.getOwnerId() == currentUser.getId());
//                }
//                break;
//        }
//        if (!authorized) {
//            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
//            return;
//        }
//
//        // Определяем расширение
//        String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
//        String extension = "";
//        int dotIdx = fileName.lastIndexOf('.');
//        if (dotIdx > 0) extension = fileName.substring(dotIdx);
//
//        String timestamp = String.valueOf(System.currentTimeMillis());
//        String newFileName = "cover_" + entityType + "_" + entityId + "_" + timestamp + extension;
//
//        // Папка uploads/covers
//        String uploadPath = getServletContext().getRealPath("/uploads/covers");
//        File uploadDir = new File(uploadPath);
//        if (!uploadDir.exists()) uploadDir.mkdirs();
//
//        File targetFile = new File(uploadPath, newFileName);
//        filePart.write(targetFile.getAbsolutePath());
//        String coverUrl = "/uploads/covers/" + newFileName;
//
//        // Сохраняем путь в БД в зависимости от типа сущности
//        boolean updated = false;
//        switch (entityType) {
//            case "user":
//                SeekerProfileDAO profileDAO = new SeekerProfileDAO();
//                SeekerProfile profile = profileDAO.findByUserId(entityId);
//                if (profile != null) {
//                    String oldCover = profile.getCoverUrl();
//                    profile.setCoverUrl(coverUrl);
//                    updated = profileDAO.saveProfile(profile);
//                    if (oldCover != null && !oldCover.isEmpty()) {
//                        String oldPath = getServletContext().getRealPath(oldCover);
//                        if (oldPath != null) new File(oldPath).delete();
//                    }
//                }
//                break;
//            case "company":
//                CompanyDAO companyDAO = new CompanyDAO();
//                Company company = companyDAO.findById(entityId);
//                if (company != null) {
//                    String oldCover = company.getCoverUrl();
//                    company.setCoverUrl(coverUrl);
//                    updated = companyDAO.updateCompany(company);
//                    if (oldCover != null && !oldCover.isEmpty()) {
//                        String oldPath = getServletContext().getRealPath(oldCover);
//                        if (oldPath != null) new File(oldPath).delete();
//                    }
//                }
//                break;
//            case "opportunity":
//                OpportunityDAO oppDAO = new OpportunityDAO();
//                Opportunity opp = oppDAO.findById(entityId);
//                if (opp != null) {
//                    String oldCover = opp.getCoverUrl();
//                    opp.setCoverUrl(coverUrl);
//                    updated = oppDAO.updateOpportunity(opp);
//                    if (oldCover != null && !oldCover.isEmpty()) {
//                        String oldPath = getServletContext().getRealPath(oldCover);
//                        if (oldPath != null) new File(oldPath).delete();
//                    }
//                }
//                break;
//        }
//
//        if (updated) {
//            session.setAttribute("success", "Обложка обновлена");
//        } else {
//            session.setAttribute("error", "Не удалось обновить обложку");
//        }
//
//        resp.sendRedirect(getReferer(req));
//    }
//
//    private String getReferer(HttpServletRequest req) {
//        String referer = req.getHeader("Referer");
//        if (referer == null) return req.getContextPath() + "/";
//        return referer;
//    }
//}