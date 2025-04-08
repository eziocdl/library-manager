package com.managerlibrary.entities;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

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
    private ObjectProperty returnDate;

    public Loan() {

        this.id = new SimpleIntegerProperty();
        this.bookId = new SimpleIntegerProperty();
        this.userId = new SimpleIntegerProperty();
        this.loanDate = new SimpleObjectProperty<>();
        this.returnDate = new SimpleObjectProperty<>();

    }


    public Loan(int id, int bookId, int userId, LocalDate loanDate, LocalDate returnDate) {
        this.id = new SimpleIntegerProperty(id);
        this.bookId = new SimpleIntegerProperty(bookId);
        this.userId = new SimpleIntegerProperty(userId);
        this.loanDate = new SimpleObjectProperty<>(loanDate);
        this.returnDate = new SimpleObjectProperty<>(returnDate);
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public int getBookId() {
        return bookId.get();
    }

    public IntegerProperty bookIdProperty() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId.set(bookId);
    }

    public int getUserId() {
        return userId.get();
    }

    public IntegerProperty userIdProperty() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId.set(userId);
    }

    public LocalDate getLoanDate() {
        return loanDate.get();
    }

    public ObjectProperty<LocalDate> loanDateProperty() {
        return loanDate;
    }

    public void setLoanDate(LocalDate loanDate) {
        this.loanDate.set(loanDate);
    }

    public Object getReturnDate() {
        return returnDate.get();
    }

    public ObjectProperty returnDateProperty() {
        return returnDate;
    }

    public void setReturnDate(Object returnDate) {
        this.returnDate.set(returnDate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Loan loan = (Loan) o;
        return Objects.equals(id, loan.id) && Objects.equals(bookId, loan.bookId) && Objects.equals(userId, loan.userId) && Objects.equals(loanDate, loan.loanDate) && Objects.equals(returnDate, loan.returnDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, bookId, userId, loanDate, returnDate);
    }

    @Override
    public String toString() {
        return "Loan{" +
                "id=" + id +
                ", bookId=" + bookId +
                ", userId=" + userId +
                ", loanDate=" + loanDate +
                ", returnDate=" + returnDate +
                '}';
    }
}
