package com.tuling.order.feign;

import io.seata.core.exception.TransactionException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Fox
 */
@FeignClient(name = "account-service",path = "/account")
@Repository
public interface AccountFeignService {
    
    @RequestMapping("/debit")
    Boolean debit(@RequestParam("userId") String userId,@RequestParam("money") int money);
}
