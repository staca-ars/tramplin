package models;

/**
 * Модель избранной вакансии пользователя.
 * Связывает пользователя (соискателя) с вакансией, которую он добавил в избранное.
 */
public class Favorite {
    private int id;
    private int userId;
    private int opportunityId;

    // Геттеры и сеттеры
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getOpportunityId() {
        return opportunityId;
    }

    public void setOpportunityId(int opportunityId) {
        this.opportunityId = opportunityId;
    }
}