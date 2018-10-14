package com.nrdc.managementPanel.model.dto;

public class TokenDTO extends BaseModel {
    private Long id;
    private Long fkUserId;
    private String token;
    private Long fkSystemId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFkUserId() {
        return fkUserId;
    }

    public void setFkUserId(Long fkUserId) {
        this.fkUserId = fkUserId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getFkSystemId() {
        return fkSystemId;
    }

    public void setFkSystemId(Long fkSystemId) {
        this.fkSystemId = fkSystemId;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("TokenDTO{");
        sb.append("id=").append(id);
        sb.append(", fkUserId=").append(fkUserId);
        sb.append(", token='").append(token).append('\'');
        sb.append(", fkSystemId=").append(fkSystemId);
        sb.append('}');
        return sb.toString();
    }
}
