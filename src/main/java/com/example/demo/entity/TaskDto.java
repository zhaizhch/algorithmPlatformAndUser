package com.example.demo.entity;

import java.util.Date;
import java.util.List;

public class TaskDto {
    private String taskId;
    private String taskName;
    private String namespace;
    private Integer replicas;
    private String deleteFlag;
    private String gpuModel;
    private String createUserId;
    private String svcIp;
    private String taskType;
    private String algoType;
    private Date taskCreateTime;
    private Integer realTimeStream;
    private Integer videoFile;
    private UserResult userResult;
    private ImageDto imageDto;
    private Integer count;
    private List<String> taskIds;
    private Boolean flag;
    private String searchCondition;

    public String getSearchCondition() {
        return searchCondition;
    }

    public void setSearchCondition(String searchCondition) {
        this.searchCondition = searchCondition;
    }

    public Integer getRealTimeStream() {
        return realTimeStream;
    }

    public void setRealTimeStream(Integer realTimeStream) {
        this.realTimeStream = realTimeStream;
    }

    public UserResult getUserResult() {
        return userResult;
    }

    public void setUserResult(UserResult userResult) {
        this.userResult = userResult;
    }

    public ImageDto getImageDto() {
        return imageDto;
    }

    public void setImageDto(ImageDto imageDto) {
        this.imageDto = imageDto;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<String> getTaskIds() {
        return taskIds;
    }

    public void setTaskIds(List<String> taskIds) {
        this.taskIds = taskIds;
    }

    public Boolean getFlag() {
        return flag;
    }

    public void setFlag(Boolean flag) {
        this.flag = flag;
    }

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

    public String getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(String deleteFlag) {
        this.deleteFlag = deleteFlag;
    }

    public String getGpuModel() {
        return gpuModel;
    }

    public void setGpuModel(String gpuModel) {
        this.gpuModel = gpuModel;
    }

    public String getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }

    public String getSvcIp() {
        return svcIp;
    }

    public void setSvcIp(String svcIp) {
        this.svcIp = svcIp;
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

    public Date getTaskCreateTime() {
        return taskCreateTime;
    }

    public void setTaskCreateTime(Date taskCreateTime) {
        this.taskCreateTime = taskCreateTime;
    }

    public Integer getRealtimeStream() {
        return realTimeStream;
    }

    public void setRealtimeStream(Integer realtimeStream) {
        this.realTimeStream = realtimeStream;
    }

    public Integer getVideoFile() {
        return videoFile;
    }

    public void setVideoFile(Integer videoFile) {
        this.videoFile = videoFile;
    }

    @Override
    public String toString() {
        return "TaskDto{" +
                "taskId='" + taskId + '\'' +
                ", taskName='" + taskName + '\'' +
                ", namespace='" + namespace + '\'' +
                ", replicas=" + replicas +
                ", deleteFlag='" + deleteFlag + '\'' +
                ", gpuModel='" + gpuModel + '\'' +
                ", createUserId='" + createUserId + '\'' +
                ", svcIp='" + svcIp + '\'' +
                ", taskType='" + taskType + '\'' +
                ", algoType='" + algoType + '\'' +
                ", taskCreateTime=" + taskCreateTime +
                ", realTimeStream=" + realTimeStream +
                ", videoFile=" + videoFile +
                ", userResult=" + userResult +
                ", imageDto=" + imageDto +
                ", count=" + count +
                ", taskIds=" + taskIds +
                ", flag=" + flag +
                ", searchCondition='" + searchCondition + '\'' +
                '}';
    }
}
