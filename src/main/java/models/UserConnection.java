package models;

/**
 * Модель связи (контакта) между двумя соискателями.
 * Используется для нетворкинга: пользователи могут добавлять друг друга в контакты.
 * Связь неориентированная: порядок user1 и user2 не важен.
 */
public class UserConnection {
    private int id;
    private int user1Id;
    private int user2Id;

    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUser1Id() { return user1Id; }
    public void setUser1Id(int user1Id) { this.user1Id = user1Id; }

    public int getUser2Id() { return user2Id; }
    public void setUser2Id(int user2Id) { this.user2Id = user2Id; }
}