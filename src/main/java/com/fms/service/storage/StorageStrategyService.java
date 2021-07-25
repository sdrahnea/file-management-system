package com.fms.service.storage;

import com.fms.model.StorageDto;

import java.util.Map;

public interface StorageStrategyService {

    Map<String, String> store(StorageDto storageDto);

}
