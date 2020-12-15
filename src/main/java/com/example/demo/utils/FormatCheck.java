package com.example.demo.utils;

import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * @author User
 */
@Component
public class FormatCheck {
    //判断镜像名称格式：首尾字符为数字或小写字母，中间字符为数字或小写字母或特殊字符/.-_
    public Boolean imageNameCheck(String imageName){
        String compile="(^[0-9a-z]+)([0-9a-z./_-]*)([0-9a-z]+$)";
        if(imageName.matches(compile)){
            return true;
        }
        return false;
    }

    //判断镜像标签格式：首尾字符为数字或小写字母，中间字符为数字或小写字母或特殊字符/.-_
    public Boolean tagCheck(String imageName){
        String compile="^[0-9a-z][0-9a-z\\.\\-\\_\\/]*[0-9a-z]$|[0-9a-z]";
        if(imageName.matches(compile)){
            return true;
        }
        return false;
    }

    //判断镜像描述是否是空格
    public Boolean algoDescCheck(String algoDesc){
        if(algoDesc==null||algoDesc.equals("")){
            return true;
        }
        String compile="\\s+";
        if(algoDesc.matches(compile)){
            return false;
        }
        return true;
    }

    //判断端口格式：端口类型：端口号，端口类型：端口号,端口类型为a-z小写字母。例：ssh：22，jupyter：8888
    public Boolean imagePortsCheck(String imagePorts){
        if(imagePorts==null||imagePorts.equals("")){
            return true;
        }
        ArrayList<String[]> splitResult=new ArrayList<>();
        String[] firstSplit=imagePorts.split("\\,");
        for(String each:firstSplit){
            if(each.split("\\:").length==3){
                continue;
            }
            splitResult.add(each.split("\\:"));
        }
        for(int i=0;i<splitResult.size();i++){
            String compileString="[a-z]+";
            String compileDigite="[0-9]+";
            if(splitResult.get(i).length!=2){
                return false;
            }
            if(!splitResult.get(i)[0].matches(compileString)){
                return false;
            }
            if(!splitResult.get(0)[1].matches(compileDigite)){
                return false;
            }
        }
        return true;
    }

    //判断合法的路径名称。例/data-ym/vol_09"
    public Boolean imageMountCheck(String imageMount){
        if(imageMount==null||imageMount.equals("")){
            return true;
        }
        String[] splitResult=imageMount.split("\\,");
        String compile="^/([A-Za-z0-9_-]+/?)+$";
        for(String each:splitResult){
            if(!each.matches(compile)){
                return false;
            }
        }
        return true;
    }

    //判断正确的配置文件信息，可包含：“A-Za-z0-9.-”例/data/gelin-k8s.config
    public Boolean imageConfigCheck(String imageConfig){
        if(imageConfig==null||imageConfig.equals("")){
            return true;
        }
        String[] splitResult=imageConfig.split("\\,");
        String compile="^/([A-Za-z0-9-._]+/?)+$";
        for(String each:splitResult){
            if(!each.matches(compile)){
                return false;
            }
        }
        return true;
    }

