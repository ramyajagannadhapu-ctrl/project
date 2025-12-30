package com.hackerrank.orm.service;

import com.hackerrank.orm.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class ItemService {
    @Autowired
    public ItemRepository itemRepository;
}
