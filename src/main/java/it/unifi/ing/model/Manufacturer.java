package it.unifi.ing.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "manufacturers")
public class Manufacturer extends BaseEntity {

    @Column(name = "name")
    private String name;

    public Manufacturer() {}
    public Manufacturer(String uuid) { super(uuid); }

    public String getName() { return this.name; }
    public void setName(String name) { this.name = name; }
}
