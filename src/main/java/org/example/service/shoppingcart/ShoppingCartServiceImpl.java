package org.example.service.shoppingcart;

import org.example.dto.cartItem.CartItemRequestDto;
import org.example.dto.cartItem.CartItemUpdateDto;
import org.example.dto.shoppingcart.ShoppingCartDto;
import org.example.model.Book;
import org.example.model.CartItem;
import org.example.model.ShoppingCart;
import org.example.model.User;
import org.example.exception.EntityNotFoundException;
import org.example.mapper.CartItemMapper;
import org.example.repository.BookRepository;
import org.example.repository.CartItemRepository;
import org.example.repository.ShoppingCartRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Transactional
@RequiredArgsConstructor
@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final BookRepository bookRepository;
    private final CartItemRepository cartItemRepository;
    private final CartItemMapper cartItemMapper;

    @Override
    public ShoppingCartDto getByUserId(Long authenticationId) {
        return shoppingCartRepository.findByUserId(authenticationId)
                .map(cartItemMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't get user by Id " + authenticationId));
    }

    @Override
    public ShoppingCartDto save(Long authenticationId, CartItemRequestDto requestDto) {
        ShoppingCart shoppingCart = getShoppingCart(authenticationId);

        Book book = bookRepository.findById(requestDto.getBookId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find book by id " + requestDto.getBookId()));

        CartItem cartItem = shoppingCart.getCartItems().stream()
                .filter(item -> item.getBook().getId().equals(book.getId()))
                .findFirst()
                .orElse(null);

        if (cartItem != null) {
            cartItem.setQuantity(cartItem.getQuantity() + requestDto.getQuantity());
        } else {
            cartItem = new CartItem();
            cartItem.setBook(book);
            cartItem.setShoppingCart(shoppingCart);
            cartItem.setQuantity(requestDto.getQuantity());
            shoppingCart.getCartItems().add(cartItem);
        }

        cartItemRepository.save(cartItem);
        return cartItemMapper.toDto(shoppingCart);
    }

    @Override
    public void saveShoppingCartForUser(User user) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        shoppingCartRepository.save(shoppingCart);
    }

    @Override
    public ShoppingCartDto update(Long authenticationId, Long cartItemId, CartItemUpdateDto updateDto) {
        ShoppingCart shoppingCart = getShoppingCart(authenticationId);

        CartItem cartItem = cartItemRepository
                .findByIdAndShoppingCartId(cartItemId, shoppingCart.getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find cart item by id " + cartItemId));
        cartItem.setQuantity(updateDto.getQuantity());
        return cartItemMapper.toDto(shoppingCart);
    }

    @Override
    public void deleteById(Long authenticationId, Long id) {
        ShoppingCart shoppingCart = getShoppingCart(authenticationId);

        CartItem cartItem = cartItemRepository
                .findByIdAndShoppingCartId(id, shoppingCart.getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find cart item by id " + id));
        shoppingCart.getCartItems().remove(cartItem);
    }

    private ShoppingCart getShoppingCart(Long authenticationId) {
        return shoppingCartRepository.findByUserId(authenticationId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't get user by Id " + authenticationId));
    }
}
