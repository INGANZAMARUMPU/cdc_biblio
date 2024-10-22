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
import jakarta.persistence.JoinColumn;
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
@Table(name = "emprunteur", catalog = "bibliotheque", schema = "public")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Emprunteur.findAll", query = "SELECT e FROM Emprunteur e"),
    @NamedQuery(name = "Emprunteur.findById", query = "SELECT e FROM Emprunteur e WHERE e.id = :id"),
    @NamedQuery(name = "Emprunteur.findByNom", query = "SELECT e FROM Emprunteur e WHERE e.nom = :nom"),
    @NamedQuery(name = "Emprunteur.findByPrenom", query = "SELECT e FROM Emprunteur e WHERE e.prenom = :prenom"),
    @NamedQuery(name = "Emprunteur.findByAddresse", query = "SELECT e FROM Emprunteur e WHERE e.addresse = :addresse"),
    @NamedQuery(name = "Emprunteur.findByTelephone", query = "SELECT e FROM Emprunteur e WHERE e.telephone = :telephone"),
    @NamedQuery(name = "Emprunteur.findByEmail", query = "SELECT e FROM Emprunteur e WHERE e.email = :email")})
public class Emprunteur implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Size(max = 64)
    @Column(name = "nom", length = 64)
    private String nom;
    @Size(max = 64)
    @Column(name = "prenom", length = 64)
    private String prenom;
    @Size(max = 64)
    @Column(name = "addresse", length = 64)
    private String addresse;
    @Size(max = 64)
    @Column(name = "telephone", length = 64)
    private String telephone;
    // @Pattern(regexp="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message="Invalid email")//if the field contains email address consider using this annotation to enforce field validation
    @Size(max = 64)
    @Column(name = "email", length = 64)
    private String email;
    @OneToMany(mappedBy = "emprunteurId", fetch = FetchType.LAZY)
    private Collection<Emprunt> empruntCollection;
    @OneToMany(mappedBy = "emprunteurId", fetch = FetchType.LAZY)
    private Collection<Reservation> reservationCollection;
    
    @JoinColumn(name = "biblioteque_id", referencedColumnName = "id", nullable = true)
    private Biblioteque bibliotequeId;

    public Emprunteur() {
    }

    public Emprunteur(Integer id) {
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

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getAddresse() {
        return addresse;
    }

    public void setAddresse(String addresse) {
        this.addresse = addresse;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Biblioteque getBibliotequeId() {
        return bibliotequeId;
    }

    public void setBibliotequeId(Biblioteque bibliotequeId) {
        this.bibliotequeId = bibliotequeId;
    }

    @XmlTransient
    public Collection<Emprunt> getEmpruntCollection() {
        return empruntCollection;
    }

    public void setEmpruntCollection(Collection<Emprunt> empruntCollection) {
        this.empruntCollection = empruntCollection;
    }

    @XmlTransient
    public Collection<Reservation> getReservationCollection() {
        return reservationCollection;
    }

    public void setReservationCollection(Collection<Reservation> reservationCollection) {
        this.reservationCollection = reservationCollection;
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
        if (!(object instanceof Emprunteur)) {
            return false;
        }
        Emprunteur other = (Emprunteur) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return nom + " " + prenom;
    }
    
}
