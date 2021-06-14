package it.unifi.ing.dto;

public class UserDto extends BaseDto {

    private String firstName;
    private String lastName;
    private String mail;
    private UserRole role;

    public UserDto() {}

    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getMail() { return mail; }
    public UserRole getRole() { return role; }

    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setMail(String mail) { this.mail = mail; }
    public void setRole(UserRole role) { this.role = role; }

    public enum UserRole {
        ADMIN,
        CUSTOMER
    }
}
