package com.sdy.dataexchange.biz.core;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.Session;
import com.sdy.common.model.BizException;
import com.sdy.common.utils.Assert;
import com.sdy.common.utils.StringUtil;
import com.sdy.mvc.utils.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

/**
 * canal远程配置修改
 * @author zhouziqiang 
 */
@Slf4j
public class CanalTool {
    public static final String CANAL_CONFIG_PREFIX = "example";
    public static final String CANAL_CONFIG_MQ_PREFIX = "canal_example_";
    public static final String CANAL_CONFIG_TABLE_PREFIX = "canal.instance.filter.regex";
    public static final String CANAL_INSTANCE_DB_ADDRESS = "canal.instance.master.address";
    public static final String CANAL_INSTANCE_DB_USER = "canal.instance.dbUsername";
    public static final String CANAL_INSTANCE_DB_PWD = "canal.instance.dbPassword";
    public static final String CANAL_INSTANCE_MQ_TOPIC = "canal.mq.topic";
    private static final String CANAL_INSTANCE_FILE_NAME = "instance.properties";
    private static final String LOCAL_IP = HttpUtil.getLocalIpAddress();
    
    private static Properties canalDefaultInstanceProperties = new Properties();
    
    static {
        ClassPathResource classPathResource = new ClassPathResource("/canal/config/example/instance.properties");
        try (InputStream in = classPathResource.getInputStream()) {
            canalDefaultInstanceProperties.load(in);
        } catch (IOException e) {
            log.error("Load canal properties from classpath failed!", e);
            System.exit(-1);
        }
    }

    private static boolean mkdir(Session session, String filePath) throws Exception {
        if (session == null) {
            return new File(filePath).mkdirs();
        }
        return SSHRemoteCall.mkdir(session, filePath);
    }
    
    private static boolean checkFileExist(Session session, String filePath) throws Exception {
        if (session == null) {
            return new File(filePath).exists();
        }
        return SSHRemoteCall.exists(session, filePath);
    }
    
    private static Session createSession(CanalProperty canalProperty) throws Exception {
        if (canalProperty.getSshIp().equals(LOCAL_IP)) {
            return null;
        }
        return SSHRemoteCall.sshRemoteCallLogin(
                canalProperty.getSshIp(),
                canalProperty.getSshUser(),
                canalProperty.getSshPassword(),
                canalProperty.getSshPort());
    }
    
    private static void downloadFile(Session session, String originFilePath, String destFilePath) throws Exception {
        if (session == null) {
            Files.copy(new File(originFilePath).toPath(), new File(destFilePath).toPath(), StandardCopyOption.REPLACE_EXISTING);
            return;
        }
        SSHRemoteCall.fileDownload(session, originFilePath, destFilePath);
    }
    
    private static void uploadFile(Session session, String originFilePath, String destFilePath) throws Exception {
        if (session == null) {
            Files.copy(new File(originFilePath).toPath(), new File(destFilePath).toPath(), StandardCopyOption.REPLACE_EXISTING);
            return;
        }
        SSHRemoteCall.uploadFile(session, destFilePath, originFilePath);
    }
    
    private static String genTmpFileDir() {
        String s = "/opt/file/tmp/canal_" + System.currentTimeMillis();
        new File(s).mkdirs();
        return s;
    }
    
    public static void updateTable(CanalProperty canalProperty, String tableName, boolean add) throws Exception {
        checkProperties(canalProperty);
        Session session = createSession(canalProperty);
        try {
            boolean needWrite;
            String configFilePath = canalProperty.getCanalPath().concat("/conf/").concat(canalProperty.getDestination()) + "/" + CANAL_INSTANCE_FILE_NAME;
            if (!checkFileExist(session, configFilePath)) {
                throw new BizException("采集点对应的配置文件不存在" + configFilePath);
            }
            String localConfigFilePath = genTmpFileDir() + "/" + CANAL_INSTANCE_FILE_NAME;
            downloadFile(session, configFilePath, localConfigFilePath);
            Properties pro = new Properties();
            try (FileInputStream in = new FileInputStream(localConfigFilePath)) {
                pro.load(in);
                needWrite = updateListProperty(pro, CANAL_CONFIG_TABLE_PREFIX, formatTableRegx(tableName), add);
            }
            if (needWrite) {
                try (FileOutputStream oFile = new FileOutputStream(localConfigFilePath)) {
                    pro.store(oFile, "Add table " + tableName);
                }
                log.info("Update canal table config. Property:[{}], Table:[{}]", canalProperty.toString(), pro.getProperty(CANAL_CONFIG_TABLE_PREFIX));
                uploadFile(session, localConfigFilePath, configFilePath);
                doRestart(canalProperty);
            }
        } catch (Exception e) {
            log.error("Canal error! {}, msg={}", canalProperty.toString(), e.getMessage());
            throw e;
        } finally {
            SSHRemoteCall.closeSession(session);
        }
    }

