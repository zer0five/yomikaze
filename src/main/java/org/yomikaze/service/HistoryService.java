package org.yomikaze.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.yomikaze.persistence.entity.Account;
import org.yomikaze.persistence.entity.Chapter;
import org.yomikaze.persistence.entity.History;
import org.yomikaze.persistence.repository.HistoryRepository;

@Service
@RequiredArgsConstructor
public class HistoryService {

    private final HistoryRepository historyRepository;

    public void read(Account account, Chapter chapter) {
        History history = new History();
        history.setAccount(account);
        history.setChapter(chapter);
        historyRepository.save(history);
    }
}
