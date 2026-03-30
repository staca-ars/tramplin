package models;

/**
 * Модель расширенного профиля соискателя.
 * Хранит личную информацию, образование, навыки, проекты, ссылки,
 * а также фото и обложку профиля.
 */
public class SeekerProfile {
    private int userId;
    private String fullName;
    private String university;
    private int course;
    private int graduationYear;
    private String skills;
    private String projects;
    private String github;
    private String portfolio;
    private String photoUrl;
    private String coverUrl;

    // Конструкторы
    public SeekerProfile() {}

    // Геттеры и сеттеры
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getUniversity() { return university; }
    public void setUniversity(String university) { this.university = university; }

    public int getCourse() { return course; }
    public void setCourse(int course) { this.course = course; }

    public int getGraduationYear() { return graduationYear; }
    public void setGraduationYear(int graduationYear) { this.graduationYear = graduationYear; }

    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }

    public String getProjects() { return projects; }
    public void setProjects(String projects) { this.projects = projects; }

    public String getGithub() { return github; }
    public void setGithub(String github) { this.github = github; }

    public String getPortfolio() { return portfolio; }
    public void setPortfolio(String portfolio) { this.portfolio = portfolio; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public String getCoverUrl() { return coverUrl; }
    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }
}