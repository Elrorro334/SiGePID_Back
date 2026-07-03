package com.sigepid.order.domain.repository;

import com.sigepid.order.domain.entity.Order;
import com.sigepid.order.domain.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio JPA para la entidad Order.
 * Extiende JpaRepository, lo que proporciona automáticamente operaciones CRUD básicas
 * (save, findById, findAll, delete, etc.) sin necesidad de implementación.
 *
 * @Repository - Marca esta interfaz como un componente de acceso a datos de Spring,
 *               permitiendo la traducción automática de excepciones de persistencia.
 *
 * Spring Data JPA genera la implementación automáticamente a partir de los nombres de los métodos.
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Busca todos los pedidos de un usuario específico.
     * Spring Data genera la consulta: SELECT * FROM orders WHERE user_id = ?
     *
     * @param userId ID del usuario cuyos pedidos se quieren obtener.
     * @return Lista de pedidos del usuario.
     */
    List<Order> findByUserId(String userId);

    /**
     * Busca todos los pedidos que se encuentren en un estado específico.
     * Spring Data genera la consulta: SELECT * FROM orders WHERE status = ?
     *
     * @param status Estado del pedido a filtrar (PENDING, CONFIRMED, etc.).
     * @return Lista de pedidos con el estado indicado.
     */
    List<Order> findByStatus(OrderStatus status);

    /**
     * Busca pedidos de un usuario específico filtrados por estado.
     * Spring Data genera la consulta: SELECT * FROM orders WHERE user_id = ? AND status = ?
     *
     * @param userId ID del usuario.
     * @param status Estado del pedido a filtrar.
     * @return Lista de pedidos que cumplen ambos criterios.
     */
    List<Order> findByUserIdAndStatus(String userId, OrderStatus status);
}
