package com.example.demo.service;

import ch.ethz.ssh2.Connection;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.common.DisplayErrorCode;
import com.example.demo.common.RestfulEntity;
import com.example.demo.entity.*;
import com.example.demo.mapper.ContainerMapper;
import com.example.demo.mapper.ImageMapper;
import com.example.demo.mapper.TaskMapper;
import com.example.demo.utils.*;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.models.*;
import lombok.var;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.sql.Time;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.aspectj.util.LangUtil.split;

@Service
@Transactional
public class TaskService {
    private static final Logger logger = LoggerFactory.getLogger(ImageService.class);
    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private Ssh ssh;
    @Autowired
    private CreateYaml createYaml;
    @Autowired
    private ContainerMapper containerMapper;
    @Autowired
    private FormatCheck formatCheck;
    @Autowired
    private ImageMapper imageMapper;
    @Autowired
    private K8sRelated k8s;
    @Value("${MODIFY_SAVE_PATH}")
    private String MODIFY_SAVE_PATH;
    @Value("${NFS_PATH}")
    private String NFS_PATH;
    @Value("${NFS_SERVER}")
    private String NFS_SERVER;
    @Value("${NFS_PORT}")
    private Integer NFS_PORT;
    @Value("${NFS_USER}")
    private String NFS_USER;
    @Value("${NFS_PASSWD}")
    private String NFS_PASSWD;
    @Value("${HARBOR}")
    private String HARBOR;

