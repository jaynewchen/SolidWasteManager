package com.swm.dict.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.swm.common.Result;
import com.swm.common.entity.*;
import com.swm.dict.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/dict")
public class DictController {

    @Autowired
    private SwmWasteProducerMapper wasteProducerMapper;

    @Autowired
    private SwmWorkshopMapper workshopMapper;

    @Autowired
    private SwmMineSourceMapper mineSourceMapper;

    @Autowired
    private SwmWasteCategoryMapper wasteCategoryMapper;

    @Autowired
    private SwmTreatmentProcessMapper treatmentProcessMapper;

    // ==================== List endpoints (for dropdowns) ====================

    @GetMapping("/producers/list")
    public Result listProducers() {
        QueryWrapper<SwmWasteProducer> wrapper = new QueryWrapper<SwmWasteProducer>();
        wrapper.eq("status", 1);
        List<SwmWasteProducer> list = wasteProducerMapper.selectList(wrapper);
        return Result.ok(list);
    }

    @GetMapping("/workshops/list")
    public Result listWorkshops() {
        QueryWrapper<SwmWorkshop> wrapper = new QueryWrapper<SwmWorkshop>();
        wrapper.eq("status", 1);
        List<SwmWorkshop> list = workshopMapper.selectList(wrapper);
        return Result.ok(list);
    }

    @GetMapping("/mines/list")
    public Result listMines() {
        QueryWrapper<SwmMineSource> wrapper = new QueryWrapper<SwmMineSource>();
        wrapper.eq("status", 1);
        List<SwmMineSource> list = mineSourceMapper.selectList(wrapper);
        return Result.ok(list);
    }

    @GetMapping("/categories/list")
    public Result listCategories() {
        QueryWrapper<SwmWasteCategory> wrapper = new QueryWrapper<SwmWasteCategory>();
        wrapper.eq("status", 1);
        List<SwmWasteCategory> list = wasteCategoryMapper.selectList(wrapper);
        return Result.ok(list);
    }

    @GetMapping("/processes/list")
    public Result listProcesses() {
        QueryWrapper<SwmTreatmentProcess> wrapper = new QueryWrapper<SwmTreatmentProcess>();
        wrapper.eq("status", 1);
        List<SwmTreatmentProcess> list = treatmentProcessMapper.selectList(wrapper);
        return Result.ok(list);
    }

    // ==================== Producers CRUD ====================

    @GetMapping("/producers")
    @PreAuthorize("hasRole('ADMIN')")
    public Result pageProducers(@RequestParam(defaultValue = "1") Integer page,
                                @RequestParam(defaultValue = "20") Integer size) {
        Page<SwmWasteProducer> p = new Page<SwmWasteProducer>(page, size);
        p = wasteProducerMapper.selectPage(p, new QueryWrapper<SwmWasteProducer>().orderByDesc("create_time"));
        return Result.ok(p);
    }

    @PostMapping("/producers")
    @PreAuthorize("hasRole('ADMIN')")
    public Result createProducer(@RequestBody SwmWasteProducer producer) {
        wasteProducerMapper.insert(producer);
        return Result.ok(producer);
    }

    @PutMapping("/producers/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result updateProducer(@PathVariable Long id, @RequestBody SwmWasteProducer producer) {
        producer.setId(id);
        wasteProducerMapper.updateById(producer);
        return Result.ok(producer);
    }

    @DeleteMapping("/producers/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result deleteProducer(@PathVariable Long id) {
        wasteProducerMapper.deleteById(id);
        return Result.ok();
    }

    // ==================== Workshops CRUD ====================

    @GetMapping("/workshops")
    @PreAuthorize("hasRole('ADMIN')")
    public Result pageWorkshops(@RequestParam(defaultValue = "1") Integer page,
                                @RequestParam(defaultValue = "20") Integer size) {
        Page<SwmWorkshop> p = new Page<SwmWorkshop>(page, size);
        p = workshopMapper.selectPage(p, null);
        return Result.ok(p);
    }

