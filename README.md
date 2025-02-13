## Приложение «Трекер задач» – Защита с помощью Spring Security

### Что нужно сделать

Защитите приложение «Трекер задач» с помощью Spring Security.

Добавьте для сущности User поля password и roles. В приложении должны быть следующие роли: ROLE_USER, ROLE_MANAGER.  
Проведите для контроллера, отвечающего за работу с пользователями, доработку:
- получение (как списком, так и по ID), обновление и удаление профилей пользователей должны быть доступны только тем клиентам, которые имеют одну из следующих ролей: ROLE_USER, ROLE_MANAGER.

Проведите для контроллера, отвечающего за работу с задачами, доработки:
- получение списка задач, получение задачи по ID, добавление наблюдателя доступны пользователю с одной из следующих ролей: ROLE_USER, ROLE_MANAGER;
- создание, обновление и удаление задачи доступны пользователю с ролью ROLE_MANAGER.

Все данные о текущем пользователе, передаваемые в контроллер, должны быть получены из UserDetails.

#### Рекомендация

При защите приложения «Трекер задач» можете хранить данные о ролях в MongoDB в следующем стиле:

```java
@Field("roles")
private Set<RoleType> roles = new HashSet<>();
```
Здесь RoleType — это перечисление ролей.
