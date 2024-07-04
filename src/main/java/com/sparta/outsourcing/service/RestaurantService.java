package com.sparta.outsourcing.service;

import com.sparta.outsourcing.dto.RestaurantDto;
import com.sparta.outsourcing.entity.Restaurant;
import com.sparta.outsourcing.entity.User;
import com.sparta.outsourcing.enums.StatusEnum;
import com.sparta.outsourcing.exception.InvalidAccessException;
import com.sparta.outsourcing.exception.NotFoundObjException;
import com.sparta.outsourcing.repository.RestaurantRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final MessageSource messageSource;

    public ResponseEntity<String> addRestaurant(RestaurantDto restaurantDto, User user) {
        Restaurant restaurant = new Restaurant(user, restaurantDto.getRestaurantName(), restaurantDto.getRestaurantInfo(), restaurantDto.getPhoneNumber());
        restaurantRepository.save(restaurant);
        return ResponseEntity.ok("식당이 등록되었습니다.");
    }

    public ResponseEntity<String> deleteRestaurant(Long restaurantId, User user) {
        Optional<Restaurant> optionalRestaurant = restaurantRepository.findById(restaurantId);
        if (optionalRestaurant.isPresent()) {
            Restaurant restaurant = optionalRestaurant.get();
            if (restaurant.getUser().getUsername().equals(user.getUsername())) {
                restaurantRepository.delete(restaurant);
                return ResponseEntity.ok("식당 정보가 삭제되었습니다.");
            } else {
                throw new InvalidAccessException(messageSource.getMessage(
                        "invalid.access", null, "적합하지 않은 접근입니다.", Locale.getDefault()));
            }
        } else {
            throw new NotFoundObjException(messageSource.getMessage(
                    "not.found.restaurant", null, "해당 식당이 존재하지 않습니다.", Locale.getDefault()));
        }
    }

    public ResponseEntity<String> updateRestaurant(Long restaurantId, User user ,RestaurantDto restaurantDto){
        if (!restaurantRepository.findById(restaurantId).get().getUser().getUsername().equals(user.getUsername())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("본인의 가게가 아닙니다.");
        }
        Optional<Restaurant> restaurant = restaurantRepository.findByIdAndStatus(restaurantId, StatusEnum.ACTIVE);
        restaurant.get().update(restaurantDto.getRestaurantName(), restaurantDto.getRestaurantInfo(), restaurantDto.getPhoneNumber());
        restaurantRepository.save(restaurant.get());
        return ResponseEntity.status(HttpStatus.OK).body("수정 성공!");
    }

    public ResponseEntity<RestaurantDto> getRestaurant(Long restaurantId) {
        Optional<Restaurant> optionalRestaurant = restaurantRepository.findById(restaurantId);
        if (optionalRestaurant.isPresent()) {
            Restaurant restaurant = optionalRestaurant.get();
            RestaurantDto restaurantDto = convertToDto(restaurant);
            return ResponseEntity.status(HttpStatus.OK).body(restaurantDto);
        } else {
            throw new NotFoundObjException(messageSource.getMessage(
                    "not.found.restaurant", null, "해당 식당이 존재하지 않습니다.", Locale.getDefault()));
        }
    }

    public ResponseEntity<List<RestaurantDto>> getAllRestaurants(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Restaurant> restaurantPage = restaurantRepository.findAllByOrderByCreatedAtDesc(pageable);
        List<RestaurantDto> restaurantDtoList = restaurantPage.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(restaurantDtoList);
    }

    private RestaurantDto convertToDto(Restaurant restaurant) {
        return new RestaurantDto(restaurant.getRestaurantName(), restaurant.getRestaurantInfo(),
                restaurant.getPhoneNumber(), restaurant.getLikes());
    }
}