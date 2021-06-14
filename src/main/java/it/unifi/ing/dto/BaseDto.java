package it.unifi.ing.dto;

public abstract class BaseDto {

    private Long id;

    public BaseDto() {}

    public Long getId() { return this.id; }
    public void setId(Long id) { this.id = id; }
}