    public RestfulEntity<JSONObject> saveToTask(TaskSaveDto taskSaveDto) {
        if (taskSaveDto.getNamespace() == null || taskSaveDto.getNamespace().equals("")) {
            taskSaveDto.setNamespace(taskSaveDto.getUserResult().getNamespace());
        }
        //判断输入合法性
        RestfulEntity<JSONObject> inputValidCheck = taskSaveDtoCheck(taskSaveDto);
        if (!inputValidCheck.getStatus().equals("0")) {
            return inputValidCheck;
        }
        //判断任务名称是否被占用
        TaskDto taskDto = new TaskDto();
        taskDto.setNamespace(taskSaveDto.getNamespace());
        taskDto.setTaskName(taskSaveDto.getTaskName());
        Task task = taskMapper.queryTask(taskDto).get(0);
        if (task != null) {
            return RestfulEntity.getFailure(DisplayErrorCode.taskCheck);
        }
        //task和container插入到数据库中
        RestfulEntity<JSONObject> result = taskSave(taskSaveDto);

        return result;
    }
    public RestfulEntity<JSONObject>taskRun(TaskDto taskDto) throws Exception {
        String userNamespace=taskDto.getUserResult().getNamespace();
        TaskDto taskDto1=new TaskDto();
        taskDto1.setTaskId(taskDto.getTaskId());
        if(!"3".equals(taskDto.getUserResult().getAuthority())){
            taskDto1.setNamespace(userNamespace);
        }
        List<Task> taskList=taskMapper.queryTask(taskDto1);
        logger.info("_add: finish task query");
        if(taskList.isEmpty()){
            return RestfulEntity.getFailure(DisplayErrorCode.nullTask);
        }
        Task task= taskList.get(0);
        String taskName=task.getTaskName();
        String namespace=task.getNamespace();
        Integer replicas=task.getReplicas();
        String taskType=task.getTaskType();
        String kind="Deployment";
        String apiVersion= CreateYaml.getKind(kind);
        logger.info("_add: finish get kind version");
        List<ContainerDict>containerList=new ArrayList<>();
        ContainerDto containerDto=new ContainerDto();
        containerDto.setTaskId(taskDto.getTaskId());
        List<Container>containers=containerMapper.queryContainer(containerDto);
        logger.info("_add: finish container filter");
        Boolean createServiceFlag=false;
        Boolean createVolumeFlag=false;
        Date nowTime=new Date();
        HashMap<String,List<Integer>>containerPortsDict=new HashMap<>();
        HashMap<String,List<String>>containerVolumesDict=new HashMap<>();
        HashMap<String,List<String>>containerVolumesMapping=new HashMap<>();
        String configMapName;
        for(Container container: containers){
            ContainerDict containerDict=new ContainerDict();
            String imageId=container.getImageId();
            ImageDto imageDto=new ImageDto();
            imageDto.setImageId(imageId);
            List<Image> imageList=imageMapper.queryImage(imageDto);
            logger.info("_add: finish image filter");
            if(imageList.isEmpty()){
                return RestfulEntity.getFailure(DisplayErrorCode.nullTaskImage);
            }
            Image image=imageList.get(0);
            String name=image.getImageName();
            String imageTag=image.getImageTag();
            String imageName=name+":"+imageTag;
            String imagePorts=image.getImagePorts();
            String imageConfig=image.getConfig();
            String imageSubPath=image.getSubPath();
            String imageHostPath=image.getHostPath();
            String imageWorkingDir=image.getWorkingDir();
            String imageCommand=image.getCommand();
            String imageArgs=image.getArgs();
            String imageEnv=image.getEnv();
            String conId=container.getConId();
            List<Integer>ports=new ArrayList<>();
            List<String>volumes=new ArrayList<>();
            List<String>volumesMapping=new ArrayList<>();
            String hostAliases=container.getHostAliases();
            logger.info("_add: f1");
            if(imagePorts!=null&&!"".equals(imagePorts)){
                createServiceFlag=true;
                for(var i: imagePorts.split("\\,")){
                    ports.add(Integer.parseInt(i.split(":")[1]));
                }
            }
            if(ports.size()>0){
                containerPortsDict.put(conId,ports);
            }
            Integer pvcSize=image.getPvcSize();
            String imageMount=image.getImageMount();
            logger.info("_add: f2");
            if((!(imageMount==null||imageMount.equals("")))&&(pvcSize==null)){
                try{
                    createVolumeFlag=true;
                    for(var i:imageMount.split(",")){
                        volumes.add(i);
                        String volumesPath=NFS_PATH+namespace+"/"+taskName+i;
                        volumesMapping.add(volumesPath);
                        Connection conn=ssh.login(NFS_SERVER,NFS_PORT,NFS_USER,NFS_PASSWD);
                        String cmd="mkdir "+volumesPath+" -p";
                        String result=ssh.execute(conn,cmd);
                        logger.info("finish ssh mkdir");
                    }
                }catch (Exception e){
                    logger.error("error",e);
                }
            }else if((!(imageMount==null||imageMount.equals("")))&&(pvcSize!=null)){
                createVolumeFlag=true;
                for(var i:imageMount.split(",")){
                    volumes.add(i);
                    String j=i.replace("/","-");
                    String pvcName=taskName+j.toLowerCase(Locale.ROOT);
                    k8s.createPVC(userNamespace,pvcName,pvcSize);
                    logger.info("_add: finish create pvc");
                    volumesMapping.add(namespace+"-"+pvcName);
                }
            }
            logger.info("_add: f3");
            ArrayList<String>tmp= k8s.listNamespaceConfigMap(userNamespace);
            if(tmp.contains(imageName.replace(":","-").replace("/","-").replace("_","-"))){
                configMapName=imageName.replace(":","-").replace("/","-").replace("_","-");
            }else{
                configMapName="config";
            }
            if(volumes.size()>0){
                containerVolumesDict.put(conId,volumes);
                containerVolumesMapping.put(conId,volumesMapping);
            }
            logger.info("_add: f4");
            String command=container.getCommand();
            if(command==null||"".equals(command)){
                command=imageCommand;
            }
            String args=container.getArgs();
            if(args==null||"".equals(args)){
                args=imageArgs;
            }
            String env=container.getEnv();
            if(env==null||env.equals("")){
                env=imageEnv;
            }
            logger.info("_add: f5");
            Integer cpuRequests=container.getCpuRequests();
            Integer gpuRequests=container.getGpuRequests();
            Integer memRequests=container.getMemRequests();
            containerDict.setImageId(imageId);
            containerDict.setIamegName(imageName);
            containerDict.setImagePorts(imagePorts);
            containerDict.setPvcSize(pvcSize);
            containerDict.setCommand(command);
            containerDict.setArgs(args);
            containerDict.setEnv(env);
            containerDict.setCupRequest(cpuRequests.toString());
            containerDict.setGpuRequest(gpuRequests.toString());
            containerDict.setMemRequest(memRequests.toString());
            containerDict.setImageConfig(imageConfig);
            containerDict.setImageSubPath(imageSubPath);
            containerDict.setImageHostPath(imageHostPath);
            containerDict.setImageWorkingDir(imageWorkingDir);
            containerDict.setHostAliases(hostAliases);
            containerList.add(containerDict);
        }
        logger.info("_add: f6");
        logger.info("_add: start create controller yaml");
        createYaml.createControllerYaml(namespace,taskName,replicas,apiVersion,kind,containerList);
        logger.info("_add: finish create controller yaml");
       String availablePort="";
       if(createServiceFlag.equals(true)){
           List<Port> portsList = k8s.getServicePorts(namespace, taskName);
           logger.info("_add: finish get serverice port");
           for(String conId : containerPortsDict.keySet()){
               String mapping="";
               for(Port portsEach:portsList){
                   if(containerPortsDict.get(conId).contains(portsEach.getPortTarget())){
                       mapping=
                               mapping+";"+portsEach.getPortNode().toString()+":"+portsEach.getPortTarget().toString();
                   }
               }
               ContainerDto containerDto1 =new ContainerDto();
               containerDto1.setConId(conId);
               containerDto1.setPortMapping(mapping);
               containerMapper.updateContainer(containerDto1);
               logger.info("_add: finish update container mapping");
               availablePort=mapping.substring(1).split(":")[0];
           }
       }
       if(createVolumeFlag.equals(true)){
           for(String conId:containerVolumesDict.keySet()){
               String mapping="";
               Integer index=0;
               while(index<containerVolumesDict.get(conId).size()){
                   mapping=
                           mapping+","+containerVolumesMapping.get(conId).get(index)+":"+containerVolumesDict.get(conId).get(index);
                   index=index+1;
               }
               ContainerDto containerDto1=new ContainerDto();
               containerDto1.setConId(conId);
               containerDto1.setVolumeMapping(mapping);
               containerMapper.updateContainer(containerDto1);
               logger.info("_add: finish update container volume");
           }
       }
       Thread.sleep(1000*1);
        JSONObject taskStatus = k8s.getTaskStatus(namespace, taskName,
                "task_run");
        logger.info("_add: finish get task status");
        JSONObject result=new JSONObject();
        result.put("taskId",taskDto.getTaskId());
        if(taskType.equals("1")){
            result.put("servicePort",availablePort);
            logger.info("_add: finish update service port");
        }
        return RestfulEntity.getSuccess(result,taskStatus.toString());
    }

