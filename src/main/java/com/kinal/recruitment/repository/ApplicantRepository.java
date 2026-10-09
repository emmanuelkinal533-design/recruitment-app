package com.kinal.recruitment.repository;

import com.kinal.recruitment.model.Applicant;
import java.util.List;
import java.util.Optional;

public interface ApplicantRepository {

    List<Applicant> findAll();

    Optional<Applicant> findByEmail(String email);

    Applicant save(Applicant applicant);

    boolean update(Applicant applicant);

    boolean deleteById(int id);
}
