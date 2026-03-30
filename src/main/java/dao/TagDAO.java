package dao;

import models.Tag;
import utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO для работы с тегами (таблицы tags и opportunity_tags).
 * Предоставляет методы поиска, создания, связывания тегов с вакансиями.
 */
public class TagDAO {

    /**
     * Возвращает список тегов, привязанных к указанной вакансии.
     * @param oppId ID вакансии
     * @return список тегов
     */
    public List<Tag> findTagsByOpportunityId(int oppId) {
        List<Tag> list = new ArrayList<>();
        String sql = "SELECT t.* FROM tags t JOIN opportunity_tags ot ON t.id = ot.tag_id WHERE ot.opportunity_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, oppId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Tag tag = new Tag();
                tag.setId(rs.getInt("id"));
                tag.setName(rs.getString("name"));
                list.add(tag);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Находит тег по имени или создаёт новый, если не существует.
     * @param name название тега
     * @return объект Tag (существующий или новый)
     */
    public Tag findOrCreateByName(String name) {
        // Поиск существующего
        String sqlSelect = "SELECT * FROM tags WHERE name = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlSelect)) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Tag tag = new Tag();
                tag.setId(rs.getInt("id"));
                tag.setName(rs.getString("name"));
                return tag;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Создание нового
        String sqlInsert = "INSERT INTO tags (name) VALUES (?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                Tag tag = new Tag();
                tag.setId(rs.getInt(1));
                tag.setName(name);
                return tag;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Связывает тег с вакансией.
     * @param tagId ID тега
     * @param oppId ID вакансии
     * @return true если связь создана
     */
    public boolean linkTagToOpportunity(int tagId, int oppId) {
        String sql = "INSERT INTO opportunity_tags (opportunity_id, tag_id) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, oppId);
            ps.setInt(2, tagId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Проверяет, связан ли тег с вакансией.
     * @param tagId ID тега
     * @param oppId ID вакансии
     * @return true если связь существует
     */
    public boolean isTagLinkedToOpportunity(int tagId, int oppId) {
        String sql = "SELECT * FROM opportunity_tags WHERE opportunity_id = ? AND tag_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, oppId);
            ps.setInt(2, tagId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Удаляет все связи тегов с указанной вакансией.
     * @param oppId ID вакансии
     */
    public void deleteTagsForOpportunity(int oppId) {
        String sql = "DELETE FROM opportunity_tags WHERE opportunity_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, oppId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}