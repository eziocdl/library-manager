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

    // Novas propriedades para Book e User
    private ObjectProperty<Book> book;
    private ObjectProperty<User> user;

    public Loan() {
        this.id = new SimpleIntegerProperty();
        this.bookId = new SimpleIntegerProperty();
        this.userId = new SimpleIntegerProperty();
        this.loanDate = new SimpleObjectProperty<>();
        this.returnDate = new SimpleObjectProperty<>();
        this.actualReturnDate = new SimpleObjectProperty<>();
        this.status = new SimpleStringProperty();
        this.fine = new SimpleDoubleProperty(0.0); // Inicializa a multa com 0.0
        this.book = new SimpleObjectProperty<>(); // Inicializa a propriedade book
        this.user = new SimpleObjectProperty<>(); // Inicializa a propriedade user
    }

    public Loan(int id, int bookId, int userId, LocalDate loanDate, LocalDate returnDate, LocalDate actualReturnDate, String status, double fine, Book book, User user) {
        this.id = new SimpleIntegerProperty(id);
        this.bookId = new SimpleIntegerProperty(bookId);
        this.userId = new SimpleIntegerProperty(userId);
        this.loanDate = new SimpleObjectProperty<>(loanDate);
        this.returnDate = new SimpleObjectProperty<>(returnDate);
        this.actualReturnDate = new SimpleObjectProperty<>(actualReturnDate);
        this.status = new SimpleStringProperty(status);
        this.fine = new SimpleDoubleProperty(fine);
        this.book = new SimpleObjectProperty<>(book); // Inicializa a propriedade book
        this.user = new SimpleObjectProperty<>(user); // Inicializa a propriedade user
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

    // Getters e Setters para Book
    public ObjectProperty<Book> bookProperty() {
        return book;
    }

    public Book getBook() {
        return book.get();
    }

    public void setBook(Book book) {
        this.book.set(book);
    }

    // Getters e Setters para User
    public ObjectProperty<User> userProperty() {
        return user;
    }

    public User getUser() {
        return user.get();
    }

    public void setUser(User user) {
        this.user.set(user);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Loan loan = (Loan) o;
        return Objects.equals(id, loan.id) && Objects.equals(bookId, loan.bookId) && Objects.equals(userId, loan.userId) && Objects.equals(loanDate, loan.loanDate) && Objects.equals(returnDate, loan.returnDate) && Objects.equals(actualReturnDate, loan.actualReturnDate) && Objects.equals(status, loan.status) && Objects.equals(fine, loan.fine) && Objects.equals(book, loan.book) && Objects.equals(user, loan.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, bookId, userId, loanDate, returnDate, actualReturnDate, status, fine, book, user);
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
                ", book=" + (book.get() != null ? book.get().getTitle() : null) + // Exibe o título do livro
                ", user=" + (user.get() != null ? user.get().getName() : null) + // Exibe o nome do usuário
                '}';
    }
}