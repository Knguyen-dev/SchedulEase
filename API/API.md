# API for ScheduleEase



## Todos
- Our taskList now needs to include any associated tasks so be sure to update the taskListMapper and the TaskListServiceImpl
- We need to create a TaskServiceImpl and a TaskMapperImpl.
- Design and create the TaskDTO and TaskMapperImpl

- We updated the implementation so now we may need to test the delete user.
- Should probably test the findUserTaskList functions so that it correctly finds the tasks with or without the tasks

## Issues and considerations
- Regardless we always get a "BadCredentialsException", but never a "UsernameNotFoundException". I'm seeing a pattern. All the errors related to AuthenticationException are not being caught by our handler in ControllerAdvices. The BadCredentialsException, the InsufficientAuthenticationException. Also, 'AuthorizationDeniedException' is redirected to the handleGlobalException method. Granted it isn't inherited from 
'AuthenticationException' but I put a 'AccessDeniedException' which the AuthorizationDeniedException inherits from. Yeah authentication exceptions literally aren't being caught by the correct exception handler
- Session timeout error. We aren't able to set the session timeout. It's always at 30minutes even though we're modifying the 'server.servlet.session.timeout' property. So that need to be changed. Make sure ttl is also controlled and fine-grained.
- Currently not using the in-memory for testing, which kind of sucks. Maybe later we'll be able to have this and redis. So right now in order to do integration tests, you have to have the docker and redis containers running.

- Role annotations: You can create custom role annotations to make things a little more maintainable.
- Deleting user and associated data isn't working. 



## Notes
- Logout route '/auth/logout' will only work as long as you're already logged in. This actually makes a bit of sense because you shouldn't be able to log out if you're not even registered, since logging out actually affects redis and local resources.


## Accessing Redis from Docker and understanding things
Keep in mind that the redis container does have a volume, so it will retain information even after restarting or stopping the container.
```
1. docker exec -it <your-redis-container> bash
2. redis-cli

[//]: # (List all keys)
3. keys *

[//]: # (Deletes all keys)
4. flushall 
```
Okay, so when someone logs in, these keys are added:
```
# This key stores expiration information for specific sessions.  
1) "spring:session:sessions:expires:9fc78581-3075-486b-8eb4-d95f3abfeb51"

# (Just expiration timestamps for sessions)
2) "spring:session:expirations:1722633000000"

# (This string contain actual session data. The part following 'sessions:' is the unique sessionID. The value stored by the keys will be the serialized session data)
3) "spring:session:sessions:9fc78581-3075-486b-8eb4-d95f3abfeb51"

# This key is used for looking up sessiosn by the princip name, which is the username or ID. In this case it's the username 
# 'knguyen44' is mapped to a session ID
4) "spring:session:index:org.springframework.session.FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME:knguyen44"
```

Also, they can get a cookie: `ScheduleEase-SessionID=OWZjNzg1ODEtMzA3NS00ODZiLThlYjQtZDk1ZjNhYmZlYjUx; Path=/; HttpOnly;`. Here we know the cookie name and the long string is a base64-encoded string. Decoding it will yield the raw session ID that's stored in the Redis database.

So let's access the value of `"spring:session:sessions:9fc78581-3075-486b-8eb4-d95f3abfeb51`.
```
# Check the type of the value the key is linked to. It's a hash, so you can't do simple operations on it.
1. type "spring:session:sessions:9fc78581-3075-486b-8eb4-d95f3abfeb51"

# This outputs the values associated with one redis session
2. hgetall "spring:session:sessions:9fc78581-3075-486b-8eb4-d95f3abfeb51"; 
```
Remember that the data stored in the redis is just the Spring Security Context for that particular login

- NOTE: Do `ctrl + shift + c` and `ctrl + shift + v`. For copying and pasting in bash


## Accessing PostgresSQL from Docker
```
[//]: # (Enter the bash for the postgres container)
1. docker exec -it api-postgres bash

[//]: # (Connect to the database)
2. psql -U myUser -d myDatabase

[//]: # (List all tables)
3. \dt

[//]: # (Do a query on a table)
4. SELECT * FROM appUser;

```
# Credits:
1. [How to handle cookies and a Session in a Java Servlet - Baeldung](https://www.baeldung.com/java-servlet-cookies-session)
2. [Handling deleting cookies from a response](https://stackoverflow.com/questions/890935/how-do-you-remove-a-cookie-in-a-java-servlet)
3. [Roles and Privileges in Spring Security](https://www.baeldung.com/role-and-privilege-for-spring-security-registration)


## UI Inspiration
Here's some UI Inspiration if you don't want to follow the google calendar design for some reason. Though these are more 
so project management designs. Though I think we're probably going to stick with google calendar if I'm being real
1. https://www.behance.net/gallery/199866937/App-for-task-management-using-kanban-boards?tracking_source=search_projects|task+app&l=15
2. https://www.behance.net/gallery/199866937/App-for-task-management-using-kanban-boards?tracking_source=search_projects|task+app&l=15
3. https://www.behance.net/gallery/188384331/Flows-App-Task-and-Project-Management?tracking_source=search_projects|task+app&l=12
4. https://www.behance.net/gallery/188698010/OfficeSphere-Virtual-Office-Management-Dashboard?tracking_source=search_projects|task+app&l=36