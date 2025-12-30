package com.hackerrank.orm.service;

import com.hackerrank.orm.model.Item;
import com.hackerrank.orm.model.ItemStatus;
import com.hackerrank.orm.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Transactional
@Service
public class ItemService {

    @Autowired
    private ItemRepository itemRepository;

    // CREATE
    public Item createItem(Item item) {
        if (item.getItemId() != null && itemRepository.existsById(item.getItemId())) {
            throw new IllegalArgumentException("Item already exists");
        }
        return itemRepository.save(item); // MUST return saved entity
    }

    // UPDATE
    public Item updateItem(Integer itemId, Item item) {
        Item existing = itemRepository.findById(itemId)
                .orElseThrow(() -> new NoSuchElementException("Item not found"));

        if (item.getItemName() != null) {
            existing.setItemName(item.getItemName());
        }
        if (item.getItemLastModifiedByUser() != null) {
            existing.setItemLastModifiedByUser(item.getItemLastModifiedByUser());
        }

        return itemRepository.save(existing);
    }

    // GET BY ID
    public Item getItem(Integer itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NoSuchElementException("Item not found"));
    }

    // GET ALL
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    // DELETE BY ID
    public void deleteItem(Integer itemId) {
        if (!itemRepository.existsById(itemId)) {
            throw new IllegalArgumentException("Item not found");
        }
        itemRepository.deleteById(itemId);
    }

    // DELETE ALL
    public void deleteAllItems() {
        itemRepository.deleteAll();
    }

    // FILTER BY STATUS + USER
    public List<Item> filterItems(ItemStatus status, String enteredByUser) {
        return itemRepository.findByItemStatusAndItemEnteredByUser(status, enteredByUser);
    }

    // PAGINATION + SORTING
    public Page<Item> getItemsWithPagination(int page, int pageSize, String sortBy) {
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(sortBy));
        return itemRepository.findAll(pageable);
    }
}
