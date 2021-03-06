package it.unifi.ing.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "currencies")
public class Currency extends BaseEntity {

    @Column(name = "currency")
    private String currency;

    public Currency() {}
    public Currency(String uuid) { super(uuid); }

    public String getCurrency() { return this.currency; }

    public void setCurrency(String currency) { this.currency = currency; }

}
