package com.managerlibrary.entities;

import javafx.beans.property.*;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Representa um empréstimo de livro no sistema da biblioteca.
 */
public class Loan {

    private IntegerProperty id;
    private IntegerProperty bookId;
    private IntegerProperty userId;
    private ObjectProperty<LocalDate> loanDate;
    private ObjectProperty<LocalDate> returnDate;
    private ObjectProperty<LocalDate> actualReturnDate; // Data de devolução efetiva
    private StringProperty status; // Ativo, Devolvido, Atrasado
    private DoubleProperty fine; // Valor da multa

    public Loan() {
        this.id = new SimpleIntegerProperty();
        this.bookId = new SimpleIntegerProperty();
        this.userId = new SimpleIntegerProperty();
        this.loanDate = new SimpleObjectProperty<>();
        this.returnDate = new SimpleObjectProperty<>();
        this.actualReturnDate = new SimpleObjectProperty<>();
        this.status = new SimpleStringProperty();
        this.fine = new SimpleDoubleProperty(0.0); // Inicializa a multa com 0.0
    }

    public Loan(int id, int bookId, int userId, LocalDate loanDate, LocalDate returnDate, LocalDate actualReturnDate, String status, double fine) {
        this.id = new SimpleIntegerProperty(id);
        this.bookId = new SimpleIntegerProperty(bookId);
        this.userId = new SimpleIntegerProperty(userId);
        this.loanDate = new SimpleObjectProperty<>(loanDate);
        this.returnDate = new SimpleObjectProperty<>(returnDate);
        this.actualReturnDate = new SimpleObjectProperty<>(actualReturnDate);
        this.status = new SimpleStringProperty(status);
        this.fine = new SimpleDoubleProperty(fine);
    }

    // Getters e Setters para todos os atributos (incluindo actualReturnDate, status e fine)

    public ObjectProperty<LocalDate> actualReturnDateProperty() {
        return actualReturnDate;
    }

    public LocalDate getActualReturnDate() {
        return actualReturnDate.get();
    }

    public void setActualReturnDate(LocalDate actualReturnDate) {
        this.actualReturnDate.set(actualReturnDate);
    }

    public StringProperty statusProperty() {
        return status;
    }

    public String getStatus() {
        return status.get();
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    public DoubleProperty fineProperty() {
        return fine;
    }

    public double getFine() {
        return fine.get();
    }

    public void setFine(double fine) {
        this.fine.set(fine);
    }

    // Getters e setters para id, bookId, userId, loanDate, returnDate (já existentes)

    public IntegerProperty idProperty() {
        return id;
    }

    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public IntegerProperty bookIdProperty() {
        return bookId;
    }

    public int getBookId() {
        return bookId.get();
    }

    public void setBookId(int bookId) {
        this.bookId.set(bookId);
    }

    public IntegerProperty userIdProperty() {
        return userId;
    }

    public int getUserId() {
        return userId.get();
    }

    public void setUserId(int userId) {
        this.userId.set(userId);
    }

    public ObjectProperty<LocalDate> loanDateProperty() {
        return loanDate;
    }

    public LocalDate getLoanDate() {
        return loanDate.get();
    }

    public void setLoanDate(LocalDate loanDate) {
        this.loanDate.set(loanDate);
    }

    public ObjectProperty<LocalDate> returnDateProperty() {
        return returnDate;
    }

    public LocalDate getReturnDate() {
        return returnDate.get();
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate.set(returnDate);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Loan loan = (Loan) o;
        return Objects.equals(id, loan.id) && Objects.equals(bookId, loan.bookId) && Objects.equals(userId, loan.userId) && Objects.equals(loanDate, loan.loanDate) && Objects.equals(returnDate, loan.returnDate) && Objects.equals(actualReturnDate, loan.actualReturnDate) && Objects.equals(status, loan.status) && Objects.equals(fine, loan.fine);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, bookId, userId, loanDate, returnDate, actualReturnDate, status, fine);
    }

    @Override
    public String toString() {
        return "Loan{" +
                "id=" + id +
                ", bookId=" + bookId +
                ", userId=" + userId +
                ", loanDate=" + loanDate +
                ", returnDate=" + returnDate +
                ", actualReturnDate=" + actualReturnDate +
                ", status=" + status +
                ", fine=" + fine +
                '}';
    }
}

