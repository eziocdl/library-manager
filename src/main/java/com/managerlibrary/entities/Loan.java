package com.managerlibrary.entities;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Representa um empréstimo de livro no sistema da biblioteca.
 */
public class Loan {

    private int id;
    private int bookId;
    private int userId;
    private LocalDate loanDate;
    private LocalDate returnDate;

    public Loan() {

    }

    /**
     * Construtor com todos os argumentos.
     *
     * @param id         o ID do empréstimo
     * @param bookId     o ID do livro emprestado
     * @param userId     o ID do usuário que pegou o livro emprestado
     * @param loanDate   a data em que o livro foi emprestado
     * @param returnDate a data de devolução do livro
     */

    public Loan(int id, int bookId, int userId, LocalDate loanDate, LocalDate returnDate) {
        this.id = id;
        this.bookId = bookId;
        this.userId = userId;
        this.loanDate = loanDate;
        this.returnDate = returnDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Loan loan = (Loan) o;
        return id == loan.id && bookId == loan.bookId && userId == loan.userId && Objects.equals(loanDate, loan.loanDate) && Objects.equals(returnDate, loan.returnDate);
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
