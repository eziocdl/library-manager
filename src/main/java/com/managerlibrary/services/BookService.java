package com.managerlibrary.services;

import com.managerlibrary.daos.interfaces.BookDAO;
import com.managerlibrary.entities.Book;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects; // Importação adicionada para Objects.requireNonNull

/**
 * Serviço responsável por gerenciar as operações de negócio relacionadas a livros.
 * Atua como uma camada intermediária entre os controladores e o DAO de livros.
 */
public class BookService {

    private final BookDAO bookDAO;

    /**
     * Construtor do BookService.
     *
     * @param bookDAO A implementação de BookDAO a ser utilizada para acesso a dados.
     * Não pode ser nula.
     */
    public BookService(BookDAO bookDAO) {
        // Garantir que o DAO não seja nulo para evitar NullPointerException em tempo de execução.
        this.bookDAO = Objects.requireNonNull(bookDAO, "BookDAO não pode ser nulo.");
    }

    /**
     * Insere um novo livro no banco de dados.
     *
     * @param book O objeto Book a ser inserido.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     * @throws IllegalArgumentException Se o livro for nulo ou inválido (ex: título vazio).
     */
    public void insertBook(Book book) throws SQLException {
        // Validação básica de negócio
        if (book == null) {
            throw new IllegalArgumentException("O livro não pode ser nulo.");
        }
        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("O título do livro não pode ser vazio.");
        }
        // Adicionar outras validações conforme regras de negócio (ex: ISBN único, ano válido)

