package ru.practicum.shareit.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.user.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Метод проверяет наличие переданного почтового адреса у всех зарегистрированных пользователей
     *
     * @param email почтовый адрес
     * @return результат проверки
     */
    boolean existsByEmailIgnoreCase(String email);


    /**
     * Метод проверяет наличие переданного почтового адреса у всех пользователей, кроме пользователя с переданным
     * идентификатором
     *
     * @param email почтовый адрес
     * @param userId идентификатор пользователя
     * @return результат проверки
     */
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN TRUE ELSE FALSE END "
            + "FROM User u "
            + "WHERE UPPER(u.email) = UPPER(:email) "
            + "AND u.entityId <> :userId")
    boolean existsByEmailAndUserId(String email, Long userId);
}
