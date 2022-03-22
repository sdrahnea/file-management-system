package com.fms.service.id;

import com.fms.exception.MissingFileIdStrategyException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * This class contains implementation to received file id strategy based
 *      on the provided configuration.
 */
@Component
public class FileIdServiceFactory {

    private final FileIdStrategy fileIdStrategy;

    private final InstantFileIdStrategyService instantFileIdStrategyService;
    private final UUIDFileIdStrategyService uuidFileIdStrategyService;

    public FileIdServiceFactory(@Value("${file.id.type:UUID}") FileIdStrategy fileIdStrategy,
                                InstantFileIdStrategyService instantFileIdStrategyService, UUIDFileIdStrategyService uuidFileIdStrategyService) {
        this.fileIdStrategy = fileIdStrategy;
        this.instantFileIdStrategyService = instantFileIdStrategyService;
        this.uuidFileIdStrategyService = uuidFileIdStrategyService;
    }

    private FileIdStrategyService getIdService() {
        switch (fileIdStrategy) {
            case UUID:
                return uuidFileIdStrategyService;
            case INSTANT:
                return instantFileIdStrategyService;

            default:
                throw new MissingFileIdStrategyException();
        }
    }

    /**
     * Create the file id value based for file id strategy
     *
     * @return file id based on selected strategy
     */
    public String create() {
        return getIdService().createId();
    }

}
