/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mediasoft.cdc.biblio;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.Collection;

/**
 *
 * @author inganzamarumpu
 */
@Entity
@Table(name = "biblioteque", catalog = "bibliotheque", schema = "public")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Biblioteque.findAll", query = "SELECT b FROM Biblioteque b"),
    @NamedQuery(name = "Biblioteque.findById", query = "SELECT b FROM Biblioteque b WHERE b.id = :id"),
    @NamedQuery(name = "Biblioteque.findByNom", query = "SELECT b FROM Biblioteque b WHERE b.nom = :nom"),
    @NamedQuery(name = "Biblioteque.findByDetails", query = "SELECT b FROM Biblioteque b WHERE b.details = :details")})
public class Biblioteque implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Size(max = 63)
    @Column(name = "nom", length = 63)
    private String nom;
    @Size(max = 255)
    @Column(name = "details", length = 255)
    private String details;
    @OneToMany(mappedBy = "bibliotequeId", fetch = FetchType.LAZY)
    private Collection<Exemplaire> exemplaireCollection;

    public Biblioteque() {
    }

    public Biblioteque(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    @XmlTransient
    public Collection<Exemplaire> getExemplaireCollection() {
        return exemplaireCollection;
    }

    public void setExemplaireCollection(Collection<Exemplaire> exemplaireCollection) {
        this.exemplaireCollection = exemplaireCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Biblioteque)) {
            return false;
        }
        Biblioteque other = (Biblioteque) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Biblioteque " + nom;
    }
    
}
