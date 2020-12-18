package com.example.demo.service;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.common.DisplayErrorCode;
import com.example.demo.common.RestfulEntity;
import com.example.demo.entity.*;
import com.example.demo.mapper.AlgoServiceMapper;
import com.example.demo.utils.CommonUtils;
import com.example.demo.utils.CreateYaml;
import com.example.demo.utils.K8sRelated;
import org.glassfish.jersey.model.internal.CommonConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@Transactional
public class algoServiceRelatedService {

    @Autowired
    private AlgoServiceMapper algoServiceMapper;
    @Value("${SCHEDULE_UNIT}")
    private String SCHEDULE_UNIT;
    @Autowired
    private CreateYaml createYaml;
    @Autowired
    private K8sRelated k8s;
    public RestfulEntity<JSONObject> algoServiceRun(AlgoServiceDto algoServiceDto) throws Exception {
        if(algoServiceDto==null){
            return RestfulEntity.getFailure(DisplayErrorCode.nullTask);
        }
        JSONObject ksvcYaml=new JSONObject();
        ksvcYaml.put("apiVersion","serving.knative.dev/v1");
        ksvcYaml.put("kind","Service");
        //metadata
        JSONObject metadataDict=new JSONObject();
        String serviceName=
                algoServiceDto.getServiceName().replace(":","-").replace("/",
                        "-").replace("_","-");
        metadataDict.put("name",serviceName);
        metadataDict.put("namespace",algoServiceDto.getNamespace());
        ksvcYaml.put("metadata",metadataDict);
        //spec.template
        JSONObject templateDict=new JSONObject();
        JSONObject spec=new JSONObject();
        Integer target=algoServiceDto.getTarget();
        String metric=algoServiceDto.getMetric();
        JSONObject annotationDict=new JSONObject();
        JSONObject metadata=new JSONObject();
        annotationDict.put("autoscaling.knative.dev/target",target);
        annotationDict.put("autoscaling.knative.dev/metric",metric);
        metadata.put("annotations",annotationDict);
        templateDict.put("metadata",metadata);
        JSONObject containerDict=new JSONObject();
        List<JSONObject>containerDictList=new ArrayList<>();
        JSONObject templateDictSpec=new JSONObject();
        String cpuRequests=algoServiceDto.getCpuRequest()+"m";
        String gpuRequests=algoServiceDto.getGpuRequest();
        String memRequests=algoServiceDto.getMemRequest()+"Mi";
        String imageName="ehlai.com/"+algoServiceDto.getImageName();
        String imageTag=algoServiceDto.getImageTag();
        String servicePort=algoServiceDto.getServicePort();
        String portType=algoServiceDto.getPortType();
        if(portType==null||portType.equals("")){
            portType="http1";
        }
        containerDict.put("image",imageName+":"+imageTag);
        JSONObject containerPort=new JSONObject();
        containerPort.put("containerPort",servicePort);
        containerPort.put("name",portType);
        List<JSONObject>ports=new ArrayList<>();
        ports.add(containerPort);
        containerDict.put("ports",ports);
        String imageConfig=algoServiceDto.getImageConfig();
        if(createYaml.notBlank(imageConfig)){
            for(String config:imageConfig.split(",")){
                String name=CommonUtils.getRandomStr();
                JSONObject volumeMount=new JSONObject();
                volumeMount.put("name",name);
                volumeMount.put("mountPath",config);
                volumeMount.put("subPath",config.split("/")[-1]);
                List<JSONObject>volumeMountList=new ArrayList<>();
                volumeMountList.add(volumeMount);
                containerDict.put("volumeMounts",volumeMountList);
                JSONObject volume=new JSONObject();
                volume.put("name",name);
                JSONObject configMap=new JSONObject();
                configMap.put("name","config");
                volume.put("configMap",configMap);
                List<JSONObject>volumesList=new ArrayList<>();
                volumesList.add(volume);
                templateDictSpec.put("volumes",volumesList);
            }
        }
        String env=algoServiceDto.getEnv();
        if(createYaml.notBlank(env)){
            JSONObject envJson=new JSONObject();
            List<JSONObject>envList=new ArrayList<>();
            for(String envItem:env.split(",")){
                Integer index=envItem.indexOf(":");
                String name=envItem.split(":")[0];
                String value=envItem.substring(index+1);
                envJson.put("name",name);
                envJson.put("value",value);
                envList.add(envJson);
            }

            containerDict.put("env",envList);
        }
        if(createYaml.notBlank(algoServiceDto.getArgs())){ ;
            List<String>argList=new ArrayList<>();
            for(String arg:algoServiceDto.getArgs().split(",")){
                argList.add(arg);
            }
            containerDict.put("args",argList);
        }
        String gpuType="aliyun.com/gpu-mem";
        if(gpuRequests!=null&&!gpuRequests.equals("0")){
            if(algoServiceDto.getRunPosition().equals("side")){
                gpuType="tpu.bitmain.com/bm1684";
            }else{
                if(Integer.parseInt(gpuRequests)<=8){
                    gpuType="aliyun.com/gpu-count";
                }else if(SCHEDULE_UNIT.equals("EiB")){
                    Integer gpu=Integer.parseInt(gpuRequests)/10;
                    gpuRequests=gpu.toString();
                    gpu=Integer.parseInt(gpuRequests)/10;
                    gpuRequests=gpu.toString();
                }
            }
        }
        JSONObject resourceDict=new JSONObject();
        JSONObject limits=new JSONObject();
        limits.put(gpuType,gpuRequests);
        limits.put("cpu",cpuRequests);
        limits.put("memory",memRequests);
        resourceDict.put("limits",limits);
        JSONObject requests = new JSONObject();
        requests.put(gpuType,gpuRequests);
        requests.put("cpu",cpuRequests);
        requests.put("memory",memRequests);
        resourceDict.put("requests",requests);
        containerDict.put("resources",resourceDict);
        containerDictList.add(containerDict);
        templateDictSpec.put("containers",containerDictList);
        templateDict.put("spec",templateDictSpec);
        spec.put("template",templateDict);
        ksvcYaml.put("spec",spec);
        String algoServiceId=algoServiceDto.getAlgoServiceId();
        if(algoServiceId==null||"".equals(algoServiceId)){
            algoServiceId=CommonUtils.getRandomStr();
        }
        String deleteFlag="0";
        String isStart="0";
        Integer taskCount=0;
        AlgoServiceDto algoServiceDto1=new AlgoServiceDto();
        algoServiceDto1.setServiceName(serviceName);
        List<AlgoService> algoServiceList=
                algoServiceMapper.queryAlgoServiceInfo(algoServiceDto1);
        AlgoServiceDto algoServiceInstance=new AlgoServiceDto();
        if(algoServiceList.size()==0){
            String imageId=algoServiceDto.getImageId();
            algoServiceInstance.setAlgoServiceId(algoServiceId);
            algoServiceInstance.setServiceName(serviceName);
            algoServiceInstance.setEnv(env);
            algoServiceInstance.setServicePort(servicePort);
            algoServiceInstance.setCpuRequest(cpuRequests);
            algoServiceInstance.setGpuRequest(gpuRequests);
            algoServiceInstance.setMemRequest(memRequests);
            algoServiceInstance.setImageName(imageName);
            algoServiceInstance.setDeleteFlag(deleteFlag);
            algoServiceInstance.setMetric(metric);
            algoServiceInstance.setTarget(target);
            algoServiceInstance.setIsStart(isStart);
            algoServiceInstance.setPortType(portType);
            algoServiceInstance.setImageId(imageId);
            algoServiceInstance.setTaskCount(taskCount);
            algoServiceMapper.insertAlgoService(algoServiceInstance);
        }else if(algoServiceList.get(0).getIsStart().equals("1")){
            AlgoServiceDto algoServiceDto2=new AlgoServiceDto();
            algoServiceDto2.setServiceName(serviceName);
            AlgoService algoService=
                    algoServiceMapper.queryAlgoServiceInfo(algoServiceDto2).get(0);
            Integer taskCountNum=algoService.getTaskCount();
            algoServiceDto2.setTaskCount(taskCountNum+1);
            algoServiceMapper.updateTaskCount(algoServiceDto2);
            JSONObject result=new JSONObject();
            result.put("service_name",serviceName);
            return RestfulEntity.getSuccess(result,"算法服务处于开启状态，无需重复开启");
        }
        k8s.ksvcCreate(algoServiceDto.getNamespace(),ksvcYaml);
        AlgoServiceDto algoServiceDto2=new AlgoServiceDto();
        algoServiceDto2.setServiceName(serviceName);
        algoServiceDto2.setIsStart("1");
        algoServiceMapper.updateStartStatus(algoServiceDto2);
        AlgoServiceDto algoServiceDto3=new AlgoServiceDto();
        algoServiceDto3.setServiceName(serviceName);
        AlgoService algoService=
                algoServiceMapper.queryAlgoServiceInfo(algoServiceDto3).get(0);
        Integer taskCountNum=algoService.getTaskCount();
        algoServiceDto3.setTaskCount(taskCountNum+1);
        algoServiceMapper.updateTaskCount(algoServiceDto3);
        JSONObject res=new JSONObject();
        res.put("service_name",serviceName);
        for(int i=0;i<15;i++){
            if(k8s.getKsvcStatus(algoServiceDto.getNamespace(),serviceName)){
                JSONObject result=new JSONObject();
                result.put("service_name",serviceName);
                return RestfulEntity.getSuccess(result,"算法服务已就绪");
            }
            Thread.sleep(1000*2);
        }
        return RestfulEntity.getSuccess(res,"成功开启算法服务");
    }

