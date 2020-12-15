package com.example.demo.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.entity.Port;
import com.google.gson.Gson;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.apis.*;
import io.kubernetes.client.custom.Quantity;
import io.kubernetes.client.models.*;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author User
 */
@Component
public class K8sRelated {

    public void createNamespace(V1Namespace v1Namespace) throws ApiException {
        CoreV1Api v1 = new CoreV1Api();
        V1NamespaceList namespaceList = v1.listNamespace(null, null, null, null,
                null, null, null, null, null);
        int flag =0;
        for(V1Namespace namespace:namespaceList.getItems()){
            if(v1Namespace.getMetadata().getName().equals(namespace.toString())){
               flag=1;
            }
        }
        if(flag == 0){
            v1.createNamespace(v1Namespace,null,null,null);
        }
    }

    public void createService(V1Service v1Service) throws ApiException {
        CoreV1Api v1 = new CoreV1Api();
        v1.createNamespacedService(v1Service.getMetadata().getNamespace(),v1Service,null,null,null);
    }

    public void createDeployment(AppsV1beta1Deployment appsV1beta1Deployment)
            throws ApiException {
        AppsV1beta1Api k8sAppV1Beta =new AppsV1beta1Api();
        AppsV1beta1DeploymentList deploymentList = k8sAppV1Beta.listNamespacedDeployment(
                appsV1beta1Deployment.getMetadata().getNamespace(),null,null,
                null, null,null,null,null,null,
        null);
        for(AppsV1beta1Deployment deployment:deploymentList.getItems()){
            if(appsV1beta1Deployment.getMetadata().getName().equals(deployment.getMetadata().getName())){
                System.out.print("该任务已添加至启动计划");
            }
        }
        k8sAppV1Beta.createNamespacedDeployment(appsV1beta1Deployment.getMetadata().getNamespace(),appsV1beta1Deployment,null,
                null,null);
    }

    public void deleteService(String namespace,String taskName) throws ApiException {
        CoreV1Api v1 = new CoreV1Api();
        V1ServiceList serviceList = v1.listNamespacedService(namespace,null,null,
                null,null,null,null,null,null
        ,null);
        for(V1Service service:serviceList.getItems()){
            if(service.getMetadata().getName().equals(taskName)){
                v1.deleteNamespacedService(taskName,namespace,null,null,null,
                        null,null,null);
            }
        }
    }

    public void deleteDeployment(String namespace,String taskName) throws ApiException {
        AppsV1beta1Api k8sAppV1Beta =new AppsV1beta1Api();
        AppsV1Api v1 =new AppsV1Api();
        CoreV1Api v1Core =new CoreV1Api();
        AppsV1beta1DeploymentList deploymentList = k8sAppV1Beta.listNamespacedDeployment(namespace,
                null,null,null,null,null,null,
                null,null,null);
        V1ReplicaSetList replicaSetList = v1.listNamespacedReplicaSet(namespace,null,null,
                null,null,null,null,null,null,
                null);
        V1PodList podList = v1Core.listNamespacedPod(namespace,null,null,null,
                null,null,null,null,null,null);

        for(AppsV1beta1Deployment deployment:deploymentList.getItems()){
            if(deployment.getMetadata().getName().equals(taskName)){
                k8sAppV1Beta.deleteNamespacedDeployment(taskName,namespace,null,null,null,
                        null,null,null);
            }
        }

        for(V1ReplicaSet replicaSet:replicaSetList.getItems()){
            if(replicaSet.getMetadata().getLabels().containsKey("resource_name")){
                if(replicaSet.getMetadata().getLabels().get("resource_name").equals(taskName)){
                    String repName = replicaSet.getMetadata().getName();
                    v1.deleteNamespacedReplicaSet(repName,namespace,null,null,
                            null,null,null,null);
                }
            }
        }

        for(V1Pod pod:podList.getItems()){
            if(pod.getMetadata().getLabels().containsKey("resource_name")){
                if(pod.getMetadata().getLabels().get("resource_name").equals(taskName)){
                    String podName = pod.getMetadata().getName();
                    v1Core.deleteNamespacedPod(podName,namespace,null,null,null,
                            null,null,null);
                }
            }
        }

    }

