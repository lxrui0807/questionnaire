package com.wf.ew.system.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wf.ew.system.model.FileInfo;

import java.util.List;

public interface FileInfoMapper extends BaseMapper<FileInfo> {
    List<FileInfo> getFileDir(FileInfo file);
}
