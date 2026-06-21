package pe.edu.cibertec.ventarapida.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pe.edu.cibertec.ventarapida.entity.Cliente;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    // Busca un cliente por su DNI.
    // Optional<Cliente> significa: "puede que exista, puede que no".
    // Te obliga a manejar el caso en que el cliente no se encuentre,
    // evitando errores de NullPointerException más adelante.
    Optional<Cliente> findByDni(String dni);

    boolean existsByDni(String dni);

    boolean existsByEmail(String email);
}