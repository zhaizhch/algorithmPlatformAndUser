package com.example.demo.entity;

import io.kubernetes.client.custom.IntOrString;

public class Port {
    private String portName;
    private Integer portNode;
    private String portTarget;

    public String getPortName() {
        return portName;
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }

    public Integer getPortNode() {
        return portNode;
    }

    public void setPortNode(Integer portNode) {
        this.portNode = portNode;
    }

    public String getPortTarget() {
        return portTarget;
    }

    public void setPortTarget(String portTarget) {
        this.portTarget = portTarget;
    }

    @Override
    public String toString() {
        return "Port{" +
                "portName='" + portName + '\'' +
                ", portNode='" + portNode + '\'' +
                ", portTarget='" + portTarget + '\'' +
                '}';
    }
}
