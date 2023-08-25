package com.compass.challenge3.service;

import com.compass.challenge3.entity.History;
import com.compass.challenge3.repository.HistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HistoryService {

    private final HistoryRepository historyRepository;

    @Autowired
    public HistoryService(HistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    public History save(History history) {
        return historyRepository.save(history);
    }
    public List<History> saveAll(List<History> history) {
        return historyRepository.saveAll(history);
    }

}
