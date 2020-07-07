package com.sdy.dataexchange.plugin.oracle;

import com.sdy.dataadapter.DbType;
import com.sdy.dataexchange.plugin.cmdb.AbstractMonitorResultParser;
import com.sdy.dataexchange.plugin.common.FunctionResolver;
import com.sdy.dataexchange.plugin.common.ITypeConvert;
import com.sdy.dataexchange.plugin.common.converts.OracleTypeConvert;

public class OracleOriginParser extends AbstractMonitorResultParser {
    private static ITypeConvert oracleConvert = new OracleTypeConvert();
    @Override
    public DbType getDbType() {
        return DbType.ORACLE;
    }

    @Override
    public String getPkName() {
        return "ROWID";
    }

    /**
     * value中出现的函数转换
     */
    @Override
    public String functionConverse(String v) {
        for (FunctionResolver functionResolver : OracleFunctionResolverFactory.FUNCTION_RESOLVER_LIST) {
            if (functionResolver.check(v)) {
                return functionResolver.resolve(v);
            }
        }
        return v;
    }

    @Override
    public ITypeConvert getColumnConverter() {
        return oracleConvert;
    }
}
