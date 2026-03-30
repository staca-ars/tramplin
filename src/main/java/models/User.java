package models;

/**
 * Модель пользователя платформы.
 * Роли: seeker (соискатель), employer (работодатель), admin (администратор).
 * Пароль храниться в хешированном виде (BCrypt).
 */
public class User {
    private int id;
    private String email;
    private String password; // хранит BCrypt хеш (60 символов)
    private String name;
    private String role;
    private String visibility;
    private boolean blocked;

    // Конструкторы
    public User() {}

    public User(String email, String password, String name, String role) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = role;
        this.visibility = "public";
    }

    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getVisibility() { return visibility; }
    public void setVisibility(String visibility) { this.visibility = visibility; }

    public boolean isBlocked() { return blocked; }
    public void setBlocked(boolean blocked) { this.blocked = blocked; }
}