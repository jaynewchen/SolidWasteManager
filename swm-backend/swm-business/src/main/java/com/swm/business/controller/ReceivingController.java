package com.swm.business.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.swm.business.service.ReceivingService;
import com.swm.common.Result;
import com.swm.common.dto.ReceivingDTO;
import com.swm.common.dto.ReceivingQueryDTO;
import com.swm.common.vo.ReceivingVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/receiving")
public class ReceivingController {

    @Autowired
    private ReceivingService receivingService;

    @GetMapping
    @PreAuthorize("hasAuthority('receiving:list')")
    public Result<IPage<ReceivingVO>> list(ReceivingQueryDTO query) {
        IPage<ReceivingVO> page = receivingService.queryPage(query);
        return Result.ok(page);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('receiving:list')")
    public Result<ReceivingVO> detail(@PathVariable Long id) {
        ReceivingVO vo = receivingService.getById(id);
        return Result.ok(vo);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('receiving:add')")
    public Result<ReceivingVO> create(@RequestBody ReceivingDTO dto) {
        Long userId = getCurrentUserId();
        ReceivingVO vo = receivingService.create(dto, userId);
        return Result.ok(vo);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('receiving:edit')")
    public Result<ReceivingVO> update(@PathVariable Long id, @RequestBody ReceivingDTO dto) {
        ReceivingVO vo = receivingService.update(id, dto);
        return Result.ok(vo);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('receiving:delete')")
    public Result delete(@PathVariable Long id) {
        receivingService.delete(id);
        return Result.ok();
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Long) {
            return (Long) authentication.getPrincipal();
        }
        return null;
    }
}
