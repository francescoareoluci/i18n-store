package it.unifi.ing.translation;

import it.unifi.ing.model.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "localized_fields")
public class LocalizedField extends BaseEntity {

    @Column(name = "type")
    private String type;

    public LocalizedField() {}
    public LocalizedField(String uuid) { super(uuid); }

    public String getType() { return this.type; }

    public void setType(String type) { this.type = type; }
}
