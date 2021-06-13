package it.unifi.ing.model;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "administrators")
public class Admin extends AbstractUser{

    public Admin() {}
    public Admin(String uuid) { super(uuid); }

}
