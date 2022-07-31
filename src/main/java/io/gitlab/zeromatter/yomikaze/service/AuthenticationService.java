package io.gitlab.zeromatter.yomikaze.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.gitlab.zeromatter.yomikaze.entity.account.Account;
import io.gitlab.zeromatter.yomikaze.entity.account.AccountRepository;
import io.gitlab.zeromatter.yomikaze.entity.session.Session;
import io.gitlab.zeromatter.yomikaze.entity.session.SessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.apache.commons.lang3.Validate;
import org.springframework.lang.Nullable;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.logging.Level;

@RequiredArgsConstructor
@Service
@Log
public class AuthenticationService {

    private final SessionRepository sessionRepository;
    private final AccountRepository accountRepository;

    private final Argon2PasswordEncoder passwordEncoder;
    private final Algorithm jwtSigningAlgorithm;


    /**
     * Create new account and return it, not validated yet.
     *
     * @param username username of the account
     * @param email    email of the account
     * @param password password of the account
     * @return account created
     */
    protected Account createAccount(String username, String email, String password) {
        Account account = new Account();
        account.setUsername(username);
        account.setEmail(email);
        account.setPassword(passwordEncoder.encode(password));
        accountRepository.save(account);
        return account;
    }

    /**
     * Register new account
     *
     * @param username username of the account
     * @param email    email of the account
     * @param password password of the account
     * @return account registered
     */
    public Account register(String username, String email, String password, String passwordConfirmation) {
        Validate.notEmpty(username, "Username is required");
        Validate.notEmpty(email, "Email is required");
        Validate.notEmpty(password, "Password is required");
        Validate.isTrue(password.equals(passwordConfirmation), "Passwords do not match");
        Validate.isTrue(!accountRepository.existsByUsername(username), "Username is already taken");
        Validate.isTrue(!accountRepository.existsByEmail(email), "Email is already taken");
        return createAccount(username, email, password);
    }

    public boolean checkPassword(Account account, String password) {
        return passwordEncoder.matches(password, account.getPassword());
    }

    /**
     * Login with username or email and password and return account
     *
     * @param usernameOrEmail username or email of the account
     * @param password        password of the account
     * @return account logged in
     * @throws IllegalArgumentException if something goes wrong during login, e.g. username or email is empty, account does not exist, password is wrong
     */
    public Account login(String usernameOrEmail, String password) {
        Validate.notEmpty(usernameOrEmail, "Username or email is required");
        Validate.notEmpty(password, "Password is required");
        Optional<Account> accountOptional = accountRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
        if (!accountOptional.isPresent()) throw new IllegalArgumentException("Account not found");
        Account account = accountOptional.get();
        Validate.isTrue(checkPassword(account, password), "Wrong password");
        return account;
    }

    /**
     * Create a new session for the given account.
     *
     * @param account       the account to create a session for.
     * @param userAgent     user agent of the client.
     * @param remoteAddress remote address of the client.
     * @return token for the new session.
     */
    public String createSession(Account account, @Nullable String userAgent, String remoteAddress) {
        Session session = new Session(account);
        if (userAgent != null) session.setUserAgent(userAgent);
        if (remoteAddress != null) session.setRemoteAddress(remoteAddress);
        sessionRepository.save(session);
        log.log(Level.INFO, "Created session {0} for account {1}", new Object[]{session.getId(), account.getId()});
        return JWT.create()
                .withIssuer("yomikaze") // DO-LATER: set iss from config
                .withIssuedAt(session.getCreatedAt())
                .withClaim("sid", session.getId())
                .withClaim("id", account.getId())
                .withClaim("username", account.getUsername())
                .withClaim("email", account.getEmail())
                .sign(jwtSigningAlgorithm);
    }

    /**
     * Shortcut for {@link #createSession(Account, String, String)} with nulls for user agent and remote address.
     *
     * @param account the account to create a session for.
     * @return token for the new session.
     * @see #createSession(Account, String, String)
     */
    public String createSession(Account account) {
        return createSession(account, null, null);
    }

    /**
     * Validate the given token and return the associated account.
     *
     * @param token  the token to validate.
     * @param strict if true, will find the account from the id in session, otherwise will find the account from by id provided in the token.
     * @return the account associated with the token.
     * @throws IllegalArgumentException if the token is invalid.
     */
    public Account validateSession(String token, boolean strict) {
        try {
            DecodedJWT decoded = JWT.require(jwtSigningAlgorithm)
                    .build()
                    .verify(token);
            if (strict) {
                Optional<Session> session = sessionRepository.findById(decoded.getIssuer());
                if (session.isPresent()) {
                    return session.get().getAccount();
                }
            } else {
                Optional<Account> account = accountRepository.findById(decoded.getClaim("id").asLong());
                if (account.isPresent()) {
                    return account.get();
                }
            }
        } catch (SignatureVerificationException ignored) {
            // Ignore
        }
        throw new IllegalArgumentException("Invalid token");
    }

    /**
     * Shortcut for {@link #validateSession(String, boolean)} with strict = false.
     *
     * @param token the token to validate.
     * @return the account associated with the token.
     * @see #validateSession(String, boolean)
     */
    public Account validateSession(String token) {
        return validateSession(token, false);
    }


}
