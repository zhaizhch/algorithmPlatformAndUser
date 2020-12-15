package com.example.demo.utils;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Docker {
    @Value("${NFS_SERVER}")
    private String NFS_SERVER;
    @Value("${HARBOR_USER}")
    private String HARBOR_USER;
    @Value("${HARBOR_PASSWORD}")
    private String HARBOR_PASSWORD;
    @Value("${HARBOR}")
    private String HARBOR;

    public DockerClient dockerCli(){
        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost("tcp://"+NFS_SERVER+":2375")
                .withDockerTlsVerify(true)
                .withDockerCertPath("/home/user/.docker/certs")
                .withDockerConfig("/home/user/.docker")
                .withApiVersion("1.23")
                .withRegistryUrl(HARBOR)
                .withRegistryUsername(HARBOR_USER)
                .withRegistryPassword(HARBOR_PASSWORD)
                .build();
        DockerClient docker = DockerClientBuilder.getInstance(config).build();
        return docker;
    }
}
