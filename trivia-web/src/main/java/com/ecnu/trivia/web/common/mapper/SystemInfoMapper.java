package com.ecnu.trivia.web.common.mapper;

import com.ecnu.trivia.web.common.domain.SystemInfo;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemInfoMapper {

    /**获取系统基本信息*/
    SystemInfo getSystemBaseInfo();
}
