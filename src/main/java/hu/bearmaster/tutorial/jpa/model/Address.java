package hu.bearmaster.tutorial.jpa.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "addresses", schema = "blogs")
@SequenceGenerator(name = "addressIdGenerator", sequenceName = "addresses_seq", schema = "blogs", initialValue = 1, allocationSize = 1)
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "addressIdGenerator")
    private Long id;
    
    private String city;
    
    private String street;
    
    @Column(name = "house_number")
    private int houseNumber;
    
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    public Address() {}

    public Address(String city, String street, int houseNumber) {
        super();
        this.city = city;
        this.street = street;
        this.houseNumber = houseNumber;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public int getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(int houseNumber) {
        this.houseNumber = houseNumber;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Address [id=" + id + ", city=" + city + ", street=" + street + ", houseNumber=" + houseNumber
                + ", user=" + user + "]";
    }

    public static Address address(String city, String street, int houseNumber) {
        return new Address(city, street, houseNumber);
    }

}
