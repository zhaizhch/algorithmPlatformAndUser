package com.example.demo.entity;

public class ContainerDict {
    private String imageId;
    private String iamegName;
    private String imagePorts;
    private Integer PvcSize;
    private String command;
    private String args;
    private String env;
    private String cupRequest;
    private String gpuRequest;
    private String memRequest;
    private String imageConfig;
    private String imageSubPath;
    private String imageHostPath;
    private String imageWorkingDir;
    private String hostAliases;
    private String imageMount;

    public String getImageMount() {
        return imageMount;
    }

    public void setImageMount(String imageMount) {
        this.imageMount = imageMount;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getIamegName() {
        return iamegName;
    }

    public void setIamegName(String iamegName) {
        this.iamegName = iamegName;
    }

    public String getImagePorts() {
        return imagePorts;
    }

    public void setImagePorts(String imagePorts) {
        this.imagePorts = imagePorts;
    }

    public Integer getPvcSize() {
        return PvcSize;
    }

    public void setPvcSize(Integer pvcSize) {
        PvcSize = pvcSize;
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

    public String getCupRequest() {
        return cupRequest;
    }

    public void setCupRequest(String cupRequest) {
        this.cupRequest = cupRequest;
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

    public String getImageConfig() {
        return imageConfig;
    }

    public void setImageConfig(String imageConfig) {
        this.imageConfig = imageConfig;
    }

    public String getImageSubPath() {
        return imageSubPath;
    }

    public void setImageSubPath(String imageSubPath) {
        this.imageSubPath = imageSubPath;
    }

    public String getImageHostPath() {
        return imageHostPath;
    }

    public void setImageHostPath(String imageHostPath) {
        this.imageHostPath = imageHostPath;
    }

    public String getImageWorkingDir() {
        return imageWorkingDir;
    }

    public void setImageWorkingDir(String imageWorkingDir) {
        this.imageWorkingDir = imageWorkingDir;
    }

    public String getHostAliases() {
        return hostAliases;
    }

    public void setHostAliases(String hostAliases) {
        this.hostAliases = hostAliases;
    }

    @Override
    public String toString() {
        return "ContainerDict{" +
                "imageId='" + imageId + '\'' +
                ", iamegName='" + iamegName + '\'' +
                ", imagePorts='" + imagePorts + '\'' +
                ", Pvcsize=" + PvcSize +
                ", command='" + command + '\'' +
                ", args='" + args + '\'' +
                ", env='" + env + '\'' +
                ", cupRequest='" + cupRequest + '\'' +
                ", gpuRequest='" + gpuRequest + '\'' +
                ", memRequest='" + memRequest + '\'' +
                ", imageConfig='" + imageConfig + '\'' +
                ", imageSubPath='" + imageSubPath + '\'' +
                ", imageHostPath='" + imageHostPath + '\'' +
                ", imageWorkingDir='" + imageWorkingDir + '\'' +
                ", hostAliases='" + hostAliases + '\'' +
                ", imageMount='" + imageMount + '\'' +
                '}';
    }
}
