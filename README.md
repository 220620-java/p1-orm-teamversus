# p1-orm-teamversus
## ConnectionUtil
* Utility class that creates an object we use to connect to the database.
* Postgres is the RDBMS used.

## Data Access Object 
* T create(T t);
  * Takes in an object, and returns an object of the same type.
  * INSERT t as a record into the database.
* T findById(T t);
  * Takes in an object, and returns an object of the same type.
  * SELECT a result table based off the contents of t.
* List<T> findAll(Class<?> t);
  * Takes in the class of an object, and returns a list of objects of that same type.
  * SELECT records from the table corresponding to t.
* update(T t);
  * Takes in an object.
  * UPDATE a records fields to the properties of t.
* delete(T t);
  *Takes in an object.
  * DELETE a record based off the properties of t.

## Object Relational Mapping
* Class we use to implement the DAO.
* Create queries based off the properties of the objects passed with the methods, and then maps the results back into the object.
* Reflection allows the one ORM class to work across all models that have @PrimaryKey defined.

## PrimaryKey
We use this custom annotation to store the field name that corresponds with the primary key of the corresponding database table.

## Models
* Person
  * id
  * username
  * passwrd
  * first_name
  * last_name
* Artist
  * id
  * stage_name
* Album
  * id
  * title
  * artist_id references Artist
* Inventory
  * id
  * person_id references Person
  * album_id references Album
* Api Key
  * id
  * person_id references Person
