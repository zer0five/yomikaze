package org.yomikaze.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RedirectService {

    private final Set<String> blacklist = new HashSet<>(Arrays.asList(
        "/login", "/register", "/sign-in", "/sign-up", "/logout", "/sign-out"
    ));

    public boolean isBlacklisted(String path) {
        return blacklist.contains(path);
    }

    public void storeRedirect(HttpSession session, String path) {
        if (isBlacklisted(path)) {
            return;
        }
        URI uri = URI.create(path);
        session.setAttribute("redirect", uri);
    }

    public void storeRedirect(HttpSession session, URI uri) {
        storeRedirect(session, uri.getPath());
    }

    public void storeRedirect(HttpSession session, Optional<URI> referer) {
        storeRedirect(session, referer.orElse(DEFAULT_REDIRECT));
    }

    private static final URI DEFAULT_REDIRECT = URI.create("/");

    public URI getRedirect(HttpSession session) {
        if (session == null) {
            return DEFAULT_REDIRECT;
        }
        Object attribute = session.getAttribute("redirect");
        if (attribute == null) {
            return DEFAULT_REDIRECT;
        }
        URI redirect = (URI) attribute;
        session.removeAttribute("redirect");
        return redirect;
    }

    public String getRedirectSpring(HttpSession session) {
        return "redirect:" + getRedirect(session).getPath();
    }
}
