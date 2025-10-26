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
import org.example.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Transactional
@RequiredArgsConstructor
@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final CartItemRepository cartItemRepository;
    private final CartItemMapper cartItemMapper;

    @Override
    public ShoppingCartDto getByUserId(Long authenticationId) {
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(authenticationId)
                .orElseGet(() -> {
                    User user = userRepository.findById(authenticationId)
                            .orElseThrow(() -> new EntityNotFoundException("Can't get user by Id " + authenticationId));
                    ShoppingCart newCart = new ShoppingCart();
                    newCart.setUser(user);
                    return shoppingCartRepository.save(newCart);
                });
        return cartItemMapper.toDto(shoppingCart);
    }

    @Override
    public ShoppingCartDto save(Long authenticationId, CartItemRequestDto requestDto) {
        // Знайти користувача
        User user = userRepository.findById(authenticationId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't get user by Id " + authenticationId));

        // Знайти книгу
        Book book = bookRepository.findById(requestDto.getBookId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find book by id " + requestDto.getBookId()));

        // Отримати корзину користувача або створити нову
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    ShoppingCart newCart = new ShoppingCart();
                    newCart.setUser(user);
                    // Зберігаємо спочатку корзину, щоб Hibernate присвоїв ID
                    return shoppingCartRepository.saveAndFlush(newCart);
                });

        // Перевірити наявність CartItem
        CartItem cartItem = shoppingCart.getCartItems().stream()
                .filter(item -> item.getBook().getId().equals(book.getId()))
                .findFirst()
                .orElse(null);

        if (cartItem != null) {
            // Збільшуємо кількість
            cartItem.setQuantity(cartItem.getQuantity() + requestDto.getQuantity());
        } else {
            // Створюємо новий CartItem
            cartItem = new CartItem();
            cartItem.setBook(book);
            cartItem.setQuantity(requestDto.getQuantity());
            cartItem.setShoppingCart(shoppingCart);
            // Додаємо до корзини
            shoppingCart.getCartItems().add(cartItem);
        }

        // Зберігаємо CartItem
        cartItemRepository.save(cartItem);

        // Повертаємо актуальну корзину
        return cartItemMapper.toDto(shoppingCartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't get user by Id " + authenticationId)));
    }


    @Override
    public ShoppingCartDto update(Long authenticationId, Long cartItemId,
                                  CartItemUpdateDto updateDto) {
        ShoppingCart byUserId = shoppingCartRepository.findByUserId(authenticationId)
                .orElseThrow(() -> new EntityNotFoundException("Can't get user by Id "
                        + authenticationId));

        CartItem cartItem = cartItemRepository
                .findByIdAndShoppingCartId(cartItemId, byUserId.getId()).orElseThrow(()
                        -> new EntityNotFoundException("Can't find cart item by id "
                        + cartItemId));
        cartItem.setQuantity(updateDto.getQuantity());
        return cartItemMapper.toDto(byUserId);
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

    @Override
    public void saveShoppingCartForUser(User user) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        shoppingCartRepository.save(shoppingCart);
    }
}
