package com.sdy.dataexchange.biz.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.Vector;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import lombok.extern.slf4j.Slf4j;

/**
 * ssh、sftp操作
 * 说明:exec用于执行命令;sftp用于文件处理
 */
@Slf4j
public class SSHRemoteCall {
    /**
     * ssh默认端口号
     */
    private static final int DEFAULT_PORT = 22;

    /**
     * 远程登陆
     *
     * @throws Exception
     */
    public static Session sshRemoteCallLogin(String ipAddress, String userName, String password, Integer port) throws Exception {
        if (port == null) {
            port = DEFAULT_PORT;
        }
        // 创建jSch对象
        JSch jSch = new JSch();
        // 获取到jSch的session, 根据用户名、主机ip、端口号获取一个Session对象
        Session session = jSch.getSession(userName, ipAddress, port);
        // 设置密码
        session.setPassword(password);
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        // 设置超时
        session.setTimeout(3000);
        session.connect();
        return session;
    }

    /**
     * 关闭连接
     */
    public static void closeSession(Session session) {
        if (session != null) {
            session.disconnect();
        }

    }

    /**
     * 执行相关的命令
     *
     * @param command
     * @throws IOException
     */
    public static void execCommand(Session session, String command) throws IOException {
        InputStream in = null;
        Channel channel = null;
        try {
            // 如果命令command不等于null
            if (command != null) {
                // 打开channel
                //说明：exec用于执行命令;sftp用于文件处理
                channel = session.openChannel("exec");
                // 设置command
                ((ChannelExec) channel).setCommand(command);
                // channel进行连接
                channel.connect();
                // 获取到输入流
                in = channel.getInputStream();
                // 执行相关的命令
                String processDataStream = processDataStream(in);
                // 打印相关的命令
                log.info("1、打印相关返回的命令: {}", processDataStream);
            }
        } catch (Exception e) {
            log.error("", e);
        } finally {
            if (in != null) {
                in.close();
            }
            if (channel != null) {
                channel.disconnect();
            }
        }

    }

    /**
     * 对将要执行的linux的命令进行遍历
     *
     * @param in
     * @return
     * @throws Exception
     */
    public static String processDataStream(InputStream in) throws Exception {
        StringBuffer sb = new StringBuffer();
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String result = "";
        try {
            while ((result = br.readLine()) != null) {
                sb.append(result);
                // System.out.println(sb.toString());
            }
        } catch (Exception e) {
            throw new Exception("获取数据流失败: " + e);
        } finally {
            br.close();
        }
        return sb.toString();
    }

    /**
     * 上传文件 可参考:https://www.cnblogs.com/longyg/archive/2012/06/25/2556576.html
     *
     * @param directory
     *            上传文件的目录
     * @param uploadFile
     *            将要上传的文件
     */
    public static void uploadFile(Session session, String directory, String uploadFile) throws JSchException, IOException, SftpException {
        // 打开channelSftp
        ChannelSftp channelSftp = (ChannelSftp) session.openChannel("sftp");
        // 远程连接
        channelSftp.connect();
        File file = new File(uploadFile);
        try (FileInputStream fs = new FileInputStream(file)) {
            // 创建一个文件名称问uploadFile的文件
            // 将文件进行上传(sftp协议)
            // 将本地文件名为src的文件上传到目标服务器,目标文件名为dst,若dst为目录,则目标文件名将与src文件名相同.
            // 采用默认的传输模式:OVERWRITE
            channelSftp.put(fs, directory, ChannelSftp.OVERWRITE);
        } finally {
            // 切断远程连接
            channelSftp.exit();
        }

    }

    /**
     * 下载文件 采用默认的传输模式：OVERWRITE
     *
     * @param src
     *            linux服务器文件地址
     * @param dst
     *            本地存放地址
     * @throws JSchException
     * @throws SftpException
     */
    public static void fileDownload(Session session, String src, String dst) throws JSchException, SftpException {
        String os = System.getProperty("os.name");
        if(os.toLowerCase().startsWith("win")){
            dst = System.getProperty("user.dir").substring(0, 2) + dst;
        }
        // src 是linux服务器文件地址,dst 本地存放地址
        ChannelSftp channelSftp = (ChannelSftp) session.openChannel("sftp");
        // 远程连接
        channelSftp.connect(1500);
        try {
            channelSftp.get(src, dst);
        } finally {
            // 切断远程连接,quit()等同于exit(),都是调用disconnect()
            channelSftp.quit();
        }
    }

