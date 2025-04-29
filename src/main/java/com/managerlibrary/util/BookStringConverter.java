package com.managerlibrary.util;

import com.managerlibrary.entities.Book;
import javafx.util.StringConverter;

public class BookStringConverter extends StringConverter<Book> {
    @Override
    public String toString(Book book) {
        if (book == null) {
            return null;
        }
        return book.getTitle() + " - " + book.getAuthor(); // Exibe título e autor
    }

    @Override
    public Book fromString(String string) {
        // Não precisamos da conversão de String para Book para um ComboBox de seleção
        return null;
    }
}
