package pe.edu.cibertec.ventarapida.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "producto")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Long idProducto;

    @Column(name = "codigo", nullable = false, unique = true, length = 50)
    private String codigo;

    @Column(name = "nombre", nullable = false, length = 200)
    private String nombre;

    @Column(name = "descripcion", length = 500)
    private String descripcion;

    // BigDecimal es OBLIGATORIO para dinero en Java.
    // Nunca se usa double/float para precios: porque pueden generar errores
    // de redondeo (ej: 0.1 + 0.2 no da exactamente 0.3 en double).
    @Column(name = "precio", nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(name = "stock", nullable = false)
    private Integer stock = 0;

    @Column(name = "stock_minimo", nullable = false)
    private Integer stockMinimo = 5;

    @Column(name = "activo", nullable = false)
    private boolean activo = true;

    // ===== RELACIÓN CON CATEGORIA =====
    // @ManyToOne: MUCHOS productos pueden tener LA MISMA categoria
    // fetch = LAZY: Hibernate NO carga la categoría automáticamente
    // al traer un producto; solo la carga cuando realmente se le pide
    // (esto mejora el rendimiento — evita cargar datos que no se usarán)
    @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn indica cuál es la columna FK en la tabla "producto"
    @JoinColumn(name = "id_categoria")
    private Categoria categoria;

    // ===== RELACIÓN CON PROVEEDOR =====
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_proveedor")
    private Proveedor proveedor;

    public Producto() {
    }

    public Producto(String codigo, String nombre, String descripcion, BigDecimal precio,
                     Integer stock, Integer stockMinimo, Categoria categoria, Proveedor proveedor) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
        this.stockMinimo = stockMinimo;
        this.categoria = categoria;
        this.proveedor = proveedor;
        this.activo = true;
    }

    public Long getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Long idProducto) {
        this.idProducto = idProducto;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
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

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Integer getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(Integer stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    // Método de negocio: verifica si el producto necesita reabastecimiento.
    // Esto NO es un getter/setter — es lógica propia de la entidad,
    // útil más adelante para el reporte de "stock bajo mínimo"
    public boolean necesitaReabastecimiento() {
        return this.stock <= this.stockMinimo;
    }
}