    /**
     * 删除文件
     */
    public static void deleteFolder(Session session, String directoryFile) throws SftpException, JSchException {
        // 打开openChannel的sftp
        ChannelSftp channelSftp = (ChannelSftp) session.openChannel("sftp");
        // 远程连接
        channelSftp.connect();
        try {
            // 删除文件
            channelSftp.rmdir(directoryFile);
        } finally {
            // 切断远程连接
            channelSftp.exit();
        }
    }

    /**
     * 删除文件
     */
    public static void deleteFile(Session session, String directoryFile) throws SftpException, JSchException {
        // 打开openChannel的sftp
        ChannelSftp channelSftp = (ChannelSftp) session.openChannel("sftp");
        // 远程连接
        channelSftp.connect();
        try {
            // 删除文件
            channelSftp.rm(directoryFile);
        } finally {
            // 切断远程连接
            channelSftp.exit();
        }
    }

    /**
     * 列出目录下的文件
     *
     * @param directory 要列出的目录
     */
    public static Vector<ChannelSftp.LsEntry> listFiles(Session session, String directory) throws JSchException, SftpException {
        ChannelSftp channelSftp = (ChannelSftp) session.openChannel("sftp");
        // 远程连接
        channelSftp.connect();
        try {
            // 显示目录信息
            return channelSftp.ls(directory);
        } finally {
            // 切断连接
            channelSftp.exit();
        }
    }

    public static boolean mkdir(Session session, String directory) throws SftpException, JSchException {
        ChannelSftp channelSftp = (ChannelSftp) session.openChannel("sftp");
        // 远程连接
        channelSftp.connect();
        try {
            channelSftp.mkdir(directory);
        } finally {
            // 切断连接
            channelSftp.exit();
        }
        return true;
    }
    
    public static boolean exists(Session session, String directory) throws JSchException {
        ChannelSftp channelSftp = (ChannelSftp) session.openChannel("sftp");
        // 远程连接
        channelSftp.connect();
        boolean exist;
        try {
            channelSftp.stat(directory);
            exist = true;
        } catch (Exception e) {
            exist = false;
        } finally {
            // 切断连接
            channelSftp.exit();
        }
        return exist;
    }

    public static void main(String[] args) throws Exception {
        // 连接到指定的服务器
        // 1、首先远程连接ssh
        Session session = SSHRemoteCall.sshRemoteCallLogin("192.168.1.95", "root", "Aa123456", 22);
        try {
            // 打印信息
            System.out.println("0、连接192.168.1.95,ip地址: " + "192.168.1.95" + ",账号: " + "root" + ",连接成功.....");

            // 2、执行相关的命令
            // 查看目录信息
            // String command = "ls /home/hadoop/package ";
            // 查看文件信息
            // String command = "cat /home/hadoop/package/test ";
            // 查看磁盘空间大小
            // String command = "df -lh ";
            // 查看cpu的使用情况
            // String command = "top -bn 1 -i -c ";
            // 查看内存的使用情况
            String command = "free ";
            SSHRemoteCall.execCommand(session, command);

            // 3、上传文件
//            String directory = "/home/hadoop/package/poi.xlsx";// 目标文件名
//            String uploadFile = "E:\\poi.xlsx";// 本地文件名
//            SSHRemoteCall.getInstance().uploadFile(directory, uploadFile);

            // 4、下载文件
            // src 是linux服务器文件地址,dst 本地存放地址,采用默认的传输模式：OVERWRITE
            //test为文件名称哈
//            String src = "/opt/tools/canal/conf/canal.properties";
//            String dst = "C:\\opt\\1\\canal222.properties";
//            SSHRemoteCall.fileDownload(session, src, dst);

            // 5、刪除文件
//            String deleteDirectoryFile = "/home/hadoop/package/test";
//            SSHRemoteCall.getInstance().deleteFile(deleteDirectoryFile);

            // 6、展示目录下的文件信息
            String lsDirectory = "/opt/tools/canal/conf/example1";
            Vector f = SSHRemoteCall.listFiles(session, lsDirectory); 
            System.out.println(f);

        } catch (Exception e) {
            // 打印错误信息
            System.err.println("远程连接失败......");
            e.printStackTrace();
        } finally {
            // 7、关闭连接
            SSHRemoteCall.closeSession(session);
        }
    }

}