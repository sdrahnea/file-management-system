package com.fms.service.storage;

import com.fms.model.StorageDto;

import java.io.IOException;
import java.util.Map;

public interface StorageStrategyService {

    Map<String, String> store(StorageDto storageDto) throws IOException;

}
