package com.ecomnext.domain;

import com.ecomnext.util.ScalaSupport;
import scala.collection.Seq;

import java.util.List;

public class User {
    private String username;
    private String password;
    private List<String> roles;
    private boolean enabled;
    private UserType userType;

    public User() {
    }

    public User(TUser other) {
        username = other.username();
        password = other.password();
        enabled = other.enabled();
        if (other.roles() != null) {
            roles = ScalaSupport.toJavaList(other.roles());
        }
        if (other.userType() != null) {
            userType = UserType.findByValue(other.userType().value());
        }
    }

    public String getUsername() {
        return username;
    }

    public User setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }

    public List<String> getRoles() {
        return roles;
    }

    public User setRoles(List<String> roles) {
        this.roles = roles;
        return this;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public User setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public UserType getUserType() {
        return userType;
    }

    public User setUserType(UserType userType) {
        this.userType = userType;
        return this;
    }

    public TUser toTObject() {
        Seq<String> tRoles = null;
        if (roles != null) {
            tRoles = ScalaSupport.toScalaSeq(roles);
        }
        TUserType tUserType = null;
        if (userType != null) {
            tUserType = userType.toTObject();
        }

        return TUser$.MODULE$.apply(username, password, tRoles, enabled, tUserType);
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", roles=" + roles +
                ", enabled=" + enabled +
                ", userType=" + userType +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (enabled != user.enabled) return false;
        if (password != null ? !password.equals(user.password) : user.password != null) return false;
        if (roles != null ? !roles.equals(user.roles) : user.roles != null) return false;
        if (userType != user.userType) return false;
        if (username != null ? !username.equals(user.username) : user.username != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = username != null ? username.hashCode() : 0;
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (roles != null ? roles.hashCode() : 0);
        result = 31 * result + (enabled ? 1 : 0);
        result = 31 * result + (userType != null ? userType.hashCode() : 0);
        return result;
    }
}
