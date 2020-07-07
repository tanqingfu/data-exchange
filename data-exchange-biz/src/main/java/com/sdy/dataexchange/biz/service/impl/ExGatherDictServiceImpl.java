package com.sdy.dataexchange.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sdy.common.model.Response;
import com.sdy.dataexchange.biz.mapper.ExGatherDictMapper;
import com.sdy.dataexchange.biz.model.BO.GatherBO;
import com.sdy.dataexchange.biz.model.ExDbDict;
import com.sdy.dataexchange.biz.model.ExGatherDict;
import com.sdy.dataexchange.biz.model.GatherDictResult;
import com.sdy.dataexchange.biz.service.ExGatherDictService;
import com.sdy.mvc.service.impl.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zzq
 * @since 2019-07-22
 */
@Slf4j
@Service
public class ExGatherDictServiceImpl extends BaseServiceImpl<ExGatherDict> implements ExGatherDictService {
    @Autowired
    private ExGatherDictMapper exGatherDictMapper;
    @Autowired
    ExGatherDictService exGatherDictService;

    @Override
    public List<GatherDictResult> getInfo(String gatherDesc) {
        return exGatherDictMapper.getInfo(gatherDesc);
    }


    @Override
    public List<GatherDictResult> getGatherInfo(Page page) {
        return exGatherDictMapper.getGatherInfo(page);
    }

    @Override
    public GatherDictResult getOneInfo(Integer gatherId) {
        return exGatherDictMapper.getOneInfo(gatherId);
    }

    @Override
    public Integer saveGather(GatherDictResult gatherDictResult) {
        return exGatherDictMapper.saveGather(gatherDictResult);
    }

    @Override
    public Integer saveDbMapping(GatherDictResult gatherDictResult) {
        return exGatherDictMapper.saveDbMapping(gatherDictResult);
    }

    @Override
    public Integer updateInfo(ExGatherDict exGatherDict) {
        return exGatherDictMapper.updateInfo(exGatherDict);
    }

    @Override
    public ExDbDict getDbInfo(Integer dbId) {
        return exGatherDictMapper.getDbInfo(dbId);
    }

    @Override
    public Integer getTotle() {
        return exGatherDictMapper.getTotle();
    }

    @Override
    public List<GatherDictResult> getAllGatherPage() {
        return exGatherDictMapper.getAllGatherPage();
    }

    @Override
    public List<Map> getAllGather() {
        return exGatherDictMapper.getAllGather();
    }

    @Override
    public Integer getGatherId(String gatherDesc) {
        Integer gatherId = null;
        ExGatherDict exGatherDict = exGatherDictService.getOne(new LambdaQueryWrapper<ExGatherDict>()
                .eq(ExGatherDict::getGatherDesc, gatherDesc));
        if (exGatherDict != null) {
            gatherId = exGatherDict.getGatherId();
        }
        return gatherId;
    }


    /**
     * 新增交换节点
     *
     * @param gatherBO
     * @return
     */
    @Override
    public Response addGather(GatherBO gatherBO) {
        String gatherDesc = gatherBO.getGatherDesc();
        String serviceIp = gatherBO.getServiceIp();
        String serviceDesc = gatherBO.getServiceDesc();
        String gatherPath = gatherBO.getGatherPath();
        String sshPassword = gatherBO.getSshPassword();
        Integer sshPort = gatherBO.getSshPort();
        String sshUser = gatherBO.getSshUser();
        if (StringUtils.isNotBlank(gatherDesc) && StringUtils.isNotBlank(serviceDesc)) {
            ExGatherDict exGatherDict = exGatherDictService.getOne(new LambdaQueryWrapper<ExGatherDict>()
                    .eq(ExGatherDict::getGatherDesc, gatherDesc));
            if (exGatherDict != null) {
                return Response.error("新增交换节点失败,该交换节点名称已存在");
            } else {
                exGatherDictMapper.insertIntoExGatherDict(gatherDesc, serviceIp, serviceDesc, gatherPath, sshPassword
                        , sshPort, sshUser);
                return Response.success("新增交换节点成功");
            }
        } else {
            return Response.error("请填写完整信息！");
        }
    }
}
