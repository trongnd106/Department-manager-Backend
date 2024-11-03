package com.hththn.dev.department_manager.service;

import com.hththn.dev.department_manager.entity.Fund;
import com.hththn.dev.department_manager.repository.FundRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class FundService {

    private FundRepository fundRepository;


    public void deleteFund(String fundCode) throws Exception {
        if(fundCode != null) {
            this.fundRepository.findByFundCode(fundCode);
        }
        else throw new Exception("Invoice code = "+fundCode+" isn't exist ");
    }


}

