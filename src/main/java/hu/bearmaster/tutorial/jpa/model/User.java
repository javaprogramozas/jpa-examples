package hu.bearmaster.tutorial.jpa.model;

import java.time.ZonedDateTime;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "users", schema = "blogs")
@SequenceGenerator(name = "userIdGenerator", sequenceName = "users_seq", schema = "blogs", initialValue = 1, allocationSize = 1)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "userIdGenerator")
    private Long id;

    private String username;
    
    @Enumerated(EnumType.STRING)
    private UserStatus status;
    
    @Column(name = "created_at")
    private ZonedDateTime createdAt;
    
    @Transient
    private boolean loggedIn;
    
    /*
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", schema = "blogs", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role_name")
    @MapKeyColumn(name = "ordinal")
    */
    @Transient
    private Map<Integer, String> roles;
    
    @OneToOne(mappedBy = "user")
    private Address address;

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

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public Map<Integer, String> getRoles() {
        return roles;
    }

    public void setRoles(Map<Integer, String> roles) {
        this.roles = roles;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        if (address != null) {
            address.setUser(this);
        } else {
            this.address.setUser(null);
        }
        this.address = address;
    }

    @Override
    public String toString() {
        return "User [id=" + id + ", username=" + username + ", address=" + address + "]";
    }

}