    public RestfulEntity<JSONObject>taskSearch(TaskDto taskDto){
        String namespace=taskDto.getUserResult().getNamespace();
        List<JSONObject>dataAll=new ArrayList<>();
        TaskDto taskDto1 = new TaskDto();
        List<Task>searchTaskResult=new ArrayList<>();
        if(taskDto.getTaskName()==null){
            if(!taskDto.getUserResult().getAuthority().equals("3")){
                taskDto1.setNamespace(namespace);
            }
            searchTaskResult=taskMapper.queryTask(taskDto1);
        }else{
            taskDto1.setSearchCondition(taskDto.getTaskName());
            if(!taskDto.getUserResult().getAuthority().equals("3")){
                taskDto1.setNamespace(namespace);
            }
            searchTaskResult=taskMapper.queryTask(taskDto1);
        }
        JSONObject data=new JSONObject();
        for(Task each:searchTaskResult){
            if(each==null){
                data.put("taskId",each.getTaskId());
                data.put("message","该任务不存在");
            }else{
                ContainerDto containerDto = new ContainerDto();
                containerDto.setTaskId(each.getTaskId());
                Container searchContainerResult=containerMapper.queryContainer(containerDto).get(0);
                ImageDto imageDto= new ImageDto();
                imageDto.setImageId(searchContainerResult.getImageId());
                Image searchImageResult=imageMapper.queryImage(imageDto).get(0);
                data.put("taskId",each.getTaskId());
                data.put("taskName",each.getTaskName());
                data.put("taskType",each.getTaskType());
                data.put("algoType",each.getAlgoType());
                data.put("replicas",each.getReplicas());
                data.put("cpu",searchContainerResult.getCpuRequests());
                data.put("mem",searchContainerResult.getMemRequests());
                data.put("gpu",searchContainerResult.getGpuRequests());
                data.put("input",searchContainerResult.getInput());
                data.put("channelId",searchContainerResult.getChannelId());
                data.put("output",searchContainerResult.getOutput());
                data.put("args",searchContainerResult.getArgs());
                data.put("env",searchContainerResult.getEnv());
                data.put("command",searchContainerResult.getCommand());
                data.put("imageName",searchImageResult.getImageName()+":"+searchImageResult.getImageTag());
            }
            dataAll.add(data);
        }
        JSONObject res=new JSONObject();
        res.put("data",dataAll);
        return RestfulEntity.getSuccess(res,"查询成功");
    }

