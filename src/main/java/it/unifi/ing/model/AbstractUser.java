package it.unifi.ing.model;

import javax.persistence.MappedSuperclass;
import javax.persistence.Column;

@MappedSuperclass
public abstract class AbstractUser extends BaseEntity {

    @Column
    private String firstName;
    @Column
    private String lastName;
    @Column
    private String mail;
    @Column
    private String password;

    protected AbstractUser() {}

    public AbstractUser(String uuid) { super(uuid); }

    public String getFirstName() { return this.firstName; }
    public String getLastName() { return this.lastName; }
    public String getMail() { return this.mail; }
    public String getPassword() { return this.password; }

    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setMail(String mail) { this.mail = mail; }
    public void setPassword(String password) { this.password = password; }
}
