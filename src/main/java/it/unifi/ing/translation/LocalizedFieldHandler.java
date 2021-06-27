package it.unifi.ing.translation;

import java.util.ArrayList;
import java.util.List;

public class LocalizedFieldHandler {

    private LocalizedField productNameField;
    private LocalizedField productDescriptionField;
    private LocalizedField productCategoryField;
    private LocalizedField productPriceField;

    public LocalizedFieldHandler() {}

    public LocalizedField getProductNameField() { return this.productNameField; }
    public LocalizedField getProductDescriptionField() { return this.productDescriptionField; }
    public LocalizedField getProductCategoryField() { return this.productCategoryField; }
    public LocalizedField getProductPriceField() { return this.productPriceField; }

    public boolean setProductLocalizedFields(List<LocalizedField> localizedFieldList)
    {
        boolean foundName = false;
        boolean foundDescr = false;
        boolean foundCat = false;
        boolean foundPrice = false;
        for (LocalizedField lf : localizedFieldList) {
            switch (lf.getType()) {
                case TranslatableField.productName:
                    productNameField = lf;
                    foundName = true;
                    break;
                case TranslatableField.productDescription:
                    productDescriptionField = lf;
                    foundDescr = true;
                    break;
                case TranslatableField.productCategory:
                    productCategoryField = lf;
                    foundCat = true;
                    break;
                case TranslatableField.productPrice:
                    productPriceField = lf;
                    foundPrice = true;
                default:
                    break;
            }
        }

        if (!foundName || !foundDescr || !foundCat || !foundPrice) {
            return false;
        }
        return true;
    }

    public List<LocalizedField> getProductLocalizedFieldList()
    {
        List<LocalizedField> localizedFieldList = new ArrayList<>();
        localizedFieldList.add(productNameField);
        localizedFieldList.add(productDescriptionField);
        localizedFieldList.add(productCategoryField);
        localizedFieldList.add(productPriceField);

        return localizedFieldList;
    }
}