    public JSONObject changeDeployment(String deploymentName, String namespace, int replica) throws ApiException {
        JSONObject changeInfo = new JSONObject();
        ExtensionsV1beta1Api k8sExtensionV1Beta1 = new ExtensionsV1beta1Api();
        ExtensionsV1beta1Scale deployInfo = k8sExtensionV1Beta1.readNamespacedDeploymentScale(deploymentName, namespace,
                null);
        ExtensionsV1beta1ScaleSpec spec = new ExtensionsV1beta1ScaleSpec().replicas(replica);
        ExtensionsV1beta1Scale body = new ExtensionsV1beta1Scale().apiVersion("extensions/v1beta1").kind("Deployment")
                .metadata(deployInfo.getMetadata()).spec(spec);
        k8sExtensionV1Beta1.patchNamespacedDeploymentScale(deploymentName,namespace,body,null,null);
        changeInfo.put("deployment",deploymentName);
        changeInfo.put("namespace",namespace);
        changeInfo.put("replica",replica);
        return changeInfo;

    }

    public JSONObject getMetrics() throws ApiException {
        JSONObject metricsDict =new JSONObject();
       int podsU = new CoreV1Api().listPodForAllNamespaces(null,null,null,
               null,null,null,null,null,null).
               getItems().size();
       int pods =0;
       V1NodeList nodeList=new CoreV1Api().listNode(null,null,null,null,
               null,null,null,null,null);
       String version = nodeList.getItems().get(0).getStatus().getNodeInfo().getKernelVersion();
       int nodes = nodeList.getItems().size();
       int cpu = 0;
       int memoryKiB =0;
       for (V1Node node:nodeList.getItems()){
           cpu+=Integer.parseInt(node.getStatus().getCapacity().get("cpu").toString());
           String m = node.getStatus().getCapacity().get("memory").toString();
           memoryKiB +=Integer.parseInt(m.substring(0,m.length()-2));
           pods+= Integer.parseInt(node.getStatus().getCapacity().get("pods").toString());
       }
       double memory = Math.round(memoryKiB*1.0/1024/1024);
       metricsDict.put("version",version);
       metricsDict.put("nodes",nodes);
       metricsDict.put("cpu",cpu);
       metricsDict.put("memory",memory);
       metricsDict.put("pods",pods);
       metricsDict.put("podsU",podsU);
       return metricsDict;
    }

    public JSONObject getTaskStatus(String namespace, String taskName, String useType) throws ApiException {
        CoreV1Api v1 =new CoreV1Api();
        V1PodList podList = v1.listNamespacedPod(namespace,null,null,null,
                null,null,null,null,null,null);
        int replicasCount = 0;
        int normalRunning = 0;
        int pendingCount = 0;
        JSONObject statusDict = new JSONObject();
        JSONArray statusList = new JSONArray();
        for(V1Pod pod:podList.getItems()){
            if(pod.getMetadata().getLabels().containsKey("resource_name")){
                if(pod.getMetadata().getLabels().get("resource_name").equals(taskName)){
                    replicasCount +=1;
                    if(!pod.getStatus().getContainerStatuses().isEmpty()){
                        for(V1ContainerStatus containerStatus:pod.getStatus().getContainerStatuses()){
                            if(!containerStatus.getState().getRunning().toString().isEmpty()){
                                normalRunning+=1;
                            }
                        }
                    }
                    else {
                        pendingCount+=1;
                    }
                }
            }
        }
        if(replicasCount == 0){
            statusDict.put("running","此任务未运行");
            return statusDict;
        }
        if(useType.equals("task_run")){
            if(normalRunning==0){
                statusDict.put("running","已添加至启动计划，暂未启动成功，可查询获取最新状态");
            }
            else{
                statusDict.put("running","任务启动情况:"+ normalRunning + "/" + replicasCount);
            }
        }
        if(useType.equals("task_status")){
            if(normalRunning == 0){
                statusDict.put("running","任务暂未启动成功，可查询获取最新状态");
            }
            else{
                statusDict.put("running","任务启动情况:"+normalRunning+"/"+replicasCount);
            }
        }
        if(pendingCount>0){
            JSONObject status =new JSONObject();
            status.put("task_name",taskName);
            status.put("status",pendingCount+"/"+replicasCount+ "服务器资源不足，进入等待队列");
            statusList.add(status);
            statusDict.put("pending",statusList);
        }
        return statusDict;
    }

