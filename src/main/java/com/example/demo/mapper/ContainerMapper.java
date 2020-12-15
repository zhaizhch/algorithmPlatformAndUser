package com.example.demo.mapper;

import com.example.demo.entity.Container;
import com.example.demo.entity.ContainerDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface ContainerMapper {
    int insertContainerInfo(ContainerDto containerDto);

    List<Container> queryContainer(ContainerDto containerDto);

    int updateContainer(ContainerDto containerDto);

    int deleteContainer(ContainerDto containerDto);

}
