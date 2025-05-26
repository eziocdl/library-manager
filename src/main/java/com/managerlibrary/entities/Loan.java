package com.managerlibrary.entities;

import java.time.LocalDate;

public class Loan {
    private int id;
    private Book book;
    private User user;
    private LocalDate loanDate;
    private LocalDate expectedReturnDate; // Renomeado de 'returnDate'
    private LocalDate actualReturnDate;
    private String status;
    private double fine;
    private boolean returned; // Novo campo para indicar se foi devolvido

    // Construtor padrão (necessário para alguns frameworks como JPA)
    public Loan() {
    }

    // CONSTRUTOR COMPLETO - Inclui todos os campos, inclusive 'returned'
    public Loan(int id, Book book, User user, LocalDate loanDate, LocalDate expectedReturnDate, LocalDate actualReturnDate, String status, double fine, boolean returned) {
        this.id = id;
        this.book = book;
        this.user = user;
        this.loanDate = loanDate;
        this.expectedReturnDate = expectedReturnDate;
        this.actualReturnDate = actualReturnDate;
        this.status = status;
        this.fine = fine;
        this.returned = returned; // Inicializa o novo campo
    }

    // --- Getters e Setters ---

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDate getLoanDate() {
        return loanDate;
    }

    public void setLoanDate(LocalDate loanDate) {
        this.loanDate = loanDate;
    }

    public LocalDate getExpectedReturnDate() {
        return expectedReturnDate;
    }

    public void setExpectedReturnDate(LocalDate expectedReturnDate) {
        this.expectedReturnDate = expectedReturnDate;
    }

    public LocalDate getActualReturnDate() {
        return actualReturnDate;
    }

    public void setActualReturnDate(LocalDate actualReturnDate) {
        this.actualReturnDate = actualReturnDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getFine() {
        return fine;
    }

    public void setFine(double fine) {
        this.fine = fine;
    }

    // Getter e Setter para o novo campo 'returned'
    public boolean isReturned() {
        return returned;
    }

    public void setReturned(boolean returned) {
        this.returned = returned;
    }

    // Opcional: ajustar toString() para incluir 'returned'
    @Override
    public String toString() {
        return "Loan{" +
                "id=" + id +
                ", book=" + (book != null ? book.getTitle() : "N/A") +
                ", user=" + (user != null ? user.getName() : "N/A") +
                ", loanDate=" + loanDate +
                ", expectedReturnDate=" + expectedReturnDate +
                ", actualReturnDate=" + actualReturnDate +
                ", status='" + status + '\'' +
                ", fine=" + fine +
                ", returned=" + returned +
                '}';
    }
}