    public List<Port> getServicePorts(String namespace,
                                      String taskName) throws ApiException {
        CoreV1Api v1 =new CoreV1Api();
        V1ServiceList serviceList = v1.listNamespacedService(namespace,null,null,
                null,null,null,null,null,null,
                null);
        List<Port>portList=new ArrayList<>();
        for(V1Service service:serviceList.getItems()){
            if(service.getMetadata().getName().equals(taskName)){
                for(V1ServicePort port:service.getSpec().getPorts()){
                    Port port1=new Port();
//                    JSONObject portsDict =new JSONObject();
                    port1.setPortName(port.getName());
                    port1.setPortNode(port.getNodePort());
                    port1.setPortTarget(port.getTargetPort());
                    portList.add(port1);
                    break;
                }
            }
        }
        return portList;
    }

    public void deleteSvc(String namespace,String taskName) throws ApiException {
        CoreV1Api v1=new CoreV1Api();
        V1ServiceList ret = v1.listNamespacedService(namespace,null,null,null,null,
                null,null,null,null,null);
        for(V1Service service:ret.getItems()){
            if(service.getMetadata().getName().equals(taskName)){
                v1.deleteNamespacedService(taskName,namespace,null,null,null,null,
                        null,null);
            }
        }
    }

    public void applyYaml(JSONObject yml) throws ApiException {

//        JSONObject apiMap =new JSONObject();
//        JSONArray deploymentList = new JSONArray();
//        JSONArray serviceList = new JSONArray();

//        JSONArray ingressList  = new JSONArray();
//        JSONArray daemonSetList =new JSONArray();
//        JSONArray statefulSetList = new JSONArray();
//        JSONArray configMapList = new JSONArray();
//        JSONArray secretList =new JSONArray();
//        deploymentList.add(new AppsV1Api().createNamespacedDeployment(null,null,null
//        ,null,null));
//        deploymentList.add((new AppsV1Api().replaceNamespacedDeployment(null,null,null,
//                null,null)));
//        deploymentList.add(new AppsV1Api().listNamespacedDeployment(null,null,null,
//                null,null,null,null,null,null,
//                null));
//
//        serviceList.add(new CoreV1Api().createNamespacedService(null,null,null,
//                null,null));
//        serviceList.add(new CoreV1Api().replaceNamespacedService(null,null,null,null,
//                null));
//        serviceList.add(new CoreV1Api().listNamespacedService(null,null,null,
//                null,null,null,null,null,null,
//                null));

//        ingressList.add(new ExtensionsV1beta1Api().createNamespacedIngress(null,null,
//                ,null,null));
//        ingressList.add(new ExtensionsV1beta1Api().replaceNamespacedIngress(null,null,null,
//                null,null));
//        ingressList.add(new ExtensionsV1beta1Api().listNamespacedIngress(null,null,
//                null,null,null,null,null,null,
//                null,null));
//
//        apiMap.put("deployment",deploymentList);
//        apiMap.put("service",serviceList);
//        apiMap.put("ingress",ingressList);


//        JSONArray listAll = apiMap.getJSONArray(yml.getString("kind").toLowerCase());
//        JSONObject create =  listAll.getJSONObject(0);
//        JSONObject replace = listAll.getJSONObject(1);
//        JSONObject list = listAll.getJSONObject(2);
        String kindName = yml.getString("kind").toLowerCase();
        String namespace = yml.getJSONObject("metadata").containsKey("namespace")? yml.getJSONObject("metadata").
                getString("namespace"):"default";
        String name =yml.getJSONObject("metadata").getString("name");

        if(kindName.equals("deployment")){
            AppsV1Api v1 =new AppsV1Api();
            V1Deployment ymlToDeployment = JSONObject.parseObject(String.valueOf(yml),V1Deployment.class);
            V1DeploymentList res = v1.listNamespacedDeployment(namespace,null,null,
                    null,null,null,null,null,
                    null, null);
            for(V1Deployment deployment :res.getItems()){
                if(deployment.getMetadata().getName().equals(name)){
                    v1.replaceNamespacedDeployment(name,namespace,ymlToDeployment,null,null);
                    break;
                }
                else{
                    v1.createNamespacedDeployment(namespace,ymlToDeployment,null,null,null);
                }
            }
        }
        if(kindName.equals("service")){
            CoreV1Api v1 =new CoreV1Api();
            V1Service ymlToService = JSONObject.parseObject(String.valueOf(yml),V1Service.class);
            V1ServiceList res = v1.listNamespacedService(namespace,null,null,null,
                    null,null,null,null,null,null);
            for(V1Service service:res.getItems()){
                if(service.getMetadata().getName().equals(name)){
                    v1.replaceNamespacedService(name,namespace,ymlToService,null,null);
                    break;
                }
                else{
                    v1.createNamespacedService(namespace,ymlToService,null,null,null);
                }
            }
        }
        if(kindName.equals("ingress")){
            ExtensionsV1beta1Api v1 =new ExtensionsV1beta1Api();
            V1beta1Ingress ymlToIngress = JSONObject.parseObject(String.valueOf(yml),V1beta1Ingress.class);
            V1beta1IngressList res = v1.listNamespacedIngress(namespace,null,null,null,
                    null,null,null,null,null,null);
            for(V1beta1Ingress ingress:res.getItems()){
                if(ingress.getMetadata().getName().equals(name)){
                    v1.replaceNamespacedIngress(name,namespace,ymlToIngress,null,null);
                    break;
                }
                else{
                    v1.createNamespacedIngress(namespace,ymlToIngress,null,null,null);
                }
            }
        }
        if(kindName.equals("daemonset")){
            AppsV1Api v1 =new AppsV1Api();
            V1DaemonSet ymlToDaemonset = JSONObject.parseObject(String.valueOf(yml),V1DaemonSet.class);
            V1DaemonSetList res = v1.listNamespacedDaemonSet(namespace,null,null,null,
                    null,null,null,null,null,null);
            for(V1DaemonSet daemonSet:res.getItems()){
                if(daemonSet.getMetadata().getName().equals(name)){
                    v1.replaceNamespacedDaemonSet(name,namespace,ymlToDaemonset,null,null);
                    break;
                }
                else{
                    v1.createNamespacedDaemonSet(namespace,ymlToDaemonset,null,null,null);
                }
            }
        }
        if(kindName.equals("statefulset")){
            AppsV1Api v1 =new AppsV1Api();
            V1StatefulSet ymlToStatefulSet = JSONObject.parseObject(String.valueOf(yml),V1StatefulSet.class);
            V1StatefulSetList res = v1.listNamespacedStatefulSet(namespace,null,null,null,
                    null,null,null,null,null,null);
            for(V1StatefulSet statefulSet:res.getItems()){
                if(statefulSet.getMetadata().getName().equals(name)){
                    v1.replaceNamespacedStatefulSet(name,namespace,ymlToStatefulSet,null,null);
                    break;
                }
                else{
                    v1.createNamespacedStatefulSet(namespace,ymlToStatefulSet,null,null,null);
                }
            }
        }
        if(kindName.equals("configmap")){
            CoreV1Api v1 =new CoreV1Api();
            V1ConfigMap ymlToConfigMap = JSONObject.parseObject(String.valueOf(yml),V1ConfigMap.class);
            V1ConfigMapList res = v1.listNamespacedConfigMap(namespace,null,null,null,
                    null,null,null,null,null,null);
            for(V1ConfigMap configMap:res.getItems()){
                if(configMap.getMetadata().getName().equals(name)){
                    v1.replaceNamespacedConfigMap(name,namespace,ymlToConfigMap,null,null);
                    break;
                }
                else{
                    v1.createNamespacedConfigMap(namespace,ymlToConfigMap,null,null,null);
                }
            }
        }
        if(kindName.equals("secret")){
            CoreV1Api v1 =new CoreV1Api();
            V1Secret ymlToSecret = JSONObject.parseObject(String.valueOf(yml),V1Secret.class);
            V1SecretList res = v1.listNamespacedSecret(namespace,null,null,null,
                    null,null,null,null,null,null);
            for(V1Secret secret:res.getItems()){
                if(secret.getMetadata().getName().equals(name)){
                    v1.replaceNamespacedSecret(name,namespace,ymlToSecret,null,null);
                    break;
                }
                else{
                    v1.createNamespacedSecret(namespace,ymlToSecret,null,null,null);
                }
            }
        }



    }

