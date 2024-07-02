package com.sparta.outsourcing.controller;

import com.sparta.outsourcing.dto.MenuDto;
import com.sparta.outsourcing.entity.Menu;
import com.sparta.outsourcing.repository.MenuRepository;
import com.sparta.outsourcing.security.UserDetailsImpl;
import com.sparta.outsourcing.service.MenuService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/menu")
@RestController
public class MenuController {

    private final MenuService menuService;

    @PostMapping("/{restaurantId}")
    public ResponseEntity<String> addMenu(@Valid @RequestBody MenuDto menuDto,
                                          @PathVariable("restaurantId") Long restaurantId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return menuService.addMenu(restaurantId, menuDto, userDetails.getUser());
    }

    @GetMapping("/{restaurantId}")
    public ResponseEntity<List<MenuDto>> getMenu(@PathVariable("restaurantId") Long restaurantId) {
        return menuService.getMenus(restaurantId);
    }

    @PatchMapping("/{restaurantId}/{menuId}")
    public ResponseEntity<String> updateMenu(@Valid @RequestBody MenuDto menuDto,
                                             @PathVariable("restaurantId") Long restaurantId, @AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable("menuId") Long menuId) {
        return menuService.updateMenu(restaurantId, menuDto, userDetails.getUser(),menuId);
    }

    @DeleteMapping("/{restaurantId}/{menuId}")
    public ResponseEntity<String> deleteMenu(@PathVariable("restaurantId") Long restaurantId, @AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long menuId) {
        return menuService.deleteMenu(restaurantId, userDetails.getUser(), menuId);
    }
}