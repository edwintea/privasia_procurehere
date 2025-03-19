package com.privasia.procurehere.core.pojo;

import com.privasia.procurehere.core.entity.Bq;
import com.privasia.procurehere.core.entity.Sor;
import com.privasia.procurehere.core.enums.BqUserTypes;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SorPojo implements Serializable {
    private static final long serialVersionUID = 3862261023800908354L;
    private String id;
    private String eventId;
    private String sorName;
    private String sorDesc;

    private String field1Label;
    private BqUserTypes field1FilledBy;
    private boolean field1ToShowSupplier;
    private boolean field1Required;

    private String field2Label;
    private BqUserTypes field2FilledBy;
    private boolean field2ToShowSupplier;
    private boolean field2Required;

    private String field3Label;
    private BqUserTypes field3FilledBy;
    private boolean field3ToShowSupplier;
    private boolean field3Required;

    private String field4Label;
    private BqUserTypes field4FilledBy;
    private boolean field4ToShowSupplier;
    private boolean field4Required;

    private String field5Label;
    private BqUserTypes field5FilledBy;
    private boolean field5ToShowSupplier;
    private boolean field5Required;

    private String field6Label;
    private BqUserTypes field6FilledBy;
    private boolean field6ToShowSupplier;
    private boolean field6Required;

    private String field7Label;
    private BqUserTypes field7FilledBy;
    private boolean field7ToShowSupplier;
    private boolean field7Required;

    private String field8Label;
    private BqUserTypes field8FilledBy;
    private boolean field8ToShowSupplier;
    private boolean field8Required;

    private String field9Label;
    private BqUserTypes field9FilledBy;
    private boolean field9ToShowSupplier;
    private boolean field9Required;

    private String field10Label;
    private BqUserTypes field10FilledBy;
    private boolean field10ToShowSupplier;
    private boolean field10Required;

    private String searchVal;
    private String filterVal;

    private Integer start;
    private Integer pageLength;
    private Integer pageNo;
    private Integer bqOrder;

    public SorPojo(String id, String bqName, Integer bqOrder) {
        this.id = id;
        this.sorName = bqName;
        this.bqOrder = bqOrder;
    }

    public SorPojo(Sor rftEventBq) {

        this.field1Label = rftEventBq.getField1Label();
        this.field1FilledBy = rftEventBq.getField1FilledBy();
        this.field1ToShowSupplier = rftEventBq.getField1ToShowSupplier();
        this.field1Required = rftEventBq.getField1Required();

    }
}
