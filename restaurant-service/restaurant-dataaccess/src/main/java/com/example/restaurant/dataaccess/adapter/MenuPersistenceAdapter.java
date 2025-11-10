package com.example.restaurant.dataaccess.adapter;

import com.example.restaurant.application.ports.output.repository.MenuRepository;
import com.example.restaurant.dataaccess.mapper.MenuItemDataAccessMapper;
import com.example.restaurant.dataaccess.repository.MenuItemJpaRepository;
import com.example.restaurant.domain.core.entity.MenuItem;
import com.example.restaurant.domain.core.valueobject.ProductId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MenuPersistenceAdapter implements MenuRepository {

    private final MenuItemJpaRepository menuItemJpaRepository;
    private final MenuItemDataAccessMapper menuItemDataAccessMapper;

    @Override
    public Optional<MenuItem> findByProductId(ProductId productId) {
        return menuItemJpaRepository.findByProductId(productId.getValue())
                .map(menuItemDataAccessMapper::menuItemEntityToMenuItem);
    }
}

