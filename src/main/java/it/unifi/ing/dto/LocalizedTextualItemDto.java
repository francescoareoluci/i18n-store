package it.unifi.ing.dto;

public class LocalizedTextualItemDto extends LocalizedItemDto {

    private String text;

    public LocalizedTextualItemDto() {}

    public String getText() { return this.text; }

    public void setText(String text) { this.text = text; }
}
