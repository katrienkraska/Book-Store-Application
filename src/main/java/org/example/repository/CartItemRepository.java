package org.example.repository;

import org.example.model.CartItem;
import org.example.model.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByIdAndShoppingCartId(Long id, Long shoppingCartId);

    List<CartItem> findCartItemByShoppingCart(ShoppingCart shoppingCart);
}
