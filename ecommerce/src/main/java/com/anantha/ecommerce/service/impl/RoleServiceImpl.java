package com.anantha.ecommerce.service.impl;

import com.anantha.ecommerce.dto.RoleDto;
import com.anantha.ecommerce.entity.Role;
import com.anantha.ecommerce.repository.RoleRepository;
import com.anantha.ecommerce.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor  // constructor-based DI
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public RoleDto createRole(RoleDto roleDto) {
        // check duplicate
        if (roleRepository.existsByName(roleDto.getName())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Role with name '" + roleDto.getName() + "' already exists"
            );
        }

        Role role = mapToEntity(roleDto);
        Role saved = roleRepository.save(role);
        return mapToDto(saved);
    }

    @Override
    public RoleDto getRoleById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Role not found with id: " + id
                ));
        return mapToDto(role);
    }

    @Override
    public List<RoleDto> getAllRoles() {
        return roleRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public RoleDto updateRole(Long id, RoleDto roleDto) {
        Role existing = roleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Role not found with id: " + id
                ));

        // If name changes, ensure uniqueness
        if (!existing.getName().equalsIgnoreCase(roleDto.getName())
                && roleRepository.existsByName(roleDto.getName())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Role with name '" + roleDto.getName() + "' already exists"
            );
        }

        existing.setName(roleDto.getName());
        Role updated = roleRepository.save(existing);
        return mapToDto(updated);
    }

    @Override
    public void deleteRole(Long id) {
        Role existing = roleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Role not found with id: " + id
                ));
        roleRepository.delete(existing);
    }

    // ---------- Mapping helpers ----------

    private RoleDto mapToDto(Role role) {
        return RoleDto.builder()
                .id(role.getId())
                .name(role.getName())
                .build();
    }

    private Role mapToEntity(RoleDto dto) {
        return Role.builder()
                .id(dto.getId())
                .name(dto.getName())
                .build();
    }
}
