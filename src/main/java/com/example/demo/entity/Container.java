package com.example.demo.entity;

/**
 * @author User
 */
public class Container {
    private String conId;
    private String taskId;
    private String imageId;
    private String command;
    private String args;
    private String env;
    private String portMapping;
    private String volumeMapping;
    private Integer cpuRequests;
    private Integer gpuRequests;
    private Integer memRequests;
    private Integer storageRequests;
    private String info;
    private String analysisType;
    private String input;
    private String output;
    private String channelId;
    private String deleteFlag;
    private String configMapping;
    private Integer tvChannel;
    private String namespace;
    private String hostAliases;

    public String getConId() {
        return conId;
    }

    public String getTaskId() {
        return taskId;
    }

    public String getImageId() {
        return imageId;
    }

    public String getCommand() {
        return command;
    }

    public String getArgs() {
        return args;
    }

    public String getEnv() {
        return env;
    }

    public String getPortMapping() {
        return portMapping;
    }

    public String getVolumeMapping() {
        return volumeMapping;
    }

    public Integer getCpuRequests() {
        return cpuRequests;
    }

    public Integer getGpuRequests() {
        return gpuRequests;
    }

    public Integer getMemRequests() {
        return memRequests;
    }

    public Integer getStorageRequests() {
        return storageRequests;
    }

    public String getInfo() {
        return info;
    }

    public String getAnalysisType() {
        return analysisType;
    }

    public String getInput() {
        return input;
    }

    public String getOutput() {
        return output;
    }

    public String getChannelId() {
        return channelId;
    }

    public String getDeleteFlag() {
        return deleteFlag;
    }

    public String getConfigMapping() {
        return configMapping;
    }

    public Integer getTvChannel() {
        return tvChannel;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getHostAliases() {
        return hostAliases;
    }

    public void setConId(String conId) {
        this.conId = conId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public void setArgs(String args) {
        this.args = args;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public void setPortMapping(String portMapping) {
        this.portMapping = portMapping;
    }

    public void setVolumeMapping(String volumeMapping) {
        this.volumeMapping = volumeMapping;
    }

    public void setCpuRequests(Integer cpuRequests) {
        this.cpuRequests = cpuRequests;
    }

    public void setGpuRequests(Integer gpuRequests) {
        this.gpuRequests = gpuRequests;
    }

    public void setMemRequests(Integer memRequests) {
        this.memRequests = memRequests;
    }

    public void setStorageRequests(Integer storageRequests) {
        this.storageRequests = storageRequests;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public void setAnalysisType(String analysisType) {
        this.analysisType = analysisType;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public void setDeleteFlag(String deleteFlag) {
        this.deleteFlag = deleteFlag;
    }

    public void setConfigMapping(String configMapping) {
        this.configMapping = configMapping;
    }

    public void setTvChannel(Integer tvChannel) {
        this.tvChannel = tvChannel;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public void setHostAliases(String hostAliases) {
        this.hostAliases = hostAliases;
    }

    @Override
    public String toString() {
        return "Container{" +
                "conId='" + conId + '\'' +
                ", taskId='" + taskId + '\'' +
                ", imageId='" + imageId + '\'' +
                ", command='" + command + '\'' +
                ", args='" + args + '\'' +
                ", env='" + env + '\'' +
                ", portMapping='" + portMapping + '\'' +
                ", volumeMapping='" + volumeMapping + '\'' +
                ", cpuRequests=" + cpuRequests +
                ", gpuRequests=" + gpuRequests +
                ", memRequests=" + memRequests +
                ", storageRequests=" + storageRequests +
                ", info='" + info + '\'' +
                ", analysisType='" + analysisType + '\'' +
                ", input='" + input + '\'' +
                ", output='" + output + '\'' +
                ", channelId='" + channelId + '\'' +
                ", deleteFlag='" + deleteFlag + '\'' +
                ", configMapping='" + configMapping + '\'' +
                ", tvChannel=" + tvChannel +
                ", namespace='" + namespace + '\'' +
                ", hostAliases='" + hostAliases + '\'' +
                '}';
    }
}