    public void createPatchConfigMap(JSONObject yml) throws ApiException {
        CoreV1Api v1 =new CoreV1Api();
        String name = yml.getJSONObject("metadata").getString("name");
        String namespace = yml.getJSONObject("metadata").containsKey("namespace")? yml.getJSONObject("metadata").
                getString("namespace"):"default";
        V1ConfigMapList configMapList = new CoreV1Api().listNamespacedConfigMap(namespace,null,
                null, null,null,null,null,null,
                null, null);
        HashMap<String,String> data =new HashMap<>();
        data.put(yml.getJSONObject("data").keySet().toString(),yml.getJSONObject("data").values().toString());
        V1ConfigMap v1ConfigMap =new V1ConfigMapBuilder()
                .withApiVersion("v1")
                .withKind("ConfigMap")
                .withNewMetadata()
                    .withName(yml.getJSONObject("metadata").getString("name"))
                    .withNamespace(yml.getJSONObject("metadata").getString("namespace"))
                .endMetadata()
                .withData(data)
                .build();
        for(V1ConfigMap configMap:configMapList.getItems()){
            if(configMap.getMetadata().getName().equals(name)){
                v1.patchNamespacedConfigMap(name,namespace,configMap,null,null);
            }
            break;
        }
        v1.createNamespacedConfigMap(namespace,  v1ConfigMap,null,null,null);

    }
    public ArrayList<String> listNamespaceConfigMap(String namespace) throws ApiException {
        ArrayList<String> list =new ArrayList<>();
        V1ConfigMapList configMapList=new CoreV1Api().listNamespacedConfigMap(namespace,null,null,
                null,null,null,null,null,null,
                null);
        for(V1ConfigMap configMap:configMapList.getItems()){
            list.add(configMap.getMetadata().getName());
        }
        return list;
    }

