package ar.com.syswarp.data.entity;

import java.time.LocalDate;
import javax.persistence.Entity;

@Entity
public class Horas extends AbstractEntity {

    private LocalDate fecha;
    private Integer cantidadhoras;
    private String observaciones;

    public LocalDate getFecha() {
        return fecha;
    }
    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }
    public Integer getCantidadhoras() {
        return cantidadhoras;
    }
    public void setCantidadhoras(Integer cantidadhoras) {
        this.cantidadhoras = cantidadhoras;
    }
    public String getObservaciones() {
        return observaciones;
    }
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

}
