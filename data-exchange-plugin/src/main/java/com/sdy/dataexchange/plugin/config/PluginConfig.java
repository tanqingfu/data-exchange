package com.sdy.dataexchange.plugin.config;

import com.sdy.dataadapter.DbType;
import com.sdy.dataexchange.core.JobListener;
import com.sdy.dataexchange.plugin.common.DbFmtResolver;
import com.sdy.dataexchange.plugin.common.SqlParser;
import com.sdy.dataexchange.plugin.common.SqlRedoFactory;
import com.sdy.dataexchange.plugin.mysql.MysqlFmtResolver;
import com.sdy.dataexchange.plugin.mysql.MysqlFullParser;
import com.sdy.dataexchange.plugin.mysql.MysqlOriginParser;
import com.sdy.dataexchange.plugin.mysql.MysqlSqlRedoFactory;
import com.sdy.dataexchange.plugin.oracle.OracleFmtResolver;
import com.sdy.dataexchange.plugin.oracle.OracleFullParser;
import com.sdy.dataexchange.plugin.oracle.OracleOriginParser;
import com.sdy.dataexchange.plugin.oracle.OracleSqlRedoFactory;
import com.sdy.dataexchange.plugin.sqlserver.SqlServerFmtResolver;
import com.sdy.dataexchange.plugin.sqlserver.SqlServerSqlRedoFactory;
import lombok.Getter;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class PluginConfig {
    public interface SyncType {
        String FULL = "FULL";
        String APPEND = "APPEND";
    }
    @Getter
    private static Map<DbType, SqlRedoFactory> redoFactoryMap = new HashMap<>();
    @Getter
    private static Map<DbType, Map<String, SqlParser>> parserMap = new HashMap<>();
    @Getter
    private static Map<DbType, DbFmtResolver> dbResolverMap = new HashMap<>(16);
    @Getter
    private static List<JobListener> jobListenerList = new ArrayList<>();
    static {
        redoFactoryMap.put(DbType.MYSQL, new MysqlSqlRedoFactory());
        redoFactoryMap.put(DbType.ORACLE, new OracleSqlRedoFactory());
        redoFactoryMap.put(DbType.SQL_SERVER, new SqlServerSqlRedoFactory());
        Map<String, SqlParser> parserMapMysql = new HashMap<>();
        parserMapMysql.put(SyncType.FULL, new MysqlFullParser());
        parserMapMysql.put(SyncType.APPEND, new MysqlOriginParser());
        Map<String, SqlParser> parserMapOracle = new HashMap<>();
        parserMapOracle.put(SyncType.FULL, new OracleFullParser());
        parserMapOracle.put(SyncType.APPEND, new OracleOriginParser());
        Map<String, SqlParser> parserMapSqlServer = new HashMap<>();
        parserMapSqlServer.put(SyncType.FULL, new OracleFullParser());
        parserMapSqlServer.put(SyncType.APPEND, new OracleOriginParser());
        parserMap.put(DbType.MYSQL, parserMapMysql);
        parserMap.put(DbType.ORACLE, parserMapOracle);
        parserMap.put(DbType.SQL_SERVER, parserMapSqlServer);
        dbResolverMap.put(DbType.MYSQL, new MysqlFmtResolver());
        dbResolverMap.put(DbType.ORACLE, new OracleFmtResolver());
        dbResolverMap.put(DbType.SQL_SERVER, new SqlServerFmtResolver());
    }
}
