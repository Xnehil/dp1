package com.dp1.backend.models;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;



import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "cliente")
@SQLDelete(sql = "UPDATE cliente SET active = false WHERE id = ?")
@SQLRestriction( value = "active = true")
public class Cliente extends BaseModel {    

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "email", nullable = false, unique = true)
    private String email;


    public Cliente( String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public Cliente() {
    }


    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


}
