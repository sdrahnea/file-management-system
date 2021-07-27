package com.fms.service.storage;

import com.fms.model.StorageDto;

import java.util.Map;

/**
 * StorageStrategyService class contains the skeleton for implementation.
 */
public interface StorageStrategyService {

    Map<String, String> store(StorageDto storageDto);

}
