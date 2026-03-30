package dao;

import models.User;
import utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO для работы с таблицей connections (контакты между соискателями).
 * Обеспечивает добавление, удаление, поиск контактов и проверку существования связи.
 */
public class ConnectionDAO {

    /**
     * Добавляет связь между двумя пользователями (дружба).
     * @param user1Id ID первого пользователя
     * @param user2Id ID второго пользователя
     * @return true, если связь успешно добавлена
     */
    public boolean addConnection(int user1Id, int user2Id) {
        String sql = "INSERT INTO connections (user1_id, user2_id) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, user1Id);
            ps.setInt(2, user2Id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Удаляет связь между двумя пользователями.
     * @param user1Id ID первого пользователя
     * @param user2Id ID второго пользователя
     * @return true, если связь успешно удалена
     */
    public boolean removeConnection(int user1Id, int user2Id) {
        String sql = "DELETE FROM connections WHERE (user1_id = ? AND user2_id = ?) OR (user1_id = ? AND user2_id = ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, user1Id);
            ps.setInt(2, user2Id);
            ps.setInt(3, user2Id);
            ps.setInt(4, user1Id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Возвращает список пользователей, с которыми у данного пользователя есть связь.
     * @param userId ID пользователя
     * @return список пользователей-контактов
     */
    public List<User> findConnections(int userId) {
        List<User> list = new ArrayList<>();
        String sql = "SELECT u.* FROM users u JOIN connections c ON (u.id = c.user2_id AND c.user1_id = ?) OR (u.id = c.user1_id AND c.user2_id = ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setEmail(rs.getString("email"));
                user.setName(rs.getString("name"));
                user.setRole(rs.getString("role"));
                user.setVisibility(rs.getString("visibility"));
                list.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Проверяет, существует ли связь между двумя пользователями.
     * @param user1Id ID первого пользователя
     * @param user2Id ID второго пользователя
     * @return true, если связь существует
     */
    public boolean isConnected(int user1Id, int user2Id) {
        String sql = "SELECT 1 FROM connections WHERE (user1_id = ? AND user2_id = ?) OR (user1_id = ? AND user2_id = ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, user1Id);
            ps.setInt(2, user2Id);
            ps.setInt(3, user2Id);
            ps.setInt(4, user1Id);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}