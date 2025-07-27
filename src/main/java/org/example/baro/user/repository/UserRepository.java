package org.example.baro.user.repository;
import org.example.baro.user.entity.User;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class UserRepository {

    private final ConcurrentHashMap<Long, User> users = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(0L);

    public User save(User user) {
        user.setId(sequence.incrementAndGet());
        users.put(user.getId(), user);
        return user;
    }

    public  Optional<User> findByEmail(String email) {
        return users.values().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }

    public Optional<User> findById(long id) {
        return Optional.ofNullable(users.get(id));
    }

    public void clearUsers() {
        this.users.clear();
    }
}
