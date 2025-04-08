package com.managerlibrary.controllers;

import com.managerlibrary.entities.Loan;
import com.managerlibrary.services.LoanService;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.awt.*;
import java.util.Date;

public class LoanController {

    @FXML
    private TextField bookIdTextField;
    @FXML
    private TextField userIdTextField;
    @FXML
    private DatePicker loanDatePicker;
    @FXML
    private DatePicker returnDatePicker;

    @FXML
    private TableView<Loan> loansTableView;

    private TableColumn<Loan, Integer> idColumn;
    private TableColumn<Loan, Integer> bookIdColumn;
    private TableColumn<Loan, Integer> userIdColumn;
    private TableColumn<Loan, Date> loanDateColumn;
    private TableColumn<Loan, Date> returnDateColumn;

    private LoanService loanService = new LoanService();

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(cellData -> CellData.getValue().idProperty().asObject());
        bookIdColumn.setCellValueFactory(cellData -> cellData.getValue().bookIdProperty().asObject());
        userIdColumn.setCellValueFactory(cellData -> cellData.getValue().userIdProperty().asObject());
        loanDateColumn.setCellValueFactory(cellData -> cellData.getValue().loanDateProperty());
        returnDateColumn.setCellValueFactory(cellData -> cellData.getValue().returnDateProperty());
        returnDateColumn.setCellValueFactory(cellData -> cellData.getValue().returnDateProperty());

        loadingLoans();
    }
}