    public RestfulEntity<JSONObject>algoServiceDelete(AlgoServiceDto algoServiceDto) throws Exception {
        String serviceName=
                algoServiceDto.getServiceName().replace(":","-").replace("/",
                        "-").replace("_","-");
        AlgoServiceDto algoServiceDto1=new AlgoServiceDto();
        algoServiceDto1.setAlgoServiceId(algoServiceDto1.getServiceId());
        List<AlgoService> algoService=
                algoServiceMapper.queryAlgoServiceInfo(algoServiceDto);
        AlgoService algoServiceResult=algoService.get(0);
        if(algoServiceResult.getTaskCount()<=1){
            k8s.deleteKsvc(serviceName,algoServiceDto.getNamespace());
            AlgoServiceDto algoServiceDto2=new AlgoServiceDto();
            algoServiceDto2.setServiceName(serviceName);
            algoServiceDto2.setStatus("0");
            algoServiceMapper.updateStartStatus(algoServiceDto2);
        }
       AlgoServiceDto algoServiceDto3=new AlgoServiceDto();
        algoServiceDto3.setServiceName(serviceName);
        algoServiceDto3.setOption(-1);
        algoServiceMapper.updateTaskCount(algoServiceDto3);
        JSONObject result=new JSONObject();
        result.put("serviceName",serviceName);
        return RestfulEntity.getFailure(result,"算法服务关闭接口调用成功");
    }

}
