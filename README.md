## Environment:
- Java version: 17
- Maven version: 3.*
- Spring Boot version: 3.0.6

## Read-Only Files:
- src/test/*

## Data:
Example of a Item data JSON object:
```json
{
    "itemId":1,
    "itemName":"item_x",
     "itemEnteredByUser":"user_x",
    "itemEnteredDate":"2020-05-10T13:00:41.499",
    "itemBuyingPrice":50.0,
    "itemSellingPrice":55.0,
    "itemLastModifiedDate":"2020-05-10T13:00:41.498",
    "itemLastModifiedByUser":"user_y",
    "itemStatus":"AVAILABLE"
}
```

## Requirements:
Create a Spring Boot ORM/Hibernate-based MVC project that provides the REST endpoints for a basic inventory management system. For this project, the inventory system contains only the single entity named `Item`.

You need to implement the `/app/item` REST endpoint for the following 8 operations:

`POST` request to `/app/item`:

* should accept POST requests at /app/item and item data as a JSON body
* if the itemId exists in the database, then it should return status code 400
* If the itemId doesn't exist in the database, then it should insert the data and return the inserted item as a response with status code 201
 
`PUT` request to `/app/item/{itemId}`:

* should accept PUT requests at /app/item/{itemId} and item data as a JSON body, where itemId is a path variable
* if the itemId exists in the database, then it should update and return the updated item as a response with status code 200
* if the itemId doesn't exist in the database, it should return status code 404

`DELETE` request to `/app/item/{itemId}`:

* should accept DELETE requests at /app/item/{itemId} where itemId is a path variable
* if the itemId exists in the database, then it should delete the specified item and return status code 200
* if the itemId doesn't exist in the database, it should return status code 400
 
`DELETE` requests at `/app/item`:

* should accept DELETE requests at /app/item
* should delete all the items from the database and return status code 200

`GET` request to `/app/item/{itemId}`:

* should accept GET requests at /app/item/{itemId} where itemId is a path variable
* if the itemId exists in the database, then it should return the item with status code 200
* if the itemId doesn't exist in the database, it should return status code 404

`GET` request to `/app/item`:

* should accept GET requests at /app/item
* should return all the items from the database with return status code 200

`GET` request to `/app/item?itemStatus={status}&itemEnteredByUser={enteredBy}`:

* should accept GET requests at /app/item?itemStatus={status}&itemEnteredByUser={enteredBy}
* should return all the items having itemStatus=status and itemEnteredByUser=enteredBy, where status and enteredBy are request params, with status code 200
 
`GET` request to `/app/item?pageSize={pageSize}&page={page}&sortBy={sortByField}`:

* should accept GET requests at /app/item?pageSize={pageSize}&page={page}&sortBy={sortByField}
* should return the requested page by paginating with pageSize and sorting by the sortBy field

Your task is to implement the above 8 endpoints in `ItemController` class, and the corresponding service methods in the `ItemService` class, so that it passes all the test cases when running the provided unit tests. The project by default supports the use of the H2 database. Implement the POST request to `/app/item` first because testing the other methods requires POST to work correctly.

## Commands
- run: 
```bash
mvn clean package; java -jar target/SpringBootORM-1.0.jar
```
- install: 
```bash
mvn clean install
```
- test: 
```bash
mvn clean test
```
