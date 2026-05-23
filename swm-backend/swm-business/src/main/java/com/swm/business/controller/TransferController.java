package com.swm.business.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.swm.business.service.TransferService;
import com.swm.common.Result;
import com.swm.common.dto.TransferDTO;
import com.swm.common.dto.TransferQueryDTO;
import com.swm.common.vo.BatchOptionVO;
import com.swm.common.vo.TransferVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/transfer")
public class TransferController {

    @Autowired
    private TransferService transferService;

    @GetMapping
    @PreAuthorize("hasAuthority('transfer:list')")
    public Result<IPage<TransferVO>> list(TransferQueryDTO query) {
        return Result.ok(transferService.queryPage(query));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('transfer:list')")
    public Result<TransferVO> detail(@PathVariable Long id) {
        return Result.ok(transferService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('transfer:add')")
    public Result<TransferVO> create(@RequestBody TransferDTO dto) {
        Long userId = getCurrentUserId();
        return Result.ok(transferService.create(dto, userId));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('transfer:edit')")
    public Result<TransferVO> update(@PathVariable Long id, @RequestBody TransferDTO dto) {
        return Result.ok(transferService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('transfer:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        transferService.delete(id);
        return Result.ok();
    }

    @GetMapping("/batches/available")
    @PreAuthorize("hasAuthority('transfer:add')")
    public Result<List<BatchOptionVO>> availableBatches() {
        return Result.ok(transferService.getAvailableBatches());
    }

    @GetMapping("/users/active")
    @PreAuthorize("hasAuthority('transfer:add')")
    public Result<List<Map<String, Object>>> activeUsers() {
        return Result.ok(transferService.getActiveUsers());
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Long) {
            return (Long) authentication.getPrincipal();
        }
        return null;
    }
}
