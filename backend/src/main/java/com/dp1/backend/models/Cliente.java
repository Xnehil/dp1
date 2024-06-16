package com.dp1.backend.models;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;



import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "cliente")
@SQLDelete(sql = "UPDATE cliente SET active = false WHERE id = ?")
@SQLRestriction( value = "active = true")
public class Cliente extends BaseModel {    

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name ="numero_documento", nullable = false, unique = true)
    private String numeroDocumento;

    @Column(name = "tipo_documento", nullable = false)
    private int tipoDocumento;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "apellido")
    private String apellido;

    @Column(name = "segundo_nombre")
    private String segundoNombre;

    @Column(name = "codigo_pais")
    private int codigoPais;

    @Column(name = "telefono")
    private String telefono;




    public Cliente( String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public Cliente() {
    }


    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getNumeroDocumento() {
        return this.numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public int getTipoDocumento() {
        return this.tipoDocumento;
    }

    public void setTipoDocumento(int tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getNombre() {
        return this.nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return this.apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getSegundoNombre() {
        return this.segundoNombre;
    }

    public void setSegundoNombre(String segundoNombre) {
        this.segundoNombre = segundoNombre;
    }

    public int getCodigoPais() {
        return this.codigoPais;
    }

    public void setCodigoPais(int codigoPais) {
        this.codigoPais = codigoPais;
    }

    public String getTelefono() {
        return this.telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
    

}
