package com.kinal.recruitment.repository.jdbc;

import com.kinal.recruitment.config.DataBaseConnection;
import com.kinal.recruitment.exception.DatabaseException;
import com.kinal.recruitment.model.Role;
import com.kinal.recruitment.repository.RoleRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

public class JdbcRoleRepository implements RoleRepository {

    private static final String SELECT_BY_NAME = "SELECT role_id, role_name FROM role WHERE role_name = ?";
    private static final String INSERT = "INSERT INTO role (role_name) VALUES (?)";

    private final DataBaseConnection dataBase;

    public JdbcRoleRepository(DataBaseConnection dataBase) {
        this.dataBase = dataBase;
    }

    @Override
    public Optional<Role> findByName(String name) {
        try (Connection con = dataBase.getConnection();
                PreparedStatement ps = con.prepareStatement(SELECT_BY_NAME)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Role(rs.getInt("role_id"), rs.getString("role_name")));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error al consultar el rol.", e);
        }
    }

    @Override
    public Role save(Role role) {
        try (Connection con = dataBase.getConnection();
                PreparedStatement ps = con.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, role.getName());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    role.setId(keys.getInt(1));
                }
            }
            return role;
        } catch (SQLException e) {
            throw new DatabaseException("Error al guardar el rol.", e);
        }
    }
}
