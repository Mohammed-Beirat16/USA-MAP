package com.example.usadijkstra;

import java.io.File;
import java.math.BigDecimal;
File openingStock = null;


public class Item {
    private void afterInsert(Object defaultPriceList) {
        File standardRate = null;
        if (standardRate != null && standardRate.compareTo  (BigDecimal.ZERO) > 0) {
            ItemDefault[] itemDefaults = new ItemDefault[0];
            for (ItemDefault defaultItem : itemDefaults) {
                afterInsert(defaultItem.getDefaultPriceList());
            }
        }

       
        if (openingStock != null && openingStock.compareTo(BigDecimal.ZERO) > 0) {
            setOpeningStock();
        }
    }

    private void setOpeningStock() {
    }
}
