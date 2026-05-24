package com.swm.business.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.swm.business.service.StorageService;
import com.swm.common.Result;
import com.swm.common.annotation.OperationLog;
import com.swm.common.dto.StorageDTO;
import com.swm.common.dto.StorageQueryDTO;
import com.swm.common.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/storage")
public class StorageController {

    @Autowired
    private StorageService storageService;

    @GetMapping
    @PreAuthorize("hasAuthority('storage:list')")
    public Result<IPage<StorageVO>> list(StorageQueryDTO query) {
        return Result.ok(storageService.queryPage(query));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('storage:list')")
    public Result<StorageVO> detail(@PathVariable Long id) {
        return Result.ok(storageService.getById(id));
    }

    @OperationLog(module = "入库管理", operation = "新增入库")
    @PostMapping
    @PreAuthorize("hasAuthority('storage:add')")
    public Result<StorageVO> create(@RequestBody StorageDTO dto) {
        Long userId = getCurrentUserId();
        return Result.ok(storageService.create(dto, userId));
    }

    @OperationLog(module = "入库管理", operation = "编辑入库")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('storage:edit')")
    public Result<StorageVO> update(@PathVariable Long id, @RequestBody StorageDTO dto) {
        return Result.ok(storageService.update(id, dto));
    }

    @OperationLog(module = "入库管理", operation = "删除入库", level = "IMPORTANT")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('storage:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        storageService.delete(id);
        return Result.ok();
    }

    @GetMapping("/stats/inventory-by-area")
    @PreAuthorize("hasAuthority('storage:list')")
    public Result<List<InventoryByAreaVO>> inventoryByArea() {
        return Result.ok(storageService.getInventoryByArea());
    }

    @GetMapping("/stats/distribution-by-category")
    @PreAuthorize("hasAuthority('storage:list')")
    public Result<List<WasteDistributionVO>> distributionByCategory() {
        return Result.ok(storageService.getDistributionByCategory());
    }

    @GetMapping("/batches/available")
    @PreAuthorize("hasAuthority('storage:add')")
    public Result<List<BatchOptionVO>> availableBatches() {
        return Result.ok(storageService.getAvailableBatches());
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Long) {
            return (Long) authentication.getPrincipal();
        }
        return null;
    }
}