    @PostMapping("/workshops")
    @PreAuthorize("hasRole('ADMIN')")
    public Result createWorkshop(@RequestBody SwmWorkshop workshop) {
        workshopMapper.insert(workshop);
        return Result.ok(workshop);
    }

    @PutMapping("/workshops/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result updateWorkshop(@PathVariable Long id, @RequestBody SwmWorkshop workshop) {
        workshop.setId(id);
        workshopMapper.updateById(workshop);
        return Result.ok(workshop);
    }

    @DeleteMapping("/workshops/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result deleteWorkshop(@PathVariable Long id) {
        workshopMapper.deleteById(id);
        return Result.ok();
    }

    // ==================== Mine sources CRUD ====================

    @GetMapping("/mines")
    @PreAuthorize("hasRole('ADMIN')")
    public Result pageMines(@RequestParam(defaultValue = "1") Integer page,
                            @RequestParam(defaultValue = "20") Integer size) {
        Page<SwmMineSource> p = new Page<SwmMineSource>(page, size);
        p = mineSourceMapper.selectPage(p, null);
        return Result.ok(p);
    }

    @PostMapping("/mines")
    @PreAuthorize("hasRole('ADMIN')")
    public Result createMine(@RequestBody SwmMineSource mine) {
        mineSourceMapper.insert(mine);
        return Result.ok(mine);
    }

    @PutMapping("/mines/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result updateMine(@PathVariable Long id, @RequestBody SwmMineSource mine) {
        mine.setId(id);
        mineSourceMapper.updateById(mine);
        return Result.ok(mine);
    }

    @DeleteMapping("/mines/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result deleteMine(@PathVariable Long id) {
        mineSourceMapper.deleteById(id);
        return Result.ok();
    }

    // ==================== Categories CRUD ====================

    @GetMapping("/categories")
    @PreAuthorize("hasRole('ADMIN')")
    public Result pageCategories(@RequestParam(defaultValue = "1") Integer page,
                                 @RequestParam(defaultValue = "20") Integer size) {
        Page<SwmWasteCategory> p = new Page<SwmWasteCategory>(page, size);
        p = wasteCategoryMapper.selectPage(p, null);
        return Result.ok(p);
    }

    @PostMapping("/categories")
    @PreAuthorize("hasRole('ADMIN')")
    public Result createCategory(@RequestBody SwmWasteCategory category) {
        wasteCategoryMapper.insert(category);
        return Result.ok(category);
    }

    @PutMapping("/categories/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result updateCategory(@PathVariable Long id, @RequestBody SwmWasteCategory category) {
        category.setId(id);
        wasteCategoryMapper.updateById(category);
        return Result.ok(category);
    }

    @DeleteMapping("/categories/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result deleteCategory(@PathVariable Long id) {
        wasteCategoryMapper.deleteById(id);
        return Result.ok();
    }

    // ==================== Processes CRUD ====================

    @GetMapping("/processes")
    @PreAuthorize("hasRole('ADMIN')")
    public Result pageProcesses(@RequestParam(defaultValue = "1") Integer page,
                                @RequestParam(defaultValue = "20") Integer size) {
        Page<SwmTreatmentProcess> p = new Page<SwmTreatmentProcess>(page, size);
        p = treatmentProcessMapper.selectPage(p, null);
        return Result.ok(p);
    }

    @PostMapping("/processes")
    @PreAuthorize("hasRole('ADMIN')")
    public Result createProcess(@RequestBody SwmTreatmentProcess process) {
        treatmentProcessMapper.insert(process);
        return Result.ok(process);
    }

    @PutMapping("/processes/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result updateProcess(@PathVariable Long id, @RequestBody SwmTreatmentProcess process) {
        process.setId(id);
        treatmentProcessMapper.updateById(process);
        return Result.ok(process);
    }

    @DeleteMapping("/processes/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result deleteProcess(@PathVariable Long id) {
        treatmentProcessMapper.deleteById(id);
        return Result.ok();
    }
}