    public static void updateDatabase(CanalProperty canalProperty, Integer dbId, boolean add) throws Exception {
        try {
            checkProperties(canalProperty);
            updateConfigFolder(canalProperty, dbId, add);
        } catch (Exception e) {
            log.error("Canal error! {}, msg={}", canalProperty.toString(), e.getMessage());
            throw e;
        }
    }

    /**
     * 创建数据源配置文件
     */
    private static void updateConfigFolder(CanalProperty canalProperty, Integer dbId, boolean add) throws Exception {
        Session session = createSession(canalProperty);
        try {
            String folderPath = canalProperty.getCanalPath().concat("/conf/").concat(CANAL_CONFIG_PREFIX + dbId);
//            File folder = new File(folderPath);
            String instFilePath = folderPath.concat("/instance.properties");
            if (add) {
                String localInstFilePath = genTmpFileDir() + "/" + CANAL_INSTANCE_FILE_NAME;
                List<String> defaultTbList = new ArrayList<>();
                if (checkFileExist(session, folderPath)) {
                    if (checkFileExist(session, instFilePath)) {
                        log.info("数据源配置文件夹已存在，instance.properties文件已存在！");
                        downloadFile(session, instFilePath, localInstFilePath);
                        // 读取原有配置文件
                        Properties pro = new Properties();
                        try (FileInputStream in = new FileInputStream(localInstFilePath)) {
                            pro.load(in);
                            String tbStrs = pro.getProperty(CANAL_CONFIG_TABLE_PREFIX, "");
                            String dbAddress = pro.getProperty(CANAL_INSTANCE_DB_ADDRESS, "");
                            String dbUser = pro.getProperty(CANAL_INSTANCE_DB_USER, "");
                            String dbPwd = pro.getProperty(CANAL_INSTANCE_DB_PWD, "");
                            if (dbAddress.equals(canalProperty.getSyncDbAddr())
                                    && dbUser.equals(canalProperty.getSyncUser())
                                    && dbPwd.equals(canalProperty.getSyncPwd())) {
                                return;
                            }
                            // 配置文件账号有错，需要初始化
                            defaultTbList = new ArrayList<>(Arrays.asList(tbStrs.split(",")));
                        }
                    } else {
                        log.warn("数据源配置文件夹已存在，instance.properties文件未创建，继续更新！");
                    }
                } else {
                    Assert.isTrue(!mkdir(session, folderPath), "配置文件夹创建失败");
                }
                canalDefaultInstanceProperties.setProperty(CANAL_INSTANCE_DB_ADDRESS, canalProperty.getSyncDbAddr());
                canalDefaultInstanceProperties.setProperty(CANAL_INSTANCE_DB_USER, canalProperty.getSyncUser());
                canalDefaultInstanceProperties.setProperty(CANAL_INSTANCE_DB_PWD, canalProperty.getSyncPwd());
                canalDefaultInstanceProperties.setProperty(CANAL_INSTANCE_MQ_TOPIC, CANAL_CONFIG_MQ_PREFIX + dbId);
                canalDefaultInstanceProperties.setProperty(CANAL_CONFIG_TABLE_PREFIX, String.join(",", defaultTbList));
                try (FileOutputStream oFile = new FileOutputStream(localInstFilePath)) {
                    canalDefaultInstanceProperties.store(oFile, "Add db " + dbId);
                }
                log.info("Update canal db config. Property:[{}], table:[{}]", canalProperty.toString(), canalDefaultInstanceProperties.getProperty(CANAL_CONFIG_TABLE_PREFIX));
                uploadFile(session, localInstFilePath, instFilePath);
            } else {
                if (!checkFileExist(session, folderPath)) {
                    log.warn("数据源配置文件不存在，无需删除！");
                    return;
                }
                log.info("Delete canal db config. Property:[{}]", canalProperty.toString());
                delFile(session, folderPath);
            }
            doRestart(canalProperty);
        } finally {
            SSHRemoteCall.closeSession(session);
        }
    }

