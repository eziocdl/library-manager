package com.managerlibrary.entities;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class User {
    private final IntegerProperty id = new SimpleIntegerProperty(this, "id");
    private final StringProperty name = new SimpleStringProperty(this, "name");
    private final StringProperty address = new SimpleStringProperty(this, "address");
    private final StringProperty phone = new SimpleStringProperty(this, "phone");
    private final StringProperty email = new SimpleStringProperty(this, "email");
    private final StringProperty registrationNumber = new SimpleStringProperty(this, "registrationNumber");
    private final StringProperty cpf = new SimpleStringProperty(this, "cpf");

    // Construtores
    public User() {
    }

    public User(int id) { // Adicionado construtor que aceita apenas o ID
        setId(id);
    }

    public User(int id, String name, String address, String phone, String email, String registrationNumber, String cpf) {
        setId(id);
        setName(name);
        setAddress(address);
        setPhone(phone);
        setEmail(email);
        setRegistrationNumber(registrationNumber);
        setCpf(cpf);
    }

    // Getters para as Properties
    public IntegerProperty idProperty() {
        return id;
    }

    public StringProperty nameProperty() {
        return name;
    }

    public StringProperty addressProperty() {
        return address;
    }

    public StringProperty phoneProperty() {
        return phone;
    }

    public StringProperty emailProperty() {
        return email;
    }

    public StringProperty registrationNumberProperty() {
        return registrationNumber;
    }

    public StringProperty cpfProperty() {
        return cpf;
    }

    // Getters para os valores
    public int getId() {
        return id.get();
    }

    public String getName() {
        return name.get();
    }

    public String getAddress() {
        return address.get();
    }

    public String getPhone() {
        return phone.get();
    }

    public String getEmail() {
        return email.get();
    }

    public String getRegistrationNumber() {
        return registrationNumber.get();
    }

    public String getCpf() {
        return cpf.get();
    }

    // Setters para os valores
    public void setId(int id) {
        this.id.set(id);
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public void setAddress(String address) {
        this.address.set(address);
    }

    public void setPhone(String phone) {
        this.phone.set(phone);
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber.set(registrationNumber);
    }

    public void setCpf(String cpf) {
        this.cpf.set(cpf);
    }
}