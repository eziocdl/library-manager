// com/managerlibrary/entities/Loan.java
package com.managerlibrary.entities;

import java.time.LocalDate;

public class Loan {
    private int id;
    private Book book;
    private User user;
    private LocalDate loanDate;
    private LocalDate returnDate;
    private LocalDate actualReturnDate;
    private String status;
    private double fine;
    private boolean returned; // Adicionado para consistência com o método markAsReturned

    // Construtores
    public Loan() {
    }

    public Loan(int id, Book book, User user, LocalDate loanDate, LocalDate returnDate, LocalDate actualReturnDate, String status, double fine, boolean returned) {
        this.id = id;
        this.book = book;
        this.user = user;
        this.loanDate = loanDate;
        this.returnDate = returnDate;
        this.actualReturnDate = actualReturnDate;
        this.status = status;
        this.fine = fine;
        this.returned = returned;
    }

    // Getters e Setters
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

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
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

    public boolean isReturned() {
        return returned;
    }

    public void setReturned(boolean returned) {
        this.returned = returned;
    }
}