package com.hackerrank.orm.controller;

import com.hackerrank.orm.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ItemController {
    @Autowired
    ItemService itemService;

    //1. insert POST


    //2. update PUT


    //3. delete by itemId DELETE


    //4. delete all DELETE


    //5. get by itemId GET


    //6. get all GET


    //7. filters by fields ?itemStatus={status}&itemEnteredByUser={modifiedBy} GET


    //8. select all with sorting and pagination ?pageSize={pageSize}&page={page}&sortBy={sortBy} GET

}