    public void createPVC(String namespace,String name,int size) throws ApiException {
        CoreV1Api v1 =new CoreV1Api();
        HashMap<String,String> annotation = new HashMap<>();
        ArrayList<String> accessModes =new ArrayList<>();
        annotation.put("volume.beta.kubernetes.io/storage-class","data-delete");
        accessModes.add("ReadWriteMany");
        V1PersistentVolumeClaim body = new V1PersistentVolumeClaimBuilder()
                .withApiVersion("v1")
                .withKind("PersistentVolumeClaim")
                .withNewMetadata()
                    .withName(name)
                    .withNamespace(namespace)
                    .withAnnotations(annotation)
                .endMetadata()
                .withNewSpec()
                    .withAccessModes(accessModes)
                    .withNewResources()
                        .addToRequests("storage", Quantity.fromString(size+"Gi"))
                    .endResources()
                .endSpec()
                .build();
        v1.createNamespacedPersistentVolumeClaim(namespace,body,null,null,null);
    }

    public void deletePVC(String name,String namespace) throws ApiException {
        CoreV1Api v1 =new CoreV1Api();
        v1.deleteNamespacedPersistentVolumeClaim(name,namespace,null,null,null,
                null,null,null);

    }

    public ArrayList<String> listNamespacedPVC(String namespace) throws ApiException {
        CoreV1Api v1 =new CoreV1Api();
        ArrayList<String> list=new ArrayList<>();
        V1PersistentVolumeClaimList persistentVolumeClaimList = v1.listNamespacedPersistentVolumeClaim(namespace, null, null, null,
                null, null, null, null, null, null);
        for(V1PersistentVolumeClaim persistentVolumeClaim:persistentVolumeClaimList.getItems()){
            list.add(persistentVolumeClaim.getMetadata().getName());
        }
        return list;
    }

