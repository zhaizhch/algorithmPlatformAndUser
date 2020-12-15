package com.example.demo.service;

import com.example.demo.entity.Container;
import com.example.demo.entity.ContainerDto;
import com.example.demo.mapper.ContainerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ContainerService {
    @Autowired
    private ContainerMapper containerMapper;

    public int insertTest(ContainerDto containerDto) {
        int ret = containerMapper.insertContainerInfo(containerDto);
        return ret;
    }

    public List<Container> queryContainer(ContainerDto containerDto) {
        List<Container> containerList = containerMapper.queryContainer(containerDto);
        return containerList;
    }

    public int updateTest(ContainerDto containerDto) {
        int ret = containerMapper.updateContainer(containerDto);
        return ret;
    }

    public int deleteTest(ContainerDto containerDto) {
        int ret = containerMapper.deleteContainer(containerDto);
        return ret;
    }


}
