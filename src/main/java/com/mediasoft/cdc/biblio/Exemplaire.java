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
@Table(name = "exemplaire", catalog = "bibliotheque", schema = "public")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Exemplaire.findAll", query = "SELECT e FROM Exemplaire e"),
    @NamedQuery(name = "Exemplaire.findById", query = "SELECT e FROM Exemplaire e WHERE e.id = :id"),
    @NamedQuery(name = "Exemplaire.findBySituation", query = "SELECT e FROM Exemplaire e WHERE e.situation = :situation")})
public class Exemplaire implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Size(max = 64)
    @Column(name = "situation", length = 64)
    private String situation;
    @OneToMany(mappedBy = "exemplaireId", fetch = FetchType.LAZY)
    private Collection<Reservation> reservationCollection;
    @JoinColumn(name = "biblioteque_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Biblioteque bibliotequeId;
    @JoinColumn(name = "livre_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Livre livreId;

    public Exemplaire() {
    }

    public Exemplaire(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSituation() {
        return situation;
    }

    public void setSituation(String situation) {
        this.situation = situation;
    }

    @XmlTransient
    public Collection<Reservation> getReservationCollection() {
        return reservationCollection;
    }

    public void setReservationCollection(Collection<Reservation> reservationCollection) {
        this.reservationCollection = reservationCollection;
    }

    public Biblioteque getBibliotequeId() {
        return bibliotequeId;
    }

    public void setBibliotequeId(Biblioteque bibliotequeId) {
        this.bibliotequeId = bibliotequeId;
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
        if (!(object instanceof Exemplaire)) {
            return false;
        }
        Exemplaire other = (Exemplaire) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return livreId.toString() + " chez "+ bibliotequeId.toString();
    }
    
}
