/**
 *
 */
package com.privasia.procurehere.core.pojo;

import java.io.Serializable;

/**
 * @author user
 */
public class RfaSupplierSorPojo implements Serializable {
    private final String supplierCompanyName;

    private final String supplierId;

    private final String sorName;

    public RfaSupplierSorPojo(String supplierCompanyName, String supplierId, String bqName) {

        this.supplierCompanyName = supplierCompanyName;
        this.supplierId = supplierId;
        this.sorName = bqName;

    }

    public String getSupplierCompanyName() {
        return supplierCompanyName;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public String getSorName() {
        return sorName;
    }


}
