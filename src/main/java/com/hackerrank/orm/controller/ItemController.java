package com.hackerrank.orm.controller;

import com.hackerrank.orm.model.Item;
import com.hackerrank.orm.model.ItemStatus;
import com.hackerrank.orm.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/app/item")
public class ItemController {

    @Autowired
    private ItemService itemService;


    // INSERT - POST /app/item

    @PostMapping
    public ResponseEntity<Item> createItem(@RequestBody Item item) {
        try {
            Item savedItem = itemService.createItem(item);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedItem);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }


    // UPDATE - PUT /app/item/{itemId}

    @PutMapping("/{itemId}")
    public ResponseEntity<Item> updateItem(
            @PathVariable Integer itemId,
            @RequestBody Item item) {
        try {
            Item updatedItem = itemService.updateItem(itemId, item);
            return ResponseEntity.ok(updatedItem);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }


    // DELETE BY ID - DELETE /app/item/{itemId}
  
    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deleteItem(@PathVariable Integer itemId) {
        try {
            itemService.deleteItem(itemId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }


    // DELETE ALL - DELETE /app/item

    @DeleteMapping
    public ResponseEntity<Void> deleteAllItems() {
        itemService.deleteAllItems();
        return ResponseEntity.ok().build();
    }


    // GET BY ID - GET /app/item/{itemId}

    @GetMapping("/{itemId}")
    public ResponseEntity<Item> getItem(@PathVariable Integer itemId) {
        try {
            return ResponseEntity.ok(itemService.getItem(itemId));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // 6️⃣ GET ALL - GET /app/item
    @GetMapping
    public ResponseEntity<List<Item>> getAllItems() {
        return ResponseEntity.ok(itemService.getAllItems());
    }


    //FILTER - GET /app/item?itemStatus={status}&itemEnteredByUser={user}

    @GetMapping(params = {"itemStatus", "itemEnteredByUser"})
    public ResponseEntity<List<Item>> filterItems(
            @RequestParam ItemStatus itemStatus,
            @RequestParam String itemEnteredByUser) {

        return ResponseEntity.ok(
                itemService.filterItems(itemStatus, itemEnteredByUser)
        );
    }


    // PAGINATION + SORTING

    // GET /app/item?pageSize={pageSize}&page={page}&sortBy={sortBy}
    @GetMapping(params = {"pageSize", "page", "sortBy"})
    public ResponseEntity<List<Item>> getItemsWithPagination(
            @RequestParam int pageSize,
            @RequestParam int page,
            @RequestParam String sortBy) {

        return ResponseEntity.ok(
                itemService.getItemsWithPagination(page, pageSize, sortBy)
                        .getContent()
        );
    }
}
