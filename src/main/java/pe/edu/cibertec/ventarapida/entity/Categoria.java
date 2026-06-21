package pe.edu.cibertec.ventarapida.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity

@Table(name = "categoria")
public class Categoria {

   
    @Id
    // @GeneratedValue 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // @Column conecta este atributo con la columna "id_categoria" de MySQL
    @Column(name = "id_categoria")
    private Long idCategoria;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    // Constructor vacío
    // crear instancias de la clase cuando lee datos desde MySQL
    public Categoria() {
    }

    // Constructor con parámetros
    public Categoria(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    // Getters y Setters: JPA y Spring los usan internamente para leer/escribir
    // los valores de cada atributo (es la forma "segura" de acceder a campos privados)
    //Long: en lugar de int, porque permite valores null
    public Long getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(Long idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}