    /**
     * 检查配置文件是否有误
     * @return
     */
    private static boolean checkInstanceConfigCorrect() {
        return true;
    }

    /**
     * 删除文件夹
     */
    private static void delFile(Session session, String folder) throws Exception {
        if (session == null) {
            delFileLocal(new File(folder));
            return;
        }
        Vector<ChannelSftp.LsEntry> files = SSHRemoteCall.listFiles(session, folder);
        for (ChannelSftp.LsEntry file : files) {
            if (!".".equals(file.getFilename()) && !"..".equals(file.getFilename())) {
                SSHRemoteCall.deleteFile(session, folder + "/" + file.getFilename());
            }
        }
        SSHRemoteCall.deleteFolder(session, folder);
    }
    
    /**
     * 递归删除文件
     * @param file
     */
    private static void delFileLocal(File file) {
        if (!file.exists()) {
            return;
        }
        if (file.isDirectory() && file.listFiles() != null) {
            Arrays.stream(file.listFiles()).forEach(CanalTool::delFileLocal);
        }
        File[] files = file.listFiles();
        if (file.isDirectory() && (files == null || files.length == 0) || !file.isDirectory()) {
            boolean r = file.delete();
            if (!r) {
                log.error("Delete file [{}] failed!", file.getAbsolutePath());
            }
        }
    }
    
    private static boolean updateListProperty(Properties properties, String key, String value, boolean add) {
        String line = properties.getProperty(key, "");
        String[] configedData = line.trim().split(",");
        List<String> configedDataList = new ArrayList<>();
        Arrays.stream(configedData).filter(StringUtil::isNotBlank).forEach(configedDataList::add);
        log.info("Update canal table config. Type:[{}], Before:[{}]", add, properties.getProperty(key));
        if (add) {
            if (!configedDataList.contains(value)) {
                configedDataList.add(value);
                properties.setProperty(key, String.join(",", configedDataList));
                return true;
            }
        } else {
            boolean removed = configedDataList.removeIf(k -> k.equals(value));
            if (removed) {
                properties.setProperty(key, String.join(",", configedDataList));
                return true;
            }
        }
        log.info("Update canal table config. Type:[{}], After:[{}]", add, properties.getProperty(key));
        return false;
    }
    
    private static String getCanalDestConfigFilePath(CanalProperty canalProperty) throws BizException {
        return canalProperty.getCanalPath()
                .concat("/conf/")
                .concat(canalProperty.getDestination())
                .concat("/instance.properties");
    }

    private static String getCanalConfigFile(CanalProperty canalProperty) throws BizException {
        return canalProperty.getCanalPath()
                .concat("/")
                .concat(canalProperty.getDestination())
                .concat("/canal.properties");
    }
    
    private static void doRestart(CanalProperty canalProperty) throws IOException, InterruptedException, BizException {
///        log.info("配置更改{}:{}，重启canal.", canalProperty.getSshIp(), canalProperty.getDestination());
///        Process pStop = Runtime.getRuntime().exec(canalProperty.getCanalPath().concat("/bin/stop.sh"));
///        pStop.waitFor(5, TimeUnit.SECONDS);
///        Assert.isTrue(!checkCanalStopped(canalProperty), "canal进程停止失败，请尝试手动重启。");
///        Runtime.getRuntime().exec(canalProperty.getCanalPath().concat("/bin/startup.sh"));
    }
    
    private static boolean checkCanalStopped(CanalProperty canalProperty) {
        File file = new File(canalProperty.getCanalPath().concat("/bin/canal.pid"));
        return !file.exists();
    }
    
    private static String formatTableRegx(String tableName) {
        return ".*\\." + tableName;
    }
    
    private static void checkProperties(CanalProperty canalProperty) throws BizException {
//        if (!"127.0.0.1".equals(canalProperty.getSshIp()) && !"localhost".equals(canalProperty.getSshIp())) {
//            throw new BizException(String.format("No support for ip %s except 127.0.0.1 or localhost!", canalProperty.getSshIp()));
//        }
    }
}
