package com.tuling.account.controller;

import com.tuling.account.service.AccountService;
import io.seata.core.context.RootContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

/**
 * @author Fox
 */
@RestController
@RequestMapping("/account")
public class AccountController {
    
    @Autowired
    private AccountService accountService;
    
    @RequestMapping("/debit")
    public Boolean debit(String userId, int money) throws Exception {
        // 用户账户扣款
        accountService.debit(userId, money);
        return true;
    }
    
}
