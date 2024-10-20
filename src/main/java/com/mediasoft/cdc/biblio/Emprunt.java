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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author inganzamarumpu
 */
@Entity
@Table(name = "emprunt", catalog = "bibliotheque", schema = "public")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Emprunt.findAll", query = "SELECT e FROM Emprunt e"),
    @NamedQuery(name = "Emprunt.findById", query = "SELECT e FROM Emprunt e WHERE e.id = :id"),
    @NamedQuery(name = "Emprunt.findByDateEmprunt", query = "SELECT e FROM Emprunt e WHERE e.dateEmprunt = :dateEmprunt"),
    @NamedQuery(name = "Emprunt.findByDateRemise", query = "SELECT e FROM Emprunt e WHERE e.dateRemise = :dateRemise")})
public class Emprunt implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "date_emprunt")
    @Temporal(TemporalType.DATE)
    private Date dateEmprunt;
    @Column(name = "date_remise")
    @Temporal(TemporalType.DATE)
    private Date dateRemise;
    @JoinColumn(name = "emprunteur_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Emprunteur emprunteurId;
    @JoinColumn(name = "livre_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Livre livreId;

    public Emprunt() {
    }

    public Emprunt(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDateEmprunt() {
        return dateEmprunt;
    }

    public void setDateEmprunt(Date dateEmprunt) {
        this.dateEmprunt = dateEmprunt;
    }

    public Date getDateRemise() {
        return dateRemise;
    }

    public void setDateRemise(Date dateRemise) {
        this.dateRemise = dateRemise;
    }

    public Emprunteur getEmprunteurId() {
        return emprunteurId;
    }

    public void setEmprunteurId(Emprunteur emprunteurId) {
        this.emprunteurId = emprunteurId;
    }

    public Livre getLivreId() {
        return livreId;
    }

    public void setLivreId(Livre livreId) {
        this.livreId = livreId;
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
        if (!(object instanceof Emprunt)) {
            return false;
        }
        Emprunt other = (Emprunt) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mediasoft.cdc.biblio.Emprunt[ id=" + id + " ]";
    }
    
}
