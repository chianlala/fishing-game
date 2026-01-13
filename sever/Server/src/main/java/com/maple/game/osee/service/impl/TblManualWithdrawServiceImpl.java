package com.maple.game.osee.service.impl;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.maple.game.osee.dao.log.mapper.TblManualWithdrawMapper;
import com.maple.game.osee.model.entity.TblManualWithdrawDO;
import org.springframework.stereotype.Service;

@Service
public class TblManualWithdrawServiceImpl extends ServiceImpl<TblManualWithdrawMapper, TblManualWithdrawDO>
    implements IService<TblManualWithdrawDO> {

}
