package pe.edu.cibertec.ventarapida.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pe.edu.cibertec.ventarapida.entity.Proveedor;

@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {

    // Verifica si ya existe un proveedor con ese RUC
    // (útil para validar antes de registrar uno nuevo)
    boolean existsByRuc(String ruc);
}