package com.example.demo.utils;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Calendar;

@Component
public class Ssh {
    private static String DEFAULT_CHAR_SET = "UTF-8";
    private static String tipStr = "=======================%s=======================";
    private static String splitStr = "=====================================================";

    /**
     * 登录主机
     * @return
     *  登录成功返回true，否则返回false
     */
    public Connection login(String ip, Integer port, String userName, String password){
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
    public String execute(Connection conn, String cmd){
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
    private String processStdout(InputStream in, String charset){
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
