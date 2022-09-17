package com.crud.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "users")
@SequenceGenerator(
        name = "users-gen",
        sequenceName = "users_id_seq",
        initialValue = 1, allocationSize = 1)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users-gen")
    private Long id;

    @Column(name = "username")
    @NotBlank(message = "Username can't be empty")
    @Size(min = 6, max = 30, message = "Username should be more then 6 and less than 30")
    private String username;

    @Column(name = "password")
    @NotBlank(message = "Password can't be empty")
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id", referencedColumnName = "id")})
    private List<Role> roles;

    @Enumerated(EnumType.STRING)
    @Column(name = "users_status")
    private UserStatus status;

    public User(Long id, String username, String password, List<Role> roles, UserStatus status) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.roles = roles;
        this.status = status;
    }

    public User(String username, String password, List<Role> roles, UserStatus status) {
        this.username = username;
        this.password = password;
        this.roles = roles;
        this.status = status;
    }

    public User(Long id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(username, user.username) && Objects.equals(password, user.password) && Objects.equals(roles, user.roles) && status == user.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, password, roles, status);
    }
}
