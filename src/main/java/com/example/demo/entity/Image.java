package com.example.demo.entity;

public class Image {
    private String imageId;
    private String imageName;
    private String imageTag;
    private String imagePorts;
    private String imageMount;
    private Integer pvcSize;
    private String factory;
    private String algoName;
    private String algoDesc;
    private String deleteFlag;
    private Integer tvChannel;
    private Integer pics;
    private Integer cpuRequests;
    private Integer gpuRequests;
    private Integer memRequests;
    private String config;
    private String algoType;
    private Integer eventType;
    private String subPath;
    private String hostPath;
    private String workingDir;
    private String env;
    private String command;
    private String args;
    private String dependencyServiceId;
    private Integer frameInterval;
    private Integer frameNumber;
    private String incidentId;
    private String useableFlag;

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }


    public String getImagePorts() {
        return imagePorts;
    }

    public void setImagePorts(String imagePorts) {
        this.imagePorts = imagePorts;
    }

    public String getImageMount() {
        return imageMount;
    }

    public void setImageMount(String imageMount) {
        this.imageMount = imageMount;
    }

    public Integer getPvcSize() {
        return pvcSize;
    }

    public void setPvcSize(Integer pvcSize) {
        this.pvcSize = pvcSize;
    }

    public String getFactory() {
        return factory;
    }

    public void setFactory(String factory) {
        this.factory = factory;
    }

    public String getAlgoName() {
        return algoName;
    }

    public void setAlgoName(String algoName) {
        this.algoName = algoName;
    }

    public String getAlgoDesc() {
        return algoDesc;
    }

    public void setAlgoDesc(String algoDesc) {
        this.algoDesc = algoDesc;
    }

    public String getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(String deleteFlag) {
        this.deleteFlag = deleteFlag;
    }

    public Integer getTvChannel() {
        return tvChannel;
    }

    public void setTvChannel(Integer tvChannel) {
        this.tvChannel = tvChannel;
    }

    public Integer getPics() {
        return pics;
    }

    public void setPics(Integer pics) {
        this.pics = pics;
    }

    public Integer getCpuRequests() {
        return cpuRequests;
    }

    public void setCpuRequests(Integer cpuRequests) {
        this.cpuRequests = cpuRequests;
    }

    public Integer getGpuRequests() {
        return gpuRequests;
    }

    public void setGpuRequests(Integer gpuRequests) {
        this.gpuRequests = gpuRequests;
    }

    public Integer getMemRequests() {
        return memRequests;
    }

    public void setMemRequests(Integer memRequests) {
        this.memRequests = memRequests;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public String getAlgoType() {
        return algoType;
    }

    public void setAlgoType(String algoType) {
        this.algoType = algoType;
    }

    public Integer getEventType() {
        return eventType;
    }

    public void setEventType(Integer eventType) {
        this.eventType = eventType;
    }

    public String getSubPath() {
        return subPath;
    }

    public void setSubPath(String subPath) {
        this.subPath = subPath;
    }

    public String getHostPath() {
        return hostPath;
    }

    public void setHostPath(String hostPath) {
        this.hostPath = hostPath;
    }

    public String getWorkingDir() {
        return workingDir;
    }

    public void setWorkingDir(String workingDir) {
        this.workingDir = workingDir;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getArgs() {
        return args;
    }

    public void setArgs(String args) {
        this.args = args;
    }

    public String getDependencyServiceId() {
        return dependencyServiceId;
    }

    public void setDependencyServiceId(String dependencyServiceId) {
        this.dependencyServiceId = dependencyServiceId;
    }

    public Integer getFrameInterval() {
        return frameInterval;
    }

    public void setFrameInterval(Integer frameInterval) {
        this.frameInterval = frameInterval;
    }

    public Integer getFrameNumber() {
        return frameNumber;
    }

    public void setFrameNumber(Integer frameNumber) {
        this.frameNumber = frameNumber;
    }

    public String getIncidentId() {
        return incidentId;
    }

    public void setIncidentId(String incidentId) {
        this.incidentId = incidentId;
    }

    public String getUseableFlag() {
        return useableFlag;
    }

    public void setUseableFlag(String useableFlag) {
        this.useableFlag = useableFlag;
    }

    public String getImageTag() {
        return imageTag;
    }

    public void setImageTag(String imageTag) {
        this.imageTag = imageTag;
    }

    @Override
    public String toString() {
        return "Image{" +
                "imageId='" + imageId + '\'' +
                ", imageName='" + imageName + '\'' +
                ", imageTag='" + imageTag + '\'' +
                ", imagePorts='" + imagePorts + '\'' +
                ", imageMount='" + imageMount + '\'' +
                ", pvcSize=" + pvcSize +
                ", factory='" + factory + '\'' +
                ", algoName='" + algoName + '\'' +
                ", algoDesc='" + algoDesc + '\'' +
                ", deleteFlag='" + deleteFlag + '\'' +
                ", tvChannel=" + tvChannel +
                ", pics=" + pics +
                ", cpuRequests=" + cpuRequests +
                ", gpuRequests=" + gpuRequests +
                ", memRequests=" + memRequests +
                ", config='" + config + '\'' +
                ", algoType='" + algoType + '\'' +
                ", eventType=" + eventType +
                ", subPath='" + subPath + '\'' +
                ", hostPath='" + hostPath + '\'' +
                ", workingDir='" + workingDir + '\'' +
                ", env='" + env + '\'' +
                ", command='" + command + '\'' +
                ", args='" + args + '\'' +
                ", dependencyServiceId='" + dependencyServiceId + '\'' +
                ", frameInterval=" + frameInterval +
                ", frameNumber=" + frameNumber +
                ", incidentId='" + incidentId + '\'' +
                ", useableFlag='" + useableFlag + '\'' +
                '}';
    }
}
