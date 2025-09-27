package org.example.service.shoppingcart;

import lombok.RequiredArgsConstructor;
import org.example.dto.cartItem.CartItemRequestDto;
import org.example.dto.cartItem.CartItemUpdateDto;
import org.example.dto.shoppingcart.ShoppingCartDto;
import org.example.exception.EntityNotFoundException;
import org.example.mapper.CartItemMapper;
import org.example.model.Book;
import org.example.model.CartItem;
import org.example.model.User;
import org.example.model.ShoppingCart;
import org.example.repository.BookRepository;
import org.example.repository.CartItemRepository;
import org.example.repository.ShoppingCartRepository;
import org.example.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final CartItemRepository cartItemRepository;
    private final CartItemMapper cartItemMapper;

    @Override
    public ShoppingCartDto getByUserId(Long authenticationId) {
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(authenticationId)
                .orElseThrow(() -> new EntityNotFoundException("Can't get user by Id " + authenticationId));

        return cartItemMapper.toDto(shoppingCart);
    }

    @Override
    public ShoppingCartDto save(Long authenticationId, CartItemRequestDto requestDto) {
        User user = userRepository.findById(authenticationId).orElseThrow(()
                -> new EntityNotFoundException("Can't get user by Id " + authenticationId));

        Book book = bookRepository.findById(requestDto.getBookId()).orElseThrow(()
                -> new EntityNotFoundException("Can't find book by id " + authenticationId));

        ShoppingCart shoppingCartByUserId = shoppingCartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Can't get user by Id " + authenticationId));

        List<CartItem> list = shoppingCartByUserId
                .getCartItems().stream()
                .filter(cartItem -> cartItem.getBook().getId().equals(book.getId())).toList();

        CartItem cartItem = new CartItem();

        if (!list.isEmpty()) {
            cartItem = list.get(0);
            int increaseQuantity = cartItem.getQuantity() + requestDto.getQuantity();
            cartItem.setQuantity(increaseQuantity);
            return cartItemMapper.toResponseDto(cartItem);
        }
        cartItem.setBook(book);
        cartItem.setQuantity(requestDto.getQuantity());
        cartItem.setShoppingCart(shoppingCartByUserId);
        CartItem savedCartItem = cartItemRepository.save(cartItem);
        shoppingCartByUserId.setCartItems(savedCartItem.getShoppingCart().getCartItems());
        return cartItemMapper.toDto(shoppingCartByUserId);
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
                .orElseThrow(() -> new EntityNotFoundException("Can't get user by Id " + authenticationId));

        CartItem cartItem = cartItemRepository
                .findByIdAndShoppingCartId(cartItemId, shoppingCart.getId()).orElseThrow(()
                        -> new EntityNotFoundException("Can't find cart item by id " + cartItemId));
        cartItem.setQuantity(updateDto.getQuantity());
        return cartItemMapper.toDto(shoppingCart);
    }

    @Override
    public void deleteById(Long authenticationId, Long id) {
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(authenticationId)
                .orElseThrow(() -> new EntityNotFoundException("Can't get user by Id "
                        + authenticationId));
        CartItem cartItem = cartItemRepository
                .findByIdAndShoppingCartId(id, shoppingCart.getId()).orElseThrow(()
                        -> new EntityNotFoundException("Can't find cart item by id " + id));
        shoppingCart.getCartItems().remove(cartItem);
    }
}
