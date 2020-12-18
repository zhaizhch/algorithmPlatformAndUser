package com.example.demo;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@SpringBootTest
public class DemoApplicationTests {


    private static String  DEFAULTCHART="UTF-8";
    public static void main(String[] args) throws IOException {
/*        Integer port=22;
        String ip="192.168.119.100";
        String userName="root";
        String password="ehl1234";
        Connection conn=login(ip,port,userName,password);
        String cmd="touch test.txt";
        String result=execute(conn,cmd);
        System.out.println(splitStr + "\n执行的结果如下: \n" + result + splitStr);*/
    }
    /**
     * 解压zip文件
     *
     * @param zipFile目标文件
     * @param descDir解压后存放的位置
     * @return true/false
     */
    public static boolean unZip(File zipFile, String descDir) {
        boolean flag = false;
        File pathFile = new File(descDir);
        if (!pathFile.exists()) {
            pathFile.mkdirs();
        }
        ZipFile zip = null;
        try {
            // 指定编码，否则压缩包里面不能有中文目录
            zip = new ZipFile(zipFile, Charset.forName("gbk"));
            for (Enumeration entries = zip.entries(); entries.hasMoreElements();) {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                String zipEntryName = entry.getName();
                InputStream in = zip.getInputStream(entry);
                String outPath = (descDir + zipEntryName).replace("/",
                        File.separator);
                // 判断路径是否存在,不存在则创建文件路径
                File file = new File(outPath.substring(0,
                        outPath.lastIndexOf(File.separator)));
                if (!file.exists()) {
                    file.mkdirs();
                }
                // 判断文件全路径是否为文件夹,如果是上面已经上传,不需要解压
                if (new File(outPath).isDirectory()) {
                    continue;
                }

                OutputStream out = new FileOutputStream(outPath);
                byte[] buf1 = new byte[2048];
                int len;
                while ((len = in.read(buf1)) > 0) {
                    out.write(buf1, 0, len);
                }
                in.close();
                out.close();
            }
            flag = true;
            // 必须关闭，否则无法删除该zip文件
            zip.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return flag;
    }
    private static String DEFAULT_CHAR_SET = "UTF-8";
    private static String tipStr = "=======================%s=======================";
    private static String splitStr = "=====================================================";

    /**
     * 登录主机
     * @return
     *  登录成功返回true，否则返回false
     */
    public static Connection login(String ip,Integer port,String userName, String password){
        boolean isAuthenticated = false;
        Connection conn = null;
        long startTime = Calendar.getInstance().getTimeInMillis();
        try {
            conn = new Connection(ip,port);
            conn.connect(); // 连接主机

            isAuthenticated = conn.authenticateWithPassword(userName, password); // 认证
            if(isAuthenticated){
                System.out.println(String.format(tipStr, "认证成功"));
            } else {
                System.out.println(String.format(tipStr, "认证失败"));
            }
        } catch (IOException e) {
            System.err.println(String.format(tipStr, "登录失败"));
            e.printStackTrace();
        }
        long endTime = Calendar.getInstance().getTimeInMillis();
        System.out.println("登录用时: " + (endTime - startTime)/1000.0 + "s\n" + splitStr);
        return conn;
    }

    /**
     * 远程执行shell脚本或者命令
     * @param cmd
     *  即将执行的命令
     * @return
     *  命令执行完后返回的结果值
     */
    public static String execute(Connection conn, String cmd){
        String result = "";
        Session session = null;
        try {
            if(conn != null){
                session = conn.openSession(); // 打开一个会话
                session.execCommand(cmd);  // 执行命令
                result = processStdout(session.getStdout(), DEFAULT_CHAR_SET);

                //如果为得到标准输出为空，说明脚本执行出错了
                if(StringUtils.isBlank(result)){
                    System.err.println("【得到标准输出为空】\n执行的命令如下：\n" + cmd);
                    result = processStdout(session.getStderr(), DEFAULT_CHAR_SET);
                }else{
                    System.out.println("【执行命令成功】\n执行的命令如下：\n" + cmd);
                }
            }
        } catch (IOException e) {
            System.err.println("【执行命令失败】\n执行的命令如下：\n" + cmd + "\n" + e.getMessage());
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.close();
            }
            if (session != null) {
                session.close();
            }
        }
        return result;
    }

    /**
     * 解析脚本执行返回的结果集
     * @param in 输入流对象
     * @param charset 编码
     * @return
     *  以纯文本的格式返回
     */
    private static String processStdout(InputStream in, String charset){
        InputStream stdout = new StreamGobbler(in);
        StringBuffer buffer = new StringBuffer();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(stdout, charset));
            String line = null;
            while((line = br.readLine()) != null){
                buffer.append(line + "\n");
            }
        } catch (UnsupportedEncodingException e) {
            System.err.println("解析脚本出错：" + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("解析脚本出错：" + e.getMessage());
            e.printStackTrace();
        }
        return buffer.toString();
    }


}
