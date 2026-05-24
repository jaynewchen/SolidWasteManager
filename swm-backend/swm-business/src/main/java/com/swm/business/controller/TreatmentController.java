package com.swm.business.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.swm.business.service.TreatmentService;
import com.swm.common.Result;
import com.swm.common.annotation.OperationLog;
import com.swm.common.dto.TreatmentDTO;
import com.swm.common.dto.TreatmentQueryDTO;
import com.swm.common.vo.BatchOptionVO;
import com.swm.common.vo.TreatmentVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/treatment")
public class TreatmentController {

    @Autowired
    private TreatmentService treatmentService;

    @GetMapping
    @PreAuthorize("hasAuthority('treatment:list')")
    public Result<IPage<TreatmentVO>> list(TreatmentQueryDTO query) {
        return Result.ok(treatmentService.queryPage(query));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('treatment:list')")
    public Result<TreatmentVO> detail(@PathVariable Long id) {
        return Result.ok(treatmentService.getById(id));
    }

    @OperationLog(module = "处置管理", operation = "新增处置")
    @PostMapping
    @PreAuthorize("hasAuthority('treatment:add')")
    public Result<TreatmentVO> create(@RequestBody TreatmentDTO dto) {
        Long userId = getCurrentUserId();
        return Result.ok(treatmentService.create(dto, userId));
    }

    @OperationLog(module = "处置管理", operation = "编辑处置")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('treatment:edit')")
    public Result<TreatmentVO> update(@PathVariable Long id, @RequestBody TreatmentDTO dto) {
        return Result.ok(treatmentService.update(id, dto));
    }

    @OperationLog(module = "处置管理", operation = "删除处置", level = "IMPORTANT")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('treatment:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        treatmentService.delete(id);
        return Result.ok();
    }

    @GetMapping("/batches/available")
    @PreAuthorize("hasAuthority('treatment:add')")
    public Result<List<BatchOptionVO>> availableBatches() {
        return Result.ok(treatmentService.getAvailableBatches());
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Long) {
            return (Long) authentication.getPrincipal();
        }
        return null;
    }
}
