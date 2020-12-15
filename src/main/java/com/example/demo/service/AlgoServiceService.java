package com.example.demo.service;

import com.example.demo.entity.AlgoService;
import com.example.demo.entity.AlgoServiceDto;
import com.example.demo.mapper.AlgoServiceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AlgoServiceService {
    @Autowired
    AlgoServiceMapper algoServiceMapper;

    //更新算法服务开始状态
    public int updateStartStatus(AlgoServiceDto algoServiceDto) {
        int res = algoServiceMapper.updateStartStatus(algoServiceDto);
        return res;
    }

    //更新算法服务任务数量
    public int updateTaskCount(AlgoServiceDto algoServiceDto) {
        AlgoService algoService1 = algoServiceMapper.queryAlgoServiceInfo(algoServiceDto).get(0);
        algoServiceDto.setTaskCount(algoService1.getTaskCount() + algoServiceDto.getOption());
        int res = algoServiceMapper.updateTaskCount(algoServiceDto);
        return res;
    }

    //插入算法服务信息
    public int insertAlgoService(AlgoServiceDto algoServiceDto) {
        int res = algoServiceMapper.insertAlgoService(algoServiceDto);
        return res;
    }
}
