package com.managerlibrary.daos.implement;

import com.managerlibrary.daos.interfaces.LoanDAO;
import com.managerlibrary.entities.Book;
import com.managerlibrary.entities.Loan;
import com.managerlibrary.entities.User;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LoanDAOImpl implements LoanDAO {

    private Connection connection;

    public LoanDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void insertLoan(Loan loan) throws SQLException {
        // CORRIGIDO: Adicionado 'returned' na query SQL
        String sql = "INSERT INTO loan (book_id, user_id, loan_date, return_date, actual_return_date, status, fine, returned) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, loan.getBook().getId());
            pstmt.setInt(2, loan.getUser().getId());
            pstmt.setDate(3, Date.valueOf(loan.getLoanDate()));
            pstmt.setDate(4, Date.valueOf(loan.getExpectedReturnDate()));
            pstmt.setDate(5, loan.getActualReturnDate() == null ? null : Date.valueOf(loan.getActualReturnDate()));
            pstmt.setString(6, loan.getStatus());
            pstmt.setDouble(7, loan.getFine());
            pstmt.setBoolean(8, loan.isReturned()); // NOVO: Define o valor de 'returned'
            pstmt.executeUpdate();

            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                loan.setId(generatedKeys.getInt(1));
            }
        }
    }

    @Override
    public Loan findLoanByIdWithDetails(int id) throws SQLException {
        String sql = "SELECT l.id AS loan_id, l.book_id, l.user_id, l.loan_date, l.return_date, l.actual_return_date, l.status, l.fine, l.returned, " + // NOVO: Seleciona 'returned'
                "b.id AS b_id, b.title AS book_title, b.author AS book_author, b.isbn AS book_isbn, b.genre AS book_genre, b.total_copies AS book_total_copies, b.available_copies AS book_available_copies, b.publisher AS book_publisher, b.year AS book_year, b.image_url AS book_image_url, b.cover_image_path AS book_cover_image_path, " +
                "u.id AS u_id, u.name AS user_name, u.cpf AS user_cpf, u.email AS user_email, u.phone AS user_phone, u.address AS user_address, u.profile_image_path AS user_profile_image_path " +
                "FROM loan l " +
                "JOIN books b ON l.book_id = b.id " +
                "JOIN \"users\" u ON l.user_id = u.id " +
                "WHERE l.id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToLoanWithDetails(rs);
            }
            return null;
        }
    }

    @Override
    public List<Loan> findAllLoans() throws SQLException {
        // Este método geralmente não precisa trazer os detalhes completos de Book/User
        // se o seu uso principal for uma lista simples. Se o getAllLoans() do LoanService
        // chama findAllLoans(), então você deve decidir se ele precisa de detalhes ou não.
        // Para fins de UI, provavelmente você sempre quer detalhes, então o método
        // findAllLoansWithBookAndUser é mais adequado.
        // Se você precisa de uma lista "leve", remova o join aqui e apenas instancie Book/User com IDs.
        // Por simplicidade e para a UI, vou chamar findAllLoansWithBookAndUser()
        return findAllLoansWithBookAndUser();
    }


    @Override
    public void updateLoan(Loan loan) throws SQLException {
        // CORRIGIDO: Adicionado 'returned' na query SQL
        String sql = "UPDATE loan SET book_id = ?, user_id = ?, loan_date = ?, return_date = ?, actual_return_date = ?, status = ?, fine = ?, returned = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, loan.getBook().getId());
            pstmt.setInt(2, loan.getUser().getId());
            pstmt.setDate(3, Date.valueOf(loan.getLoanDate()));
            pstmt.setDate(4, Date.valueOf(loan.getExpectedReturnDate()));
            pstmt.setDate(5, loan.getActualReturnDate() == null ? null : Date.valueOf(loan.getActualReturnDate()));
            pstmt.setString(6, loan.getStatus());
            pstmt.setDouble(7, loan.getFine());
            pstmt.setBoolean(8, loan.isReturned()); // NOVO: Atualiza o valor de 'returned'
            pstmt.setInt(9, loan.getId()); // ID é o 9º parâmetro agora
            pstmt.executeUpdate();
        }
    }

    @Override
    public void deleteLoan(int id) throws SQLException {
        String sql = "DELETE FROM loan WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    @Override
    public void markAsReturned(int loanId, LocalDate returnDate) throws SQLException {
        // CORRIGIDO: Adicionado 'returned = TRUE' na query SQL
        String sql = "UPDATE loan SET actual_return_date = ?, status = 'Devolvido', returned = TRUE WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDate(1, Date.valueOf(returnDate));
            pstmt.setInt(2, loanId);
            pstmt.executeUpdate();
        }
    }

    @Override
    public List<Loan> findAllLoansWithBookAndUser() throws SQLException {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT l.id AS loan_id, l.book_id, l.user_id, l.loan_date, l.return_date, l.actual_return_date, l.status, l.fine, l.returned, " + // NOVO: Seleciona 'returned'
                "b.id AS b_id, b.title AS book_title, b.author AS book_author, b.isbn AS book_isbn, b.genre AS book_genre, b.total_copies AS book_total_copies, b.available_copies AS book_available_copies, b.publisher AS book_publisher, b.year AS book_year, b.image_url AS book_image_url, b.cover_image_path AS book_cover_image_path, " +
                "u.id AS u_id, u.name AS user_name, u.cpf AS user_cpf, u.email AS user_email, u.phone AS user_phone, u.address AS user_address, u.profile_image_path AS user_profile_image_path " +
                "FROM loan l " +
                "JOIN books b ON l.book_id = b.id " +
                "JOIN \"users\" u ON l.user_id = u.id";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                loans.add(mapResultSetToLoanWithDetails(rs));
            }
        }
        return loans;
    }

    @Override
    public List<Loan> findLoansByUserId(int userId) throws SQLException {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT l.id AS loan_id, l.book_id, l.user_id, l.loan_date, l.return_date, l.actual_return_date, l.status, l.fine, l.returned, " + // NOVO: Seleciona 'returned'
                "b.id AS b_id, b.title AS book_title, b.author AS book_author, b.isbn AS book_isbn, b.genre AS book_genre, b.total_copies AS book_total_copies, b.available_copies AS book_available_copies, b.publisher AS book_publisher, b.year AS book_year, b.image_url AS book_image_url, b.cover_image_path AS book_cover_image_path, " +
                "u.id AS u_id, u.name AS user_name, u.cpf AS user_cpf, u.email AS user_email, u.phone AS user_phone, u.address AS user_address, u.profile_image_path AS user_profile_image_path " +
                "FROM loan l " +
                "JOIN books b ON l.book_id = b.id " +
                "JOIN \"users\" u ON l.user_id = u.id " +
                "WHERE l.user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                loans.add(mapResultSetToLoanWithDetails(rs));
            }
        }
        return loans;
    }

    @Override
    public List<Loan> findLoansByBookId(int bookId) throws SQLException {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT l.id AS loan_id, l.book_id, l.user_id, l.loan_date, l.return_date, l.actual_return_date, l.status, l.fine, l.returned, " + // NOVO: Seleciona 'returned'
                "b.id AS b_id, b.title AS book_title, b.author AS book_author, b.isbn AS book_isbn, b.genre AS book_genre, b.total_copies AS book_total_copies, b.available_copies AS book_available_copies, b.publisher AS book_publisher, b.year AS book_year, b.image_url AS book_image_url, b.cover_image_path AS book_cover_image_path, " +
                "u.id AS u_id, u.name AS user_name, u.cpf AS user_cpf, u.email AS user_email, u.phone AS user_phone, u.address AS user_address, u.profile_image_path AS user_profile_image_path " +
                "FROM loan l " +
                "JOIN books b ON l.book_id = b.id " +
                "JOIN \"users\" u ON l.user_id = u.id " +
                "WHERE l.book_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                loans.add(mapResultSetToLoanWithDetails(rs));
            }
        }
        return loans;
    }

    @Override
    public List<Loan> findLoansByStatus(String status) throws SQLException {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT l.id AS loan_id, l.book_id, l.user_id, l.loan_date, l.return_date, l.actual_return_date, l.status, l.fine, l.returned, " + // NOVO: Seleciona 'returned'
                "b.id AS b_id, b.title AS book_title, b.author AS book_author, b.isbn AS book_isbn, b.genre AS book_genre, b.total_copies AS book_total_copies, b.available_copies AS book_available_copies, b.publisher AS book_publisher, b.year AS book_year, b.image_url AS book_image_url, b.cover_image_path AS book_cover_image_path, " +
                "u.id AS u_id, u.name AS user_name, u.cpf AS user_cpf, u.email AS user_email, u.phone AS user_phone, u.address AS user_address, u.profile_image_path AS user_profile_image_path " +
                "FROM loan l " +
                "JOIN books b ON l.book_id = b.id " +
                "JOIN \"users\" u ON l.user_id = u.id " +
                "WHERE l.status = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, status);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                loans.add(mapResultSetToLoanWithDetails(rs));
            }
        }
        return loans;
    }

    // --- NOVAS IMPLEMENTAÇÕES DE FILTRAGEM VIA BANCO DE DADOS (conforme a interface atualizada) ---

    @Override
    public List<Loan> findActiveLoansWithDetails() throws SQLException {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT l.id AS loan_id, l.book_id, l.user_id, l.loan_date, l.return_date, l.actual_return_date, l.status, l.fine, l.returned, " +
                "b.id AS b_id, b.title AS book_title, b.author AS book_author, b.isbn AS book_isbn, b.genre AS book_genre, b.total_copies AS book_total_copies, b.available_copies AS book_available_copies, b.publisher AS book_publisher, b.year AS book_year, b.image_url AS book_image_url, b.cover_image_path AS book_cover_image_path, " +
                "u.id AS u_id, u.name AS user_name, u.cpf AS user_cpf, u.email AS user_email, u.phone AS user_phone, u.address AS user_address, u.profile_image_path AS user_profile_image_path " +
                "FROM loan l " +
                "JOIN books b ON l.book_id = b.id " +
                "JOIN \"users\" u ON l.user_id = u.id " +
                "WHERE l.status = 'Ativo' AND l.actual_return_date IS NULL"; // Condição crucial
        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                loans.add(mapResultSetToLoanWithDetails(rs));
            }
        }
        return loans;
    }

    @Override
    public List<Loan> findReturnedLoansWithDetails() throws SQLException {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT l.id AS loan_id, l.book_id, l.user_id, l.loan_date, l.return_date, l.actual_return_date, l.status, l.fine, l.returned, " +
                "b.id AS b_id, b.title AS book_title, b.author AS book_author, b.isbn AS book_isbn, b.genre AS book_genre, b.total_copies AS book_total_copies, b.available_copies AS book_available_copies, b.publisher AS book_publisher, b.year AS book_year, b.image_url AS book_image_url, b.cover_image_path AS book_cover_image_path, " +
                "u.id AS u_id, u.name AS user_name, u.cpf AS user_cpf, u.email AS user_email, u.phone AS user_phone, u.address AS user_address, u.profile_image_path AS user_profile_image_path " +
                "FROM loan l " +
                "JOIN books b ON l.book_id = b.id " +
                "JOIN \"users\" u ON l.user_id = u.id " +
                "WHERE l.actual_return_date IS NOT NULL"; // Condição crucial
        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                loans.add(mapResultSetToLoanWithDetails(rs));
            }
        }
        return loans;
    }

    @Override
    public List<Loan> findOverdueLoansWithDetails() throws SQLException {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT l.id AS loan_id, l.book_id, l.user_id, l.loan_date, l.return_date, l.actual_return_date, l.status, l.fine, l.returned, " +
                "b.id AS b_id, b.title AS book_title, b.author AS book_author, b.isbn AS book_isbn, b.genre AS book_genre, b.total_copies AS book_total_copies, b.available_copies AS book_available_copies, b.publisher AS book_publisher, b.year AS book_year, b.image_url AS book_image_url, b.cover_image_path AS book_cover_image_path, " +
                "u.id AS u_id, u.name AS user_name, u.cpf AS user_cpf, u.email AS user_email, u.phone AS user_phone, u.address AS user_address, u.profile_image_path AS user_profile_image_path " +
                "FROM loan l " +
                "JOIN books b ON l.book_id = b.id " +
                "JOIN \"users\" u ON l.user_id = u.id " +
                "WHERE l.status = 'Ativo' AND l.actual_return_date IS NULL AND l.return_date < CURRENT_DATE"; // Condição crucial: 'return_date' é a data de devolução prevista
        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                loans.add(mapResultSetToLoanWithDetails(rs));
            }
        }
        return loans;
    }

    @Override
    public List<Loan> searchLoansByBookTitleOrUserNameOrUserCpf(String searchTerm) throws SQLException {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT l.id AS loan_id, l.book_id, l.user_id, l.loan_date, l.return_date, l.actual_return_date, l.status, l.fine, l.returned, " +
                "b.id AS b_id, b.title AS book_title, b.author AS book_author, b.isbn AS book_isbn, b.genre AS book_genre, b.total_copies AS book_total_copies, b.available_copies AS book_available_copies, b.publisher AS book_publisher, b.year AS book_year, b.image_url AS book_image_url, b.cover_image_path AS book_cover_image_path, " +
                "u.id AS u_id, u.name AS user_name, u.cpf AS user_cpf, u.email AS user_email, u.phone AS user_phone, u.address AS user_address, u.profile_image_path AS user_profile_image_path " +
                "FROM loan l " +
                "JOIN books b ON l.book_id = b.id " +
                "JOIN \"users\" u ON l.user_id = u.id " +
                "WHERE LOWER(b.title) LIKE ? OR LOWER(u.name) LIKE ? OR LOWER(u.cpf) LIKE ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            String likeTerm = "%" + searchTerm.toLowerCase() + "%";
            pstmt.setString(1, likeTerm);
            pstmt.setString(2, likeTerm);
            pstmt.setString(3, likeTerm);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    loans.add(mapResultSetToLoanWithDetails(rs));
                }
            }
        }
        return loans;
    }


    // --- Métodos de Mapeamento ---
    private Loan mapResultSetToLoanWithDetails(ResultSet rs) throws SQLException {
        Loan loan = new Loan();
        loan.setId(rs.getInt("loan_id"));
        loan.setLoanDate(rs.getDate("loan_date").toLocalDate());
        loan.setExpectedReturnDate(rs.getDate("return_date").toLocalDate()); // return_date do banco
        Date actualReturnDate = rs.getDate("actual_return_date");
        loan.setActualReturnDate(actualReturnDate != null ? actualReturnDate.toLocalDate() : null);
        loan.setStatus(rs.getString("status"));
        loan.setFine(rs.getDouble("fine"));
        loan.setReturned(rs.getBoolean("returned")); // NOVO: Mapeia o campo 'returned'

        Book book = new Book();
        book.setId(rs.getInt("b_id"));
        book.setTitle(rs.getString("book_title"));
        book.setAuthor(rs.getString("book_author"));
        book.setIsbn(rs.getString("book_isbn"));
        book.setGenre(rs.getString("book_genre"));
        book.setTotalCopies(rs.getInt("book_total_copies"));
        book.setAvailableCopies(rs.getInt("book_available_copies"));
        book.setPublisher(rs.getString("book_publisher"));
        book.setYear(rs.getInt("book_year"));
        book.setImageUrl(rs.getString("book_image_url"));
        book.setCoverImagePath(rs.getString("book_cover_image_path"));
        loan.setBook(book);

        User user = new User();
        user.setId(rs.getInt("u_id"));
        user.setName(rs.getString("user_name"));
        user.setCpf(rs.getString("user_cpf"));
        user.setEmail(rs.getString("user_email"));
        user.setPhone(rs.getString("user_phone"));
        user.setAddress(rs.getString("user_address"));
        user.setProfileImagePath(rs.getString("user_profile_image_path"));
        loan.setUser(user);

        return loan;
    }
}