package com.example.demo.entity;

public class AlgoServiceDto {
    private String serviceId;
    private String serviceName;
    private String env;
    private String servicePort;
    private String portType;
    private String cpuRequest;
    private String gpuRequest;
    private String memRequest;
    private String imageMount;
    private String imageName;
    private String deleteFlag;
    private String config;
    private String eventType;
    private String metric;
    private Integer target;
    private String isStart;
    private String imageId;
    private Integer taskCount;
    private String imageTag;
    private String status;
    private Integer option;
    private String subpath;
    private String hostpath;
    private String imagePort;
    private String args;
    private String algoServiceId;
    private String imageConfig;
    private String taskId;
    private String namespace;
    private String runPosition;

    public String getRunPosition() {
        return runPosition;
    }

    public void setRunPosition(String runPosition) {
        this.runPosition = runPosition;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public String getServicePort() {
        return servicePort;
    }

    public void setServicePort(String servicePort) {
        this.servicePort = servicePort;
    }

    public String getPortType() {
        return portType;
    }

    public void setPortType(String portType) {
        this.portType = portType;
    }

    public String getCpuRequest() {
        return cpuRequest;
    }

    public void setCpuRequest(String cpuRequest) {
        this.cpuRequest = cpuRequest;
    }

    public String getGpuRequest() {
        return gpuRequest;
    }

    public void setGpuRequest(String gpuRequest) {
        this.gpuRequest = gpuRequest;
    }

    public String getMemRequest() {
        return memRequest;
    }

    public void setMemRequest(String memRequest) {
        this.memRequest = memRequest;
    }

    public String getImageMount() {
        return imageMount;
    }

    public void setImageMount(String imageMount) {
        this.imageMount = imageMount;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(String deleteFlag) {
        this.deleteFlag = deleteFlag;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }

    public Integer getTarget() {
        return target;
    }

    public void setTarget(Integer target) {
        this.target = target;
    }

    public String getIsStart() {
        return isStart;
    }

    public void setIsStart(String isStart) {
        this.isStart = isStart;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public Integer getTaskCount() {
        return taskCount;
    }

    public void setTaskCount(Integer taskCount) {
        this.taskCount = taskCount;
    }

    public String getImageTag() {
        return imageTag;
    }

    public void setImageTag(String imageTag) {
        this.imageTag = imageTag;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getOption() {
        return option;
    }

    public void setOption(Integer option) {
        this.option = option;
    }

    public String getSubpath() {
        return subpath;
    }

    public void setSubpath(String subpath) {
        this.subpath = subpath;
    }

    public String getHostpath() {
        return hostpath;
    }

    public void setHostpath(String hostpath) {
        this.hostpath = hostpath;
    }

    public String getImagePort() {
        return imagePort;
    }

    public void setImagePort(String imagePort) {
        this.imagePort = imagePort;
    }

    public String getArgs() {
        return args;
    }

    public void setArgs(String args) {
        this.args = args;
    }

    public String getAlgoServiceId() {
        return algoServiceId;
    }

    public void setAlgoServiceId(String algoServiceId) {
        this.algoServiceId = algoServiceId;
    }

    public String getImageConfig() {
        return imageConfig;
    }

    public void setImageConfig(String imageConfig) {
        this.imageConfig = imageConfig;
    }

    @Override
    public String toString() {
        return "AlgoServiceDto{" +
                "serviceId='" + serviceId + '\'' +
                ", serviceName='" + serviceName + '\'' +
                ", env='" + env + '\'' +
                ", servicePort='" + servicePort + '\'' +
                ", portType='" + portType + '\'' +
                ", cpuRequest='" + cpuRequest + '\'' +
                ", gpuRequest='" + gpuRequest + '\'' +
                ", memRequest='" + memRequest + '\'' +
                ", imageMount='" + imageMount + '\'' +
                ", imageName='" + imageName + '\'' +
                ", deleteFlag='" + deleteFlag + '\'' +
                ", config='" + config + '\'' +
                ", eventType='" + eventType + '\'' +
                ", metric='" + metric + '\'' +
                ", target=" + target +
                ", isStart='" + isStart + '\'' +
                ", imageId='" + imageId + '\'' +
                ", taskCount=" + taskCount +
                ", imageTag='" + imageTag + '\'' +
                ", status='" + status + '\'' +
                ", option=" + option +
                ", subpath='" + subpath + '\'' +
                ", hostpath='" + hostpath + '\'' +
                ", imagePort='" + imagePort + '\'' +
                ", args='" + args + '\'' +
                ", algoServiceId='" + algoServiceId + '\'' +
                ", imageConfig='" + imageConfig + '\'' +
                ", taskId='" + taskId + '\'' +
                ", namespace='" + namespace + '\'' +
                ", runPosition='" + runPosition + '\'' +
                '}';
    }
}
