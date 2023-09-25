package com.epam.storageservice.service;

import com.epam.storageservice.model.Storage;
import com.epam.storageservice.repository.StorageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StorageService {

    private final StorageRepository storageRepository;

    public List<Storage> getStorages() {
        return storageRepository.findAll();
    }

    public Long saveStorage(Storage storage) {
        if (validateStorage(storage)) {
            Storage savedStorage = storageRepository.save(storage);
            return savedStorage.getId();
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    public List<Long> deleteStorages(List<Long> ids) {
        storageRepository.deleteAllById(ids);
        return ids;
    }

    private static boolean validateStorage(Storage storage) {
        return storage != null
                && !ObjectUtils.isEmpty(storage.getBucket())
                && !ObjectUtils.isEmpty(storage.getStorageType())
                && !ObjectUtils.isEmpty(storage.getPath());
    }
}
