package com.managerlibrary.entities;

import javafx.beans.property.*;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Representa um empréstimo de livro no sistema da biblioteca.
 */
public class Loan {

    private IntegerProperty id;
    private ObjectProperty<Book> book;
    private ObjectProperty<User> user;
    private ObjectProperty<LocalDate> loanDate;
    private ObjectProperty<LocalDate> returnDate;
    private ObjectProperty<LocalDate> actualReturnDate; // Data de devolução efetiva
    private StringProperty status; // Ativo, Devolvido, Atrasado
    private DoubleProperty fine; // Valor da multa
    private BooleanProperty returned; // Novo atributo para indicar se o livro foi devolvido

    public Loan() {
        this.id = new SimpleIntegerProperty();
        this.book = new SimpleObjectProperty<>();
        this.user = new SimpleObjectProperty<>();
        this.loanDate = new SimpleObjectProperty<>();
        this.returnDate = new SimpleObjectProperty<>();
        this.actualReturnDate = new SimpleObjectProperty<>();
        this.status = new SimpleStringProperty();
        this.fine = new SimpleDoubleProperty(0.0); // Inicializa a multa com 0.0
        this.returned = new SimpleBooleanProperty(false); // Inicializa como não devolvido
    }

    public Loan(int id, Book book, User user, LocalDate loanDate, LocalDate returnDate, LocalDate actualReturnDate, String status, double fine, boolean returned) {
        this.id = new SimpleIntegerProperty(id);
        this.book = new SimpleObjectProperty<>(book);
        this.user = new SimpleObjectProperty<>(user);
        this.loanDate = new SimpleObjectProperty<>(loanDate);
        this.returnDate = new SimpleObjectProperty<>(returnDate);
        this.actualReturnDate = new SimpleObjectProperty<>(actualReturnDate);
        this.status = new SimpleStringProperty(status);
        this.fine = new SimpleDoubleProperty(fine);
        this.returned = new SimpleBooleanProperty(returned); // Inicializa o status de devolução
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

    public ObjectProperty<Book> bookProperty() {
        return book;
    }

    public Book getBook() {
        return book.get();
    }

    public void setBook(Book book) {
        this.book.set(book);
    }

    public ObjectProperty<User> userProperty() {
        return user;
    }

    public User getUser() {
        return user.get();
    }

    public void setUser(User user) {
        this.user.set(user);
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

    public BooleanProperty returnedProperty() {
        return returned;
    }

    public boolean isReturned() {
        return returned.get();
    }

    public void setReturned(boolean returned) {
        this.returned.set(returned);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Loan loan = (Loan) o;
        return Objects.equals(id, loan.id) && Objects.equals(book, loan.book) && Objects.equals(user, loan.user) && Objects.equals(loanDate, loan.loanDate) && Objects.equals(returnDate, loan.returnDate) && Objects.equals(actualReturnDate, loan.actualReturnDate) && Objects.equals(status, loan.status) && Objects.equals(fine, loan.fine) && Objects.equals(returned, loan.returned);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, book, user, loanDate, returnDate, actualReturnDate, status, fine, returned);
    }

    @Override
    public String toString() {
        return "Loan{" +
                "id=" + id +
                ", book=" + (book.get() != null ? book.get().getTitle() : null) +
                ", user=" + (user.get() != null ? user.get().getName() : null) +
                ", loanDate=" + loanDate +
                ", returnDate=" + returnDate +
                ", actualReturnDate=" + actualReturnDate +
                ", status=" + status +
                ", fine=" + fine +
                ", returned=" + returned +
                '}';
    }
}