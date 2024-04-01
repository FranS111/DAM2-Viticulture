package model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import utils.TipoVid;

@Entity
@Table(name= "vid")
public class Vid {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "id", unique = true, nullable = true)
	private int id;
	@Column(name = "tipo_vid", nullable = true)
	private TipoVid vid;
	@Column(name = "cantidad", nullable = true)
	private int cantidad;
	@Column(name = "precio", nullable = true)
	private double precio;
	
	public Vid() {}
		
	public Vid(TipoVid vid, int cantidad, double precio) {
		this.vid = vid;
		this.cantidad = cantidad;
		this.precio = 1f;
	}
	public int getId() {
		return this.id;
	}
	public TipoVid getVid() {
		return vid;
	}
	public int getCantidad() {
		return cantidad;
	}
	public double getPrecio() {
		return precio;
	}
	@Override
	public String toString() {
        return "Vid [vid=" + vid + ", cantidad=" + cantidad + ", precio=" + precio + "]";
    }
}
