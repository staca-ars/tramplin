package dao;

import models.SeekerProfile;
import utils.DBConnection;

import java.sql.*;

/**
 * DAO для работы с расширенным профилем соискателя (таблица seeker_profiles).
 * Предоставляет методы сохранения (вставка или обновление) и поиска по ID пользователя.
 */
public class SeekerProfileDAO {

    /**
     * Сохраняет профиль соискателя (вставка или обновление).
     * Использует ON DUPLICATE KEY UPDATE для upsert.
     * @param profile объект профиля
     * @return true если операция успешна
     */
    public boolean saveProfile(SeekerProfile profile) {
        String sql = "INSERT INTO seeker_profiles (user_id, full_name, university, course, graduation_year, skills, projects, github, portfolio, photo_url, cover_url) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE full_name=?, university=?, course=?, graduation_year=?, skills=?, projects=?, github=?, portfolio=?, photo_url=?, cover_url=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            // INSERT параметры (1-11)
            ps.setInt(1, profile.getUserId());
            ps.setString(2, profile.getFullName());
            ps.setString(3, profile.getUniversity());
            ps.setInt(4, profile.getCourse());
            ps.setInt(5, profile.getGraduationYear());
            ps.setString(6, profile.getSkills());
            ps.setString(7, profile.getProjects());
            ps.setString(8, profile.getGithub());
            ps.setString(9, profile.getPortfolio());
            ps.setString(10, profile.getPhotoUrl());
            ps.setString(11, profile.getCoverUrl());

            // UPDATE параметры (12-21)
            ps.setString(12, profile.getFullName());
            ps.setString(13, profile.getUniversity());
            ps.setInt(14, profile.getCourse());
            ps.setInt(15, profile.getGraduationYear());
            ps.setString(16, profile.getSkills());
            ps.setString(17, profile.getProjects());
            ps.setString(18, profile.getGithub());
            ps.setString(19, profile.getPortfolio());
            ps.setString(20, profile.getPhotoUrl());
            ps.setString(21, profile.getCoverUrl());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Находит профиль соискателя по ID пользователя.
     * @param userId ID пользователя
     * @return объект SeekerProfile или null, если профиль не найден
     */
    public SeekerProfile findByUserId(int userId) {
        String sql = "SELECT * FROM seeker_profiles WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                SeekerProfile profile = new SeekerProfile();
                profile.setUserId(rs.getInt("user_id"));
                profile.setFullName(rs.getString("full_name"));
                profile.setUniversity(rs.getString("university"));
                profile.setCourse(rs.getInt("course"));
                profile.setGraduationYear(rs.getInt("graduation_year"));
                profile.setSkills(rs.getString("skills"));
                profile.setProjects(rs.getString("projects"));
                profile.setGithub(rs.getString("github"));
                profile.setPortfolio(rs.getString("portfolio"));
                profile.setPhotoUrl(rs.getString("photo_url"));
                profile.setCoverUrl(rs.getString("cover_url"));
                return profile;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}