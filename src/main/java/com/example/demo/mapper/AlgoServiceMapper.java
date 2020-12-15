package com.example.demo.mapper;

import com.example.demo.entity.AlgoService;
import com.example.demo.entity.AlgoServiceDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface AlgoServiceMapper {
    int insertAlgoService(AlgoServiceDto algoServiceDto);

    List<AlgoService> queryAlgoServiceInfo(AlgoServiceDto algoServiceDto);

    int updateStartStatus(AlgoServiceDto algoServiceDto);

    int updateTaskCount(AlgoServiceDto algoServiceDto);

    //置deleteFlag为0
    int deleteAlgoService(AlgoServiceDto algoServiceDto);
}
