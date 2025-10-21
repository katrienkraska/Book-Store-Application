package org.example.service.shoppingcart;

import lombok.RequiredArgsConstructor;
import org.example.dto.cartItem.CartItemRequestDto;
import org.example.dto.cartItem.CartItemUpdateDto;
import org.example.dto.shoppingcart.ShoppingCartDto;
import org.example.exception.EntityNotFoundException;
import org.example.mapper.ShoppingCartMapper;
import org.example.model.Book;
import org.example.model.CartItem;
import org.example.model.ShoppingCart;
import org.example.model.User;
import org.example.repository.BookRepository;
import org.example.repository.CartItemRepository;
import org.example.repository.ShoppingCartRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final BookRepository bookRepository;
    private final CartItemRepository cartItemRepository;
    private final ShoppingCartMapper shoppingCartMapper;

    @Override
    public ShoppingCartDto getByUserId(Long authenticationId) {
        return shoppingCartRepository.findByUserId(authenticationId)
                .map(shoppingCartMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't get shopping cart for user " + authenticationId));
    }

    @Override
    public ShoppingCartDto save(Long authenticationId, CartItemRequestDto requestDto) {
        Book book = bookRepository.findById(requestDto.getBookId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find book by id " + requestDto.getBookId()));

        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(authenticationId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't get shopping cart for user " + authenticationId));

        CartItem cartItem = shoppingCart.getCartItems().stream()
                .filter(item -> item.getBook().getId().equals(book.getId()))
                .findFirst()
                .orElse(null);

        if (cartItem != null) {
            cartItem.setQuantity(cartItem.getQuantity() + requestDto.getQuantity());
            cartItemRepository.save(cartItem);
        } else {
            cartItem = new CartItem();
            cartItem.setBook(book);
            cartItem.setQuantity(requestDto.getQuantity());
            cartItem.setShoppingCart(shoppingCart);
            cartItemRepository.save(cartItem);
        }

        return shoppingCartRepository.findByUserId(authenticationId)
                .map(shoppingCartMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't get shopping cart for user " + authenticationId));
    }

    @Override
    public void saveShoppingCartForUser(User user) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        shoppingCartRepository.save(shoppingCart);
    }

    @Override
    public ShoppingCartDto update(Long authenticationId, Long cartItemId, CartItemUpdateDto updateDto) {
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(authenticationId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't get shopping cart for user " + authenticationId));

        CartItem cartItem = cartItemRepository.findByIdAndShoppingCartId(cartItemId, shoppingCart.getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find cart item by id " + cartItemId));

        cartItem.setQuantity(updateDto.getQuantity());
        cartItemRepository.save(cartItem);

        return shoppingCartRepository.findByUserId(authenticationId)
                .map(shoppingCartMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't get shopping cart for user " + authenticationId));
    }

    @Override
    public void deleteById(Long authenticationId, Long cartItemId) {
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(authenticationId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't get shopping cart for user " + authenticationId));

        CartItem cartItem = cartItemRepository.findByIdAndShoppingCartId(cartItemId, shoppingCart.getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find cart item by id " + cartItemId));

        shoppingCart.getCartItems().remove(cartItem);
        cartItemRepository.delete(cartItem);
    }
}
