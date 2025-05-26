package com.managerlibrary.daos.interfaces;

import com.managerlibrary.entities.Loan;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public interface LoanDAO {

    void insertLoan(Loan loan) throws SQLException;

    Loan findLoanByIdWithDetails(int id) throws SQLException;

    List<Loan> findAllLoans() throws SQLException;

    void updateLoan(Loan loan) throws SQLException;

    void deleteLoan(int id) throws SQLException;

    List<Loan> findAllLoansWithBookAndUser() throws SQLException;

    void markAsReturned(int loanId, LocalDate returnDate) throws SQLException;

    List<Loan> findLoansByUserId(int userId) throws SQLException;

    List<Loan> findLoansByBookId(int bookId) throws SQLException;

    // Métodos adicionais (alguns já existiam, mas o foco agora é a implementação)
    List<Loan> findLoansByStatus(String status) throws SQLException; // Mantém este, mas não será o principal para Ativos/Devolvidos

    // NOVO: Métodos específicos para buscar empréstimos por status via banco de dados
    List<Loan> findActiveLoansWithDetails() throws SQLException; // <--- NOVO
    List<Loan> findReturnedLoansWithDetails() throws SQLException; // <--- NOVO
    List<Loan> findOverdueLoansWithDetails() throws SQLException; // <--- RENOMEADO/MELHORADO
    List<Loan> searchLoansByBookTitleOrUserNameOrUserCpf(String searchTerm) throws SQLException; // <--- NOVO (para pesquisa)
}