    //判断合法的子目录映射。例/data-ym/vol.config:/data/vol.config
    public Boolean subPathMappingCheck(String subPath){
        if(subPath==null||subPath.equals("")){
            return true;
        }
        String[] splitResult=subPath.split("\\,");
        String compile="(/[A-Za-z0-9_.-]+)*(.[a-z0-9])*";
        for(String each:splitResult){
            String[] files=each.split("\\:");
            if(files.length!=2){
                return false;
            }
            else{
                for(String file:files){
                    if(!file.matches(compile)){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    //判断合法的主机目录映射。例/data-ym/vol:/data/vol
    public Boolean hostPathMappingCheck(String hostPath){
        if(hostPath==null||hostPath.equals("")){
            return true;
        }
        String[] splitResult=hostPath.split("\\,");
        String compile="(/[A-Za-z0-9_.-]+)*";
        for(String each:splitResult){
            String[] files=each.split("\\:");
            if(files.length!=2){
                return false;
            }
            else{
                for(String file:files){
                    if(!file.matches(compile)){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    //判断合法的路径名称。例/data-ym/vol_09
    public Boolean workingDirCheck(String workingDir){
        if(workingDir==null||workingDir.equals("")){
            return true;
        }
        String compile="/[A-Za-z0-9_.-]*[/A-Za-z0-9_.-]*";
        if(!workingDir.matches(compile)){
            return false;
        }
        return true;
    }

    //判断合法的算法类型  0：自研  1：生态  2：其他
    public Boolean algoTypeCheck(String algoType){
        if(algoType==null||algoType.equals("")){
            return true;
        }
        String[] splitResult=algoType.split("\\,");
        for(String each:splitResult){
            if(!(each.equals("0")||each.equals("1")||each.equals("2"))){
                return false;
            }
        }
        return true;
    }

    //判断合法环境变量，例如 NAME:ZHANGSAN9527
    public Boolean envCheck(String env){
        if(env==null||env.equals("")||env.matches("\\s")) {
            return true;
        }
        ArrayList<String> envEach=new ArrayList<>();
        for(String each:env.split("\\,")){
            if(each.split("\\:").length==1){
                return false;
            }
            String[] eachSplit=each.split("\\:",2);
            if(eachSplit.length!=2){
                return false;
            }
            if(eachSplit[0].matches("\\s")||eachSplit[1].matches("\\s")){
                return false;
            }
        }
        return true;
    }

    //判断镜像格式
    public Boolean imageNameTagCheck(String each){
        String[] splitResult=each.split("\\:");
        if(splitResult.length<2){
            return false;
        }
        return (imageNameCheck(splitResult[0])&&tagCheck(splitResult[1]));
    }

    //自研算法算法依赖不能为空
    public Boolean dependencyCheck(String dependencyServiceId){
        if(dependencyServiceId==null||dependencyServiceId.equals("")||dependencyServiceId.matches("\\s")){
            return true;
        }
        String[] eachDep=dependencyServiceId.split("\\,");
        for(String each:eachDep){
            return imageNameTagCheck(each);
        }
        return true;
    }

    //检查任务名字：算子任务名称格式：首位字符为小写字母，中间字符为数字或小写字母或特殊字符- ，结尾为小写字母或数字
    public boolean taskNameCheck(String taskName){
        String compile="^[0-9a-z][0-9a-z\\.\\-\\_\\/]*[0-9a-z]$|[0-9a-z]";
        if(!taskName.matches(compile)){
            return false;
        }
        return true;
    }

    //任务类型检测  tasktype:0或1   0：后台任务、1：在线服务
    public Boolean taskTypeCheck(String taskType){
        if(taskType==null||taskType.equals("")){
            return true;
        }
        if(!taskType.equals("0")&&!taskType.equals("1")){
            return false;
        }
        return true;
    }

    //启动方式检查 startType:0或1   立即启动方式 0:是,1:否
    public Boolean numberStartTypeCheck(String startType){
        if(startType==null||startType.equals("")){
            return true;
        }
        if (!startType.equals("0") && !startType.equals("1")) {
            return false;
        }
        return true;
    }

    //域名格式错误，格式为:“ip  hostname，ip hostname  IP格式错误  域名格式错误。首尾字符为数字或小写字母，中间字符为数字或小写字母或特殊字符/.-_
    public Boolean hostAliasesCheck(String hostAliases){
        if(hostAliases==null||hostAliases.equals("")){
            return true;
        }
        String[] hostAliasesList=hostAliases.split("\\,");
        for(String each: hostAliasesList){
            String[] ipHostSplit=each.split("\\s+");
            if(ipHostSplit.length!=2){
                return false;
            }
            else{
                String ipCompile="^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
                String hostCompile="^[0-9a-z][0-9a-z\\.\\-\\_\\/]*[0-9a-z]$|[0-9a-z]";
                if(!ipHostSplit[0].matches(ipCompile)||!ipHostSplit[1].matches(hostCompile)){
                    return false;
                }
            }
        }
        return true;
    }

    //pvsSize
    public boolean pvcSizeCheck(Integer pvcSize){
        if(pvcSize==null){
            return true;
        }
        if(!(pvcSize>=1&&pvcSize<=20)){
            return false;
        }
        return true;
    }
}
