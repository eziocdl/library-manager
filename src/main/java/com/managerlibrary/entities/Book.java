package com.managerlibrary.entities;

import javafx.beans.property.*;

import java.util.Objects;

/**
 * Representa um livro no sistema da biblioteca.
 */
public class Book {

    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty title = new SimpleStringProperty();
    private final StringProperty author = new SimpleStringProperty();
    private final StringProperty isbn = new SimpleStringProperty();
    private final StringProperty publisher = new SimpleStringProperty();
    private final StringProperty year = new SimpleStringProperty();
    private final StringProperty genre = new SimpleStringProperty();
    private final IntegerProperty totalCopies = new SimpleIntegerProperty();
    private final IntegerProperty availableCopies = new SimpleIntegerProperty();
    private final StringProperty coverImagePath = new SimpleStringProperty();
    private final StringProperty imageUrl = new SimpleStringProperty(); // Adicionado

    public Book() {
    }

    // Construtor com argumentos (adicione imageUrl se necessário)
    public Book(int id, String title, String author, String isbn, String publisher, String year, String genre, int totalCopies, int availableCopies, String coverImagePath, String imageUrl) {
        this.id.set(id);
        this.title.set(title);
        this.author.set(author);
        this.isbn.set(isbn);
        this.publisher.set(publisher);
        this.year.set(year);
        this.genre.set(genre);
        this.totalCopies.set(totalCopies);
        this.availableCopies.set(availableCopies);
        this.coverImagePath.set(coverImagePath);
        this.imageUrl.set(imageUrl); // Inicialize no construtor
    }

    public Book(String orgulhoEPreconceito, String janeAusten, String s, String penguinClassics, String number, String romance, int i, int i1) {
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

    public StringProperty titleProperty() {
        return title;
    }

    public String getTitle() {
        return title.get();
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public StringProperty authorProperty() {
        return author;
    }

    public String getAuthor() {
        return author.get();
    }

    public void setAuthor(String author) {
        this.author.set(author);
    }

    public StringProperty isbnProperty() {
        return isbn;
    }

    public String getIsbn() {
        return isbn.get();
    }

    public void setIsbn(String isbn) {
        this.isbn.set(isbn);
    }

    public StringProperty publisherProperty() {
        return publisher;
    }

    public String getPublisher() {
        return publisher.get();
    }

    public void setPublisher(String publisher) {
        this.publisher.set(publisher);
    }

    public StringProperty yearProperty() {
        return year;
    }

    public String getYear() {
        return year.get();
    }

    public void setYear(String year) {
        this.year.set(year);
    }

    public StringProperty genreProperty() {
        return genre;
    }

    public String getGenre() {
        return genre.get();
    }

    public void setGenre(String genre) {
        this.genre.set(genre);
    }

    public IntegerProperty totalCopiesProperty() {
        return totalCopies;
    }

    public int getTotalCopies() {
        return totalCopies.get();
    }

    public void setTotalCopies(int totalCopies) {
        this.totalCopies.set(totalCopies);
    }

    public IntegerProperty availableCopiesProperty() {
        return availableCopies;
    }

    public int getAvailableCopies() {
        return availableCopies.get();
    }

    public void setAvailableCopies(int availableCopies) {
        this.availableCopies.set(availableCopies);
    }

    public StringProperty coverImagePathProperty() {
        return coverImagePath;
    }

    public String getCoverImagePath() {
        return coverImagePath.get();
    }

    public void setCoverImagePath(String coverImagePath) {
        this.coverImagePath.set(coverImagePath);
    }

    // Getter e Setter para imageUrl
    public StringProperty imageUrlProperty() {
        return imageUrl;
    }

    public String getImageUrl() {
        return imageUrl.get();
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl.set(imageUrl);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(id, book.id) && Objects.equals(title, book.title) && Objects.equals(author, book.author) && Objects.equals(isbn, book.isbn) && Objects.equals(publisher, book.publisher) && Objects.equals(year, book.year) && Objects.equals(genre, book.genre) && Objects.equals(totalCopies, book.totalCopies) && Objects.equals(availableCopies, book.availableCopies) && Objects.equals(coverImagePath, book.coverImagePath) && Objects.equals(imageUrl, book.imageUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, author, isbn, publisher, year, genre, totalCopies, availableCopies, coverImagePath, imageUrl);
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", title=" + title +
                ", author=" + author +
                ", isbn=" + isbn +
                ", publisher=" + publisher +
                ", year=" + year +
                ", genre=" + genre +
                ", totalCopies=" + totalCopies +
                ", availableCopies=" + availableCopies +
                ", coverImagePath=" + coverImagePath +
                ", imageUrl=" + imageUrl +
                '}';
    }
}