package org.yomikaze.service;

import org.springframework.stereotype.Service;
import org.yomikaze.persistence.entity.Comic;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

@Service
public class ComicService {

    private static final Pattern NON_LATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("\\s");

    public String getSlug(Comic comic) {
        String name = comic.getName();
        String noWhitespace = WHITESPACE.matcher(name).replaceAll("-");
        String normalized = Normalizer.normalize(noWhitespace, Normalizer.Form.NFD);
        String slug = NON_LATIN.matcher(normalized).replaceAll("");
        return slug.toLowerCase(Locale.ENGLISH);
    }
}
