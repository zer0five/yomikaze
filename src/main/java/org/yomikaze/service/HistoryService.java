package org.yomikaze.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.yomikaze.persistence.entity.Account;
import org.yomikaze.persistence.entity.Chapter;
import org.yomikaze.persistence.entity.Comic;
import org.yomikaze.persistence.entity.History;
import org.yomikaze.persistence.repository.HistoryRepository;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

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

    public Chapter getLastReadChapter(Account account, Comic comic) {
        return historyRepository
            .findFirstByAccountAndChapterComicOrderByReadAtDesc(account, comic)
            .map(History::getChapter)
            .orElse(null);
    }

    public Collection<Chapter> getHistorySummary(Account account) {
        Collection<Comic> comics = historyRepository.findDistinctChapterComicByAccount(account);
        return comics.stream()
            .map(comic -> getLastReadChapter(account, comic))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }


}
