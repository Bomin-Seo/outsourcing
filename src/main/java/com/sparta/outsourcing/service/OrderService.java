package com.sparta.outsourcing.service;

import com.sparta.outsourcing.dto.OrderRequestDto;
import com.sparta.outsourcing.dto.OrderResponseDto;
import com.sparta.outsourcing.entity.Menu;
import com.sparta.outsourcing.entity.Order;
import com.sparta.outsourcing.entity.Restaurant;
import com.sparta.outsourcing.entity.User;
import com.sparta.outsourcing.exception.InvalidAccessException;
import com.sparta.outsourcing.repository.MenuRepository;
import com.sparta.outsourcing.repository.OrderRepository;
import com.sparta.outsourcing.repository.RestaurantRepository;
import com.sparta.outsourcing.security.UserDetailsImpl;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class OrderService {
    private final OrderRepository orderRepository;
    private final MenuRepository menuRepository;
    private final RestaurantRepository restaurantRepository;
    private final MessageSource messageSource;

    public OrderService(OrderRepository orderRepository, MenuRepository menuRepository, RestaurantRepository restaurantRepository, MessageSource messageSource) {
        this.orderRepository = orderRepository;
        this.menuRepository = menuRepository;
        this.restaurantRepository = restaurantRepository;
        this.messageSource = messageSource;
    }

    // 주문 등록
    public OrderResponseDto createOrder(Long restaurantId, List<OrderRequestDto> menuList, UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        Restaurant restaurant;

        Optional<Restaurant> optionalRestaurant = restaurantRepository.findById(restaurantId);
        if (optionalRestaurant.isPresent()) {
            restaurant = optionalRestaurant.get();
        } else {
            throw new InvalidAccessException(messageSource.getMessage(
                    "invalid.access", null, "적합하지 않은 접근입니다.", Locale.getDefault()));
        }

        checkRestaurant(menuList);
        List<String> menus = getMenus(menuList, restaurantId);
        int totalPrice = getTotalPrice(menuList);

        Order order = new Order();
        order.setUser(user);
        order.setRestaurant(restaurant);
        order.setMenuList(menus);
        order.setTotalPrice(totalPrice);
        orderRepository.save(order);
        return OrderResponseDto.toDto(order);
    }

    // 모든 주문 조회
    public Page<OrderResponseDto> getOrders(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());
        Page<Order> orderPage = orderRepository.findAll(pageable);
        return orderPage.map(OrderResponseDto::toDto);
    }

    // 특정 주문 조회
    public List<OrderResponseDto> getOrder(long orderId) {
        return orderRepository.findById(orderId).stream().map(OrderResponseDto::toDto).toList();
    }

    // 주문 수정
    public OrderResponseDto updateOrder(long orderId, List<OrderRequestDto> menuList, UserDetailsImpl userDetails) {
        User user = userDetails.getUser();

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

        if (!Objects.equals(order.getUser().getId(), user.getId())) {
            throw new IllegalArgumentException("주문한 사람만 수정할 수 있습니다");
        }

        checkRestaurant(menuList);
        List<String> menus = getMenus(menuList, order.getRestaurant().getId());
        int totalPrice = getTotalPrice(menuList);

        order.setMenuList(menus);
        order.setTotalPrice(totalPrice);
        orderRepository.save(order);
        return OrderResponseDto.toDto(order);
    }

    // 주문 삭제
    public void deleteOrder(long orderId, UserDetailsImpl userDetails) {
        User user = userDetails.getUser();

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

        if (!Objects.equals(order.getUser().getId(), user.getId())) {
            throw new IllegalArgumentException("주문한 사람만 삭제할 수 있습니다");
        }

        orderRepository.delete(order);
    }

    //menuId로 메뉴 찾기
    private Menu findMenuById(Long menuId) {
        return menuRepository.findById(menuId).orElseThrow(() ->
                new IllegalArgumentException("해당 메뉴을 찾을 수 없습니다."));
    }

    // 다른 가게인지 체크
    private List<Long> checkRestaurant(List<OrderRequestDto> menuList) {
        List<Long> restaurants = new ArrayList<>();
        for (OrderRequestDto requestDto : menuList) {
            Menu menu = findMenuById(requestDto.getMenuId());
            restaurants.add(menu.getRestaurant().getId());
        }
        if (restaurants.stream().distinct().count() != 1) {
            throw new IllegalArgumentException("같은 가게에서만 주문할 수 있습니다.");
        }
        return restaurants;
    }


    //주문 메뉴 목록
    private List<String> getMenus(List<OrderRequestDto> menuList, Long restaurantId) {
        List<String> menus = new ArrayList<>();
        for (OrderRequestDto requestDto : menuList) {
            Menu menu = findMenuById(requestDto.getMenuId());
            if (!menu.getRestaurant().getId().equals(restaurantId)) {
                throw new InvalidAccessException(messageSource.getMessage(
                        "invalid.access", null, "적합하지 않은 접근입니다.", Locale.getDefault()));
            }
            String count = Integer.toString(requestDto.getMenuCount());
            menus.add(menu.getMenuName() + " 수량: " + count );
        }
        return menus;
    }


    // 주문 총 가격
    private int getTotalPrice(List<OrderRequestDto> menuList) {
        int totalPrice = 0;
        for (OrderRequestDto requestDto : menuList) {
            Menu menu = findMenuById(requestDto.getMenuId());
            totalPrice += (int) (menu.getPrice()*requestDto.getMenuCount());
        }
        return totalPrice;
    }

}