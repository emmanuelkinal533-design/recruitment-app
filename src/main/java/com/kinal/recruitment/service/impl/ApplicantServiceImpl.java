package com.kinal.recruitment.service.impl;

import com.kinal.recruitment.dto.ApplicantRequest;
import com.kinal.recruitment.dto.ApplicantResponse;
import com.kinal.recruitment.exception.ResourceNotFoundException;
import com.kinal.recruitment.exception.ValidationException;
import com.kinal.recruitment.model.Applicant;
import com.kinal.recruitment.repository.ApplicantRepository;
import com.kinal.recruitment.service.ApplicantService;
import java.util.List;
import java.util.regex.Pattern;

public class ApplicantServiceImpl implements ApplicantService {

    private static final int MIN_AGE = 18;
    private static final int MAX_AGE = 100;
    private static final Pattern EMAIL = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");
    private static final Pattern PHONE = Pattern.compile("^[0-9+()\\- ]{7,20}$");

    private final ApplicantRepository applicantRepository;

    public ApplicantServiceImpl(ApplicantRepository applicantRepository) {
        this.applicantRepository = applicantRepository;
    }

    @Override
    public List<ApplicantResponse> findAll() {
        return applicantRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public ApplicantResponse create(ApplicantRequest request) {
        Applicant applicant = toValidEntity(0, request);
        return toResponse(applicantRepository.save(applicant));
    }

    @Override
    public ApplicantResponse update(int id, ApplicantRequest request) {
        Applicant applicant = toValidEntity(id, request);
        if (!applicantRepository.update(applicant)) {
            throw new ResourceNotFoundException("El aspirante ya no existe.");
        }
        return toResponse(applicant);
    }

    @Override
    public void delete(int id) {
        if (!applicantRepository.deleteById(id)) {
            throw new ResourceNotFoundException("El aspirante ya no existe.");
        }
    }

    private Applicant toValidEntity(int id, ApplicantRequest request) {
        String firstName = clean(request.firstName());
        String lastName = clean(request.lastName());
        String email = clean(request.email()).toLowerCase();
        String phone = clean(request.phone());
        String address = clean(request.address());

        if (firstName.isEmpty() || lastName.isEmpty()) {
            throw new ValidationException("El nombre y el apellido son obligatorios.");
        }
        if (firstName.length() > 50 || lastName.length() > 50) {
            throw new ValidationException("El nombre y el apellido pueden tener máximo 50 caracteres.");
        }
        if (email.length() > 100 || !EMAIL.matcher(email).matches()) {
            throw new ValidationException("Ingresa un correo válido.");
        }
        if (!phone.isEmpty() && !PHONE.matcher(phone).matches()) {
            throw new ValidationException("El teléfono solo puede tener números, +, -, paréntesis y espacios (7 a 20 caracteres).");
        }
        if (address.length() > 255) {
            throw new ValidationException("La dirección puede tener máximo 255 caracteres.");
        }
        int age = parseAge(request.age());

        applicantRepository.findByEmail(email)
                .filter(other -> other.getId() != id)
                .ifPresent(other -> {
                    throw new ValidationException("Ya existe un aspirante con ese correo.");
                });

        return new Applicant(id, firstName, lastName, email,
                phone.isEmpty() ? null : phone,
                address.isEmpty() ? null : address,
                age);
    }

    private int parseAge(String text) {
        int age;
        try {
            age = Integer.parseInt(clean(text));
        } catch (NumberFormatException e) {
            throw new ValidationException("La edad debe ser un número entero.");
        }
        if (age < MIN_AGE || age > MAX_AGE) {
            throw new ValidationException("La edad debe estar entre " + MIN_AGE + " y " + MAX_AGE + " años.");
        }
        return age;
    }

    private ApplicantResponse toResponse(Applicant applicant) {
        return new ApplicantResponse(applicant.getId(), applicant.getFirstName(), applicant.getLastName(),
                applicant.getEmail(), nullToEmpty(applicant.getPhone()), nullToEmpty(applicant.getAddress()),
                applicant.getAge());
    }

    private String clean(String value) {
        return value == null ? "" : value.trim();
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
