package com.sdy.dataexchange.plugin.oracle;

import com.baomidou.mybatisplus.annotation.TableField;
import com.sdy.dataadapter.annotation.TableFieldAdapter;
import com.sdy.dataexchange.biz.model.MonitorResult;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class MonitorResultPlus extends MonitorResult {
    @TableFieldAdapter(exist = false)
    @TableField(exist = false)
    private BlobTmpObject blobData;
    
    public static MonitorResultPlus fromBase(MonitorResult base) {
        return (MonitorResultPlus) new MonitorResultPlus()
                .setUuid(base.getUuid())
                .setUsername(base.getUsername())
                .setTableName(base.getTableName())
                .setScnNo(base.getScnNo())
                .setTimestampNo(base.getTimestampNo())
                .setSqlRedo(base.getSqlRedo())
                .setDealtime(base.getDealtime())
                .setDbId(base.getDbId())
                .setTableId(base.getTableId())
                .setRowseq(base.getRowseq())
                .setOperCode(base.getOperCode())
                .setDealFlag(base.getDealFlag())
                .setIsComplete(base.getIsComplete())
                .setEmpty(base.getEmpty());
    }
}
