package com.kinal.recruitment.controller;

import com.kinal.recruitment.dto.ApplicantRequest;
import com.kinal.recruitment.dto.ApplicantResponse;
import com.kinal.recruitment.dto.UserResponse;
import com.kinal.recruitment.exception.AppException;
import com.kinal.recruitment.service.ApplicantService;
import com.kinal.recruitment.util.Alerts;
import com.kinal.recruitment.util.SceneManager;
import com.kinal.recruitment.util.SessionManager;
import com.kinal.recruitment.util.View;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class ApplicantController implements Initializable {

    private final ApplicantService applicantService;
    private final SessionManager session;
    private final SceneManager sceneManager;

    @FXML
    private Label lblUser;
    @FXML
    private Label lblCount;
    @FXML
    private TableView<ApplicantResponse> tblApplicants;
    @FXML
    private TableColumn<ApplicantResponse, Integer> colId;
    @FXML
    private TableColumn<ApplicantResponse, String> colFirstName;
    @FXML
    private TableColumn<ApplicantResponse, String> colLastName;
    @FXML
    private TableColumn<ApplicantResponse, String> colEmail;
    @FXML
    private TableColumn<ApplicantResponse, String> colPhone;
    @FXML
    private TableColumn<ApplicantResponse, String> colAddress;
    @FXML
    private TableColumn<ApplicantResponse, Integer> colAge;
    @FXML
    private TextField txtFirstName;
    @FXML
    private TextField txtLastName;
    @FXML
    private TextField txtEmail;
    @FXML
    private TextField txtPhone;
    @FXML
    private TextField txtAddress;
    @FXML
    private TextField txtAge;
    @FXML
    private Label lblMessage;
    @FXML
    private Button btnUpdate;
    @FXML
    private Button btnDelete;

    public ApplicantController(ApplicantService applicantService, SessionManager session,
            SceneManager sceneManager) {
        this.applicantService = applicantService;
        this.session = session;
        this.sceneManager = sceneManager;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        session.getCurrentUser().ifPresent(this::showCurrentUser);

        colId.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().id()));
        colFirstName.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().firstName()));
        colLastName.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().lastName()));
        colEmail.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().email()));
        colPhone.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().phone()));
        colAddress.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().address()));
        colAge.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().age()));

        tblApplicants.getSelectionModel().selectedItemProperty()
                .addListener((obs, previous, selected) -> fillForm(selected));
        btnUpdate.disableProperty().bind(tblApplicants.getSelectionModel().selectedItemProperty().isNull());
        btnDelete.disableProperty().bind(tblApplicants.getSelectionModel().selectedItemProperty().isNull());

        loadTable();
    }

    @FXML
    private void handleAdd() {
        runAction(() -> {
            applicantService.create(readForm());
            clearForm();
            loadTable();
            showSuccess("Aspirante agregado.");
        });
    }

    @FXML
    private void handleUpdate() {
        ApplicantResponse selected = tblApplicants.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }
        runAction(() -> {
            applicantService.update(selected.id(), readForm());
            clearForm();
            loadTable();
            showSuccess("Aspirante actualizado.");
        });
    }

    @FXML
    private void handleDelete() {
        ApplicantResponse selected = tblApplicants.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }
        if (!Alerts.confirm("Eliminar aspirante", "¿Eliminar a " + selected.fullName() + "?")) {
            return;
        }
        runAction(() -> {
            applicantService.delete(selected.id());
            clearForm();
            loadTable();
            showSuccess("Aspirante eliminado.");
        });
    }

    @FXML
    private void handleClear() {
        clearForm();
    }

    @FXML
    private void handleLogout() {
        session.end();
        sceneManager.show(View.LOGIN);
    }

    private void loadTable() {
        runAction(() -> {
            tblApplicants.setItems(FXCollections.observableArrayList(applicantService.findAll()));
            lblCount.setText(tblApplicants.getItems().size() + " aspirantes");
        });
    }

    private void showCurrentUser(UserResponse user) {
        lblUser.setText(user.username() + "  |  " + user.role());
    }

    private void fillForm(ApplicantResponse applicant) {
        if (applicant == null) {
            return;
        }
        txtFirstName.setText(applicant.firstName());
        txtLastName.setText(applicant.lastName());
        txtEmail.setText(applicant.email());
        txtPhone.setText(applicant.phone());
        txtAddress.setText(applicant.address());
        txtAge.setText(String.valueOf(applicant.age()));
        lblMessage.setText("");
    }

    private void clearForm() {
        tblApplicants.getSelectionModel().clearSelection();
        txtFirstName.clear();
        txtLastName.clear();
        txtEmail.clear();
        txtPhone.clear();
        txtAddress.clear();
        txtAge.clear();
        lblMessage.setText("");
    }

    private ApplicantRequest readForm() {
        return new ApplicantRequest(txtFirstName.getText(), txtLastName.getText(), txtEmail.getText(),
                txtPhone.getText(), txtAddress.getText(), txtAge.getText());
    }

    private void runAction(Runnable action) {
        try {
            action.run();
        } catch (AppException e) {
            Alerts.showException(e);
        }
    }

    private void showSuccess(String text) {
        lblMessage.setText(text);
    }
}
