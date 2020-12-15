package com.example.demo.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.entity.ContainerDict;
import io.kubernetes.client.StringUtil;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.apis.ExtensionsV1beta1Api;
import io.kubernetes.client.custom.IntOrString;
import io.kubernetes.client.custom.Quantity;
import io.kubernetes.client.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.*;

@Component
public class CreateYaml {
    @Value("${SCHEDULE_UNIT}")
    private String scheduleUnit;

    @Autowired
    K8sRelated k8s;

    public boolean notBlank(String strData){
        return strData != null && strData.trim().length() != 0;
    }

    //@PostMapping(value = "/ehl/k8s/createK8sController")
    public void createControllerYaml(String nameSpace, String taskName, Integer replicas, String apiVersion, String kind,
                                    List<ContainerDict> containerList) throws Exception {
        ArrayList<V1ServicePort> servicePortArrayList = new ArrayList<>();//创建service中withPort参数

        ArrayList<V1ContainerPort> containerPortArrayList =new ArrayList<>();//创建container中withPort参数
        ArrayList<V1VolumeMount> volumeMountArrayList =new ArrayList<>();//创建container中withVolumeMounts参数
        ArrayList<V1EnvVar> envVarArrayList =new ArrayList<>();//创建container中withEnv参数
        ArrayList<String> argsArrayList = new ArrayList<>();//创建container中withArgs参数
        ArrayList<String> commandArrayList =new ArrayList<>();//创建container中withCommand参数

        ArrayList<V1HostAlias> hostAliasArrayList =new ArrayList<>();//创建deployment中withHostAliases参数
        ArrayList<V1Container> containerArrayList =new ArrayList<>();//deploy中withContainer参数
        ArrayList<V1Volume> volumeArrayList =new ArrayList<>();//创建deployment中withVolumes参数

        V1Namespace v1Namespace = new V1NamespaceBuilder()
                .withApiVersion("v1")
                .withKind("Namespace")
                .withNewMetadata()
                  .withName(nameSpace)
                .endMetadata()
                .build();
        k8s.createNamespace(v1Namespace);

        for (int i=0;i < containerList.size();i++){
            ContainerDict container = containerList.get(i);
            if(notBlank(container.getHostAliases())){
                for(String hostAliases: container.getHostAliases().split(",")){
                    String[] ipHosts = hostAliases.split(" ");
                    String hostNames = ipHosts[1];
                    V1HostAlias v1HostAlias = new V1HostAliasBuilder()
                            .withIp(ipHosts[0])
                            .withHostnames(hostNames,ipHosts[0])
                            .build();  //hostAliases withProblems
                    hostAliasArrayList.add(v1HostAlias);
                }
            }
            if(notBlank(container.getImagePorts())){
                String[] portList = container.getImagePorts().split(",");
                for (String port : portList) {
                    V1ContainerPort v1ContainerPort =new V1ContainerPortBuilder()
                            .withName(port.split(":")[0])
                            .withContainerPort(Integer.valueOf(port.split(":")[1]))
                            .build();
                    containerPortArrayList.add(v1ContainerPort);
                    if (port.split(":").length == 3) {
                        V1ServicePort v1ServicePort = new V1ServicePortBuilder()
                                .withName(port.split(":")[0])
                                .withPort(Integer.valueOf(port.split(":")[1]))
                                .withTargetPort(new IntOrString(port.split(":")[1]))
                                .withNodePort(Integer.valueOf(port.split(":")[2]))
                                .build();
                        servicePortArrayList.add(v1ServicePort);

                    } else {
                        V1ServicePort v1ServicePort = new V1ServicePortBuilder()
                                .withName(port.split(":")[0])
                                .withPort(Integer.valueOf(port.split(":")[1]))
                                .withTargetPort(new IntOrString(port.split(":")[1]))
                                .build();
                        servicePortArrayList.add(v1ServicePort);
                    }
                }
            }

            if (notBlank(container.getArgs())){
                //String[] argList = container.getString("args").split(",");
                argsArrayList.addAll(Arrays.asList(container.getArgs().split(",")));
            }
            if(notBlank(container.getCommand())){
                commandArrayList.add(container.getCommand());
            }

            /*TODO 算法端边云运行环境*/
            String runPosition ="cloud";
            if(notBlank(container.getEnv())){
                for(String env:container.getEnv().split(",")){
                    int index = env.indexOf(":");
                    String name = env.split(":")[0];
                    String value = env.substring(index+1,env.length()-1);
                    V1EnvVar envVar =new V1EnvVarBuilder()
                            .withName(name)
                            .withValue(value)
                            .build();
                    envVarArrayList.add(envVar);
                    if(name.equals("RUN_POSITION")&&value.equals("SIDE")){
                        runPosition ="side";
                    }
                }
            }


            V1ResourceRequirements v1ResourceRequirements= new V1ResourceRequirementsBuilder()
                    .addToRequests("memory", Quantity.fromString(container.getMemRequest()+"Mi"))
                    .build();
            if(!container.getGpuRequest().equals("0")){
                if(runPosition.equals("side")){
                    String gpuRequest = container.getGpuRequest();
                    v1ResourceRequirements=new V1ResourceRequirementsBuilder()
                            .addToRequests("memory", Quantity.fromString(container.getMemRequest()+"Mi"))
                            .addToRequests("tpu.bitmain.com/bm1684", Quantity.fromString(gpuRequest))
                            .addToLimits("tpu.bitmain.com/bm1684", Quantity.fromString(gpuRequest))
                            .build();

                }
                else{
                    if(Integer.parseInt(container.getGpuRequest())<=8){
                        String gpuRequest = container.getGpuRequest();
                        v1ResourceRequirements =new V1ResourceRequirementsBuilder()
                                .addToRequests("memory", Quantity.fromString(container.getMemRequest()+"Mi"))
                                .addToRequests("aliyun.com/gpu-count",Quantity.fromString(gpuRequest))
                                .addToLimits("aliyun.com/gpu-count",Quantity.fromString(container.getGpuRequest()))
                                .build();
                    }
                    else if(scheduleUnit.equals("EiB")){
                        String gpuRequest = String.valueOf(Integer.parseInt(container.getGpuRequest())/10);
                        v1ResourceRequirements =new V1ResourceRequirementsBuilder()
                                .addToRequests("memory", Quantity.fromString(container.getMemRequest()+"Mi"))
                                .addToRequests("aliyun.com/gpu-mem",Quantity.fromString(gpuRequest))
                                .addToLimits("aliyun.com/gpu-mem", Quantity.fromString(gpuRequest))
                                .build();
                    }
                    else{
                        String gpuRequest = container.getGpuRequest();
                        v1ResourceRequirements =new V1ResourceRequirementsBuilder()
                                .addToRequests("memory", Quantity.fromString(container.getMemRequest()+"Mi"))
                                .addToRequests("aliyun.com/gpu-mem",Quantity.fromString(gpuRequest))
                                .addToLimits("aliyun.com/gpu-mem", Quantity.fromString(gpuRequest))
                                .build();
                    }
                }

            }
            /*
            V1ResourceRequirements v1ResourceRequirements =new V1ResourceRequirementsBuilder()
                    .addToRequests("memory", Quantity.fromString(container.getString("mem_requests")+"Mi"))
                    .addToRequests("cpu", Quantity.fromString(container.getString("cpu_request")+"m"))
                    .addToRequests(aliyun, Quantity.fromString(gpuRequest))
                    .addToLimits("memory", Quantity.fromString(container.getString("mem_requests")+"Mi"))
                    .addToLimits("cpu",Quantity.fromString(container.getString("cpu_request")+"m"))
                    .addToLimits(aliyun, Quantity.fromString(gpuRequest))
                    .build();  //container resource() */
            if(notBlank(container.getImageSubPath())){
                HashMap<String,List<String>> hostPathDict = new HashMap<>();
                String[] subPathMappingList= container.getImageSubPath().split(",");
                for(String subPathMapping: subPathMappingList){
                    //int index =subPathMapping.split(":")[0].split("/").length-1;
                    String[] path = subPathMapping.split(":")[0].split("/");
                    int index = path.length;
                    String[] newPath = Arrays.copyOfRange(path,0,index-1);//包括from 不包括to
                    String hostPath = StringUtil.join(newPath,"/");
                    String subPathFileMapping =path[index-1] + ":" + subPathMapping.split(":")[0];
                    if(!hostPathDict.containsKey(hostPath)){
                        hostPathDict.put(hostPath, Collections.singletonList(subPathFileMapping));
                    }
                    else{
                        hostPathDict.get(hostPath).add(subPathFileMapping);
                    }
                }
                for(String hostPath:hostPathDict.keySet()){
                    String name = MathUtil.getRandStrCode(16);
                     V1Volume v1Volume = new V1VolumeBuilder()
                            .withName(name)
                            .withNewHostPath()
                              .withPath(hostPath)
                            .endHostPath()
                            .build();
                     volumeArrayList.add(v1Volume);
                    for(String subPathFileMapping:hostPathDict.get(hostPath)){
                        V1VolumeMount v1VolumeMount = new V1VolumeMountBuilder()
                                .withName(name)
                                .withMountPath(subPathFileMapping.split(":")[1])
                                .withSubPath(subPathFileMapping.split(":")[0])
                                .withReadOnly(Boolean.TRUE)
                                .build();
                        volumeMountArrayList.add(v1VolumeMount);
                    }
                }
            }
            if(notBlank(container.getImageMount()) && container.getPvcSize()==null){
                String[] volumeList = container.getImageMount().split(",");
                for(String volume: volumeList){
                    String name = MathUtil.getRandStrCode(16);
                    V1VolumeMount v1VolumeMount = new V1VolumeMountBuilder()
                            .withName(name)
                            .withMountPath(volume)
                            .build();
                    volumeMountArrayList.add(v1VolumeMount);
                    V1Volume v1Volume = new V1VolumeBuilder()
                            .withName(name)
                            .withNewNfs()
                              .withServer("10.20.5.3") //K8S_SERVER 应为变量
                              .withPath("k8sConfig.NFS_PATH" + nameSpace +"/" +taskName +volume)
                            .endNfs()
                            .build();
                    volumeArrayList.add(v1Volume);
                }
            }
            if(notBlank(container.getImageMount())&&(container.getPvcSize()!=null)){
                String[] volumeList = container.getImageMount().split(",");
                for(String volume:volumeList){
                    String name = MathUtil.getRandStrCode(16);
                    String volumeChange=volume.replace("/","-");
                    V1VolumeMount v1VolumeMount = new V1VolumeMountBuilder()
                            .withName(name)
                            .withMountPath(volume)
                            .build();
                    volumeMountArrayList.add(v1VolumeMount);
                    V1Volume v1Volume =new V1VolumeBuilder()
                            .withName(name)
                            .withNewPersistentVolumeClaim()
                                .withClaimName(taskName+volumeChange.toLowerCase())
                            .endPersistentVolumeClaim()
                            .build();
                    volumeArrayList.add(v1Volume);

               }
            }

            V1VolumeMount v1VolumeMount1 = new V1VolumeMountBuilder()
                    .withName("localtime")
                    .withMountPath("/etc/localtime")
                    .withReadOnly(Boolean.TRUE)
                    .build();
            volumeMountArrayList.add(v1VolumeMount1);
            V1Volume v1Volume1 = new V1VolumeBuilder()
                    .withName("localtime")
                    .withNewHostPath()
                      .withPath("/etc/localtime")
                    .endHostPath()
                    .build();
            volumeArrayList.add(v1Volume1);

            if(notBlank(container.getImageHostPath())){
                for(String hostPath:container.getImageHostPath().split(",")){
                    String name = MathUtil.getRandStrCode(16);
                    V1VolumeMount v1VolumeMount = new V1VolumeMountBuilder()
                            .withName(name)
                            .withMountPath(hostPath.split(":")[1])
                            .withReadOnly(Boolean.TRUE)
                            .build();
                    volumeMountArrayList.add(v1VolumeMount);
                    V1Volume v1Volume = new V1VolumeBuilder()
                            .withName(name)
                            .withNewHostPath()
                              .withPath(hostPath.split(":")[0])
                            .endHostPath()
                            .build();
                    volumeArrayList.add(v1Volume);

                }
            }
//            if(notBlank(container.getString("image_working_dir"))){
//                workingDir = container.getString("image_working_dir");
//            }
            if(notBlank(container.getImageConfig())){
                for(String config : container.getImageConfig().split(",")){
                    String name = MathUtil.getRandStrCode(16);
                    int index = config.split("/").length-1;
                    V1VolumeMount v1VolumeMount = new V1VolumeMountBuilder()
                            .withName(name)
                            .withMountPath(config)
                            .withSubPath(config.split("/")[index])
                            .build();
                    volumeMountArrayList.add(v1VolumeMount);
                    V1Volume v1Volume = new V1VolumeBuilder()
                            .withName(name)
                            .withNewConfigMap()
                              .withName("config")
                            .endConfigMap()
                            .build();
                    volumeArrayList.add(v1Volume);
                }
            }

            /*创建container对象*/
            V1Container v1Container = new V1ContainerBuilder()
                    .withName(taskName)
                    .withImage("k8sHarborImage")
                    .withPorts(containerPortArrayList)
                    .withVolumeMounts(volumeMountArrayList)
                    .withEnv(envVarArrayList)
                    .withArgs(argsArrayList)
                    .withCommand(commandArrayList)
                    .withImagePullPolicy("Always")
                    .withResources(v1ResourceRequirements)
                    .build();
            if(notBlank(container.getImageWorkingDir())){
                String workingDir = container.getImageWorkingDir();
                v1Container = new V1ContainerBuilder()
                        .withName(taskName)
                        .withImage("k8sHarborImage")
                        .withPorts(containerPortArrayList)
                        .withVolumeMounts(volumeMountArrayList)
                        .withEnv(envVarArrayList)
                        .withArgs(argsArrayList)
                        .withCommand(commandArrayList)
                        .withImagePullPolicy("Always")
                        .withResources(v1ResourceRequirements)
                        .withWorkingDir(workingDir)
                        .build();
            }
            containerArrayList.add(v1Container);

            /*创建service对象*/
            V1Service v1Service = new V1ServiceBuilder()
                    .withApiVersion("v1")
                    .withKind("Service")
                    .withNewMetadata()
                        .withName(taskName)
                        .withNamespace(nameSpace)
                        .addToLabels("resource_name",taskName)
                    .endMetadata()
                    .withNewSpec()
                        .withType("NodePort")
                        .withPorts(servicePortArrayList) // 变量
                        .addToSelector("resource_name",taskName)
                    .endSpec()
                    .build();
            k8s.createService(v1Service);
            /*创建deployment对象*/
            AppsV1beta1Deployment v1beta1Deployment = new AppsV1beta1DeploymentBuilder()
                    .withApiVersion(apiVersion)
                    .withKind(kind)
                    .withNewMetadata()
                        .withName(taskName)
                        .withNamespace(nameSpace)
                    .endMetadata()
                    .withNewSpec()
                        .withReplicas(replicas)
                        .withNewTemplate()
                            .withNewMetadata()
                                .addToLabels("resource_name",taskName)
                            .endMetadata()
                            .withNewSpec()
                                .withHostAliases(hostAliasArrayList)///变量
                                .withContainers(containerArrayList)
                                .withVolumes(volumeArrayList) /// 变量
                            .endSpec()
                        .endTemplate()
                    .endSpec()
                    .build();
            k8s.createDeployment(v1beta1Deployment);


            int portExits = 0;
            for(V1Container containerEach:v1beta1Deployment.getSpec().getTemplate().getSpec().getContainers()){
                if(containerEach.getPorts().toString().length()>0){
                    portExits =1;
                }
            }
            if(portExits==1){
                k8s.createService(v1Service);
            }
        }
    }
/****************************************************************/
/*
    public Object containerPort() {
        V1ResourceRequirements v1ResourceRequirements = new V1ResourceRequirements();
        V1Container v1Container = new V1ContainerBuilder()
                .withName(taskName)
                .withImage("k8sHarborImage")
                .withPorts()
                .withVolumeMounts()
                .withEnv()
                .withArgs()
                .withCommand()
                .withResources(v1ResourceRequirements)
                .withWorkingDir("workingDir") //
                .build();
        for (int i=0;i < containerList.toArray().length;i++){
            JSONObject container = containerList.getJSONObject(i);
            if(notBlank(container.getString("image_ports"))){
                for(int j =0;j<container.getString("image_port").length();j++){
                    String[] portList = container.getString("image_port").split(",");
                    v1Container = new V1ContainerBuilder()
                            .withName(portList[j].split(":")[0])
                            .addToPorts(new V1ContainerPort().containerPort(Integer.valueOf(portList[j].split(":")[1])))
                            .build();
                }
            }
        }
        return v1Container;
    } */

//    @PostMapping(value = "/ehl/createNamespace")//创建namespace
//    public void createNamespace() throws Exception{
//        V1Namespace v1Namespace = new V1NamespaceBuilder()
//                .withApiVersion("v1")
//                .withKind("Namespace")
//                .withNewMetadata()
//                  .withName(nameSpace)
//                .endMetadata()
//                .build();
//        K8sRelated.createNamespace(v1Namespace);
//    }
//
//    @PostMapping(value = "/ehl/createService") //创建service
//    public V1Service createService(V1ServicePort v1ServicePort) throws Exception{
//        V1Service v1Service = new V1ServiceBuilder()
//                .withApiVersion("v1")
//                .withKind("Service")
//                .withNewMetadata()
//                  .withName(taskName)
//                  .withNamespace(nameSpace)
//                  .addToLabels("resource_name",taskName)
//                .endMetadata()
//                .withNewSpec()
//                  .withType("NodePort")
//                  .withPorts() // 变量
//                  .addToSelector("resource_name",taskName)
//                .endSpec()
//                .build();
//        return new CoreV1Api().createNamespacedService(nameSpace,v1Service,null,null, null);
//    }
//
//    @PostMapping(value = "/ehl/createDeployment") //创建deployment
//    public ExtensionsV1beta1Deployment v1beta1Deployment(V1Volume v1Volume) throws Exception{
//        /*
//        V1Container v1Container = new V1ContainerBuilder()
//                .withName(taskName)
//                .withImage("k8sImage")
//                .addToPorts()
//                .build();
//         */
//        ExtensionsV1beta1Deployment v1beta1Deployment = new ExtensionsV1beta1DeploymentBuilder()
//                .withApiVersion(apiVersion)
//                .withKind(kind)
//                .withNewMetadata()
//                  .withName(taskName)
//                  .withNamespace(nameSpace)
//                .endMetadata()
//                .withNewSpec()
//                  .withReplicas(replicas)
//                  .withNewTemplate()
//                    .withNewMetadata()
//                      .addToLabels("resource_name",taskName)
//                    .endMetadata()
//                    .withNewSpec()
//                      .withHostAliases()///变量
//                      .withContainers()///变量
//                      .withVolumes() /// 变量
//                    .endSpec()
//                  .endTemplate()
//                  .endSpec()
//                .build();
//        return new ExtensionsV1beta1Api().createNamespacedDeployment(nameSpace,v1beta1Deployment,null,
//                                                                    null,null);
//    }
    public static String getKind(String kind){
        HashMap<String,String> kindDict = new HashMap<>();
        kindDict.put("Deployment","apps/v1beta1");
        return kindDict.get(kind);
    }

}
