package org.yomikaze.service;

import org.springframework.stereotype.Service;
import org.yomikaze.persistence.entity.Comic;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Service
public class ComicService {
    private static final Map<String, Pattern> VIETNAMESE_STRIP_MAP = new LinkedHashMap<>();
    static {
        VIETNAMESE_STRIP_MAP.put("a", Pattern.compile("[áàảãạâấầẩẫậăắằẳẵặ]"));
        VIETNAMESE_STRIP_MAP.put("e", Pattern.compile("[éèẻẽẹêếềểễệ]"));
        VIETNAMESE_STRIP_MAP.put("i", Pattern.compile("[íìỉĩị]"));
        VIETNAMESE_STRIP_MAP.put("o", Pattern.compile("[óòỏõọôốồổỗộơớờởỡợ]"));
        VIETNAMESE_STRIP_MAP.put("u", Pattern.compile("[úùủũụưứừửữự]"));
        VIETNAMESE_STRIP_MAP.put("y", Pattern.compile("[ýỳỷỹỵ]"));
        VIETNAMESE_STRIP_MAP.put("d", Pattern.compile("đ"));
        // uppercase
        VIETNAMESE_STRIP_MAP.put("A", Pattern.compile("[ÁÀẢÃẠÂẤẦẨẪẬĂẮẰẲẴẶ]"));
        VIETNAMESE_STRIP_MAP.put("E", Pattern.compile("[ÉÈẺẼẸÊẾỀỂỄỆ]"));
        VIETNAMESE_STRIP_MAP.put("I", Pattern.compile("[ÍÌỈĨỊ]"));
        VIETNAMESE_STRIP_MAP.put("O", Pattern.compile("[ÓÒỎÕỌÔỐỒỔỖỘƠỚỜỞỠỢ]"));
        VIETNAMESE_STRIP_MAP.put("U", Pattern.compile("[ÚÙỦŨỤƯỨỪỬỮỰ]"));
    }

    private static final Pattern SPACES = Pattern.compile("\\s+");

    public String getUrlFriendlyName(Comic comic) {
        String name = comic.getName();
        name = SPACES.matcher(name).replaceAll("-");
        for (Map.Entry<String, Pattern> entry : VIETNAMESE_STRIP_MAP.entrySet()) {
            name = entry.getValue().matcher(name).replaceAll(entry.getKey());
        }
        return name;
    }
}
