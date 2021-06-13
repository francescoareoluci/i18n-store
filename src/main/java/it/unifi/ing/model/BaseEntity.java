package it.unifi.ing.model;

import javax.persistence.MappedSuperclass;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.Column;
import javax.persistence.GenerationType;

@MappedSuperclass
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String uuid;

    protected BaseEntity() {}
    public BaseEntity(String uuid)
    {
        if (uuid == null) {
            throw new IllegalArgumentException("Null UUID");
        }
        this.uuid = uuid;
    }

    public Long getId() { return this.id; }
    public String getUuid() { return this.uuid; }

    @Override
    public int hashCode()
    {
        int primeNumber = 31;
        int result = 1;
        return primeNumber * result + uuid.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof BaseEntity)) {
            return false;
        }
        BaseEntity otherEntity = (BaseEntity)obj;
        return uuid.equals(otherEntity.getUuid());
    }
}
