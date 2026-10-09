package com.kinal.recruitment.service;

import com.kinal.recruitment.dto.ApplicantRequest;
import com.kinal.recruitment.dto.ApplicantResponse;
import java.util.List;

public interface ApplicantService {

    List<ApplicantResponse> findAll();

    ApplicantResponse create(ApplicantRequest request);

    ApplicantResponse update(int id, ApplicantRequest request);

    void delete(int id);
}