    public V1ConfigMap configMapCreate(String filePath, String namespace, String configName) throws IOException,
            ApiException {
        CoreV1Api apiInstance =new CoreV1Api();
        InputStream is = new FileInputStream(filePath);
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        StringBuffer buffer =new StringBuffer();
        String line = "";
        while((line=in.readLine()) !=null){
            buffer.append(line);
        }
        HashMap<String,String> data =new HashMap<>();
        data.put(filePath,buffer.toString());
        V1ConfigMap body = new V1ConfigMapBuilder()
                .withData(data)
                .withNewMetadata()
                    .withName(configName)
                .endMetadata()
                .build();

        return apiInstance.createNamespacedConfigMap(namespace,body,null,
                null,null);
    }

    public V1Status configMapDelete(String namespace, String configName) throws ApiException {
        CoreV1Api apiInstance = new CoreV1Api();
        return apiInstance.deleteNamespacedConfigMap(configName,namespace,null,null,null,
                null,null,null);
    }

    public Boolean getKsvcStatus(String namespace,String name) throws ApiException {
        CustomObjectsApi api =new CustomObjectsApi();
        Gson gson =new Gson();
        boolean status;
        Object ksvc = api.getNamespacedCustomObjectStatus("serving.knative.dev", "v1", namespace, "service",
                name);
        try{
            String jsonObject = gson.toJson(ksvc);
            JSONObject  res = JSONObject.parseObject(jsonObject);
            String statusTemp = res.getJSONObject("status").getJSONArray("conditions").getJSONObject(1).
                    getString("status");
            status = statusTemp.equals("True");
        } catch (Exception e){
            status = false;
        }
        return status;
    }

    public void ksvcCreate(String namespace,JSONObject ksvcYml) throws Exception {
        String serviceName = ksvcYml.getJSONObject("metadata").getString("name");
        CustomObjectsApi api =new CustomObjectsApi();
        boolean ksvcStatus = false;
        boolean exists = true;
        Object apiResponce=new Object();
        try{
            ksvcStatus =getKsvcStatus(namespace,serviceName);
        } catch (ApiException e) {
            e.printStackTrace();
            if(e.getCode()!=404){
                throw new Exception("获取算法服务时发生错误：" + e.toString());
            }
            else{
                exists =false;
            }
        }
        if(ksvcStatus){
            System.out.println("算法服务已存在,无需重复开启");
        }
        try{
            if(exists){
                apiResponce = api.patchNamespacedCustomObject("serving.knative.dev", "v1", namespace,
                        "services", serviceName, ksvcYml);
            }
            else {
                apiResponce = api.createNamespacedCustomObject("serving.knative.dev", "v1",namespace,
                        "services",ksvcYml,null);
            }
            System.out.println(apiResponce);
        } catch (Exception e){
            throw new Exception("创建算法服务时发生错误： " + e.toString());
        }

    }

    public Object listKsvc(String namespace) throws ApiException {
        CustomObjectsApi api=new CustomObjectsApi();
        return api.listNamespacedCustomObject("serving.knative.dev","v1",namespace,"services",
                null,null,null,null,null);
    }

    public void deleteKsvc(String serviceName,String namespace) throws Exception {
        CustomObjectsApi api =new CustomObjectsApi();
        try{
            getKsvcStatus(namespace,serviceName);
        } catch (ApiException e) {
            if(e.getCode()==404){
                throw new Exception("该算法服务已不存在，无法删除。");
            }
            else {
                throw new  Exception("获取算法服务时发生错误：" + e.toString());
            }
        }

        try{
            Object apiResponse = api.deleteNamespacedCustomObject("serving.knative.dev", "v1", namespace,
                    "services", serviceName, null, null, null,
                    null);
        }catch (Exception e){
            //System.out.println(e);
            throw new Exception("删除算法服务时发生异常： " + e.toString());
        }

    }



}