        System.out.println("BookService.insertBook recebendo: " + book.getTitle() + ", Editora: " + book.getPublisher() + ", Ano: " + book.getYear());
        bookDAO.insertBook(book);
    }

    /**
     * Busca livros no banco de dados com base em critérios definidos no objeto Book fornecido.
     * Este método atua como uma busca genérica.
     *
     * @param book Um objeto Book contendo os critérios de busca (ex: título, autor, ISBN).
     * @return Uma lista de livros que correspondem aos critérios.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    public List<Book> searchBook(Book book) throws SQLException {
        // Poderia adicionar validações para os campos de busca, se necessário
        return bookDAO.searchBook(book);
    }

    /**
     * Encontra um livro pelo seu ID.
     *
     * @param id O ID do livro a ser encontrado.
     * @return O objeto Book correspondente ao ID, ou null se não for encontrado.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     * @throws IllegalArgumentException Se o ID for inválido (menor ou igual a zero).
     */
    public Book findBookById(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("ID do livro inválido.");
        }
        Book book = bookDAO.findBookById(id);
        if (book != null) {
            System.out.println("BookService.findBookById: Livro encontrado - Título: " + book.getTitle() + ", Editora: " + book.getPublisher() + ", Ano: " + book.getYear());
        } else {
            System.out.println("BookService.findBookById: Livro não encontrado com ID: " + id);
        }
        return book;
    }

    /**
     * Retorna todos os livros cadastrados no banco de dados.
     *
     * @return Uma lista de todos os livros.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    public List<Book> findAllBooks() throws SQLException {
        return bookDAO.findAllBooks();
    }

    /**
     * Atualiza as informações de um livro existente no banco de dados.
     *
     * @param book O objeto Book com as informações atualizadas.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     * @throws IllegalArgumentException Se o livro for nulo ou inválido.
     */
    public void updateBook(Book book) throws SQLException {
        if (book == null || book.getId() <= 0) {
            throw new IllegalArgumentException("Livro para atualização inválido.");
        }
        // Poderia adicionar mais validações de campos do livro aqui
        bookDAO.updateBook(book);
    }

    /**
     * Exclui um livro do banco de dados pelo seu ID.
     *
     * @param id O ID do livro a ser excluído.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     * @throws IllegalArgumentException Se o ID for inválido.
     */
    public void deleteBook(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("ID do livro para exclusão inválido.");
        }
        // Poderia adicionar lógica de negócio aqui, como verificar se o livro não está emprestado
        bookDAO.deleteBook(id);
    }

    /**
     * Decrementa o número de cópias disponíveis de um livro.
     *
     * @param bookId O ID do livro.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     * @throws IllegalArgumentException Se o ID do livro for inválido.
     * @throws IllegalStateException Se não houver cópias disponíveis para decremento.
     */
    public void decrementAvailableCopies(int bookId) throws SQLException {
        if (bookId <= 0) {
            throw new IllegalArgumentException("ID do livro inválido para decremento.");
        }
        Book book = bookDAO.findBookById(bookId);
        if (book != null) {
            if (book.getAvailableCopies() > 0) {
                book.setAvailableCopies(book.getAvailableCopies() - 1);
                bookDAO.updateBook(book);
            } else {
                throw new IllegalStateException("Não há cópias disponíveis para decremento do livro ID: " + bookId);
            }
        } else {
            throw new IllegalArgumentException("Livro não encontrado com ID: " + bookId);
        }
    }

    /**
     * Incrementa o número de cópias disponíveis de um livro.
     *
     * @param bookId O ID do livro.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     * @throws IllegalArgumentException Se o ID do livro for inválido.
     */
    public void incrementAvailableCopies(int bookId) throws SQLException {
        if (bookId <= 0) {
            throw new IllegalArgumentException("ID do livro inválido para incremento.");
        }
        Book book = bookDAO.findBookById(bookId);
        if (book != null) {
            book.setAvailableCopies(book.getAvailableCopies() + 1);
            bookDAO.updateBook(book);
        } else {
            throw new IllegalArgumentException("Livro não encontrado com ID: " + bookId);
        }
    }

    /**
     * Busca livros pelo título.
     *
     * @param title O título do livro a ser buscado (pode ser parcial).
     * @return Uma lista de livros que correspondem ao título.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    public List<Book> findBooksByTitle(String title) throws SQLException {
        return bookDAO.findBooksByTitle(title);
    }

    /**
     * Busca livros pelo autor.
     *
     * @param author O autor do livro a ser buscado (pode ser parcial).
     * @return Uma lista de livros que correspondem ao autor.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    public List<Book> findBooksByAuthor(String author) throws SQLException {
        return bookDAO.findBooksByAuthor(author);
    }

    /**
     * Busca um livro pelo seu ISBN.
     *
     * @param isbn O ISBN do livro a ser buscado.
     * @return O objeto Book correspondente ao ISBN, ou null se não for encontrado.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    public Book findBookByISBN(String isbn) throws SQLException {
        return bookDAO.findBookByISBN(isbn);
    }

    /**
     * Busca livros por gênero.
     *
     * @param genre O gênero do livro a ser buscado.
     * @return Uma lista de livros que correspondem ao gênero.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    public List<Book> findBooksByGenre(String genre) throws SQLException {
        return bookDAO.findBooksByGenre(genre);
    }

    /**
     * Retorna todos os livros que possuem cópias disponíveis.
     *
     * @return Uma lista de livros disponíveis.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    public List<Book> getAllAvailableBooks() throws SQLException {
        // Assumindo que seu BookDAO tem um método findAllAvailable
        return bookDAO.findAllAvailable();
    }

    /**
     * Marca um livro como emprestado, decrementando suas cópias disponíveis.
     * Este método é equivalente a `decrementAvailableCopies`.
     *
     * @param bookId O ID do livro a ser marcado como emprestado.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     * @throws IllegalArgumentException Se o ID do livro for inválido ou o livro não for encontrado.
     * @throws IllegalStateException Se não houver cópias disponíveis para emprestar.
     */
    public void markBookAsBorrowed(int bookId) throws SQLException {
        // Este método é funcionalmente idêntico a decrementAvailableCopies(bookId).
        // Pode ser mantido para clareza semântica ou pode-se decidir usar apenas decrementAvailableCopies.
        decrementAvailableCopies(bookId);
    }
}