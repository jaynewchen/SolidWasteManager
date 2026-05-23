package com.swm.business.service;

import com.swm.business.mapper.SwmReceivingMapper;
import com.swm.common.entity.SwmMineSource;
import com.swm.common.entity.SwmWasteProducer;
import com.swm.common.entity.SwmWorkshop;
import com.swm.dict.mapper.SwmMineSourceMapper;
import com.swm.dict.mapper.SwmWasteProducerMapper;
import com.swm.dict.mapper.SwmWorkshopMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class BatchNoGenerator {

    @Autowired
    private SwmWasteProducerMapper wasteProducerMapper;

    @Autowired
    private SwmWorkshopMapper workshopMapper;

    @Autowired
    private SwmMineSourceMapper mineSourceMapper;

    @Autowired
    private SwmReceivingMapper swmReceivingMapper;

    public String generate(Long producerId, Long workshopId, Long mineSourceId, LocalDate receiveDate) {
        String licenseSuffix = "";
        String workshopAbbr = "";
        String mineCode = "";

        if (producerId != null) {
            SwmWasteProducer producer = wasteProducerMapper.selectById(producerId);
            if (producer != null && producer.getLicenseSuffix() != null) {
                licenseSuffix = producer.getLicenseSuffix();
            }
        }

        if (workshopId != null) {
            SwmWorkshop workshop = workshopMapper.selectById(workshopId);
            if (workshop != null && workshop.getWorkshopAbbr() != null) {
                workshopAbbr = workshop.getWorkshopAbbr();
            }
        }

        if (mineSourceId != null) {
            SwmMineSource mineSource = mineSourceMapper.selectById(mineSourceId);
            if (mineSource != null && mineSource.getMineCode() != null) {
                mineCode = mineSource.getMineCode();
            }
        }

        String dateStr;
        if (receiveDate != null) {
            dateStr = receiveDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        } else {
            dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        }

        String base = licenseSuffix + "-" + workshopAbbr + "-" + mineCode + "-" + dateStr;

        String maxBatchNo = swmReceivingMapper.selectMaxBatchNoByPrefix(base);
        if (maxBatchNo == null || maxBatchNo.length() == 0) {
            return base;
        }

        // First collision: maxBatchNo is the base itself (no counter suffix)
        if (maxBatchNo.length() == base.length()) {
            return base + "-01";
        }

        // Subsequent collisions: extract numeric counter and increment
        int lastDash = maxBatchNo.lastIndexOf("-");
        if (lastDash >= 0) {
            String suffixStr = maxBatchNo.substring(lastDash + 1);
            try {
                int suffix = Integer.parseInt(suffixStr);
                return base + "-" + String.format("%02d", suffix + 1);
            } catch (NumberFormatException e) {
                return base;
            }
        }

        return base;
    }
}
