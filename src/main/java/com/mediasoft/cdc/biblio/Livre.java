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
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
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
@Table(name = "livre", catalog = "bibliotheque", schema = "public", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"id"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Livre.findAll", query = "SELECT l FROM Livre l"),
    @NamedQuery(name = "Livre.findByIsbn", query = "SELECT l FROM Livre l WHERE l.isbn = :isbn"),
    @NamedQuery(name = "Livre.findByTitre", query = "SELECT l FROM Livre l WHERE l.titre = :titre"),
    @NamedQuery(name = "Livre.findByAuteur", query = "SELECT l FROM Livre l WHERE l.auteur = :auteur"),
    @NamedQuery(name = "Livre.findByFonctionAuteur", query = "SELECT l FROM Livre l WHERE l.fonctionAuteur = :fonctionAuteur"),
    @NamedQuery(name = "Livre.findByEditeur", query = "SELECT l FROM Livre l WHERE l.editeur = :editeur"),
    @NamedQuery(name = "Livre.findByAnneEdition", query = "SELECT l FROM Livre l WHERE l.anneEdition = :anneEdition"),
    @NamedQuery(name = "Livre.findByResume", query = "SELECT l FROM Livre l WHERE l.resume = :resume"),
    @NamedQuery(name = "Livre.findById", query = "SELECT l FROM Livre l WHERE l.id = :id")})
public class Livre implements Serializable {

    private static final long serialVersionUID = 1L;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 63)
    @Column(name = "isbn", nullable = false, length = 63)
    private String isbn;
    @Size(max = 63)
    @Column(name = "titre", length = 63)
    private String titre;
    @Size(max = 63)
    @Column(name = "auteur", length = 63)
    private String auteur;
    @Size(max = 63)
    @Column(name = "fonction_auteur", length = 63)
    private String fonctionAuteur;
    @Size(max = 63)
    @Column(name = "editeur", length = 63)
    private String editeur;
    @Column(name = "anne_edition")
    private Integer anneEdition;
    @Size(max = 255)
    @Column(name = "resume", length = 255)
    private String resume;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Integer id;
    @OneToMany(mappedBy = "livreId", fetch = FetchType.LAZY)
    private Collection<Emprunt> empruntCollection;
    @OneToMany(mappedBy = "livreId", fetch = FetchType.LAZY)
    private Collection<Exemplaire> exemplaireCollection;

    public Livre() {
    }

    public Livre(Integer id) {
        this.id = id;
    }

    public Livre(Integer id, String isbn) {
        this.id = id;
        this.isbn = isbn;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getAuteur() {
        return auteur;
    }

    public void setAuteur(String auteur) {
        this.auteur = auteur;
    }

    public String getFonctionAuteur() {
        return fonctionAuteur;
    }

    public void setFonctionAuteur(String fonctionAuteur) {
        this.fonctionAuteur = fonctionAuteur;
    }

    public String getEditeur() {
        return editeur;
    }

    public void setEditeur(String editeur) {
        this.editeur = editeur;
    }

    public Integer getAnneEdition() {
        return anneEdition;
    }

    public void setAnneEdition(Integer anneEdition) {
        this.anneEdition = anneEdition;
    }

    public String getResume() {
        return resume;
    }

    public void setResume(String resume) {
        this.resume = resume;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @XmlTransient
    public Collection<Emprunt> getEmpruntCollection() {
        return empruntCollection;
    }

    public void setEmpruntCollection(Collection<Emprunt> empruntCollection) {
        this.empruntCollection = empruntCollection;
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
        if (!(object instanceof Livre)) {
            return false;
        }
        Livre other = (Livre) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return titre;
    }
    
}
