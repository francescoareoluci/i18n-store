package it.unifi.ing.dto;

public class LocalizedTextualItemDto extends BaseDto {

    private String fieldType;
    private String text;

    public LocalizedTextualItemDto() {}

    public String getFieldType() { return this.fieldType; }
    public String getText() { return this.text; }

    public void setFieldType(String fieldType) { this.fieldType = fieldType; }
    public void setText(String text) { this.text = text; }
}
