package com.kinal.recruitment.repository.jdbc;

import com.kinal.recruitment.config.DataBaseConnection;
import com.kinal.recruitment.exception.DatabaseException;
import com.kinal.recruitment.model.Role;
import com.kinal.recruitment.model.User;
import com.kinal.recruitment.repository.UserRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class JdbcUserRepository implements UserRepository {

    private static final String SELECT_BY_EMAIL
            = "SELECT u.user_id, u.username, u.email, u.password, r.role_id, r.role_name "
            + "FROM `user` u INNER JOIN role r ON u.role_id = r.role_id "
            + "WHERE u.email = ?";

    private static final String EXISTS_BY_EMAIL = "SELECT 1 FROM `user` WHERE email = ?";
    private static final String EXISTS_BY_USERNAME = "SELECT 1 FROM `user` WHERE username = ?";

    private static final String INSERT
            = "INSERT INTO `user` (username, email, password, role_id) VALUES (?, ?, ?, ?)";

    private final DataBaseConnection dataBase;

    public JdbcUserRepository(DataBaseConnection dataBase) {
        this.dataBase = dataBase;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        try (Connection con = dataBase.getConnection();
                PreparedStatement ps = con.prepareStatement(SELECT_BY_EMAIL)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Role role = new Role(rs.getInt("role_id"), rs.getString("role_name"));
                    return Optional.of(new User(rs.getInt("user_id"), rs.getString("username"),
                            rs.getString("email"), rs.getString("password"), role));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error al consultar el usuario.", e);
        }
    }

    @Override
    public boolean existsByEmail(String email) {
        return exists(EXISTS_BY_EMAIL, email);
    }

    @Override
    public boolean existsByUsername(String username) {
        return exists(EXISTS_BY_USERNAME, username);
    }

    @Override
    public void save(User user) {
        try (Connection con = dataBase.getConnection();
                PreparedStatement ps = con.prepareStatement(INSERT)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPasswordHash());
            ps.setInt(4, user.getRole().getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error al registrar el usuario.", e);
        }
    }

    private boolean exists(String sql, String value) {
        try (Connection con = dataBase.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, value);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error al consultar el usuario.", e);
        }
    }
}
