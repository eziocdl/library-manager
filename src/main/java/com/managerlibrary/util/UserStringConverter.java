package com.managerlibrary.util;

import com.managerlibrary.entities.User;
import javafx.util.StringConverter;

public class UserStringConverter extends StringConverter<User> {

    @Override
    public String toString(User user) {
        if (user == null) {
            return null;
        }
        return user.getName() + " - " + user.getEmail(); // Exibe nome e email
    }

    @Override
    public User fromString(String string) {
        // Não precisamos da conversão de String para User para um ComboBox de seleção
        return null;
    }
}
