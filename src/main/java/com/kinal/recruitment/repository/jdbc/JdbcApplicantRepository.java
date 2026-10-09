package com.kinal.recruitment.repository.jdbc;

import com.kinal.recruitment.config.DataBaseConnection;
import com.kinal.recruitment.exception.DatabaseException;
import com.kinal.recruitment.model.Applicant;
import com.kinal.recruitment.repository.ApplicantRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcApplicantRepository implements ApplicantRepository {

    private static final String SELECT_BASE
            = "SELECT applicant_id, first_name, last_name, email, phone, address, age FROM applicant";

    private static final String INSERT
            = "INSERT INTO applicant (first_name, last_name, email, phone, address, age) VALUES (?, ?, ?, ?, ?, ?)";

    private static final String UPDATE
            = "UPDATE applicant SET first_name = ?, last_name = ?, email = ?, phone = ?, address = ?, age = ? "
            + "WHERE applicant_id = ?";

    private static final String DELETE = "DELETE FROM applicant WHERE applicant_id = ?";

    private final DataBaseConnection dataBase;

    public JdbcApplicantRepository(DataBaseConnection dataBase) {
        this.dataBase = dataBase;
    }

    @Override
    public List<Applicant> findAll() {
        List<Applicant> applicants = new ArrayList<>();
        try (Connection con = dataBase.getConnection();
                PreparedStatement ps = con.prepareStatement(SELECT_BASE + " ORDER BY applicant_id");
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                applicants.add(map(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error al consultar los aspirantes.", e);
        }
        return applicants;
    }

    @Override
    public Optional<Applicant> findByEmail(String email) {
        try (Connection con = dataBase.getConnection();
                PreparedStatement ps = con.prepareStatement(SELECT_BASE + " WHERE email = ?")) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error al consultar el aspirante.", e);
        }
    }

    @Override
    public Applicant save(Applicant applicant) {
        try (Connection con = dataBase.getConnection();
                PreparedStatement ps = con.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
            fillParameters(ps, applicant);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    applicant.setId(keys.getInt(1));
                }
            }
            return applicant;
        } catch (SQLException e) {
            throw new DatabaseException("Error al guardar el aspirante.", e);
        }
    }

    @Override
    public boolean update(Applicant applicant) {
        try (Connection con = dataBase.getConnection();
                PreparedStatement ps = con.prepareStatement(UPDATE)) {
            fillParameters(ps, applicant);
            ps.setInt(7, applicant.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error al actualizar el aspirante.", e);
        }
    }

    @Override
    public boolean deleteById(int id) {
        try (Connection con = dataBase.getConnection();
                PreparedStatement ps = con.prepareStatement(DELETE)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error al eliminar el aspirante.", e);
        }
    }

    private void fillParameters(PreparedStatement ps, Applicant applicant) throws SQLException {
        ps.setString(1, applicant.getFirstName());
        ps.setString(2, applicant.getLastName());
        ps.setString(3, applicant.getEmail());
        ps.setString(4, applicant.getPhone());
        ps.setString(5, applicant.getAddress());
        ps.setInt(6, applicant.getAge());
    }

    private Applicant map(ResultSet rs) throws SQLException {
        return new Applicant(
                rs.getInt("applicant_id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("email"),
                rs.getString("phone"),
                rs.getString("address"),
                rs.getInt("age"));
    }
}
