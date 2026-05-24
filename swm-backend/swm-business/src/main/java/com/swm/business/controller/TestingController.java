package com.swm.business.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.swm.business.service.TestingService;
import com.swm.common.Result;
import com.swm.common.annotation.OperationLog;
import com.swm.common.dto.TestingDTO;
import com.swm.common.dto.TestingQueryDTO;
import com.swm.common.vo.BatchOptionVO;
import com.swm.common.vo.TestingVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/testing")
public class TestingController {

    @Autowired
    private TestingService testingService;

    @GetMapping
    @PreAuthorize("hasAuthority('testing:list')")
    public Result<IPage<TestingVO>> list(TestingQueryDTO query) {
        return Result.ok(testingService.queryPage(query));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('testing:list')")
    public Result<TestingVO> detail(@PathVariable Long id) {
        return Result.ok(testingService.getById(id));
    }

    @OperationLog(module = "检测管理", operation = "新增检测")
    @PostMapping
    @PreAuthorize("hasAuthority('testing:add')")
    public Result<TestingVO> create(@RequestBody TestingDTO dto) {
        Long userId = getCurrentUserId();
        return Result.ok(testingService.create(dto, userId));
    }

    @OperationLog(module = "检测管理", operation = "编辑检测")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('testing:edit')")
    public Result<TestingVO> update(@PathVariable Long id, @RequestBody TestingDTO dto) {
        return Result.ok(testingService.update(id, dto));
    }

    @OperationLog(module = "检测管理", operation = "删除检测", level = "IMPORTANT")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('testing:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        testingService.delete(id);
        return Result.ok();
    }

    @OperationLog(module = "检测管理", operation = "审核检测")
    @PutMapping("/{id}/review")
    @PreAuthorize("hasAuthority('testing:edit')")
    public Result<TestingVO> review(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Long reviewerId = getCurrentUserId();
        String opinion = (String) body.get("opinion");
        Boolean qualified = body.get("qualified") != null ? (Boolean) body.get("qualified") : null;
        return Result.ok(testingService.review(id, opinion, qualified, reviewerId));
    }

    @GetMapping("/batches/available")
    @PreAuthorize("hasAuthority('testing:add')")
    public Result<List<BatchOptionVO>> availableBatches() {
        return Result.ok(testingService.getAvailableBatches());
    }

    @GetMapping("/users/active")
    @PreAuthorize("hasAuthority('testing:add')")
    public Result<List<Map<String, Object>>> activeUsers() {
        return Result.ok(testingService.getActiveUsers());
    }

    @GetMapping("/statistics")
    @PreAuthorize("hasAuthority('testing:list')")
    public Result<Map<String, Object>> statistics(
            @RequestParam(required = false) Integer testingType,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return Result.ok(testingService.getStatistics(testingType, startDate, endDate));
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Long) {
            return (Long) authentication.getPrincipal();
        }
        return null;
    }
}
