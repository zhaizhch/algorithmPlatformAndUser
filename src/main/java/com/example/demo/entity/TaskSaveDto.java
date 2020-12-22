package com.example.demo.entity;

import javax.validation.constraints.NotEmpty;

public class TaskSaveDto {

    public static interface saveGroup {
    }


    @NotEmpty(message = "taskName不能为空", groups = {saveGroup.class})
    private String taskName;
    private String namespace;
    private Integer replicas;
    private String createUserId;
    @NotEmpty(message = "taskType不能为空", groups = {saveGroup.class})
    private String taskType;
    private String algoType;
    private String command;
    private String args;
    private String env;
    private Integer cpuRequests;
    private Integer gpuRequests;
    private Integer memRequests;
    private String input;
    private String output;
    private String channelId;
    private Integer tvChannel;
    private String hostAliases;
    @NotEmpty(message = "imageName不能为空", groups = {saveGroup.class})
    private String imageName;
    private String StartType;
    private UserResult userResult;
    private String taskId;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public Integer getReplicas() {
        return replicas;
    }

    public void setReplicas(Integer replicas) {
        this.replicas = replicas;
    }

    public String getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getAlgoType() {
        return algoType;
    }

    public void setAlgoType(String algoType) {
        this.algoType = algoType;
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

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
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

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public Integer getTvChannel() {
        return tvChannel;
    }

    public void setTvChannel(Integer tvChannel) {
        this.tvChannel = tvChannel;
    }

    public String getHostAliases() {
        return hostAliases;
    }

    public void setHostAliases(String hostAliases) {
        this.hostAliases = hostAliases;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getStartType() {
        return StartType;
    }

    public void setStartType(String startType) {
        StartType = startType;
    }

    public UserResult getUserResult() {
        return userResult;
    }

    public void setUserResult(UserResult userResult) {
        this.userResult = userResult;
    }

    @Override
    public String toString() {
        return "TaskSaveDto{" +
                "taskName='" + taskName + '\'' +
                ", namespace='" + namespace + '\'' +
                ", replicas=" + replicas +
                ", createUserId='" + createUserId + '\'' +
                ", taskType='" + taskType + '\'' +
                ", algoType='" + algoType + '\'' +
                ", command='" + command + '\'' +
                ", args='" + args + '\'' +
                ", env='" + env + '\'' +
                ", cpuRequests=" + cpuRequests +
                ", gpuRequests=" + gpuRequests +
                ", memRequests=" + memRequests +
                ", input='" + input + '\'' +
                ", output='" + output + '\'' +
                ", channelId='" + channelId + '\'' +
                ", tvChannel=" + tvChannel +
                ", hostAliases='" + hostAliases + '\'' +
                ", imageName='" + imageName + '\'' +
                ", StartType='" + StartType + '\'' +
                ", userResult=" + userResult +
                ", taskId='" + taskId + '\'' +
                '}';
    }
}