    public RestfulEntity<JSONObject>taskDelete(TaskDto taskDto) throws ApiException {
        String userNamespace=taskDto.getUserResult().getNamespace();
        TaskDto taskDto1=new TaskDto();
        taskDto1.setTaskId(taskDto.getTaskId());
        if(!taskDto.getUserResult().getAuthority().equals("3")){
            taskDto1.setNamespace(userNamespace);
        }
        List<Task> taskList=taskMapper.queryTask(taskDto1);
        if(taskList.isEmpty()){
            return RestfulEntity.getFailure(DisplayErrorCode.nullTask);
        }
        Task taskDeleteResult=taskList.get(0);
        ContainerDto containerDto=new ContainerDto();
        containerDto.setTaskId(taskDto.getTaskId());
        List<Container> containerDeleteResult=
                containerMapper.queryContainer(containerDto);
        String namespace=taskDeleteResult.getNamespace();
        String taskName=taskDeleteResult.getTaskName();
        String compile="task-*";
        if(taskName.matches(compile)&&taskDto.getFlag()==null){
            return RestfulEntity.getFailure(210,"禁止删除视频任务算子");
        }
        try{
            k8s.deleteDeployment(namespace,taskName);
        }catch (Exception e){

        }
        k8s.deleteSvc(namespace,taskName);
        try{
            ImageDto imageDto= new ImageDto();
            imageDto.setImageId(containerDeleteResult.get(0).getImageId());
            Image imageResult=imageMapper.queryImage(imageDto).get(0);
            if(imageResult.getPvcSize()==null){
                Connection conn=ssh.login(NFS_SERVER,NFS_PORT,NFS_USER,NFS_PASSWD);
                String mvTime=taskDeleteResult.getTaskCreateTime().toString();
                for(Container each:containerDeleteResult){
                    String volumeMapping=each.getVolumeMapping();
                    String volumeMappingNew="";
                    if(volumeMapping!=null){
                        for(String volume:volumeMapping.split(".")){
                            String[] volumeMv=volume.split(":");
                            String cmd=
                                    "mv "+volumeMv[0]+" "+volumeMv[0]+mvTime;
                            ssh.execute(conn,cmd);
                            volumeMappingNew=
                                    volumeMappingNew+","+volumeMv[0]+mvTime+
                                            ":"+volumeMv[1];
                        }
                    }
                    if(!(volumeMappingNew.equals(""))){
                        each.setVolumeMapping(volumeMappingNew.substring(1));
                    }
                    each.setDeleteFlag("1");
                    ContainerDto containerDto1=new ContainerDto();
                    containerDto1.setConId(each.getConId());
                    containerDto1.setVolumeMapping(each.getVolumeMapping());
                    containerDto1.setDeleteFlag("1");
                    containerMapper.deleteContainer(containerDto1);
                }
            }
            else{
                for(Container each:containerDeleteResult){
                    String volumeMapping=each.getVolumeMapping();
                    if(!(volumeMapping==null)){
                        for(String volume:volumeMapping.split(",")){
                            String[] volumeSplit=volume.split(":");
                            String pvcName=taskName+volumeSplit[1].replace(
                                    "/","-").toLowerCase(Locale.ROOT);
                            k8s.deleteSvc(namespace,pvcName);
                            Thread.sleep(1000*1);
                            Integer count=0;
                            while(k8s.listNamespaceConfigMap(namespace).contains(pvcName)){
                                k8s.deletePVC(namespace,pvcName);
                                count+=1;
                                if(count>=2){
                                    break;
                                }
                            }
                        }
                    }
                    ContainerDto containerDto1=new ContainerDto();
                    containerDto1.setDeleteFlag("1");
                    containerMapper.deleteContainer(containerDto1);
                }
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        TaskDto taskDto2=new TaskDto();
        taskDto2.setTaskId(taskDeleteResult.getTaskId());
        taskDto2.setDeleteFlag("1");
        taskMapper.deleteTask(taskDto2);
        return RestfulEntity.getSuccess("已删除此任务");
    }

    public RestfulEntity<JSONObject>taskStop(TaskDto taskDto) throws ApiException {
        String userNamespace=taskDto.getUserResult().getNamespace();
        TaskDto taskDto1=new TaskDto();
        taskDto1.setTaskId(taskDto.getTaskId());
        if(!"3".equals(taskDto.getUserResult().getAuthority())){
            taskDto1.setNamespace(userNamespace);
        }
        Task task=taskMapper.queryTask(taskDto1).get(0);
        if(task==null||"".equals(task)){
            return RestfulEntity.getFailure(DisplayErrorCode.nullTask);
        }
        String namespace=task.getNamespace();
        String taskName=task.getTaskName();
        String pattern="task-*";
        if(!taskName.matches(pattern)){
            return RestfulEntity.getFailure(211,"禁止暂停视频任务算子");
        }
        k8s.deleteDeployment(namespace,taskName);
        k8s.deleteSvc(namespace,taskName);
        return RestfulEntity.getSuccess("任务已加入停止计划");
    }

    public RestfulEntity<List<JSONObject>>taskStatus(TaskDto taskDto) throws ApiException {
        String userNamespace=taskDto.getUserResult().getNamespace();
        List<String>taskIds=taskDto.getTaskIds();
        String authority=taskDto.getUserResult().getAuthority();
        TaskDto taskDto1=new TaskDto();
        List<String>searchTaskResult=new ArrayList<>();
        if(taskIds==null||taskIds.isEmpty()){
            if("3".equals(authority)){
                searchTaskResult=taskMapper.queryTaskIds(taskDto1);
            }else{
                taskDto1.setNamespace(userNamespace);
                searchTaskResult=taskMapper.queryTaskIds(taskDto1);
            }
            for(String each:searchTaskResult){
                taskIds.add(each);
            }
            System.out.println(taskIds);
        }
        List<JSONObject> statusResult=new ArrayList<>();
        JSONObject eachTask=new JSONObject();
        for(String each: taskIds){
            taskDto1.setTaskId(each);
            if(!"3".equals(authority)){
                taskDto1.setNamespace(userNamespace);
            }
            Task taskStatusResult=taskMapper.queryTask(taskDto1).get(0);
            if(taskStatusResult==null||taskStatusResult.equals("")){
                eachTask.put(each,"无此任务");
            }else{
                String namespace=taskStatusResult.getNamespace();
                String taskName=taskStatusResult.getTaskName();
                JSONObject taskStatus=k8s.getTaskStatus(namespace,taskName,
                        "task_status");
                eachTask.put(each,taskStatus);
            }
            statusResult.add(eachTask);
        }
        return RestfulEntity.getSuccess(statusResult,"查询成功");
    }

    public RestfulEntity<JSONObject>videoFileUpdate(TaskDto taskDto) throws InterruptedException {
        TaskDto taskDto1=new TaskDto();
        taskDto1.setTaskId(taskDto.getTaskId());
        Task taskResult=taskMapper.queryTask(taskDto1).get(0);
        Integer videoFile=taskResult.getVideoFile()-taskDto.getCount();
        Thread.sleep(30*1000);
        TaskDto taskDto2=new TaskDto();
        taskDto2.setTaskId(taskDto.getTaskId());
        taskDto2.setVideoFile(taskDto.getVideoFile());
        taskMapper.updateTask(taskDto2);
        return RestfulEntity.getSuccess("修改成功");
    }

    public RestfulEntity<JSONObject>modifyRun(TaskDto taskDto) throws ApiException {
        ImageDto imageDto=new ImageDto();
        imageDto.setAlgoName(taskDto.getImageDto().getAlgoName());
        imageDto.setEventType(taskDto.getImageDto().getEventType());
        List<Image> imageList=imageMapper.queryImage(imageDto);
        if(imageList.isEmpty()){
            return RestfulEntity.getFailure(2,"找不到对应的算法镜像");
        }
        Image searchImage=imageList.get(0);
        String realName=
                "task-"+searchImage.getImageId()+"-"+taskDto.getImageDto().getEventType().toString();
        String logPath=
                MODIFY_SAVE_PATH+taskDto.getImageDto().getEventType().toString()+"/LOG";
        String codePath=
                MODIFY_SAVE_PATH+taskDto.getImageDto().getEventType().toString()+"/modify";
        Connection conn=ssh.login(NFS_SERVER,NFS_PORT,NFS_USER,NFS_PASSWD);
        String cmd="mkdir "+logPath+" -p";
        ssh.execute(conn,cmd);
        String cmd2="mkdir " + codePath + " -p";
        ssh.execute(conn,cmd2);
        logger.info("finish ssh mkdir");
        //创建netYaml
        ArrayList<V1ServicePort>portList=new ArrayList<>();
        V1ServicePort v1ServicePort=new V1ServicePort();
        v1ServicePort.setPort(8888);
        portList.add(v1ServicePort);
        V1Service netYaml = new V1ServiceBuilder()
                .withApiVersion("v1")
                .withKind("Service")
                .withNewMetadata()
                  .withName(realName)
                  .withNamespace(taskDto.getNamespace())
                .endMetadata()
                .withNewSpec()
                  .withType("NodePort")
                  .withPorts(portList) // 变量
                  .addToSelector("resource_name",realName)
                .endSpec()
                .build();
        //创建depYaml
        ArrayList<V1Container> containerArrayList =new ArrayList<>();

        V1Container v1Container=new V1Container();
        v1Container.setName(realName);
        v1Container.setImage(HARBOR+"/"+searchImage.getImageName()+":"+searchImage.getImageTag());
        v1Container.setImagePullPolicy("Always");
        ArrayList<V1ContainerPort>v1ServicePorts=new ArrayList<>();
        V1ContainerPort v1ContainerPort =new V1ContainerPort();
        v1ContainerPort.setContainerPort(8888);
        v1ServicePorts.add(v1ContainerPort);
        v1Container.setPorts(v1ServicePorts);
        List<String>command=new ArrayList<>();
        String str="/bin/bash";
        String str2="-c";
        String str3="cp -r /app/workdir/* /app/modify/; sh /run_jupyter.sh";
        command.add(str);
        command.add(str2);
        command.add(str3);
        v1Container.setCommand(command);
        v1Container.setWorkingDir("/app");
        List<V1VolumeMount> volumeMounts=new ArrayList<>();
        V1VolumeMount v1VolumeMount=new V1VolumeMount();
        v1VolumeMount.setName("jupyter-log");
        v1VolumeMount.setMountPath("/LOG");
        v1VolumeMount.setReadOnly(false);
        volumeMounts.add(v1VolumeMount);

        V1VolumeMount v1VolumeMount2=new V1VolumeMount();
        v1VolumeMount2.setName("modify");
        v1VolumeMount2.setMountPath("/app/modify");
        v1VolumeMount2.setReadOnly(false);
        volumeMounts.add(v1VolumeMount2);
        v1Container.setVolumeMounts(volumeMounts);
        containerArrayList.add(v1Container);
        //创建depYaml中的volumes
        ArrayList<V1Volume> volumeArrayList =new ArrayList<>();
        //volumes1
        V1Volume v1Volume=new V1Volume();
        v1Volume.setName("jupyter-log");
        V1HostPathVolumeSource v1HostPathVolumeSource=
                new V1HostPathVolumeSource();
        v1HostPathVolumeSource.setPath(MODIFY_SAVE_PATH+taskDto.getImageDto().getEventType().toString()+"/LOG");
        v1Volume.setHostPath(v1HostPathVolumeSource);
        volumeArrayList.add(v1Volume);
        //volumes2
        V1Volume v1Volume2=new V1Volume();
        v1Volume2.setName("modify");
        V1HostPathVolumeSource v1HostPathVolumeSource2=
                new V1HostPathVolumeSource();
        v1HostPathVolumeSource2.setPath(MODIFY_SAVE_PATH+taskDto.getImageDto().getEventType().toString()+"/modify");
        v1Volume2.setHostPath(v1HostPathVolumeSource2);
        volumeArrayList.add(v1Volume2);



        AppsV1beta1Deployment depYaml = new AppsV1beta1DeploymentBuilder()
                .withApiVersion("apps/v1beta1")
                .withKind("Deployment")
                .withNewMetadata()
                .withName(realName)
                .withNamespace(taskDto.getNamespace())
                .endMetadata()
                .withNewSpec()
                    .withReplicas(1)
                    .withNewTemplate()
                        .withNewMetadata()
                            .addToLabels("resource_name",realName)
                        .endMetadata()
                        .withNewSpec()
                            .withContainers(containerArrayList)
                            .withVolumes(volumeArrayList)
                        .endSpec()
                    .endTemplate()
                .endSpec()
                .build();

        k8s.createService(netYaml);
        k8s.createDeployment(depYaml);
        return RestfulEntity.getSuccess("启动成功");
    }

    public RestfulEntity<JSONObject>modifyStop(TaskDto taskDto) throws ApiException {
        ImageDto imageDto=new ImageDto();
        imageDto.setAlgoName(taskDto.getImageDto().getAlgoName());
        imageDto.setEventType(taskDto.getImageDto().getEventType());
        List<Image>imageList=imageMapper.queryImage(imageDto);
        if(imageList.isEmpty()){
            return RestfulEntity.getFailure(2,"找不到对应的算法镜像");
        }
        var searchImage=imageList.get(0);
        String realName="task-"+searchImage.getImageId()+"-"+imageDto.getEventType().toString();
        k8s.deleteSvc(taskDto.getNamespace(),realName);
        k8s.deleteDeployment(taskDto.getNamespace(),realName);
        return RestfulEntity.getSuccess("删除成功");
    }

    public RestfulEntity<JSONObject>modifyPortToken(TaskDto taskDto){
        ImageDto imageDto=new ImageDto();
        imageDto.setAlgoName(taskDto.getImageDto().getAlgoName());
        imageDto.setEventType(taskDto.getImageDto().getEventType());
        List<Image>imageList=imageMapper.queryImage(imageDto);
        if(imageList.isEmpty()){
            return RestfulEntity.getFailure(2,"找不到对应的算法镜像");
        }
        var searchImage=imageList.get(0);
        String realName="task-"+searchImage.getImageId()+"-"+imageDto.getEventType().toString();

        try{
//            List<String>portsList=k8s.getServicePorts(taskDto.getNamespace(),realName);
            String pattern="token=[0-9a-z]*";
            Pattern p = Pattern.compile(pattern);

            String token=new String();
            String logPath=MODIFY_SAVE_PATH+taskDto.getImageDto().getEventType().toString()+"/LOG";
            List<String> lines = FileUtils.readLines(new File(logPath), "UTF-8");
            for(String line:lines){
                if(line.substring(0,1).equals("h")){
                    Matcher matcher = p.matcher(line);
                    if(matcher.find()){
                       token=matcher.toString().substring(6);
                    }
                }
            }
            JSONObject result=new JSONObject();
            result.put("status",0);
//            result.put("port",portsList.get(0));
            result.put("token",token);
            return RestfulEntity.getSuccess(result);

        } catch (IOException e) {
            e.printStackTrace();
            return RestfulEntity.getFailure(2,"获取port和token失败");
        }
    }

    public RestfulEntity<JSONObject>modifyLog(TaskDto taskDto) throws IOException {
        String logPath=MODIFY_SAVE_PATH+taskDto.getImageDto().getEventType().toString()+"/"+taskDto.getUserResult().getUserName()+".log";
        String s = FileUtils.readFileToString(new File(logPath));
        return RestfulEntity.getSuccess(s);
    }

    public Task queryTask(TaskDto taskDto) {
        Task task = taskMapper.queryTask(taskDto).get(0);
        return task;
    }

    public int updateTask(TaskDto taskDto) {
        int ret = taskMapper.updateTask(taskDto);
        return ret;
    }

    public int deleteTask(TaskDto taskDto) {
        int ret = taskMapper.deleteTask(taskDto);
        return ret;
    }

    public List<String> queryTaskIds(TaskDto taskDto) {
        List<String> taskIdsList = taskMapper.queryTaskIds(taskDto);
        return taskIdsList;
    }

    //检验输入taskSaveDto的有效性
    private RestfulEntity<JSONObject> taskSaveDtoCheck(TaskSaveDto taskSaveDto) {
        if (!formatCheck.imageNameTagCheck(taskSaveDto.getImageName())) {
            return RestfulEntity.getFailure(DisplayErrorCode.imageTagCheck);
        }
        if (!formatCheck.taskNameCheck(taskSaveDto.getTaskName())) {
            return RestfulEntity.getFailure(DisplayErrorCode.taskNameCheck);
        }
        if (!formatCheck.taskTypeCheck(taskSaveDto.getTaskType())) {
            return RestfulEntity.getFailure(DisplayErrorCode.taskTypeCheck);
        }
        if (!formatCheck.algoTypeCheck(taskSaveDto.getAlgoType())) {
            return RestfulEntity.getFailure(DisplayErrorCode.algoTypeCheck);
        }
        if (!formatCheck.numberStartTypeCheck(taskSaveDto.getStartType())) {
            return RestfulEntity.getFailure(DisplayErrorCode.getStartTypeCheck);
        }
        if (!formatCheck.hostAliasesCheck(taskSaveDto.getHostAliases())) {
            return RestfulEntity.getFailure(DisplayErrorCode.hostAliasesCheck);
        }
        return RestfulEntity.getSuccess("输入有效");
    }

    //task和container插入数据库
    private RestfulEntity<JSONObject> taskSave(TaskSaveDto taskSaveDto) {
        //task初始化
        TaskDto taskDto = new TaskDto();
        taskDto.setDeleteFlag("0");
        String taskId = CommonUtils.getRandomStr();
        taskDto.setTaskId(taskId);
        taskDto.setTaskName(taskSaveDto.getTaskName());
        taskDto.setNamespace(taskSaveDto.getNamespace());
        taskDto.setReplicas(taskSaveDto.getReplicas());
        taskDto.setGpuModel("1");
        taskDto.setCreateUserId(taskSaveDto.getCreateUserId());
        taskDto.setSvcIp(null);
        taskDto.setTaskType(taskSaveDto.getTaskType());
        taskDto.setAlgoType(taskSaveDto.getAlgoType());
        Date date = new Date();
        taskDto.setTaskCreateTime(date);
        taskDto.setRealtimeStream(0);
        taskDto.setVideoFile(0);
        //判断镜像是否被注册
        String[] imageNameTag = taskSaveDto.getImageName().split("\\:");
        ImageDto imageDto = new ImageDto();
        imageDto.setImageName(imageNameTag[0]);
        imageDto.setImageTag(imageNameTag[1]);
        List<Image> imageList = imageMapper.queryImage(imageDto);
        if (imageList.size() == 0 || imageList == null) {
            return RestfulEntity.getFailure(DisplayErrorCode.imageCheck);
        }
        Image image = imageList.get(0);
        //container初始化
        ContainerDto containerDto = new ContainerDto();
        containerDto.setConId(CommonUtils.getRandomStr());
        containerDto.setTaskId(taskId);
        containerDto.setImageId(image.getImageId());
        containerDto.setCommand(taskSaveDto.getCommand());
        containerDto.setInput(taskSaveDto.getInput());
        containerDto.setOutput(taskSaveDto.getOutput());
        containerDto.setChannelId(taskSaveDto.getChannelId());
        containerDto.setDeleteFlag("0");
        containerDto.setTvChannel(taskSaveDto.getTvChannel());
        containerDto.setNamespace(taskSaveDto.getNamespace());
        containerDto.setHostAliases(taskSaveDto.getHostAliases());
        if (taskSaveDto.getArgs() == null || taskSaveDto.getArgs().equals("")) {
            taskSaveDto.setArgs(image.getArgs());
        }
        containerDto.setArgs(taskSaveDto.getArgs());
        if (taskSaveDto.getEnv() == null || taskSaveDto.getEnv().equals("")) {
            taskSaveDto.setEnv(image.getEnv());
        }
        containerDto.setEnv(taskSaveDto.getEnv());
        if (taskSaveDto.getCpuRequests() == null) {
            if (image.getCpuRequests() == null) {
                containerDto.setCpuRequests(500);
            } else {
                containerDto.setCpuRequests(image.getCpuRequests());
            }
        }
        if (taskSaveDto.getGpuRequests() == null) {
            if (image.getGpuRequests() == null) {
                containerDto.setGpuRequests(0);
            } else {
                containerDto.setGpuRequests(image.getGpuRequests());
            }
        }
        if (taskSaveDto.getMemRequests() == null) {
            if (image.getMemRequests() == null) {
                containerDto.setMemRequests(1024);
            } else {
                containerDto.setMemRequests(image.getMemRequests());
            }
        }
        //保存task
        try {
            taskMapper.insertTaskInfo(taskDto);
            containerMapper.insertContainerInfo(containerDto);
            return RestfulEntity.getSuccess("taskDto、containerDto插入成功");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            logger.info("插入数据失败, insertTaskInfoList -> TaskDto = " + taskDto);
            logger.info("插入数据失败, insertcontainerInfoList -> ContainerDto = " + containerDto);
            return RestfulEntity.getFailure(504, "task、container插入失败");
        }
    }
}
